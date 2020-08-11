package com.apis.controller;

import java.sql.Connection;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.apis.objects.ObjCfgFields;
import com.genesyslab.platform.applicationblocks.com.CfgObject;
import com.genesyslab.platform.applicationblocks.com.ConfServiceFactory;
import com.genesyslab.platform.applicationblocks.com.ConfigException;
import com.genesyslab.platform.applicationblocks.com.IConfService;
import com.genesyslab.platform.applicationblocks.com.Subscription;
import com.genesyslab.platform.applicationblocks.com.objects.CfgAccessGroup;
import com.genesyslab.platform.applicationblocks.com.objects.CfgAgentGroup;
import com.genesyslab.platform.applicationblocks.com.objects.CfgApplication;
import com.genesyslab.platform.applicationblocks.com.objects.CfgCallingList;
import com.genesyslab.platform.applicationblocks.com.objects.CfgCallingListInfo;
import com.genesyslab.platform.applicationblocks.com.objects.CfgCampaign;
import com.genesyslab.platform.applicationblocks.com.objects.CfgCampaignGroup;
import com.genesyslab.platform.applicationblocks.com.objects.CfgDN;
import com.genesyslab.platform.applicationblocks.com.objects.CfgDeltaAccessGroup;
import com.genesyslab.platform.applicationblocks.com.objects.CfgDeltaFormat;
import com.genesyslab.platform.applicationblocks.com.objects.CfgField;
import com.genesyslab.platform.applicationblocks.com.objects.CfgFormat;
import com.genesyslab.platform.applicationblocks.com.objects.CfgID;
import com.genesyslab.platform.applicationblocks.com.objects.CfgPerson;
import com.genesyslab.platform.applicationblocks.com.objects.CfgServer;
import com.genesyslab.platform.applicationblocks.com.objects.CfgTableAccess;
import com.genesyslab.platform.applicationblocks.com.queries.CfgAccessGroupQuery;
import com.genesyslab.platform.applicationblocks.com.queries.CfgAgentGroupQuery;
import com.genesyslab.platform.applicationblocks.com.queries.CfgApplicationQuery;
import com.genesyslab.platform.applicationblocks.com.queries.CfgCallingListQuery;
import com.genesyslab.platform.applicationblocks.com.queries.CfgCampaignGroupQuery;
import com.genesyslab.platform.applicationblocks.com.queries.CfgCampaignQuery;
import com.genesyslab.platform.applicationblocks.com.queries.CfgDNQuery;
import com.genesyslab.platform.applicationblocks.com.queries.CfgFieldQuery;
import com.genesyslab.platform.applicationblocks.com.queries.CfgFormatQuery;
import com.genesyslab.platform.applicationblocks.com.queries.CfgPersonQuery;
import com.genesyslab.platform.applicationblocks.com.queries.CfgTableAccessQuery;
import com.genesyslab.platform.applicationblocks.warmstandby.WarmStandbyConfiguration;
import com.genesyslab.platform.applicationblocks.warmstandby.WarmStandbyService;
import com.genesyslab.platform.commons.connection.configuration.PropertyConfiguration;
import com.genesyslab.platform.commons.collections.KVList;
import com.genesyslab.platform.commons.collections.KeyValueCollection;
import com.genesyslab.platform.commons.connection.configuration.ClientADDPOptions.AddpTraceMode;
import com.genesyslab.platform.commons.protocol.Endpoint;
import com.genesyslab.platform.commons.protocol.ProtocolException;
import com.genesyslab.platform.commons.protocol.RegistrationException;
import com.genesyslab.platform.configuration.protocol.ConfServerProtocol;
import com.genesyslab.platform.configuration.protocol.types.CfgAppType;
import com.genesyslab.platform.configuration.protocol.types.CfgDNType;
import com.genesyslab.platform.configuration.protocol.types.CfgDialMode;
import com.genesyslab.platform.configuration.protocol.types.CfgFlag;
import com.genesyslab.platform.configuration.protocol.types.CfgObjectState;
import com.genesyslab.platform.configuration.protocol.types.CfgObjectType;
import com.genesyslab.platform.configuration.protocol.types.CfgTableType;
import com.testing.App;

import io.netty.util.collection.IntCollections;

public class Genesys {
	private String host;
	private int port;
	private String backupHost;
	private String localUser;
	private String localPwd;
	private String localDbURL;
	private int backupPort;
	private String clientName;
	private String userName;
	private String password;
	private int skillLevel;
	private int tenantID;
	private static ConfServerProtocol protocol;
    private static WarmStandbyService  warmStandbyService;
    private static IConfService confService;
    private Connection connDB;
    private static Subscription subscriptionForAll;
    private static Subscription subscriptionForObject;
    private Logger mylog;
    
    
	public Genesys() {
		// TODO Auto-generated constructor stub
	}
	
    public Genesys(String host, int port, String backupHost, int backupPort, String clientName, String userName,
			String password, int skillLevel, int tenantID, String localUser) {
		super();
		this.host = host;
		this.port = port;
		this.backupHost = backupHost;
		this.backupPort = backupPort;
		this.clientName = clientName;
		this.userName = userName;
		this.password = password;
		this.skillLevel = skillLevel;
		this.tenantID = tenantID;
		this.localDbURL = "";
	}

//###################################################################################################################	
    public void InitializePSDKProtocolAndAppBlocks(Genesys app) throws RegistrationException, ProtocolException, IllegalStateException, InterruptedException {
		// TODO Auto-generated method stub
    	mylog.getLogger(Genesys.class.getName());
		PropertyConfiguration config = new PropertyConfiguration();
        config.setUseAddp(true);config.setAddpServerTimeout(20);config.setAddpClientTimeout(10);
        config.setAddpTraceMode(AddpTraceMode.Both);        
        Endpoint endpoint = new Endpoint(app.getHost(), app.getPort(), config);
        Endpoint backupEndpoint = new Endpoint(app.getBackupHost(), app.getBackupPort(), config);
        protocol = new ConfServerProtocol(endpoint);
        protocol.setClientName(app.getClientName());
        protocol.setUserName(app.getUserName());
        protocol.setUserPassword(app.getPassword());
        CfgAppType cfgDesktop = CfgAppType.CFGAgentDesktop;
        protocol.setClientApplicationType(cfgDesktop.asInteger());
        WarmStandbyConfiguration warmStandbyConfig = new WarmStandbyConfiguration(endpoint, backupEndpoint);
        warmStandbyConfig.setTimeout(5000);
        warmStandbyConfig.setAttempts((short) 2);
        warmStandbyService = new WarmStandbyService(protocol);
        warmStandbyService.applyConfiguration(warmStandbyConfig);
        warmStandbyService.start();
        confService = ConfServiceFactory.createConfService(protocol);
//        confService.Register(new Action<ConfEvent>(this.ConfServerEventsHandler));
//        confService.register(new Action<>, arg1);
	}
    
    public ConfServerProtocol ConnectProtocol() throws RegistrationException, ProtocolException, IllegalStateException, InterruptedException {
		 protocol.open();
		return protocol;
		 
	 }
	public void disConnectProtocl(ConfServerProtocol protocol2) throws ProtocolException, IllegalStateException, InterruptedException {
		 protocol2.close();
	     Thread.sleep(10);
	     System.out.println(protocol2.getState());
//	     System.exit(-1);
	}
	
//########################################################################################################################
	
