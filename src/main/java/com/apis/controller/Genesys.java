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
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.apis.objects.ObjCfgFields;
import com.genesyslab.platform.applicationblocks.com.ConfServiceFactory;
import com.genesyslab.platform.applicationblocks.com.ConfigException;
import com.genesyslab.platform.applicationblocks.com.IConfService;
import com.genesyslab.platform.applicationblocks.com.Subscription;
import com.genesyslab.platform.applicationblocks.com.objects.CfgAccessGroup;
import com.genesyslab.platform.applicationblocks.com.objects.CfgAgentGroup;
import com.genesyslab.platform.applicationblocks.com.objects.CfgAgentInfo;
import com.genesyslab.platform.applicationblocks.com.objects.CfgAgentLogin;
import com.genesyslab.platform.applicationblocks.com.objects.CfgApplication;
import com.genesyslab.platform.applicationblocks.com.objects.CfgCallingList;
import com.genesyslab.platform.applicationblocks.com.objects.CfgCallingListInfo;
import com.genesyslab.platform.applicationblocks.com.objects.CfgCampaign;
import com.genesyslab.platform.applicationblocks.com.objects.CfgCampaignGroup;
import com.genesyslab.platform.applicationblocks.com.objects.CfgDN;
import com.genesyslab.platform.applicationblocks.com.objects.CfgField;
import com.genesyslab.platform.applicationblocks.com.objects.CfgFolder;
import com.genesyslab.platform.applicationblocks.com.objects.CfgFormat;
import com.genesyslab.platform.applicationblocks.com.objects.CfgGroup;
import com.genesyslab.platform.applicationblocks.com.objects.CfgID;
import com.genesyslab.platform.applicationblocks.com.objects.CfgPerson;
import com.genesyslab.platform.applicationblocks.com.objects.CfgPlace;
import com.genesyslab.platform.applicationblocks.com.objects.CfgRole;
import com.genesyslab.platform.applicationblocks.com.objects.CfgRoleMember;
import com.genesyslab.platform.applicationblocks.com.objects.CfgSkill;
import com.genesyslab.platform.applicationblocks.com.objects.CfgSkillLevel;
import com.genesyslab.platform.applicationblocks.com.objects.CfgTableAccess;
import com.genesyslab.platform.applicationblocks.com.queries.CfgAccessGroupQuery;
import com.genesyslab.platform.applicationblocks.com.queries.CfgAgentGroupQuery;
import com.genesyslab.platform.applicationblocks.com.queries.CfgAgentLoginQuery;
import com.genesyslab.platform.applicationblocks.com.queries.CfgApplicationQuery;
import com.genesyslab.platform.applicationblocks.com.queries.CfgCallingListQuery;
import com.genesyslab.platform.applicationblocks.com.queries.CfgCampaignGroupQuery;
import com.genesyslab.platform.applicationblocks.com.queries.CfgCampaignQuery;
import com.genesyslab.platform.applicationblocks.com.queries.CfgDNQuery;
import com.genesyslab.platform.applicationblocks.com.queries.CfgFieldQuery;
import com.genesyslab.platform.applicationblocks.com.queries.CfgFolderQuery;
import com.genesyslab.platform.applicationblocks.com.queries.CfgFormatQuery;
import com.genesyslab.platform.applicationblocks.com.queries.CfgPersonQuery;
import com.genesyslab.platform.applicationblocks.com.queries.CfgPlaceQuery;
import com.genesyslab.platform.applicationblocks.com.queries.CfgRoleQuery;
import com.genesyslab.platform.applicationblocks.com.queries.CfgSkillQuery;
import com.genesyslab.platform.applicationblocks.com.queries.CfgTableAccessQuery;
import com.genesyslab.platform.applicationblocks.warmstandby.WarmStandbyConfiguration;
import com.genesyslab.platform.applicationblocks.warmstandby.WarmStandbyService;
import com.genesyslab.platform.commons.collections.KVList;
import com.genesyslab.platform.commons.connection.configuration.ClientADDPOptions.AddpTraceMode;
import com.genesyslab.platform.commons.connection.configuration.PropertyConfiguration;
import com.genesyslab.platform.commons.protocol.Endpoint;
import com.genesyslab.platform.commons.protocol.ProtocolException;
import com.genesyslab.platform.commons.protocol.RegistrationException;
import com.genesyslab.platform.configuration.protocol.ConfServerProtocol;
import com.genesyslab.platform.configuration.protocol.types.CfgAppType;
import com.genesyslab.platform.configuration.protocol.types.CfgDNType;
import com.genesyslab.platform.configuration.protocol.types.CfgFlag;
import com.genesyslab.platform.configuration.protocol.types.CfgObjectState;
import com.genesyslab.platform.configuration.protocol.types.CfgObjectType;
import com.genesyslab.platform.configuration.protocol.types.CfgTableType;

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

    private Logger LOGGER = Logger.getLogger(Genesys.class.getName());