	public ArrayList<ObjCfgFields> GetAllFileds() throws Exception {
		List<ObjCfgFields> results = new ArrayList<ObjCfgFields>();
		if(protocol.getState() != null) {
			 CfgFieldQuery cfgFieldQ = new CfgFieldQuery();
			 Collection<CfgField> cfgFieldList = confService.retrieveMultipleObjects(CfgField.class, cfgFieldQ);
	           int i =0;
			 Iterator<CfgField>iListApps = cfgFieldList.iterator();
          	  while (iListApps.hasNext()) {
          		CfgField item = iListApps.next();
          		System.out.println("DBID : "+item.getDBID()+", Value : "+item.getName());
          		ObjCfgFields objct = new ObjCfgFields(item.getDBID(),item.getTenantDBID(),item.getName(),
          				item.getType().toString(),item.getLength(),item.getFieldType().toString(),
          				item.getIsPrimaryKey().toString(),item.getIsUnique().toString(),
          				item.getIsNullable().toString(),item.getState().toString(),
          				item.getUserProperties().toString());
          		results.add(objct);
			  }
			 
		  } else {
		  	throw new Exception("Genesys protocol is closed!");
		  }// TODO Auto-generated method stub
		return (ArrayList<ObjCfgFields>) results;
	}
	
	public ArrayList<ObjCfgFields> GetAllFiledsByFormatID (int FormatId) throws Exception {
		List<ObjCfgFields> results = new ArrayList<ObjCfgFields>();
		if(protocol.getState() != null) {
			 CfgFieldQuery cfgFieldQ = new CfgFieldQuery();
			 cfgFieldQ.setFormatDbid(FormatId);
			 Collection<CfgField> cfgFieldList = confService.retrieveMultipleObjects(CfgField.class, cfgFieldQ);
	           int i =0;
			 Iterator<CfgField>iListApps = cfgFieldList.iterator();
          	  while (iListApps.hasNext()) {
          		CfgField item = iListApps.next();
          		System.out.println("DBID : "+item.getDBID()+", Value : "+item.getName()+", Type : "+item.getType().toString());
          		ObjCfgFields objct = new ObjCfgFields(item.getDBID(),item.getTenantDBID(),item.getName(),
          				item.getType().toString(),item.getLength(),item.getFieldType().toString(),
          				item.getIsPrimaryKey().toString(),item.getIsUnique().toString(),
          				item.getIsNullable().toString(),item.getState().toString(),
          				item.getUserProperties().toString());
          		results.add(objct);
			  }
			 
		  } else {
		  	throw new Exception("Genesys protocol is closed!");
		  }// TODO Auto-generated method stub
		return (ArrayList<ObjCfgFields>) results;
	}

	public void GetAllFormat() throws Exception {
		if(protocol.getState() != null) {
			 CfgFormatQuery cfgFormatQ = new CfgFormatQuery();
			 Collection<CfgFormat> cfgFormatList = confService.retrieveMultipleObjects(CfgFormat.class, cfgFormatQ);
			 Iterator<CfgFormat>iListApps = cfgFormatList.iterator();
			 while (iListApps.hasNext()) {
				 CfgFormat item = iListApps.next();
//				 System.out.println(item.getName() +", ObjectID : "+item.getObjectDbid()+", ObjType : " + item.getObjectType().ordinal());
				 System.out.println(item.getName());
//				 KeyValueCollection kvColl = item.getUserProperties();
//				 System.out.println(kvColl.toString());
				 
				 
			 }
			 
		  } else {
		  	throw new Exception("Genesys protocol is closed!");
		  }// TODO
	}
	
	public void GetAccessGroup() throws Exception {
		if(protocol.getState() != null) {
			 CfgAccessGroupQuery cfgAccessQ = new CfgAccessGroupQuery();
			 Collection<CfgAccessGroup> cfgAccessGroupList = confService.retrieveMultipleObjects(CfgAccessGroup.class, cfgAccessQ);
			 Iterator<CfgAccessGroup>iListApps = cfgAccessGroupList.iterator();
			 while (iListApps.hasNext()) {
				 CfgAccessGroup item = iListApps.next();
				 System.out.println("Iterator : "+item.getGroupInfo().getName());
//				 item.getGroupInfo().
			 }
			 
		  } else {
		  	throw new Exception("Genesys protocol is closed!");
		  }// TODO
	}
	public CfgAccessGroup GetAccessGroupByName(String AccessGroupName) throws Exception {
		CfgAccessGroup cfgAccessGroup = null;
		if(protocol.getState() != null) {
			 CfgAccessGroupQuery cfgAccessQ = new CfgAccessGroupQuery();
			 cfgAccessQ.setName(AccessGroupName);
			 cfgAccessGroup = confService.retrieveObject(CfgAccessGroup.class,cfgAccessQ);
			 	
//			 cfgAccessGroup.get
			 
		  } else {
		  	throw new Exception("Genesys protocol is closed!");
		  }// TODO
		return cfgAccessGroup;
	}
	public void GetPersons() throws Exception {
		if(protocol.getState() != null) {
			 CfgPersonQuery cfgPersonsQ = new CfgPersonQuery();
			 Collection<CfgPerson> cfgPersonsList = confService.retrieveMultipleObjects(CfgPerson.class, cfgPersonsQ);
			 Iterator<CfgPerson>iListApps = cfgPersonsList.iterator();
			 while (iListApps.hasNext()) {
				 CfgPerson item = iListApps.next();
				 System.out.println("Iterator : "+item.toString());
			 }
			 
		  } else {
		  	throw new Exception("Genesys protocol is closed!");
		  }// TODO
	}
	
	public CfgPerson GetPerson(int id) throws Exception {
		CfgPerson personObj = null;
		if(protocol.getState() != null) {
			 CfgPersonQuery cfgPersonsQ = new CfgPersonQuery();
			 cfgPersonsQ.setDbid(id);
			 personObj = confService.retrieveObject(CfgPerson.class, cfgPersonsQ);			 
		  } else {
		  	throw new Exception("Genesys protocol is closed!");
		  }
		return personObj;
	}
	
	public void CreateNewFormat(Genesys mainApp, String name, String description, Collection<Integer> filedsIds, Integer trxtmid) throws Exception {
		if(protocol.getState() != null) {
			
//				filedsIds.add(trxtmid);
				
				
				
				
				
				
			
				CfgFormat cfgFormat = new CfgFormat(confService);
            	cfgFormat.setName(name);
            	cfgFormat.setDescription(description);
            	cfgFormat.setTenantDBID(1);
            	cfgFormat.setState(CfgObjectState.CFGEnabled);
            	cfgFormat.setFieldDBIDs(filedsIds);
            	
            	cfgFormat.save();
		  } else {
		  	throw new Exception("Genesys protocol is closed!");
		  }// TODO
	}
	
	
	public CfgFormat CreateNewFormat(Genesys mainApp, String name, String description, Collection<Integer> filedsIds, String AccessGroupName) throws Exception {
		CfgFormat result=null;
		 
		
		
		if(protocol.getState() != null) {
			
//			 CfgField cfgField = new CfgField(confService);
//			 CfgFieldQuery cfgFieldQ = new CfgFieldQuery();
//			 
//			 Integer fieldDBID =0;
//			//********************record_id*********************************
//			 cfgFieldQ.setName("record_id");
//			 cfgField = confService.retrieveObject(CfgField.class, cfgFieldQ);
//			 fieldDBID = cfgField.getDBID();
//			 filedsIds.add(fieldDBID);
//			//********************phone*********************************
//			 cfgFieldQ = new CfgFieldQuery();
//			 cfgFieldQ.setName("phone");
//			 cfgField = confService.retrieveObject(CfgField.class, cfgFieldQ);
//			 fieldDBID = cfgField.getDBID();
//			 filedsIds.add(fieldDBID);
//			//********************phone_type*********************************
//			 cfgFieldQ = new CfgFieldQuery();
//			 cfgFieldQ.setName("phone_type");
//			 cfgField = confService.retrieveObject(CfgField.class, cfgFieldQ);
//			 fieldDBID = cfgField.getDBID();
//			 filedsIds.add(fieldDBID);
//			//********************record_type*********************************
//			 cfgFieldQ = new CfgFieldQuery();
//			 cfgFieldQ.setName("record_type");
//			 cfgField = confService.retrieveObject(CfgField.class, cfgFieldQ);
//			 fieldDBID = cfgField.getDBID();
//			 filedsIds.add(fieldDBID);
//			//********************record_status*********************************
//			 cfgFieldQ = new CfgFieldQuery();
//			 cfgFieldQ.setName("record_status");
//			 cfgField = confService.retrieveObject(CfgField.class, cfgFieldQ);
//			 fieldDBID = cfgField.getDBID();
//			 filedsIds.add(fieldDBID);
//			//********************call_result*********************************
//			 cfgFieldQ = new CfgFieldQuery();
//			 cfgFieldQ.setName("call_result");
//			 cfgField = confService.retrieveObject(CfgField.class, cfgFieldQ);
//			 fieldDBID = cfgField.getDBID();
//			 filedsIds.add(fieldDBID);
//			//********************attempt*********************************
//			 cfgFieldQ = new CfgFieldQuery();
//			 cfgFieldQ.setName("attempt");
//			 cfgField = confService.retrieveObject(CfgField.class, cfgFieldQ);
//			 fieldDBID = cfgField.getDBID();
//			 filedsIds.add(fieldDBID);
//			//********************dial_sched_time*********************************
//			 cfgFieldQ = new CfgFieldQuery();
//			 cfgFieldQ.setName("dial_sched_time");
//			 cfgField = confService.retrieveObject(CfgField.class, cfgFieldQ);
//			 fieldDBID = cfgField.getDBID();
//			 filedsIds.add(fieldDBID);
//			//********************call_time*********************************
//			 cfgFieldQ = new CfgFieldQuery();
//			 cfgFieldQ.setName("call_time");
//			 cfgField = confService.retrieveObject(CfgField.class, cfgFieldQ);
//			 fieldDBID = cfgField.getDBID();
//			 filedsIds.add(fieldDBID);
//			//********************daily_from*********************************
//			 cfgFieldQ = new CfgFieldQuery();
//			 cfgFieldQ.setName("daily_from");
//			 cfgField = confService.retrieveObject(CfgField.class, cfgFieldQ);
//			 fieldDBID = cfgField.getDBID();
//			 filedsIds.add(fieldDBID);
//			//********************daily_till*********************************
//			 cfgFieldQ = new CfgFieldQuery();
//			 cfgFieldQ.setName("daily_till");
//			 cfgField = confService.retrieveObject(CfgField.class, cfgFieldQ);
//			 fieldDBID = cfgField.getDBID();
//			 filedsIds.add(fieldDBID);
//			//********************tz_dbid*********************************
//			 cfgFieldQ = new CfgFieldQuery();
//			 cfgFieldQ.setName("tz_dbid");
//			 cfgField = confService.retrieveObject(CfgField.class, cfgFieldQ);
//			 fieldDBID = cfgField.getDBID();
//			 filedsIds.add(fieldDBID);
//			//********************campaign_id*********************************
//			 cfgFieldQ = new CfgFieldQuery();
//			 cfgFieldQ.setName("campaign_id");
//			 cfgField = confService.retrieveObject(CfgField.class, cfgFieldQ);
//			 fieldDBID = cfgField.getDBID();
//			 filedsIds.add(fieldDBID);
//			//********************agent_id*********************************
//			 cfgFieldQ = new CfgFieldQuery();
//			 cfgFieldQ.setName("agent_id");
//			 cfgField = confService.retrieveObject(CfgField.class, cfgFieldQ);
//			 fieldDBID = cfgField.getDBID();
//			 filedsIds.add(fieldDBID);
//			//********************chain_id*********************************
//			 cfgFieldQ = new CfgFieldQuery();
//			 cfgFieldQ.setName("chain_id");
//			 cfgField = confService.retrieveObject(CfgField.class, cfgFieldQ);
//			 fieldDBID = cfgField.getDBID();
//			 filedsIds.add(fieldDBID);
//			//********************chain_n*********************************
//			 cfgFieldQ = new CfgFieldQuery();
//			 cfgFieldQ.setName("chain_n");
//			 cfgField = confService.retrieveObject(CfgField.class, cfgFieldQ);
//			 fieldDBID = cfgField.getDBID();
//			 filedsIds.add(fieldDBID);
//			//********************group_id*********************************
//			 cfgFieldQ = new CfgFieldQuery();
//			 cfgFieldQ.setName("group_id");
//			 cfgField = confService.retrieveObject(CfgField.class, cfgFieldQ);
//			 fieldDBID = cfgField.getDBID();
//			 filedsIds.add(fieldDBID);
//			//********************app_id*********************************
//			 cfgFieldQ = new CfgFieldQuery();
//			 cfgFieldQ.setName("app_id");
//			 cfgField = confService.retrieveObject(CfgField.class, cfgFieldQ);
//			 fieldDBID = cfgField.getDBID();
//			 filedsIds.add(fieldDBID);
//			//********************treatments*********************************
//			 cfgFieldQ = new CfgFieldQuery();
//			 cfgFieldQ.setName("treatments");
//			 cfgField = confService.retrieveObject(CfgField.class, cfgFieldQ);
//			 fieldDBID = cfgField.getDBID();
//			 filedsIds.add(fieldDBID);
//			//********************media_ref*********************************
//			 cfgFieldQ = new CfgFieldQuery();
//			 cfgFieldQ.setName("media_ref");
//			 cfgField = confService.retrieveObject(CfgField.class, cfgFieldQ);
//			 fieldDBID = cfgField.getDBID();
//			 filedsIds.add(fieldDBID);
//			//********************contact_info*********************************
//			 cfgFieldQ = new CfgFieldQuery();
//			 cfgFieldQ.setName("contact_info");
//			 cfgField = confService.retrieveObject(CfgField.class, cfgFieldQ);
//			 fieldDBID = cfgField.getDBID();
//			 filedsIds.add(fieldDBID);
//			//********************contact_info_type*********************************
//			 cfgFieldQ = new CfgFieldQuery();
//			 cfgFieldQ.setName("contact_info_type");
//			 cfgField = confService.retrieveObject(CfgField.class, cfgFieldQ);
//			 fieldDBID = cfgField.getDBID();
//			 filedsIds.add(fieldDBID);
//			//********************email_subject*********************************
//			 cfgFieldQ = new CfgFieldQuery();
//			 cfgFieldQ.setName("email_subject");
//			 cfgField = confService.retrieveObject(CfgField.class, cfgFieldQ);
//			 fieldDBID = cfgField.getDBID();
//			 filedsIds.add(fieldDBID);
//			//********************email_template_id*********************************
//			 cfgFieldQ = new CfgFieldQuery();
//			 cfgFieldQ.setName("email_template_id");
//			 cfgField = confService.retrieveObject(CfgField.class, cfgFieldQ);
//			 fieldDBID = cfgField.getDBID();
//			 filedsIds.add(fieldDBID);
//			//********************switch_id*********************************
//			 cfgFieldQ = new CfgFieldQuery();
//			 cfgFieldQ.setName("switch_id");
//			 cfgField = confService.retrieveObject(CfgField.class, cfgFieldQ);
//			 fieldDBID = cfgField.getDBID();
//			 filedsIds.add(fieldDBID);
//			 //********************tm_trx_id*********************************
//			 cfgFieldQ.setName("tm_trx_id");
//			 cfgField = confService.retrieveObject(CfgField.class, cfgFieldQ);
//			 fieldDBID = cfgField.getDBID();
//			 filedsIds.add(fieldDBID);
			
			
			CfgFormat cfgFormat = new CfgFormat(confService);
        	cfgFormat.setName(name);
        	cfgFormat.setDescription(description);
        	cfgFormat.setTenantDBID(1);
        	cfgFormat.setState(CfgObjectState.CFGEnabled);
        	cfgFormat.setFieldDBIDs(filedsIds);
//			CfgAccessGroup cfgAccGroup = GetAccessGroupByName(AccessGroupName);
//			cfgFormat.setAccountPermissions(cfgAccGroup, 127);
        	cfgFormat.save();
        	result = getFormatByNameUpdateAccessGroup(name, AccessGroupName);
        	
        	
        	
	  } else {
	  	throw new Exception("Genesys protocol is closed!");
	  }
	  return result;
	}
	