//    private final static Logger LOGGER = LoggerF
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
	     LOGGER.info(protocol2.getState()+"");
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
          		LOGGER.info("DBID : "+item.getDBID()+", Value : "+item.getName());
          		
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
          		LOGGER.info("DBID : "+item.getDBID()+", Value : "+item.getName()+", Type : "+item.getType().toString());
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

	public void GetAccessGroup() throws Exception {
		if(protocol.getState() != null) {
			 CfgAccessGroupQuery cfgAccessQ = new CfgAccessGroupQuery();
			 Collection<CfgAccessGroup> cfgAccessGroupList = confService.retrieveMultipleObjects(CfgAccessGroup.class, cfgAccessQ);
			 Iterator<CfgAccessGroup>iListApps = cfgAccessGroupList.iterator();
			 while (iListApps.hasNext()) {
				 CfgAccessGroup item = iListApps.next();
				 LOGGER.info("Iterator : "+item.toString());
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
		  } else {
		  	throw new Exception("Genesys protocol is closed!");
		  }// TODO
		return cfgAccessGroup;
	}
	public CfgAccessGroup addAccessGroup(String nameGroup) {
		try {
		CfgAccessGroup result = new CfgAccessGroup(confService);
		CfgGroup groupInfo = new CfgGroup(confService,result);
		groupInfo.setName(nameGroup);
		groupInfo.setTenantDBID(1);
		result.setGroupInfo(groupInfo);
		result.save();
		} catch (ConfigException e) {
			LOGGER.warning("Error "+e.getMessage());
		}
		return null;
	}
	public CfgAccessGroup addAccessGroupWithFolder(String nameGroup, String folderName) {
		try {
		CfgAccessGroup result = new CfgAccessGroup(confService);
		CfgGroup groupInfo = new CfgGroup(confService,result);
		groupInfo.setName(nameGroup);
		groupInfo.setTenantDBID(1);
		CfgFolderQuery cfgFQ = new CfgFolderQuery();
		cfgFQ.setName(folderName);
		CfgFolder cfgF = confService.retrieveObject(CfgFolder.class, cfgFQ);
		result.setFolderId(cfgF.getDBID());
		result.setGroupInfo(groupInfo);
		result.save();
		} catch (ConfigException e) {
			LOGGER.warning("Error "+e.getMessage());
		}
		return null;
	}
	public CfgAccessGroup addUserToAccessGroupByName(String AccessGroupName, CfgPerson person) throws Exception {
		CfgAccessGroup result = new CfgAccessGroup(confService);
		try {
			CfgAccessGroupQuery agQuery = new CfgAccessGroupQuery();
			agQuery.setName(AccessGroupName);
			CfgAccessGroup agQueryResult = confService.retrieveObject(CfgAccessGroup.class, agQuery);
			CfgID cID = new CfgID(confService, person);
			cID.setDBID(person.getDBID());
			cID.setType(person.getObjectType());
			agQueryResult.getMemberIDs().add(cID);
			agQueryResult.save();
			result = agQueryResult;
		} catch (ConfigException e) {
			LOGGER.warning("Error "+e.getMessage());
		}
		return result;
	}
	public CfgAccessGroup delUserToAccessGroupByName(String AccessGroupName, CfgPerson person) throws Exception {
		CfgAccessGroup result = new CfgAccessGroup(confService);
		try {
			CfgAccessGroupQuery agQuery = new CfgAccessGroupQuery();
			agQuery.setName(AccessGroupName);
			CfgAccessGroup agQueryResult = confService.retrieveObject(CfgAccessGroup.class, agQuery);
//			CfgID cID = new CfgID(confService, person);
			Collection<CfgID> listObjOld = agQueryResult.getMemberIDs();
			Collection<CfgID> listObjNew = new ArrayList<CfgID>();
			for (CfgID cfgID : listObjOld) {
				if(cfgID.getType() == CfgObjectType.CFGPerson) {
					Integer personId = person.getDBID();
					Integer cidLoop = cfgID.getDBID();
					if(cidLoop.equals(personId)) {
					} else {
						listObjNew.add(cfgID);
					}
				} else {
					LOGGER.info(">>>>>>>>>>>>>>> TAMBAH ELSE  "+cfgID.getDBID());
					listObjNew.add(cfgID);
				}
			}
			agQueryResult.getMemberIDs().removeAll(listObjOld);
			LOGGER.info(">>>>>>>>>>>>>>> REMOVE ALL MEMBERS Of "+AccessGroupName);
			agQueryResult.setMemberIDs(listObjNew);
			LOGGER.info(">>>>>>>>>>>>>>> SET NEW MEMBERS Of "+AccessGroupName);
			agQueryResult.save();
			result = agQueryResult;
		} catch (ConfigException e) {
			LOGGER.warning("Error "+e.getMessage());
		}
		return result;
	}
	public CfgAccessGroup delAgentGroupToAccessGroupByName(String AccessGroupName, CfgAgentGroup agentGroup) throws Exception {
		CfgAccessGroup result = new CfgAccessGroup(confService);
		try {
			CfgAccessGroupQuery agQuery = new CfgAccessGroupQuery();
			agQuery.setName(AccessGroupName);
			CfgAccessGroup agQueryResult = confService.retrieveObject(CfgAccessGroup.class, agQuery);
//			CfgID cID = new CfgID(confService, person);
			Collection<CfgID> listObjOld = agQueryResult.getMemberIDs();
			Collection<CfgID> listObjNew = new ArrayList<CfgID>();
			for (CfgID cfgID : listObjOld) {
				if(cfgID.getType() == CfgObjectType.CFGAgentGroup) {
					Integer personId = agentGroup.getDBID();
					Integer cidLoop = cfgID.getDBID();
					if(cidLoop.equals(personId)) {
					} else {
						listObjNew.add(cfgID);
					}
				} else {
					LOGGER.info(">>>>>>>>>>>>>>> TAMBAH ELSE  "+cfgID.getDBID());
					listObjNew.add(cfgID);
				}
			}
			agQueryResult.getMemberIDs().removeAll(listObjOld);
			LOGGER.info(">>>>>>>>>>>>>>> REMOVE ALL MEMBERS Of "+AccessGroupName);
			agQueryResult.setMemberIDs(listObjNew);
			LOGGER.info(">>>>>>>>>>>>>>> SET NEW MEMBERS Of "+AccessGroupName);
			agQueryResult.save();
			result = agQueryResult;
		} catch (ConfigException e) {
			LOGGER.warning("Error "+e.getMessage());
		}
		return result;
	}
	public CfgAccessGroup updateAccessGroup(int dbid, String NewAccessGroupName) {
		CfgAccessGroup agQueryResult = null;
		try {
			CfgAccessGroupQuery agQuery = new CfgAccessGroupQuery();
			agQuery.setDbid(dbid);
			agQueryResult = confService.retrieveObject(CfgAccessGroup.class, agQuery);
			 CfgGroup cfgGroup = agQueryResult.getGroupInfo();
			 cfgGroup.setName(NewAccessGroupName);
			 agQueryResult.setGroupInfo(cfgGroup);
			agQueryResult.save();
			return agQueryResult;
			
		} catch (ConfigException e) {
			LOGGER.warning("Error "+e.getMessage());
		}
		return agQueryResult;
	}
	
	
	
	public String deleteAccessGroupById(Integer id) {
		try {
			CfgAccessGroupQuery agQuery = new CfgAccessGroupQuery();
			agQuery.setDbid(id);
			CfgAccessGroup result = confService.retrieveObject(CfgAccessGroup.class, agQuery);
			result.delete();
		} catch (ConfigException e) {
			LOGGER.warning("Error "+e.getMessage());
		}
		return "Success";
	}
	
	
	
	
	
	public void GetFolders() throws Exception {
		if(protocol.getState() != null) {
			CfgFolderQuery cfgAccessQ = new CfgFolderQuery();
			 Collection<CfgFolder> cfgAccessGroupList = confService.retrieveMultipleObjects(CfgFolder.class, cfgAccessQ);
			 Iterator<CfgFolder>iListApps = cfgAccessGroupList.iterator();
			 while (iListApps.hasNext()) {
				 CfgFolder item = iListApps.next();
				 LOGGER.info("Iterator : "+item.getName());
//				 item.getGroupInfo().
//				 LOGGER.info("Iterator : "+item.g);
//				 CfgAgentInfo test = item.
			 }
			 
		  } else {
		  	throw new Exception("Genesys protocol is closed!");
		  }// TODO
	}
	
	
	public List<CfgAccessGroup> GetAccessGroupByPrefix(String prefix) throws Exception {
		List<CfgAccessGroup> result = new ArrayList<CfgAccessGroup>();
		if(protocol.getState() != null) {
			 CfgAccessGroupQuery cfgAccessQ = new CfgAccessGroupQuery();
			 Collection<CfgAccessGroup> cfgAccessGroupList = confService.retrieveMultipleObjects(CfgAccessGroup.class, cfgAccessQ);
			 Iterator<CfgAccessGroup>iListApps = cfgAccessGroupList.iterator();
			 while (iListApps.hasNext()) {
				 CfgAccessGroup item = iListApps.next();
				 if ( item.getGroupInfo().getName().toLowerCase().indexOf(prefix.toLowerCase()) != -1 ) {
					   result.add(item);
//					   LOGGER.info("match : "+item.getGroupInfo().getName());
					} else {
					   LOGGER.info("Not match : "+item.getGroupInfo().getName());
				 }
			 }
			 
		  } else {
		  	throw new Exception("Genesys protocol is closed!");
		  }// TODO
		return result;
	}
	public List<CfgPerson> GetAccessGroupMembersByAccessGroupName(String accesGroupName) throws Exception {
		List<CfgPerson> result = new ArrayList<CfgPerson>();
		if(protocol.getState() != null) {
			 CfgAccessGroup aGroup = GetAccessGroupByName(accesGroupName);
			 Collection<CfgID> membeCfgID =  aGroup.getMemberIDs();
			 for (CfgID cfgID : membeCfgID) {
				if(cfgID.getType() == CfgPerson.OBJECT_TYPE) {
//					LOGGER.info("Ada sama Nih "+ cfgID.getDBID() + "\n ############################");
					CfgPerson prsn = getPersonsById(cfgID.getDBID());
					result.add(prsn);
				}
			}
			 
		  } else {
		  	throw new Exception("Genesys protocol is closed!");
		  }// TODO
		return result;
	}
	
	
//	
	
	
	
	
	public void GetPersons() throws Exception {
		if(protocol.getState() != null) {
			 CfgPersonQuery cfgPersonsQ = new CfgPersonQuery();
			 Collection<CfgPerson> cfgPersonsList = confService.retrieveMultipleObjects(CfgPerson.class, cfgPersonsQ);
			 Iterator<CfgPerson>iListApps = cfgPersonsList.iterator();
			 while (iListApps.hasNext()) {
				 CfgPerson item = iListApps.next();
				 LOGGER.info("Iterator : "+item.getFirstName()+ " "+ item.getLastName());
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
	public CfgFormat getFormatByName(String FormatName) throws Exception {
		CfgFormat formatObj = null;
		if(protocol.getState() != null) {
			 CfgFormatQuery cfgFormatQ = new CfgFormatQuery();
			 cfgFormatQ.setName(FormatName);
			 formatObj = confService.retrieveObject(CfgFormat.class, cfgFormatQ);
		  } else {
		  	throw new Exception("Genesys protocol is closed!");
		  }
		return formatObj;
	}
    public void UpdateFormat(Genesys mainApp,int formatID, String name, String descFormat, Collection<Integer> filedsIds) throws Exception {
    	
    	if(protocol.getState() != null) {
            try
            {
            	LOGGER.info("########### UPDATE FORMAT 1 ######## Katro");
            	CfgFormatQuery formatQ = new CfgFormatQuery();
            	formatQ.setName(name);
            	CfgFormat FormatFromQ = confService.retrieveObject(formatQ);
//            	FormatFromQ.setName(name);
            	FormatFromQ.setDescription(descFormat);
            	LOGGER.info("########### UPDATE FORMAT 2 ######## Query dan Update data");
            	FormatFromQ.setTenantDBID(1);
            	FormatFromQ.setState(CfgObjectState.CFGEnabled);
            	LOGGER.info("########### UPDATE FORMAT 3 ######## Mulai masukan Fields "+ filedsIds.size());
            	FormatFromQ.setFieldDBIDs(filedsIds);
            	FormatFromQ.save();
            	LOGGER.info("########### UPDATE FORMAT 4 ######## Berhasil di save");
            }
            catch (Exception e)
            {
                LOGGER.info("Exception Create campaign : "+e.getMessage());
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
            	LOGGER.info("########### UPDATE FORMAT 1 ######## Awal");
            	CfgFormatQuery formatQ = new CfgFormatQuery();
            	formatQ.setName(name);
            	FormatFromQ = confService.retrieveObject(formatQ);
            	Collection<CfgField> collLastField = FormatFromQ.getFields();
//            	FormatFromQ.setName(name);
            	LOGGER.info("########### UPDATE FORMAT 2 ######## Query data : "+FormatFromQ.getName());
            	FormatFromQ.setDescription(description);
            	FormatFromQ.getFields().removeAll(collLastField);
            	LOGGER.info("########### UPDATE FORMAT 3 ######## Clear Fields");
            	FormatFromQ.setFieldDBIDs(filedsIds);
            	LOGGER.info("########### UPDATE FORMAT 4 ######## Add Fields");
            	FormatFromQ.save();
            	LOGGER.info("########### UPDATE FORMAT 5 ######## Berhasil di save");
            }
            catch (Exception e)
            {
                LOGGER.info("Exception Update format : "+e.getMessage());
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
                LOGGER.info("Exception Create campaign : "+e.getMessage());
            }
     } else {
    	 throw new Exception("Genesys protocol is closed!");
     	 
     }
}
	
    public void getAllFormat() throws Exception, InterruptedException {
    	if(protocol.getState() != null) {
			CfgFormatQuery cfgFmtQ = new CfgFormatQuery();
        	Collection<CfgFormat> ccfgFmtColl = confService.retrieveMultipleObjects(CfgFormat.class,cfgFmtQ);
			 Iterator<CfgFormat>iListApps = ccfgFmtColl.iterator();
			 while (iListApps.hasNext()) {
				 CfgFormat cfgFmtitem = iListApps.next();
//				 String prefixDAP =cfgAppitem.getName().substring(0, 3); 
				 LOGGER.info("Format ID :"+cfgFmtitem.getDBID()+"\nFormat Name :" +cfgFmtitem.getName());
				 LOGGER.info("Format Desc :"+cfgFmtitem.getDescription());
				 Collection<CfgField> collField = cfgFmtitem.getFields();
				 System.out.print("Format Fields : ");
				 for (CfgField cfgField : collField) {
					System.out.print(cfgField.getDBID()+",");
				 }
				 LOGGER.info("");
				 LOGGER.info("#############################################################################");
			 }
			 
		  } else {
		  	throw new Exception("Genesys protocol is closed!");
		  }// TODO
    }
	
    public void getAllSkill() throws Exception, InterruptedException {
    	if(protocol.getState() != null) {
			CfgSkillQuery cfgFmtQ = new CfgSkillQuery();
        	Collection<CfgSkill> ccfgFmtColl = confService.retrieveMultipleObjects(CfgSkill.class,cfgFmtQ);
//			 Iterator<CfgSkill>iListApps = ccfgFmtColl.iterator();
//			 while (iListApps.hasNext()) {
//				 CfgSkill cfgFmtitem = iListApps.next();
//				 LOGGER.info("Skill ID :"+cfgFmtitem.getDBID()+"\nSkill Name :" +cfgFmtitem.getName());
//				
//				 LOGGER.info("");
//				 LOGGER.info("#############################################################################");
//			 }
        	LOGGER.info(ccfgFmtColl.size()+"");
			 
		  } else {
		  	throw new Exception("Genesys protocol is closed!");
		  }// TODO
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
	public CfgCallingList CreateNewCallingList(Genesys mainApp, String name, String description,String TbAccess, String AccessGroupName, String StartFrom, String EndTo, CfgTableAccess cfgTableAccess) throws Exception {
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
//			CfgTableAccess tableAcces = getTableAccessByname(TbAccess);
			cfgCallingList.setTableAccess(cfgTableAccess);
			cfgCallingList.save();
			result = getCallingListByNameUpdateAccessGroup(name, AccessGroupName, cfgTableAccess);
			
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
				 LOGGER.info("Call ID :"+cfgAppitem.getDBID()+", Name " +cfgAppitem.getName());
				 LOGGER.info("Call From :" +cfgAppitem.getTimeFrom());
				 LOGGER.info("Call to :" +cfgAppitem.getTimeUntil());
				 LOGGER.info("############################");
				 
//				 if(prefixDAP.equalsIgnoreCase("DAP")) {
//					 LOGGER.info("Name : "+cfgAppitem.getName());
//					 KVList kvList = cfgAppitem.getUserProperties().getList("default");
//					 LOGGER.info(cfgAppitem.getDBID()+ "|"+ cfgAppitem.getName() + "|"+kvList.getAsString("dbname"));
//				 }
			 }
			 
		  } else {
		  	throw new Exception("Genesys protocol is closed!");
		  }// TODO
	}
	public CfgCallingList UpdateCallingList(Genesys mainApp, Integer callinglistID,String name, String description,String TbAccess, String StartFrom, String EndTo, String AccessGroupName) throws Exception {
		CfgCallingList CallingListFromQ = null;
		if(protocol.getState() != null) {
            try
            {
            	CfgCallingListQuery callinglistQ = new CfgCallingListQuery();
            	callinglistQ.setDbid(callinglistID);
            	CallingListFromQ = confService.retrieveObject(callinglistQ);
            	CallingListFromQ.setName(name);
            	CallingListFromQ.setDescription(description);

            	Integer startFromTime = timeToDecimal(StartFrom);
    			Integer EndToTime = timeToDecimal(EndTo);
    			
    			CallingListFromQ.setTimeFrom(startFromTime);
    			CallingListFromQ.setTimeUntil(EndToTime);

            	CallingListFromQ.save();
            }
            catch (Exception e)
            {
                LOGGER.info("Exception Create campaign : "+e.getMessage());
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
	public CfgCallingList getCallingListByNameUpdateAccessGroup(String callingListName, String AccessGroupName, CfgTableAccess cfgTableAccess) throws Exception {
		CfgCallingList callingListObj = null;
		if(protocol.getState() != null) {
			CfgCallingListQuery callingListQ = new CfgCallingListQuery();
			callingListQ.setName(callingListName);
			callingListObj = confService.retrieveObject(CfgCallingList.class, callingListQ);
			 CfgAccessGroup cfgAccGroup = GetAccessGroupByName(AccessGroupName);
			 callingListObj.setTableAccess(cfgTableAccess);
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
//				LOGGER.info("# Dari object # "+callingListObj.getName());
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
                LOGGER.info("Exception Create campaign : "+e.getMessage());
            }
     } else {
    	 throw new Exception("Genesys protocol is closed!");
     	 
     }
	}
	

	public CfgCampaign CreateNewCampaign(Genesys mainApp, String name, String description,String CallingListName, String AccessGroupName, String AgentGroupName) throws Exception {
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
					LOGGER.info("################# Create Campaign Sukses ID: "+result.getDBID()+", Name : " +result.getName());
					Thread.sleep(100);
					CfgCampaignQuery cfgcamp = new CfgCampaignQuery(confService);
		            cfgcamp.setName(name);
		            cfgcamp.setDbid(result.getDBID());
		            CfgCampaign cpgn = confService.retrieveObject(CfgCampaign.class,cfgcamp);
		            LOGGER.info("################# CfgCampaignQuery Sukses ID: "+cpgn.getDBID()+", Name : " +cpgn.getName());
		            LOGGER.info("################# CfgCampaignQuery Metadata : "+cpgn.getMetaData().toString());
		//            mylog.log(Level.INFO,"Campaign Query Success Success");
		            Thread.sleep(500);
					
		            if(AgentGroupName == null)AgentGroupName=AccessGroupName;
		            
					CfgAgentGroupQuery agentGroupQuery = new CfgAgentGroupQuery(confService);
					agentGroupQuery.setName(AgentGroupName);
					CfgAgentGroup agentGroup = confService.retrieveObject(CfgAgentGroup.class, agentGroupQuery);
//					LOGGER.info("AgentGroup Query Success ");
					LOGGER.info("################# CfgAgentGroupQuery Sukses ID: "+agentGroupQuery.getDbid()+", Name : " +agentGroupQuery.getName());
					LOGGER.info("################# CfgAgentGroupQuery Metadata : "+agentGroupQuery.toString());

					CfgApplicationQuery appQuery = new CfgApplicationQuery(confService);
		            appQuery.setName("Stat_Server_Routing"); // ini fix ga usah diganti
					Collection<CfgApplication> app = confService.retrieveMultipleObjects(CfgApplication.class, appQuery);
					
					for (CfgApplication cfgApplication : app) {
						LOGGER.info("################# CfgApplicationQuery Sukses ID: "+cfgApplication.getDBID()+", Name : " +cfgApplication.getName());
			            LOGGER.info("################# CfgApplicationQuery Metadata : "+cfgApplication.getMetaData().toString());
					}
					
					CfgDNQuery dnQuery = new CfgDNQuery(confService);
//		            dnQuery.setDnNumber("5100"); // ini sementara static 5100 aja dlu
		            dnQuery.setName("5100");
		            
		            dnQuery.setDnType(CfgDNType.CFGRoutingPoint);
		            CfgDN dn = confService.retrieveObject(CfgDN.class, dnQuery);
					
		            LOGGER.info("################# CfgDNQuery Sukses ID: "+dn.getDBID()+", Name : " +dn.getName());
		            LOGGER.info("################# CfgDNQuery Metadata : "+dn.getMetaData().toString());
		            LOGGER.info("################# CfgDNQuery Metadata END ");
					
					LOGGER.info("################# ");
					
		            
		            Thread.sleep(1000);
	
		            LOGGER.info("################# Creating Campaign Group Start : ");
		            CfgCampaignGroup campaignGroup = new CfgCampaignGroup(confService);
		            campaignGroup.setTenantDBID(this.tenantID);
		            LOGGER.info("#### Set tenant ID : "+this.tenantID);
		            campaignGroup.setName(name);
		            LOGGER.info("#### Set campGroup Name : "+name);
		            int idCampDBID = cpgn.getObjectDbid();
		            campaignGroup.setCampaignDBID(idCampDBID);
		            LOGGER.info("#### Set campaign ID : "+idCampDBID);
		            campaignGroup.setState(CfgObjectState.CFGEnabled);
		            LOGGER.info("#### Set State Object : "+CfgObjectState.CFGEnabled);
		            campaignGroup.setOrigDN(dn);
//		            LOGGER.info("#### Set Orig DN : "+dn.getName());
		            int agtGroupId = agentGroup.getGroupInfo().getDBID();
		            campaignGroup.setGroupDBID(agtGroupId);
//		            LOGGER.info("#### Set Agent Group ID : "+agtGroupId);
		            campaignGroup.setGroupType(CfgObjectType.CFGAgentGroup);
//		            LOGGER.info("#### Set Group Type : "+CfgObjectType.CFGAgentGroup.name());
		            campaignGroup.setServers(app);
		            
		            LOGGER.info("#### Set server : "+app.toString());
		            LOGGER.info("################# Creating Campaign Group Saving : ");
		            campaignGroup.save();
		            LOGGER.info("################# Creating Campaign Group Saving End : ");
//		            Thread.sleep(1000);
            

			} catch (Exception e) {
				// TODO Auto-generated catch block
				LOGGER.info("##### ERROR ####### : "+e.getMessage());
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
	public CfgCampaign UpdateCfgCampaign(Genesys mainApp, Integer campaignID,String name, String description,String CallingListName, String AccessGroupName, String AgentGroupName) throws Exception {
		CfgCampaign campaignFromQ = null;
		if(protocol.getState() != null) {
            try
            {
            	CfgCampaignQuery campaignQ = new CfgCampaignQuery();
//            	campaignQ.setDbid(campaignID);
            	campaignQ.setName(name);
            	campaignFromQ = confService.retrieveObject(campaignQ);
            	campaignFromQ.setName(name);
            	campaignFromQ.setDescription(description);
            	CfgAgentGroupQuery agentGroupQuery = new CfgAgentGroupQuery(confService);
				agentGroupQuery.setName(AgentGroupName);
				CfgAgentGroup agentGroup = confService.retrieveObject(CfgAgentGroup.class, agentGroupQuery);
				
				CfgCampaignGroupQuery cfgCampaignGroupQ = new CfgCampaignGroupQuery(confService);
				cfgCampaignGroupQ.setName(name);
				CfgCampaignGroup cfgCampaignGroup = confService.retrieveObject(CfgCampaignGroup.class, cfgCampaignGroupQ);
				
				cfgCampaignGroup.setGroupDBID(agentGroup.getDBID());
	            
				cfgCampaignGroup.setGroupType(CfgObjectType.CFGAgentGroup);
				cfgCampaignGroup.save();
	            
            	campaignFromQ.save();
            	
            	
            }
            catch (Exception e)
            {
                LOGGER.info("Exception Create campaign : "+e.getMessage());
            }
     } else {
    	 throw new Exception("Genesys protocol is closed!");
     	 
     }
		
		return campaignFromQ;
	}
	public void DeleteCfgCampaign(int CfgCampaignID, String CfgCampName) throws Exception {
		if(protocol.getState() != null) {
            try
            {
            	CfgCampaignQuery cfgCampignQ = new CfgCampaignQuery();
            	cfgCampignQ.setName(CfgCampName);
            	CfgCampaign callingListFromQ = confService.retrieveObject(cfgCampignQ);
            	callingListFromQ.delete();
            }
            catch (Exception e)
            {
                LOGGER.info("Exception Create campaign : "+e.getMessage());
            }
     } else {
    	 throw new Exception("Genesys protocol is closed!");
     	 
     }
	}
	
	public void DeleteCfgCampaignGroup(String CfgCampGropName) throws Exception {
		if(protocol.getState() != null) {
            try
            {
            	CfgCampaignGroupQuery cfgCampignQ = new CfgCampaignGroupQuery();
            	cfgCampignQ.setName(CfgCampGropName);
            	CfgCampaignGroup callingListFromQ = confService.retrieveObject(cfgCampignQ);
            	callingListFromQ.delete();
            }
            catch (Exception e)
            {
                LOGGER.info("Exception Create campaign : "+e.getMessage());
            }
     } else {
    	 throw new Exception("Genesys protocol is closed!");
     	 
     }
	}
	
	public CfgFolder getFolderByName(String NameFolder) throws Exception {
		CfgFolder result = null;
		if(protocol.getState() != null) {
			 CfgFolderQuery cfgFolderQ = new CfgFolderQuery();
			 cfgFolderQ.setName(NameFolder);
			 result = confService.retrieveObject(CfgFolder.class, cfgFolderQ);
		  } else {
		  	throw new Exception("Genesys protocol is closed!");
		  }
		return result;
	}

	public void GetAllCampaign() throws Exception {
		if(protocol.getState() != null) {
			 CfgCampaignQuery cfgCampaignQ = new CfgCampaignQuery();
			 Collection<CfgCampaign> cfgTableAccessList = confService.retrieveMultipleObjects(CfgCampaign.class, cfgCampaignQ);
			 Iterator<CfgCampaign>iListApps = cfgTableAccessList.iterator();
			 while (iListApps.hasNext()) {
				 CfgCampaign item = iListApps.next();
				 LOGGER.info("Campaign Name : "+item.getName());
				 LOGGER.info("Description Name : "+item.getDescription());
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
				 LOGGER.info("Iterator : "+item.toString());
			 }
			 
		  } else {
		  	throw new Exception("Genesys protocol is closed!");
		  }
	}
	public List<CfgAgentGroup> GetAllAgentGroupByPrefix(String prefix) throws Exception {
		List<CfgAgentGroup> result  = new ArrayList<CfgAgentGroup>();
		if(protocol.getState() != null) {
			 CfgAgentGroupQuery cfgTableAccessQ = new CfgAgentGroupQuery();
			 Collection<CfgAgentGroup> cfgTableAccessList = confService.retrieveMultipleObjects(CfgAgentGroup.class, cfgTableAccessQ);
			 Iterator<CfgAgentGroup>iListApps = cfgTableAccessList.iterator();
			 while (iListApps.hasNext()) {
				 CfgAgentGroup item = iListApps.next();
				 
//				 LOGGER.info(">>>> ACCESS GROUP NAME "+item.getGroupInfo().get);
				 
				 				
				 
				 if ( item.getGroupInfo().getName().toLowerCase().indexOf(prefix.toLowerCase()) != -1 ) {
					   result.add(item);
//					   LOGGER.info("match : "+item.getGroupInfo().getName());
					} else {
					   LOGGER.info("Not match : "+item.getGroupInfo().getName());
				 }
//				 LOGGER.info("Iterator : "+item.toString());
			 }
			 
		  } else {
		  	throw new Exception("Genesys protocol is closed!");
		  }
		return result;
	}
	public CfgAgentGroup GetAgentGroupByName(String AgentGroupName) throws Exception {
		CfgAgentGroup cfgAgentGroup = null;
		if(protocol.getState() != null) {
			CfgAgentGroupQuery cfgAgentQ = new CfgAgentGroupQuery();
			cfgAgentQ.setName(AgentGroupName);
			cfgAgentGroup = confService.retrieveObject(CfgAgentGroup.class,cfgAgentQ);
		  } else {
		  	throw new Exception("Genesys protocol is closed!");
		  }// TODO
		return cfgAgentGroup;
	}
	public CfgAgentGroup addAgentGroup(String nameGroup) {
		CfgAgentGroup result=null;
		try {
			result = new CfgAgentGroup(confService);
			CfgGroup groupInfo = new CfgGroup(confService,result);
			groupInfo.setName(nameGroup);
			groupInfo.setTenantDBID(1);
			result.setGroupInfo(groupInfo);
			result.save();
		return result;
		} catch (ConfigException e) {
			LOGGER.warning("Error "+e.getMessage());
		}
		return result;
		
	}
	public CfgAgentGroup addUserToAgentGroupByName(String AgentGroupName, CfgPerson person) throws Exception {
		CfgAgentGroup result = new CfgAgentGroup(confService);
		try {
			CfgAgentGroupQuery agQuery = new CfgAgentGroupQuery();
			agQuery.setName(AgentGroupName);
			CfgAgentGroup agQueryResult = confService.retrieveObject(CfgAgentGroup.class, agQuery);
			agQueryResult.getAgents().add(person);

			agQueryResult.save();
			result = agQueryResult;
		} catch (ConfigException e) {
			LOGGER.warning("Error "+e.getMessage());
		}
		return result;
	}
	public CfgAgentGroup addUserToSupervisorAgentGroup (String nameGroup,CfgPerson person) {
		CfgAgentGroup result = new CfgAgentGroup(confService);
		try {
			CfgAgentGroupQuery agQuery = new CfgAgentGroupQuery();
			agQuery.setName(nameGroup);
			CfgAgentGroup agQueryResult = confService.retrieveObject(CfgAgentGroup.class, agQuery);
			CfgGroup agentGroupInfo = agQueryResult.getGroupInfo();
			List<CfgPerson> listPerson = new ArrayList<CfgPerson>();
			listPerson.add(person);
			agentGroupInfo.setManagers(listPerson);
			agQueryResult.setGroupInfo(agentGroupInfo);
			agQueryResult.save();
			result = agQueryResult;
		} catch (ConfigException e) {
			LOGGER.warning("Error "+e.getMessage());
		}
		return result;
//		CfgAgentGroup result=null;
//		try {
//			result = new CfgAgentGroup(confService);
//			CfgGroup groupInfo = new CfgGroup(confService,result);
//			groupInfo.setName(nameGroup);
//			groupInfo.setTenantDBID(1);
//			
//			List<CfgPerson> listPerson = new ArrayList<CfgPerson>();
//			listPerson.add(person);
//			groupInfo.setManagers(listPerson);
//			
//			result.setGroupInfo(groupInfo);
//			result.save();
//		return result;
//		} catch (ConfigException e) {
//			LOGGER.warning("Error "+e.getMessage());
//		}
//		return result;
		
	}
	
	public CfgAgentGroup addAgentGroupWithFolder(String nameGroup, String folderName) {
		try {
			CfgAgentGroup result = new CfgAgentGroup(confService);
			CfgGroup groupInfo = new CfgGroup(confService,result);
			groupInfo.setName(nameGroup);
			groupInfo.setTenantDBID(1);
			CfgFolderQuery cfgFQ = new CfgFolderQuery();
			cfgFQ.setName(folderName);
			CfgFolder cfgF = confService.retrieveObject(CfgFolder.class, cfgFQ);
			result.setFolderId(cfgF.getDBID());
			result.setGroupInfo(groupInfo);
			result.save();
			return result;
		} catch (ConfigException e) {
			LOGGER.warning("Error "+e.getMessage());
		}
		return null;
	}
	
	public CfgAgentGroup delUserToAgentGroupByName(String AgentGroupName, CfgPerson person) throws Exception {
		CfgAgentGroup result = new CfgAgentGroup(confService);
		try {
			CfgAgentGroupQuery agQuery = new CfgAgentGroupQuery();
//			agQuery.get
			agQuery.setName(AgentGroupName);
			CfgAgentGroup agQueryResult = confService.retrieveObject(CfgAgentGroup.class, agQuery);
//			CfgID cID = new CfgID(confService, person);
			Collection<Integer> listObjOld = agQueryResult.getAgentDBIDs();
			Collection<Integer> listObjNew = new ArrayList<Integer>();
			for (Integer cfgID : listObjOld) {
					Integer personId = person.getDBID();
					Integer cidLoop = cfgID;
					if(cidLoop.equals(personId)) {
					} else {
						listObjNew.add(cfgID);
					}
				
			}
			agQueryResult.getAgentDBIDs().removeAll(listObjOld);
//			agQueryResult.getMemberIDs().removeAll(listObjOld);
			LOGGER.info(">>>>>>>>>>>>>>> REMOVE ALL MEMBERS Of "+AgentGroupName);
			agQueryResult.setAgentDBIDs(listObjNew);
			LOGGER.info(">>>>>>>>>>>>>>> SET NEW MEMBERS Of "+AgentGroupName);
			agQueryResult.save();
			result = agQueryResult;
		} catch (ConfigException e) {
			LOGGER.warning("Error "+e.getMessage());
		}
		return result;
	}
	
	public CfgAgentGroup updateAccessGroupToAgentGroup(String AgentGroupName, String AccessGroupName) throws Exception {
		CfgAgentGroup result = null;
		try {
			CfgAgentGroupQuery agQuery = new CfgAgentGroupQuery();
			agQuery.setName(AgentGroupName);
			result = confService.retrieveObject(CfgAgentGroup.class, agQuery);
			CfgAccessGroup cfgAccGroup = GetAccessGroupByName(AccessGroupName);
			result.setAccountPermissions(cfgAccGroup, 127);
//			cfgAccessGroup.se
			result.save();
//			result.delete();
		} catch (ConfigException e) {
			LOGGER.warning("Error "+e.getMessage());
		}
		
		return result;
	}
	
	public CfgAgentGroup updateAgentGroup(int dbid, String NewAgentGroupName) {
		CfgAgentGroup agQueryResult = null;
		try {
			CfgAgentGroupQuery agQuery = new CfgAgentGroupQuery();
			agQuery.setDbid(dbid);
			agQueryResult = confService.retrieveObject(CfgAgentGroup.class, agQuery);
			 CfgGroup cfgGroup = agQueryResult.getGroupInfo();
			 cfgGroup.setName(NewAgentGroupName);
			 agQueryResult.setGroupInfo(cfgGroup);
			agQueryResult.save();
			return agQueryResult;
			
		} catch (ConfigException e) {
			LOGGER.warning("Error "+e.getMessage());
		}
		return agQueryResult;
	}
	
	public String deleteAgentGroupById(Integer id) {
		try {
			CfgAgentGroupQuery agQuery = new CfgAgentGroupQuery();
			agQuery.setDbid(id);
			CfgAgentGroup result = confService.retrieveObject(CfgAgentGroup.class, agQuery);
			result.delete();
		} catch (ConfigException e) {
			LOGGER.warning("Error "+e.getMessage());
		}
		return "Success";
	}
	
	
	public void GetAllTableAccess() throws Exception {
		if(protocol.getState() != null) {
			 CfgTableAccessQuery cfgTableAccessQ = new CfgTableAccessQuery();
			 Collection<CfgTableAccess> cfgTableAccessList = confService.retrieveMultipleObjects(CfgTableAccess.class, cfgTableAccessQ);
			 Iterator<CfgTableAccess>iListApps = cfgTableAccessList.iterator();
			 while (iListApps.hasNext()) {
				 CfgTableAccess item = iListApps.next();
				 LOGGER.info("Iterator : "+item.toString());
			 }
			 
		  } else {
		  	throw new Exception("Genesys protocol is closed!");
		  }// TODO
	}
	
	public void GetAllRoles() throws Exception {
		if(protocol.getState() != null) {
			 CfgRoleQuery cfgTableAccessQ = new CfgRoleQuery();
			 Collection<CfgRole> cfgTableAccessList = confService.retrieveMultipleObjects(CfgRole.class, cfgTableAccessQ);
			 Iterator<CfgRole>iListApps = cfgTableAccessList.iterator();
			 while (iListApps.hasNext()) {
				 CfgRole item = iListApps.next();
				 LOGGER.info("Iterator : "+item.toString());
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
                LOGGER.info("Exception Create campaign : "+e.getMessage());
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
//            	tableAccesstFromQ.setTenantDBID(1);
//            	tableAccesstFromQ.setState(CfgObjectState.CFGEnabled);
//            	tableAccesstFromQ.setType(CfgTableType.CFGTTCallingList);
//            	tableAccesstFromQ.setDbAccessDBID(DbAccessId);
            	tableAccesstFromQ.setFormatDBID(FormatId);
//            	tableAccesstFromQ.setDbTableName(DbTableName);
//            	CfgAccessGroup cfgAccGroup = GetAccessGroupByName(AccessGroupName);
//            	tableAccesstFromQ.setAccountPermissions(cfgAccGroup, 127);
    			tableAccesstFromQ.save();
            }
            catch (Exception e)
            {
                LOGGER.info("Exception Create campaign : "+e.getMessage());
            }
     } else {
    	 throw new Exception("Genesys protocol is closed!");
     	 
     }
		return tableAccesstFromQ;
  }
	
	public void DeleteTableAccess(int tableAccessID) throws Exception {
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
                LOGGER.info("Exception Create campaign : "+e.getMessage());
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
			 LOGGER.info("############### BCA APPLICATIONS GENESYS-SDK");
			 int i = 1;
			 while (iListApps.hasNext()) {
				 CfgApplication cfgAppitem = iListApps.next();
				 System.out.print("Iterator ############## "+i+" : ");
				 LOGGER.info(cfgAppitem.getName()+"  COntent : "+cfgAppitem.toString());
				 
				 
//				 String prefixDAP =cfgAppitem.getName().substring(0, 3); 
//				 LOGGER.info("Application " + prefixDAP + ", Iterator : "+cfgAppitem.getAppServers().size());
				 
//				 if(prefixDAP.equalsIgnoreCase("DAP")) {
////					 LOGGER.info("Name : "+cfgAppitem.getName());
//					 KVList kvList = cfgAppitem.getUserProperties().getList("default");
//					 LOGGER.info(cfgAppitem.getDBID()+ "|"+ cfgAppitem.getName() + "|"+kvList.getAsString("dbname"));
//				 }
				 LOGGER.info("################################################### ");
				 i++;
			 }
			 LOGGER.info("############### END BCA APPLICATIONS GENESYS-SDK");
			 
		  } else {
		  	throw new Exception("Genesys protocol is closed!");
		  }// TODO
	}
	public String GetAllApplications(String DAPName) throws Exception {
		String result = null;
		LOGGER.info(confService.getMetaData().getClasses().toString());
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
//					 LOGGER.info(cfgAppitem.getName() + "|"+kvList.getAsString("dbname"));
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
	public void getAllPersons() throws Exception {
		if(protocol.getState() != null) {
			CfgPersonQuery cfgPrsQ = new CfgPersonQuery();
			
//			CfgCallingListQuery cfgAppQ = new CfgCallingListQuery();
        	Collection<CfgPerson> cfgPersons = confService.retrieveMultipleObjects(CfgPerson.class,cfgPrsQ);
			 Iterator<CfgPerson>iListPers = cfgPersons.iterator();
			 while (iListPers.hasNext()) {
				 CfgPerson cfgPersonItem = iListPers.next();
				 
				 LOGGER.info("Person ID :"+cfgPersonItem.getDBID() + "\nPerson Username : "+cfgPersonItem.getUserName());
				 LOGGER.info("##########################################");
			 }
		  } else {
		  	throw new Exception("Genesys protocol is closed!");
		  }// TODO
	}
	public Iterator<CfgPerson> getListPersons() throws Exception {
		if(protocol.getState() != null) {
			CfgPersonQuery cfgPrsQ = new CfgPersonQuery();
//			CfgCallingListQuery cfgAppQ = new CfgCallingListQuery();
        	Collection<CfgPerson> cfgPersons = confService.retrieveMultipleObjects(CfgPerson.class,cfgPrsQ);
			 Iterator<CfgPerson>iListPers = cfgPersons.iterator();
			 if(iListPers.hasNext()) {
				 return iListPers;
			 } else {
				 return null;
			 }
			 
		  } else {
		  	throw new Exception("Genesys protocol is closed!");
		  }// TODO
		
	}
	
	
	
	public void getAllPlaces() throws Exception {
		if(protocol.getState() != null) {
			 CfgPlaceQuery cfgPlaceQ = new CfgPlaceQuery();
			 Collection<CfgPlace> cfgPlaceList = confService.retrieveMultipleObjects(CfgPlace.class, cfgPlaceQ);
			 Iterator<CfgPlace>iListApps = cfgPlaceList.iterator();
			 while (iListApps.hasNext()) {
				 CfgPlace item = iListApps.next();
				 LOGGER.info("Iterator Place : "+item);
			 }
			 
		  } else {
		  	throw new Exception("Genesys protocol is closed!");
		  }// TODO
	}
	
	
	public CfgPerson CreateNewPerson(String Username, String ExternalID, String FirstName, String LastName, Boolean isAgent, String password, String EmailAddress, String EmployeeID) throws Exception {
		CfgPerson result = null;
		if(protocol.getState() != null) {
			try {
				result = new CfgPerson(confService);
					
				result.setUserName(Username);
				result.setExternalID(ExternalID);
				result.setFirstName(FirstName);
				result.setEmailAddress(EmailAddress);
				result.setLastName(LastName);
				result.setEmployeeID(EmployeeID);
//				cfgCampaign.setAddress(CfgAddress);
				if(isAgent == true) {
					result.setIsAgent(CfgFlag.CFGTrue);
				} else {
					result.setIsAgent(CfgFlag.CFGFalse);
				}
				
				result.setPassword(password);
//				cfgPerson.setPhones(value);
				result.setState(CfgObjectState.CFGEnabled);
				result.setTenantDBID(1);
				
//				result.set
//				CfgAccessGroup cfgAccGroup = GetAccessGroupByName(AccessGroupName);
//				result.setAccountPermissions(cfgAccGroup, 127);
				result.save();
				LOGGER.info("##### Person di save ####### : ");
			} catch (Exception e) {
				LOGGER.info("##### ERROR ####### : "+e.getMessage());
				throw new Exception(e.getMessage());
			}
            
		} else {
			mylog.log(Level.WARNING,"Error Genesys");
		  	throw new Exception("Genesys protocol is closed!");
		}
		
		return result;
	}
public CfgPerson updatePersonById(int id, String Username, String ExternalID, String FirstName, String LastName, Boolean isAgent, String password, String EmailAddress, String EmployeeID) throws Exception {
	CfgPerson result = null;
	if(protocol.getState() != null) {
		try {
			CfgPersonQuery resultQ = new CfgPersonQuery();
			result = confService.retrieveObject(CfgPerson.class, resultQ);
			result.setUserName(Username);
			result.setExternalID(ExternalID);
			result.setFirstName(FirstName);
			result.setEmailAddress(EmailAddress);
			result.setLastName(LastName);
			result.setEmployeeID(EmployeeID);
			if(isAgent == true) {
				result.setIsAgent(CfgFlag.CFGTrue);
			} else {
				result.setIsAgent(CfgFlag.CFGFalse);
			}
			result.setPassword(password);
//			result.setState(CfgObjectState.CFGEnabled);
//			result.setTenantDBID(1);
			result.save();
			LOGGER.info("##### Person di save ####### : ");
		} catch (Exception e) {
			LOGGER.info("##### ERROR ####### : "+e.getMessage());
			throw new Exception(e.getMessage());
		}
        
	} else {
		mylog.log(Level.WARNING,"Error Genesys");
	  	throw new Exception("Genesys protocol is closed!");
	}
	
	return result;
}

public String deletePersonById(int id) throws Exception {
	CfgPerson result = null;
	if(protocol.getState() != null) {
		try {
			CfgPersonQuery resultQ = new CfgPersonQuery();
			resultQ.setDbid(id);
			result = confService.retrieveObject(CfgPerson.class, resultQ);
			result.delete();
		} catch (Exception e) {
			throw new Exception(e.getMessage());
		}
        
	} else {
		mylog.log(Level.WARNING,"Error Genesys");
	  	throw new Exception("Genesys protocol is closed!");
	}
	
	return null;
	
}


	public CfgPerson getPersonByIdUpdateAccessGroup(int dbid, String AccessGroupName) throws Exception {
	CfgPerson personObj = null;
	if(protocol.getState() != null) {
		 CfgPersonQuery cfgPersonQ = new CfgPersonQuery();
		 cfgPersonQ.setDbid(dbid);
		 personObj = confService.retrieveObject(CfgPerson.class, cfgPersonQ);
//		 CfgAccessGroup cfgAccGroup = GetAccessGroupByName(AccessGroupName);
////		 LOGGER.info("########### "+ cfgAccGroup.getMemberIDs().toString());
//		 personObj.setAccountPermissions(cfgAccGroup, 127);
//		 personObj.save();
		CfgID cID = new CfgID(confService, personObj);
		cID.setDBID(personObj.getDBID());
		cID.setType(personObj.getObjectType());
		CfgAccessGroup cfgAccGroup = GetAccessGroupByName(AccessGroupName);
		cfgAccGroup.getMemberIDs().add(cID);
		cfgAccGroup.save();
	  } else {
	  	throw new Exception("Genesys protocol is closed!");
	  }
	return personObj;
}


public CfgPerson getPersonsById(int idPerson) throws Exception {
	CfgPerson cfgPerson = null;
	if(protocol.getState() != null) {
		CfgPersonQuery cfgPersonQ = new CfgPersonQuery();
		cfgPersonQ.setDbid(idPerson);
		 cfgPerson = confService.retrieveObject(CfgPerson.class, cfgPersonQ);
	  } else {
	  	throw new Exception("Genesys protocol is closed!");
	  }
	return cfgPerson;
}


public CfgPerson getPersonsByUserName(String name) throws Exception {
	CfgPerson cfgPerson = null;
	if(protocol.getState() != null) {
		CfgPersonQuery cfgPersonQ = new CfgPersonQuery();
		cfgPersonQ.setUserName(name);
		 cfgPerson = confService.retrieveObject(CfgPerson.class, cfgPersonQ);
	  } else {
	  	throw new Exception("Genesys protocol is closed!");
	  }
	return cfgPerson;
}

public CfgPerson CreateNewPersonSkill(String userName, String skillName) throws Exception {
	CfgPerson result = null;
	if(protocol.getState() != null) {
		CfgPersonQuery cfgPersonQ = new CfgPersonQuery();
		cfgPersonQ.setUserName(userName);
		result = confService.retrieveObject(CfgPerson.class, cfgPersonQ);
		
		CfgSkillQuery skillQ = new CfgSkillQuery();
		skillQ.setName(skillName);
		CfgSkill skillFromQuery = confService.retrieveObject(CfgSkill.class, skillQ);
		int skillLevel = new Random().nextInt(10);
        Boolean found = false;
        for (CfgSkillLevel csl  : result.getAgentInfo().getSkillLevels()) {
        	 if (csl.getSkill().getName().equalsIgnoreCase(skillQ.getName()))
             {
                 csl.setLevel(skillLevel);
                 found = true;
                 break;
             }
		}
        if (!found)
        {
            CfgSkillLevel cfgSkillLevel = new CfgSkillLevel(confService, result);
            cfgSkillLevel.setSkill(skillFromQuery);
            cfgSkillLevel.setLevel(skillLevel);
            result.getAgentInfo().getSkillLevels().add(cfgSkillLevel);
        }
        result.save();
		
	  } else {
	  	throw new Exception("Genesys protocol is closed!");
	  }
	return result;
}

	public CfgRole addUserToRoleByRoleName(CfgPerson person, String roleName, String AccessGroupName) throws Exception {
		CfgRole result = null;
		if(protocol.getState() != null) {
			try {
				result = new CfgRole(confService);
				CfgRoleQuery cfgRoleQ = new CfgRoleQuery();
				cfgRoleQ.setName(roleName);
				result = confService.retrieveObject(CfgRole.class, cfgRoleQ);	
				CfgRoleMember roleMember = new CfgRoleMember(confService, result);
				roleMember.setObjectDBID(person.getDBID());
				roleMember.setObjectType(person.getObjectType());
				result.getMembers().add(roleMember);
				result.save();
			} catch (Exception e) {
				LOGGER.info("##### ERROR ####### : "+e.getMessage());
				throw new Exception(e.getMessage());
			}
            
		} else {
			mylog.log(Level.WARNING,"Error Genesys");
		  	throw new Exception("Genesys protocol is closed!");
		}
		return result;
}

public CfgRole removeUserFromRoleByRoleName(CfgPerson person, String roleName, String AccessGroupName) throws Exception {
	CfgRole result = null;
	if(protocol.getState() != null) {
				
		try {
			result = new CfgRole(confService);
			CfgRoleQuery cfgRoleQ = new CfgRoleQuery();
			cfgRoleQ.setName(roleName);
			result = confService.retrieveObject(CfgRole.class, cfgRoleQ);
			Collection<CfgRoleMember> listROleMemberOld = result.getMembers();
			List<CfgRoleMember> listROleMemberNew = new ArrayList<CfgRoleMember>();
			for (CfgRoleMember cfgRoleMember : listROleMemberOld) {
				if(cfgRoleMember.getObjectType().equals(CfgPerson.OBJECT_TYPE)) {
					if(cfgRoleMember.getObjectDBID() != person.getDBID()) {
						listROleMemberNew.add(cfgRoleMember);
					} else {
						LOGGER.info("########## ID yang sama di delete "+cfgRoleMember.getObjectDBID());
					}
				} else {
					listROleMemberNew.add(cfgRoleMember);
				}
			}
			result.getMembers().removeAll(listROleMemberOld);
			LOGGER.info("########### Teke Out User Role ######## Clear User");
			result.setMembers(listROleMemberNew);
			result.save();
		} catch (Exception e) {
			LOGGER.info("##### ERROR ####### : "+e.getMessage());
			throw new Exception(e.getMessage());
		}
        
	} else {
		mylog.log(Level.WARNING,"Error Genesys");
	  	throw new Exception("Genesys protocol is closed!");
	}
	return result;
}

public CfgAgentLogin addUserToAgentLoginByName(CfgPerson person, String AgentLoginName) throws Exception {
	CfgAgentLogin result = null;
	
	  if(protocol.getState() != null) {
		 CfgAgentLoginQuery cfgAgentLoginQ = new CfgAgentLoginQuery();
		 
		
		 
		 
//		cfgAgentLoginQ.getProperty("group")
		
	  } else {
	  	throw new Exception("Genesys protocol is closed!");
	  }
	
	
	return result;
}


public CfgAgentGroup addUserToAgentGroupByName(CfgPerson person, String AgentGroupName) throws Exception {
	CfgAgentGroup result = null;
	
	  if(protocol.getState() != null) {
		  CfgAgentGroupQuery cfgAgentGroupQ = new CfgAgentGroupQuery();
		  cfgAgentGroupQ.setName(AgentGroupName);
		  CfgAgentGroup cfgAgentGLogin = confService.retrieveObject(CfgAgentGroup.class, cfgAgentGroupQ);
		  Collection<CfgPerson> agentPerson = cfgAgentGLogin.getAgents();
		  agentPerson.add(person);
		  cfgAgentGLogin.setAgents(agentPerson);
		  cfgAgentGLogin.save();
		
	  } else {
	  	throw new Exception("Genesys protocol is closed!");
	  }
	
	
	return result;
}

public CfgAgentInfo getAgentInfofromPersons(int idPerson) throws Exception {
	CfgAgentInfo cfgAgentInfo = null;
	CfgPerson cfgPerson = null;
	
	if(protocol.getState() != null) {
//		CfgAgentInfo cfgTableAccessQ = new CfgPersonQuery();
		CfgPersonQuery cfgTableAccessQ = new CfgPersonQuery();
		 cfgTableAccessQ.setDbid(idPerson);
		 cfgPerson = confService.retrieveObject(CfgPerson.class, cfgTableAccessQ);
		 cfgAgentInfo = cfgPerson.getAgentInfo();
		 
//		 cfgAgentInfo.setSkillLevels(value);
		 
		 
//		 cfgAgentInfo.setSkillLevels);
		 
				 
//		 cfgTableAccessQ.setDbid(idPerson);
//		 cfgPerson = confService.retrieveObject(CfgPerson.class, cfgTableAccessQ);
	  } else {
	  	throw new Exception("Genesys protocol is closed!");
	  }
	return cfgAgentInfo;
}


public List<CfgAgentLogin> getAllAgentLogin() throws Exception {
	List<CfgAgentLogin> result = new ArrayList<CfgAgentLogin>();
	if(protocol.getState() != null) {
		CfgAgentLoginQuery cfgCampaignQ = new CfgAgentLoginQuery();
		 Collection<CfgAgentLogin> cfgTableAccessList = confService.retrieveMultipleObjects(CfgAgentLogin.class, cfgCampaignQ);
		 Iterator<CfgAgentLogin>iListApps = cfgTableAccessList.iterator();
		 while (iListApps.hasNext()) {
			 CfgAgentLogin item = iListApps.next();
			 LOGGER.info("Login Code : "+item.getLoginCode());
			 LOGGER.info("Meta Name : "+item.toString()+"\n########################################");
		 }
		 
	  } else {
	  	throw new Exception("Genesys protocol is closed!");
	  }
	return result;
}




public CfgSkill addUserToSkillBySkillName(String SkillName, CfgPerson cfgPerson) throws Exception {
	CfgSkill result = null;
	if(protocol.getState() != null) {
		try {
			result = new CfgSkill(confService);
			CfgSkillQuery cfgSkillQ = new CfgSkillQuery();
			cfgSkillQ.setName(SkillName);
			result = confService.retrieveObject(CfgSkill.class, cfgSkillQ);	
			
//			result.set
			CfgAgentInfo agentInfo  = getAgentInfofromPersons(cfgPerson.getDBID());
//			CfgAccessGroup cfgAccGroup = GetAccessGroupByName(AccessGroupName);
//			result.set
			result.setAccountPermissions(cfgPerson, 127);
			result.save();
//			re
		} catch (Exception e) {
			LOGGER.info("##### ERROR ####### : "+e.getMessage());
			throw new Exception(e.getMessage());
		}
        
	} else {
		mylog.log(Level.WARNING,"Error Genesys");
	  	throw new Exception("Genesys protocol is closed!");
	}
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
	public CfgRole removeUserFromRoleByName(CfgPerson person, String roleName) throws Exception {
		CfgRole result = null;
		if(protocol.getState() != null) {
			try {
				result = new CfgRole(confService);
				CfgRoleQuery cfgRoleQ = new CfgRoleQuery();
				cfgRoleQ.setName(roleName);
				result = confService.retrieveObject(CfgRole.class, cfgRoleQ);	
				Collection<CfgRoleMember> roleMembersOld = (List<CfgRoleMember>) result.getMembers();
				Collection<CfgRoleMember> roleMembersNew = new ArrayList<CfgRoleMember>();
				LOGGER.info(">>>> AWAL : "+roleMembersOld.size());
				for (CfgRoleMember cfgRoleMember : roleMembersOld) {
					if(cfgRoleMember.getObjectType() == CfgObjectType.CFGPerson) {
						Integer rolememberId = cfgRoleMember.getObjectDBID();
						Integer personId = person.getDBID();
						if(rolememberId.equals(personId)) {
							LOGGER.info(">>>> Delete ID : "+rolememberId);
						}else {
							roleMembersNew.add(cfgRoleMember);
						}
					} else {
						roleMembersNew.add(cfgRoleMember);
					}
				}
				LOGGER.info(">>>> AKHIR DR AWAL : "+roleMembersOld.size()+ " Current : "+roleMembersNew.size());
				result.getMembers().removeAll(roleMembersOld);
				Thread.sleep(1000);
				result.setMembers(roleMembersNew);
				result.save();
			} catch (Exception e) {
				LOGGER.info("##### ERROR ####### : "+e.getMessage());
				throw new Exception(e.getMessage());
			}
            
		} else {
			mylog.log(Level.WARNING,"Error Genesys");
		  	throw new Exception("Genesys protocol is closed!");
		}
		return result;
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