	public CfgFormat getFormatByNameUpdateAccessGroup(String FormatName, String AccessGroupName) throws Exception {
		CfgFormat formatObj = null;
		if(protocol.getState() != null) {
			 CfgFormatQuery cfgFormatQ = new CfgFormatQuery();
			 cfgFormatQ.setName(FormatName);
			 formatObj = confService.retrieveObject(CfgFormat.class, cfgFormatQ);
			 CfgAccessGroup cfgAccGroup = GetAccessGroupByName(AccessGroupName);
			 formatObj.setAccountPermissions(cfgAccGroup, 127);
			 formatObj.save();
		  } else {
		  	throw new Exception("Genesys protocol is closed!");
		  }
		return formatObj;
	}
    public void UpdateFormat(Genesys mainApp,int formatID, String name, String description, Collection<Integer> filedsIds) throws Exception {
    	
    	if(protocol.getState() != null) {
            try
            {
            	CfgFormatQuery formatQ = new CfgFormatQuery();
            	formatQ.setDbid(formatID);
            	CfgFormat FormatFromQ = confService.retrieveObject(formatQ);
            	FormatFromQ.setName(name);
            	FormatFromQ.setDescription(description);
            	FormatFromQ.setTenantDBID(1);
            	FormatFromQ.setState(CfgObjectState.CFGEnabled);
            	FormatFromQ.setFieldDBIDs(filedsIds);
            	FormatFromQ.save();
            }
            catch (Exception e)
            {
                System.out.println("Exception Create campaign : "+e.getMessage());
            }
     } else {
    	 throw new Exception("Genesys protocol is closed!");
     	 
     }
    	
  }
    public CfgFormat UpdateFormat(Genesys mainApp,int formatID, String name, String description, Collection<Integer> filedsIds, String AccessGroupName) throws Exception {
    	CfgFormat FormatFromQ = null;
    	if(protocol.getState() != null) {
            try
            {
            	CfgFormatQuery formatQ = new CfgFormatQuery();
            	formatQ.setDbid(formatID);
            	FormatFromQ = confService.retrieveObject(formatQ);
            	FormatFromQ.setName(name);
            	FormatFromQ.setDescription(description);
            	FormatFromQ.setTenantDBID(1);
            	FormatFromQ.setState(CfgObjectState.CFGEnabled);
            	FormatFromQ.setFieldDBIDs(filedsIds);
            	CfgAccessGroup cfgAccGroup = GetAccessGroupByName(AccessGroupName);
            	FormatFromQ.setAccountPermissions(cfgAccGroup, 127);
            	FormatFromQ.save();
            }
            catch (Exception e)
            {
                System.out.println("Exception Create campaign : "+e.getMessage());
            }
     } else {
    	 throw new Exception("Genesys protocol is closed!");
     	 
     }
    	return FormatFromQ;
  }
    public void DeleteFormat(int formatID) throws Exception {
		if(protocol.getState() != null) {
            try
            {
            	CfgFormatQuery formatQ = new CfgFormatQuery();
            	formatQ.setDbid(formatID);
            	CfgFormat FormatFromQ = confService.retrieveObject(formatQ);
            	FormatFromQ.delete();
            }
            catch (Exception e)
            {
                System.out.println("Exception Create campaign : "+e.getMessage());
            }
     } else {
    	 throw new Exception("Genesys protocol is closed!");
     	 
     }
}
	
	
	public CfgCallingList CreateNewCallingList(Genesys mainApp, String name, String description,String TbAccess, String AccessGroupName) throws Exception {
		CfgCallingList result = null;
		if(protocol.getState() != null) {
			CfgCallingList cfgCallingList = new CfgCallingList(confService);
			cfgCallingList.setName(name);
			cfgCallingList.setDescription(description);
			cfgCallingList.setTenantDBID(1);
			cfgCallingList.setState(CfgObjectState.CFGEnabled);
			CfgTableAccess tableAcces = getTableAccessByname(TbAccess);
			cfgCallingList.setTableAccess(tableAcces);
			cfgCallingList.save();
			result = getCallingListByNameUpdateAccessGroup(name, AccessGroupName);
			
		} else {
		  	throw new Exception("Genesys protocol is closed!");
		}
		return result;
	}
	
	public CfgCallingList CreateNewCallingList(Genesys mainApp, String name, String description,String TbAccess, String AccessGroupName, String StartFrom, String EndTo) throws Exception {
		CfgCallingList result = null;
		if(protocol.getState() != null) {
			CfgCallingList cfgCallingList = new CfgCallingList(confService);
			cfgCallingList.setName(name);
			cfgCallingList.setDescription(description);
			cfgCallingList.setTenantDBID(1);
			cfgCallingList.setState(CfgObjectState.CFGEnabled);
			Integer startFromTime = timeToDecimal(StartFrom);
			Integer EndToTime = timeToDecimal(EndTo);
			cfgCallingList.setTimeFrom(startFromTime);
			cfgCallingList.setTimeUntil(EndToTime);
			CfgTableAccess tableAcces = getTableAccessByname(TbAccess);
			cfgCallingList.setTableAccess(tableAcces);
			cfgCallingList.save();
			result = getCallingListByNameUpdateAccessGroup(name, AccessGroupName);
			
		} else {
		  	throw new Exception("Genesys protocol is closed!");
		}
		return result;
	}

	public void GetAllCallingList() throws Exception {
		if(protocol.getState() != null) {
			CfgCallingListQuery cfgAppQ = new CfgCallingListQuery();
        	Collection<CfgCallingList> cfgAppNewColl = confService.retrieveMultipleObjects(CfgCallingList.class,cfgAppQ);
			 Iterator<CfgCallingList>iListApps = cfgAppNewColl.iterator();
			 while (iListApps.hasNext()) {
				 CfgCallingList cfgAppitem = iListApps.next();
//				 String prefixDAP =cfgAppitem.getName().substring(0, 3); 
				 System.out.println("Call ID :"+cfgAppitem.getDBID()+", Calling List :" +cfgAppitem.getName());
				 
//				 if(prefixDAP.equalsIgnoreCase("DAP")) {
//					 System.out.println("Name : "+cfgAppitem.getName());
//					 KVList kvList = cfgAppitem.getUserProperties().getList("default");
//					 System.out.println(cfgAppitem.getDBID()+ "|"+ cfgAppitem.getName() + "|"+kvList.getAsString("dbname"));
//				 }
			 }
			 
		  } else {
		  	throw new Exception("Genesys protocol is closed!");
		  }// TODO
	}
	public CfgCallingList UpdateCallingList(Genesys mainApp, Integer callinglistID,String name, String description,String TbAccess, String AccessGroupName) throws Exception {
		CfgCallingList CallingListFromQ = null;
		if(protocol.getState() != null) {
            try
            {
            	CfgCallingListQuery callinglistQ = new CfgCallingListQuery();
            	callinglistQ.setDbid(callinglistID);
            	CallingListFromQ = confService.retrieveObject(callinglistQ);
            	CallingListFromQ.setName(name);
            	CallingListFromQ.setDescription(description);
            	CallingListFromQ.setTenantDBID(1);
            	CallingListFromQ.setState(CfgObjectState.CFGEnabled);
            	
            	CfgAccessGroup cfgAccGroup = GetAccessGroupByName(AccessGroupName);
            	CallingListFromQ.setAccountPermissions(cfgAccGroup, 127);
            	CallingListFromQ.save();
            }
            catch (Exception e)
            {
                System.out.println("Exception Create campaign : "+e.getMessage());
            }
     } else {
    	 throw new Exception("Genesys protocol is closed!");
     	 
     }
		
		return CallingListFromQ;
	}
	public CfgCallingList getCallingListByNameUpdateAccessGroup(String callingListName, String AccessGroupName) throws Exception {
		CfgCallingList callingListObj = null;
		if(protocol.getState() != null) {
			CfgCallingListQuery callingListQ = new CfgCallingListQuery();
			callingListQ.setName(callingListName);
			callingListObj = confService.retrieveObject(CfgCallingList.class, callingListQ);
			 CfgAccessGroup cfgAccGroup = GetAccessGroupByName(AccessGroupName);
			 callingListObj.setAccountPermissions(cfgAccGroup, 127);
			 callingListObj.save();
		  } else {
		  	throw new Exception("Genesys protocol is closed!");
		  }
		return callingListObj;
	}
	public CfgCallingList getCallingListByName(String callingListName) throws Exception {
		CfgCallingList callingListObj = null;
		if(protocol.getState() != null) {
			CfgCallingListQuery callingListQ = new CfgCallingListQuery();
			callingListQ.setName(callingListName);
			callingListObj = confService.retrieveObject(CfgCallingList.class, callingListQ);
			 
		  } else {
		  	throw new Exception("Genesys protocol is closed!");
		  }
		return callingListObj;
	}
	public CfgCallingListInfo getCallingListInfoByName(String callingListName) throws Exception {
		CfgCallingList callingListObj = null;
		CfgCallingListInfo callInfo = null;
		if(protocol.getState() != null) {
				CfgCallingListQuery callingListQ = new CfgCallingListQuery();
				callingListQ.setName(callingListName);
				callingListObj = confService.retrieveObject(CfgCallingList.class, callingListQ);
//				System.out.println("# Dari object # "+callingListObj.getName());
				CfgCallingListInfo callInfoTmp = new CfgCallingListInfo(confService, callingListObj);
			    
			    callInfo = callInfoTmp;
		  } else {
		  	throw new Exception("Genesys protocol is closed!");
		  }
		
		
//		CfgCallingListQuery cl = new CfgCallingListQuery(confService);
//        cl.Name = "callinglistname";
//        CfgCallingList calllist = confService.RetrieveObject<CfgCallingList>(cl);
//
//        CfgCallingListInfo info = new CfgCallingListInfo(confService, calllist);
//
//        CfgCampaign campaign = new CfgCampaign(confService);
//        campaign.SetTenantDBID(TENANT_ID);
//        campaign.Name = "";
//        campaign.CallingLists.Add(info);
		
		
		
		
		return callInfo;
	}
	public void DeleteCallingList(int callingListID) throws Exception {
		if(protocol.getState() != null) {
            try
            {
            	CfgCallingListQuery callingListQ = new CfgCallingListQuery();
            	callingListQ.setDbid(callingListID);
            	CfgCallingList callingListFromQ = confService.retrieveObject(callingListQ);
            	callingListFromQ.delete();
            }
            catch (Exception e)
            {
                System.out.println("Exception Create campaign : "+e.getMessage());
            }
     } else {
    	 throw new Exception("Genesys protocol is closed!");
     	 
     }
	}
	
//	public CfgCampaign CreateNewCampaign(Genesys mainApp, String name, String description,String CallingListName, String AccessGroupName) throws Exception {
//		CfgCampaign result = null;
//		if(protocol.getState() != null) {
//			CfgCampaign cfgCampaign = new CfgCampaign(confService);
//			cfgCampaign.setName(name);
//			cfgCampaign.setDescription(description);
//			cfgCampaign.setTenantDBID(1);
//			cfgCampaign.setState(CfgObjectState.CFGEnabled);
//
//			
//			CfgCallingList callingListByName = getCallingListByName(CallingListName);
//			CfgCallingListInfo cinfo = new CfgCallingListInfo(confService,callingListByName);
//			cinfo.setCallingListDBID(callingListByName.getObjectDbid());
//			cfgCampaign.getCallingLists().add(cinfo);
//			cfgCampaign.save();
//			result = getCfgCampaignByNameUpdateAccessGroup(name, AccessGroupName);
//		} else {
//		  	throw new Exception("Genesys protocol is closed!");
//		}
//		return result;
//	
//	}
	public CfgCampaign CreateNewCampaign(Genesys mainApp, String name, String description,String CallingListName, String AccessGroupName) throws Exception {
		CfgCampaign result = null;
		if(protocol.getState() != null) {
			try {
					CfgCampaign cfgCampaign = new CfgCampaign(confService);
					cfgCampaign.setName(name);
					cfgCampaign.setDescription(description);
					cfgCampaign.setTenantDBID(this.getTenantID());
					cfgCampaign.setState(CfgObjectState.CFGEnabled);
					CfgCallingList callingListByName;
					
						callingListByName = getCallingListByName(CallingListName);
					
					CfgCallingListInfo cinfo = new CfgCallingListInfo(confService,callingListByName);
					
					int  cldbid = callingListByName.getObjectDbid();
					
					cinfo.setCallingListDBID(cldbid);
					cfgCampaign.getCallingLists().add(cinfo);
					cfgCampaign.save();
					result = getCfgCampaignByNameUpdateAccessGroup(name, AccessGroupName);
					
					System.out.println("################# Create Campaign Sukses ID: "+result.getDBID()+", Name : " +result.getName());
					
//			mylog.log(Level.INFO,"Create New Campaign Success");
//#################################Add Campaign Group Include Agent Group#####################
			
					Thread.sleep(100);
						
					CfgCampaignQuery cfgcamp = new CfgCampaignQuery(confService);
		            cfgcamp.setName(name);
		            cfgcamp.setDbid(result.getDBID());
		            CfgCampaign cpgn = confService.retrieveObject(CfgCampaign.class,cfgcamp);
		            System.out.println("################# CfgCampaignQuery Sukses ID: "+cpgn.getDBID()+", Name : " +cpgn.getName());
		            System.out.println("################# CfgCampaignQuery Metadata : "+cpgn.getMetaData().toString());
		//            mylog.log(Level.INFO,"Campaign Query Success Success");
		//            Thread.sleep(500);
					
//					CfgAgentGroupQuery agentGroupQuery = new CfgAgentGroupQuery(confService);
//					agentGroupQuery.setName(AccessGroupName);
//					CfgAgentGroup agentGroup = confService.retrieveObject(CfgAgentGroup.class, agentGroupQuery);
////					System.out.println("AgentGroup Query Success ");
//					System.out.println("################# CfgAgentGroupQuery Sukses ID: "+agentGroupQuery.getDbid()+", Name : " +agentGroupQuery.getName());
//					System.out.println("################# CfgAgentGroupQuery Metadata : "+agentGroupQuery.toString());
//					
		            CfgAgentGroupQuery agentGroupQuery = new CfgAgentGroupQuery(confService);
					agentGroupQuery.setName(AccessGroupName);
					CfgAgentGroup agentGroup = confService.retrieveObject(CfgAgentGroup.class, agentGroupQuery);
//					System.out.println("AgentGroup Query Success ");
					System.out.println("################# CfgAgentGroupQuery Sukses ID: "+agentGroup.getGroupInfo().getDBID()+", Name : " +agentGroup.getGroupInfo().getName());
					System.out.println("################# CfgAgentGroupQuery Metadata : "+agentGroup.toString());
					System.out.println("################# CfgAgentGroupQuery End : ");
					
					
					
					CfgApplicationQuery appQuery = new CfgApplicationQuery(confService);
		            appQuery.setName("Stat_Server_Routing"); // ini fix ga usah diganti
					Collection<CfgApplication> app = confService.retrieveMultipleObjects(CfgApplication.class, appQuery);
					
					for (CfgApplication cfgApplication : app) {
						System.out.println("################# CfgApplicationQuery Sukses ID: "+cfgApplication.getDBID()+", Name : " +cfgApplication.getName());
			            System.out.println("################# CfgApplicationQuery Metadata : "+cfgApplication.getMetaData().toString());
					}
					
					CfgDNQuery dnQuery = new CfgDNQuery(confService);
//		            dnQuery.setDnNumber("5100"); // ini sementara static 5100 aja dlu
		            dnQuery.setName("5100");
		            
		            dnQuery.setDnType(CfgDNType.CFGRoutingPoint);
		            CfgDN dn = confService.retrieveObject(CfgDN.class, dnQuery);
					
		            System.out.println("################# CfgDNQuery Sukses ID: "+dn.getDBID()+", Name : " +dn.getName());
		            System.out.println("################# CfgDNQuery Metadata : "+dn.getMetaData().toString());
		            System.out.println("################# CfgDNQuery Metadata END ");
					
					System.out.println("################# ");
					
		            
		            Thread.sleep(1000);
					
		//			CfgCampaignGroup campaignGroup = new CfgCampaignGroup(confService);
		//			campaignGroup.setTenantDBID(this.tenantID);
		//			campaignGroup.setName(name+"Group");		
		//			campaignGroup.setDialMode(CfgDialMode.CFGDMPreview);
		//			campaignGroup.setGroupDBID(agentGroup.getDBID()); //ini agent group dbid yang dipilih pas create campaign
		//            campaignGroup.setGroupType(CfgObjectType.CFGAgentGroup);
		//            campaignGroup.setState(CfgObjectState.CFGEnabled);
		//            campaignGroup.setServers(app);
		//            campaignGroup.setOrigDN(dn);
		//            campaignGroup.setCampaign(cpgn);
		//            campaignGroup.save();
		            System.out.println("################# Creating Campaign Group Start : ");
		            CfgCampaignGroup campaignGroup = new CfgCampaignGroup(confService);
		            campaignGroup.setTenantDBID(this.tenantID);
		            System.out.println("#### Set tenant ID : "+this.tenantID);
		            campaignGroup.setName(name);
		            System.out.println("#### Set campGroup Name : "+name);
		            int idCampDBID = cpgn.getObjectDbid();
		            campaignGroup.setCampaignDBID(idCampDBID);
		            System.out.println("#### Set campaign ID : "+idCampDBID);
		            campaignGroup.setState(CfgObjectState.CFGEnabled);
		            System.out.println("#### Set State Object : "+CfgObjectState.CFGEnabled);
		            campaignGroup.setOrigDN(dn);
		            System.out.println("#### Set Orig DN : "+dn.getName());
		            int agtGroupId = agentGroup.getGroupInfo().getDBID();
		            campaignGroup.setGroupDBID(agtGroupId);
		            System.out.println("#### Set Agent Group ID : "+agtGroupId);
		            campaignGroup.setGroupType(CfgObjectType.CFGAgentGroup);
		            System.out.println("#### Set Group Type : "+CfgObjectType.CFGAgentGroup.name());
		            campaignGroup.setServers(app);
		            
		            System.out.println("#### Set server : "+app.toString());
		            System.out.println("################# Creating Campaign Group Saving : ");
		            campaignGroup.save();
		            System.out.println("################# Creating Campaign Group Saving End : ");
//		            Thread.sleep(1000);
            

			} catch (Exception e) {
				// TODO Auto-generated catch block
				System.out.println("##### ERROR ####### : "+e.getMessage());
				throw new Exception(e.getMessage());
			}
            
		} else {
			mylog.log(Level.WARNING,"Error Genesys");
		  	throw new Exception("Genesys protocol is closed!");
		}
		return result;
	
	}

	public CfgCampaign getCfgCampaignByNameUpdateAccessGroup(String campaignName, String AccessGroupName) throws Exception {
		CfgCampaign campaignObj = null;
		if(protocol.getState() != null) {
			CfgCampaignQuery campaignQ = new CfgCampaignQuery();
			campaignQ.setName(campaignName);
			campaignObj = confService.retrieveObject(CfgCampaign.class, campaignQ);
			 CfgAccessGroup cfgAccGroup = GetAccessGroupByName(AccessGroupName);
			 campaignObj.setAccountPermissions(cfgAccGroup, 127);
			 campaignObj.save();
		  } else {
		  	throw new Exception("Genesys protocol is closed!");
		  }
		return campaignObj;
	}
	public CfgCampaign UpdateCfgCampaign(Genesys mainApp, Integer campaignID,String name, String description,String CallingListName, String AccessGroupName) throws Exception {
		CfgCampaign campaignFromQ = null;
		if(protocol.getState() != null) {
            try
            {
            	CfgCampaignQuery campaignQ = new CfgCampaignQuery();
            	campaignQ.setDbid(campaignID);
            	campaignFromQ = confService.retrieveObject(campaignQ);
            	campaignFromQ.setName(name);
            	campaignFromQ.setDescription(description);
            	campaignFromQ.setTenantDBID(1);
            	campaignFromQ.setState(CfgObjectState.CFGEnabled);
            	CfgAccessGroup cfgAccGroup = GetAccessGroupByName(AccessGroupName);
            	campaignFromQ.setAccountPermissions(cfgAccGroup, 127);
            	CfgCallingListInfo callingListObj = getCallingListInfoByName(CallingListName);
    			ArrayList<CfgCallingListInfo> cfgCallingListArray = new ArrayList<CfgCallingListInfo>();
    			cfgCallingListArray.add(callingListObj);
    			Collection<CfgCallingListInfo>callListArray = cfgCallingListArray;
    			campaignFromQ.setCallingLists(callListArray);
            	campaignFromQ.save();
            	
            	
            }
            catch (Exception e)
            {
                System.out.println("Exception Create campaign : "+e.getMessage());
            }
     } else {
    	 throw new Exception("Genesys protocol is closed!");
     	 
     }
		
		return campaignFromQ;
	}
	

	public void GetAllCampaign() throws Exception {
		if(protocol.getState() != null) {
			 CfgCampaignQuery cfgCampaignQ = new CfgCampaignQuery();
			 Collection<CfgCampaign> cfgTableAccessList = confService.retrieveMultipleObjects(CfgCampaign.class, cfgCampaignQ);
			 Iterator<CfgCampaign>iListApps = cfgTableAccessList.iterator();
			 while (iListApps.hasNext()) {
				 CfgCampaign item = iListApps.next();
				 System.out.println("Iterator : "+item.getName());
			 }
			 
		  } else {
		  	throw new Exception("Genesys protocol is closed!");
		  }// TODO
	}
	
	
	public void GetAllAgentGroup() throws Exception {
		if(protocol.getState() != null) {
			 CfgAgentGroupQuery cfgTableAccessQ = new CfgAgentGroupQuery();
			 Collection<CfgAgentGroup> cfgTableAccessList = confService.retrieveMultipleObjects(CfgAgentGroup.class, cfgTableAccessQ);
			 Iterator<CfgAgentGroup>iListApps = cfgTableAccessList.iterator();
			 while (iListApps.hasNext()) {
				 CfgAgentGroup item = iListApps.next();
				 System.out.println("Iterator : "+item.toString());
			 }
			 
		  } else {
		  	throw new Exception("Genesys protocol is closed!");
		  }
	}
	
	
	
	
	
	
	
	
	public void GetAllTableAccess() throws Exception {
		if(protocol.getState() != null) {
			 CfgTableAccessQuery cfgTableAccessQ = new CfgTableAccessQuery();
			 Collection<CfgTableAccess> cfgTableAccessList = confService.retrieveMultipleObjects(CfgTableAccess.class, cfgTableAccessQ);
			 Iterator<CfgTableAccess>iListApps = cfgTableAccessList.iterator();
			 while (iListApps.hasNext()) {
				 CfgTableAccess item = iListApps.next();
				 System.out.println("Iterator : "+item.toString());
			 }
			 
		  } else {
		  	throw new Exception("Genesys protocol is closed!");
		  }// TODO
	}
	public CfgTableAccess getTableAccessByname(String TableAccessName) throws Exception {
		CfgTableAccess cfgTableAccess = null;
		if(protocol.getState() != null) {
			 CfgTableAccessQuery cfgTableAccessQ = new CfgTableAccessQuery();
			 cfgTableAccessQ.setName(TableAccessName);
			 cfgTableAccess = confService.retrieveObject(CfgTableAccess.class, cfgTableAccessQ);
		  } else {
		  	throw new Exception("Genesys protocol is closed!");
		  }
		return cfgTableAccess;
	}
	public CfgTableAccess getTableAccessByNameUpdateAccessGroup(String TableAccessName, String AccessGroupName) throws Exception {
		CfgTableAccess cfgTableAccess = null;
		if(protocol.getState() != null) {
			 CfgTableAccessQuery cfgTableAccessQ = new CfgTableAccessQuery();
			 cfgTableAccessQ.setName(TableAccessName);
			 cfgTableAccess = confService.retrieveObject(CfgTableAccess.class, cfgTableAccessQ);
			 CfgAccessGroup cfgAccGroup = GetAccessGroupByName(AccessGroupName);
			 cfgTableAccess.setAccountPermissions(cfgAccGroup, 127);
			 cfgTableAccess.save();
		  } else {
		  	throw new Exception("Genesys protocol is closed!");
		  }
		return cfgTableAccess;
	}
	public CfgTableAccess CreateNewTableAccess(Genesys mainApp, String name, String description, Integer DbAccessId, 
			Integer FormatId, String DbTableName, Boolean isCacheAble, String AccessGroupName) throws Exception {
		CfgTableAccess cfgTableAccess = null;
		if(protocol.getState() != null) {
			cfgTableAccess = new CfgTableAccess(confService);
			cfgTableAccess.setName(name);
			cfgTableAccess.setDescription(description);
			cfgTableAccess.setTenantDBID(1);
			cfgTableAccess.setState(CfgObjectState.CFGEnabled);
			cfgTableAccess.setType(CfgTableType.CFGTTCallingList);
			cfgTableAccess.setDbAccessDBID(DbAccessId);
			cfgTableAccess.setFormatDBID(FormatId);
			cfgTableAccess.setDbTableName(DbTableName);
			if (isCacheAble) cfgTableAccess.setIsCachable(CfgFlag.CFGTrue);
			else  cfgTableAccess.setIsCachable(CfgFlag.CFGFalse);
			cfgTableAccess.save();
			cfgTableAccess = getTableAccessByNameUpdateAccessGroup(name, AccessGroupName);
			
		} else {
		  	throw new Exception("Genesys protocol is closed!");
		}// TODO
		return cfgTableAccess;
	}
	public void CreateNewTableAccess(Genesys mainApp, String name, String description, Integer DbAccessId, 
			Integer FormatId, String DbTableName, Boolean isCacheAble) throws Exception {
		if(protocol.getState() != null) {
			CfgTableAccess cfgTableAccess = new CfgTableAccess(confService);
			cfgTableAccess.setName(name);
			cfgTableAccess.setDescription(description);
			cfgTableAccess.setTenantDBID(1);
			cfgTableAccess.setState(CfgObjectState.CFGEnabled);
			cfgTableAccess.setType(CfgTableType.CFGTTCallingList);
			cfgTableAccess.setDbAccessDBID(DbAccessId);
			cfgTableAccess.setFormatDBID(FormatId);
			cfgTableAccess.setDbTableName(DbTableName);
			if (isCacheAble) cfgTableAccess.setIsCachable(CfgFlag.CFGTrue);
			else  cfgTableAccess.setIsCachable(CfgFlag.CFGFalse);
			cfgTableAccess.save();
		} else {
		  	throw new Exception("Genesys protocol is closed!");
		}// TODO
	}
	public void UpdateTableAccess(Genesys mainApp,int tableAccessID, String name, String description, Integer DbAccessId, Integer FormatId, String DbTableName) throws Exception {
		if(protocol.getState() != null) {
            try
            {
            	CfgTableAccessQuery tableAccessQ = new CfgTableAccessQuery();
            	tableAccessQ.setDbid(tableAccessID);
            	CfgTableAccess tableAccesstFromQ = confService.retrieveObject(tableAccessQ);
            	tableAccesstFromQ.setName(name);
            	tableAccesstFromQ.setDescription(description);
            	tableAccesstFromQ.setTenantDBID(1);
            	tableAccesstFromQ.setState(CfgObjectState.CFGEnabled);
            	tableAccesstFromQ.setType(CfgTableType.CFGTTCallingList);
            	tableAccesstFromQ.setDbAccessDBID(DbAccessId);
            	tableAccesstFromQ.setFormatDBID(FormatId);
            	tableAccesstFromQ.setDbTableName(DbTableName);
    			tableAccesstFromQ.save();
            }
            catch (Exception e)
            {
                System.out.println("Exception Create campaign : "+e.getMessage());
            }
     } else {
    	 throw new Exception("Genesys protocol is closed!");
     	 
     }
  }
	public CfgTableAccess UpdateTableAccess(Genesys mainApp,int tableAccessID, String name, String description, Integer DbAccessId, Integer FormatId, String DbTableName, String AccessGroupName) throws Exception {
		CfgTableAccess tableAccesstFromQ = null;
		if(protocol.getState() != null) {
            try
            {
            	CfgTableAccessQuery tableAccessQ = new CfgTableAccessQuery();
            	tableAccessQ.setDbid(tableAccessID);
            	tableAccesstFromQ = confService.retrieveObject(tableAccessQ);
            	tableAccesstFromQ.setName(name);
            	tableAccesstFromQ.setDescription(description);
            	tableAccesstFromQ.setTenantDBID(1);
            	tableAccesstFromQ.setState(CfgObjectState.CFGEnabled);
            	tableAccesstFromQ.setType(CfgTableType.CFGTTCallingList);
            	tableAccesstFromQ.setDbAccessDBID(DbAccessId);
            	tableAccesstFromQ.setFormatDBID(FormatId);
            	tableAccesstFromQ.setDbTableName(DbTableName);
            	CfgAccessGroup cfgAccGroup = GetAccessGroupByName(AccessGroupName);
            	tableAccesstFromQ.setAccountPermissions(cfgAccGroup, 127);
    			tableAccesstFromQ.save();
            }
            catch (Exception e)
            {
                System.out.println("Exception Create campaign : "+e.getMessage());
            }
     } else {
    	 throw new Exception("Genesys protocol is closed!");
     	 
     }
		return tableAccesstFromQ;
  }
	
	public void DeleteTableAccess(Genesys mainApp,int tableAccessID) throws Exception {
		if(protocol.getState() != null) {
            try
            {
            	CfgTableAccessQuery tableAccessQ = new CfgTableAccessQuery();
            	tableAccessQ.setDbid(tableAccessID);
            	CfgTableAccess tableAccesstFromQ = confService.retrieveObject(tableAccessQ);
    			tableAccesstFromQ.delete();
            }
            catch (Exception e)
            {
                System.out.println("Exception Create campaign : "+e.getMessage());
            }
     } else {
    	 throw new Exception("Genesys protocol is closed!");
     	 
     }
  }

	public void GetAllApplications() throws Exception {
		if(protocol.getState() != null) {
			CfgApplicationQuery cfgAppQ = new CfgApplicationQuery();
        	Collection<CfgApplication> cfgAppNew = confService.retrieveMultipleObjects(CfgApplication.class,cfgAppQ);
			 Iterator<CfgApplication>iListApps = cfgAppNew.iterator();
			 System.out.println("############### BCA APPLICATIONS GENESYS-SDK");
			 int i = 1;
			 while (iListApps.hasNext()) {
				 CfgApplication cfgAppitem = iListApps.next();
				 System.out.print("Iterator ############## "+i+" : ");
				 System.out.println(cfgAppitem.getName()+"  COntent : "+cfgAppitem.toString());
				 
				 
//				 String prefixDAP =cfgAppitem.getName().substring(0, 3); 
//				 System.out.println("Application " + prefixDAP + ", Iterator : "+cfgAppitem.getAppServers().size());
				 
//				 if(prefixDAP.equalsIgnoreCase("DAP")) {
////					 System.out.println("Name : "+cfgAppitem.getName());
//					 KVList kvList = cfgAppitem.getUserProperties().getList("default");
//					 System.out.println(cfgAppitem.getDBID()+ "|"+ cfgAppitem.getName() + "|"+kvList.getAsString("dbname"));
//				 }
				 System.out.println("################################################### ");
				 i++;
			 }
			 System.out.println("############### END BCA APPLICATIONS GENESYS-SDK");
			 
		  } else {
		  	throw new Exception("Genesys protocol is closed!");
		  }// TODO
	}
	public String GetAllApplications(String DAPName) throws Exception {
		String result = null;
		System.out.println(confService.getMetaData().getClasses().toString());
//		confService.getMetaData().
//		CfgAccessGroup Agroup = confService.getClass().getClasses()
		if(protocol.getState() != null) {
			CfgApplicationQuery cfgAppQ = new CfgApplicationQuery();
        	Collection<CfgApplication> cfgAppNew = confService.retrieveMultipleObjects(CfgApplication.class,cfgAppQ);
			 Iterator<CfgApplication>iListApps = cfgAppNew.iterator();
			 while (iListApps.hasNext()) {
				 CfgApplication cfgAppitem = iListApps.next();
				 String prefixDAP =cfgAppitem.getName(); 
				 
				 if(prefixDAP.equalsIgnoreCase(DAPName)) {

					 KVList kvList = cfgAppitem.getUserProperties().getList("default");
//					 System.out.println(cfgAppitem.getName() + "|"+kvList.getAsString("dbname"));
//					 kvList.getAsString("dbname");
//					 if(kvList.getAsString("dbname").equalsIgnoreCase(DAPName)) {
						 result = cfgAppitem.getName() + "|"+kvList.getAsString("dbname");
//					 }
				 }
			 }
			 
		  } else {
		  	throw new Exception("Genesys protocol is closed!");
		  }// TODO
		return result;
	}
	
	
//########################################################################################################################	
	public String getHost() {
		return host;
	}
	public void setHost(String host) {
		this.host = host;
	}
	public int getPort() {
		return port;
	}
	public void setPort(int port) {
		this.port = port;
	}
	public String getBackupHost() {
		return backupHost;
	}
	public void setBackupHost(String backupHost) {
		this.backupHost = backupHost;
	}
	public int getBackupPort() {
		return backupPort;
	}
	public void setBackupPort(int backupPort) {
		this.backupPort = backupPort;
	}
	public String getClientName() {
		return clientName;
	}
	public void setClientName(String clientName) {
		this.clientName = clientName;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public int getSkillLevel() {
		return skillLevel;
	}
	public void setSkillLevel(int skillLevel) {
		this.skillLevel = skillLevel;
	}
	public int getTenantID() {
		return tenantID;
	}
	public void setTenantID(int tenantID) {
		this.tenantID = tenantID;
	}
	public IConfService getConfService() {
		return confService;
	}
	public void setConfService(IConfService confService) {
		this.confService = confService;
	}

	public String getLocalUser() {
		return localUser;
	}

	public void setLocalUser(String localUser) {
		this.localUser = localUser;
	}

	public String getLocalPwd() {
		return localPwd;
	}

	public void setLocalPwd(String localPwd) {
		this.localPwd = localPwd;
	}

	public String getLocalDbURL() {
		return localDbURL;
	}

	public void setLocalDbURL(String localDbURL) {
		this.localDbURL = localDbURL;
	}

	
//	public Connection 
	
	public Integer timeToString(String timeInput) throws ParseException {
		Integer result = 0;
		SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
		Date userDate = dateFormat.parse(timeInput);
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(userDate);
		result = (int) calendar.getTimeInMillis();
		return result;
	}
	
	public Integer timeToDecimal(String timeInput) throws ParseException {
		Integer result = 0;
		SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
		Date userDate = dateFormat.parse(timeInput);
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(userDate);
		
		
		Integer hours = calendar.getTime().getHours() ;
		Integer minutes = calendar.getTime().getMinutes();
		Integer second = calendar.getTime().getSeconds();
		result = ((hours*60)*60)+(minutes*60)+(second);
		
//		result = second;
		
		
		return result;
	}
	
}
