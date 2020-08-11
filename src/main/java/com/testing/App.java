package com.testing;

import java.awt.Event;
import java.beans.EventHandler;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collection;
import java.util.EventObject;
import java.util.Iterator;

import com.genesyslab.platform.applicationblocks.com.ConfServiceFactory;
import com.genesyslab.platform.applicationblocks.com.ICfgObject;
import com.genesyslab.platform.applicationblocks.com.IConfService;
import com.genesyslab.platform.applicationblocks.com.Subscription;
import com.genesyslab.platform.applicationblocks.com.objects.CfgApplication;
import com.genesyslab.platform.applicationblocks.com.objects.CfgCampaign;
import com.genesyslab.platform.applicationblocks.com.objects.CfgConnInfo;
import com.genesyslab.platform.applicationblocks.com.objects.CfgField;
import com.genesyslab.platform.applicationblocks.com.objects.CfgFormat;
import com.genesyslab.platform.applicationblocks.com.objects.CfgTableAccess;
import com.genesyslab.platform.applicationblocks.com.objects.CfgTenant;
import com.genesyslab.platform.applicationblocks.com.queries.CfgApplicationQuery;
import com.genesyslab.platform.applicationblocks.com.queries.CfgCampaignQuery;
import com.genesyslab.platform.applicationblocks.com.queries.CfgFieldQuery;
import com.genesyslab.platform.applicationblocks.warmstandby.WarmStandbyConfiguration;
import com.genesyslab.platform.applicationblocks.warmstandby.WarmStandbyService;
import com.genesyslab.platform.commons.connection.configuration.ClientADDPOptions.AddpTraceMode;
import com.genesyslab.platform.commons.connection.configuration.PropertyConfiguration;
import com.genesyslab.platform.commons.protocol.Endpoint;
import com.genesyslab.platform.commons.protocol.ProtocolException;
import com.genesyslab.platform.commons.protocol.RegistrationException;
import com.genesyslab.platform.commons.threading.CompletionHandler;
import com.genesyslab.platform.configuration.protocol.ConfServerProtocol;
import com.genesyslab.platform.configuration.protocol.types.CfgAppType;
import com.genesyslab.platform.configuration.protocol.types.CfgObjectState;
import com.genesyslab.platform.configuration.protocol.types.CfgObjectType;
import com.genesyslab.platform.configuration.protocol.types.CfgTableType;
import com.genesyslab.platform.webmedia.protocol.email.Attachment;

/**
 * Hello world!
 *
 */
public class App 
{
	private static String HOST="172.17.20.23";
	private static int PORT=2020;
	private static String BACKUP_HOST=HOST;
	private static int BACKUP_PORT=PORT;
	private static String CLIENT_NAME="AgentDesktop_Sample";
	private static String USER_NAME="default";
	private static String PASSWORD="password";
	private static int SKILL_LEVEL = 5;
	private static int TENANT_ID = 1;
	
	private static ConfServerProtocol protocol;
    private static WarmStandbyService  warmStandbyService;
    private static IConfService confService;

    private static Subscription subscriptionForAll;
    private static Subscription subscriptionForObject;
	
    public static void main( String[] args )
    {
    	App thisApp = new App();
    	try {
			InitializePSDKProtocolAndAppBlocks();
			thisApp.ConnectProtocol();
			if(protocol.getState() != null) {
	        	System.out.println(protocol.getState());
	        	System.out.println("###########################################");
//	            thisApp.ReadObjects();
//	        	thisApp.ReadApplication();
	        	thisApp.GetAllFileds();
	            Thread.sleep(100);
	            
	            
	            
	            
//	            thisApp.CreateCampaign(thisApp, "New Camp Testing 02");
//	            thisApp.deleteCampaign(thisApp, "New Camp Testing 01");
//	            thisApp.UpdateCampaign(thisApp, "New Camp Testing 02", "New Camp Testing 12");
	            System.out.println("###########################################");
	            thisApp.disConnectProtocl();
		     } else {
		     	System.out.println("INI NGGA KONEK");
		     }
			
		} catch (RegistrationException e) {
			System.out.println("RegistrationException : "+e.getMessage());
		} catch (ProtocolException e) {
			// TODO Auto-generated catch block
			System.out.println("ProtocolException : "+e.getMessage());
		} catch (IllegalStateException e) {
			// TODO Auto-generated catch block
			System.out.println("IllegalStateException : "+e.getMessage());
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			System.out.println("InterruptedException : "+e.getMessage());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    
    }

	private static void InitializePSDKProtocolAndAppBlocks() throws RegistrationException, ProtocolException, IllegalStateException, InterruptedException {
		// TODO Auto-generated method stub
		PropertyConfiguration config = new PropertyConfiguration();
        config.setUseAddp(true);
        config.setAddpServerTimeout(20);
        config.setAddpClientTimeout(10);
        config.setAddpTraceMode(AddpTraceMode.Both);
        
        Endpoint endpoint = new Endpoint(HOST, PORT, config);
        Endpoint backupEndpoint = new Endpoint(BACKUP_HOST, BACKUP_PORT, config);
        protocol = new ConfServerProtocol(endpoint);
        protocol.setClientName(CLIENT_NAME);
        protocol.setUserName(USER_NAME);
        protocol.setUserPassword(PASSWORD);
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
	 public void ConnectProtocol() throws RegistrationException, ProtocolException, IllegalStateException, InterruptedException {
		 protocol.open();
	 }
	 public void disConnectProtocl() throws ProtocolException, IllegalStateException, InterruptedException {
		 protocol.close();
	     Thread.sleep(500);
	     System.out.println(protocol.getState());
	     System.exit(-1);
	 }
//############################################ Campaign Property ################################
	 public void CreateCampaign(App mainApp, String CampName) throws RegistrationException, ProtocolException, IllegalStateException, InterruptedException {
		 if(protocol.getState() != null) {
	            try
	            {
	                CfgCampaign campaign = new CfgCampaign(confService);
	                campaign.setName(CampName);
	                campaign.setTenantDBID(1);
	                campaign.save();
	            }
	            catch (Exception e)
	            {
	                System.out.println("Exception Create campaign : "+e.getMessage());
	            }
	     } else {
	     	System.out.println("State not connected, Reconnecting ");
	     	mainApp.ConnectProtocol();
	     }
	 }
	 public void deleteCampaign(App mainApp, String CampName) throws RegistrationException, ProtocolException, IllegalStateException, InterruptedException {
		 if(protocol.getState() != null) {
	            try
	            {
	                CfgCampaignQuery campaignQ = new CfgCampaignQuery();
	                campaignQ.setName(CampName);
	                ICfgObject objectToDelete = confService.retrieveObject(campaignQ);
	                objectToDelete.delete();
	                System.out.println("Campaign: " + CampName + " deleted");
	                		
	            }
	            catch (Exception e)
	            {
	                System.out.println("Exception Delete campaign : "+e.getMessage());
	            }
	     } else {
	     	System.out.println("State not connected, Reconnecting ");
	     	mainApp.ConnectProtocol();
	     }
	 }
	 public void UpdateCampaign(App mainApp, String oldCampName, String newCampName) throws RegistrationException, ProtocolException, IllegalStateException, InterruptedException {
		 if(protocol.getState() != null) {
	            try
	            {
	            	CfgCampaignQuery campaignQ = new CfgCampaignQuery();
	            	campaignQ.setName(oldCampName);
	            	CfgCampaign campaignFromQ = confService.retrieveObject(campaignQ);
	            	campaignFromQ.setName(newCampName);
	                campaignFromQ.save();
	            }
	            catch (Exception e)
	            {
	                System.out.println("Exception Create campaign : "+e.getMessage());
	            }
	     } else {
	     	System.out.println("State not connected, Reconnecting ");
	     	mainApp.ConnectProtocol();
	     }
	 }
//############################################ End Campaign Property ################################

//############################################ Table Access Property ################################
	 public void CreateTableAccess(App mainApp, String name, String formatName, String description) throws RegistrationException, ProtocolException, IllegalStateException, InterruptedException {
		 if(protocol.getState() != null) {
	            try
	            {
	            	CfgTableAccess cfgTableAccess = new CfgTableAccess(confService);
	            	cfgTableAccess.setName(name);
	            	cfgTableAccess.setDescription(name);
	            	cfgTableAccess.setTenantDBID(1);
	            	CfgTableType tableType = CfgTableType.CFGTTCallingList;
	            	cfgTableAccess.setType(tableType);
	            	cfgTableAccess.setDbAccessDBID(1);
	            	
	            }
	            catch (Exception e)
	            {
	                System.out.println("Exception Create Table Access : "+e.getMessage());
	            }
	     } else {
	     	System.out.println("State not connected, Reconnecting ");
	     	mainApp.ConnectProtocol();
	     }
	 }
//############################################ End Table Access Property ################################

//############################################ Table Format Property ################################
	 
	 public void CreateFormat(App mainApp, String name, String description, Collection<Integer> filedsIds) throws RegistrationException, ProtocolException, IllegalStateException, InterruptedException {
		 if(protocol.getState() != null) {
	            try
	            {
//	            	CfgTableAccess cfgTableAccess = new CfgTableAccess(confService);
//	            	cfgTableAccess.setName(name);
//	            	cfgTableAccess.setDescription(name);
//	            	cfgTableAccess.setTenantDBID(1);
//	            	CfgTableType tableType = CfgTableType.CFGTTCallingList;
//	            	cfgTableAccess.setType(tableType);
//	            	cfgTableAccess.setDbAccessDBID(1);
	            	CfgFormat cfgFormat = new CfgFormat(confService);
	            	
	            	cfgFormat.setName(name);
	            	cfgFormat.setDescription(description);
	            	cfgFormat.setTenantDBID(1);
	            	cfgFormat.setFieldDBIDs(filedsIds);
//	            	CfgObjectState objState = new CfgObject
//	            	cfgFormat.setState(value);
	            	
	            	
	            	
	            	
	            }
	            catch (Exception e)
	            {
	                System.out.println("Exception Create Table Access : "+e.getMessage());
	            }
	     } else {
	     	System.out.println("State not connected, Reconnecting ");
	     	mainApp.ConnectProtocol();
	     }
	 }
	 
//############################################ End Table Format Property ################################	 
	 
	 
	 public void GetAllFileds() throws Exception {
		 if(protocol.getState() != null) {
			 CfgFieldQuery cfgFieldQ = new CfgFieldQuery();
			 Collection<CfgField> cfgFieldList = confService.retrieveMultipleObjects(CfgField.class, cfgFieldQ);
	           int i =0;
			 //System.out.println("Jumlah Fields : "+ cfgFieldList.size());
			 Iterator<CfgField>iListApps = cfgFieldList.iterator();
           	  while (iListApps.hasNext()) {
	       	  System.out.println("Iterator "+i+" : "+iListApps.next().toString());
	   				i++;
			  }
			 
		  } else {
		  	throw new Exception("Genesys protocol is closed!");
		  }
	}
	 
	 
	 
	 public void ReadObjects()
     {
		 if(protocol.getState() != null) {
	        	System.out.println(protocol.getState());
	            try
	            {
	                Collection<CfgObjectType> cfgObjectTypes = CfgObjectType.values();
	                Iterator<CfgObjectType> cfgIterator = cfgObjectTypes.iterator();
	                int i =0;
	                while (cfgIterator.hasNext()) {
		   				System.out.println("Iterator "+i+" : "+cfgIterator.next().toString());
		   				i++;
	                }
	            }
	            catch (Exception e)
	            {
	                System.out.println("Exception Read Object : "+e.getMessage());
	            }
        } else {
        	System.out.println("INI NGGA KONEK");
        }
     }
	 
	 public void ReadApplications() {
		 if(protocol.getState() != null) {
//	        	System.out.println(protocol.getState());
	            try
	            {
	            	int i =0;
	            	CfgApplicationQuery cfgAppQ = new CfgApplicationQuery();
	            	Collection<CfgApplication> cfgAppNew = confService.retrieveMultipleObjects(CfgApplication.class,cfgAppQ);
//	            	CfgCampaignQuery campaignQ = new CfgCampaignQuery();
//	            	campaignQ.setName(oldCampName);
//	            	CfgCampaign campaignFromQ = confService.retrieveObject(campaignQ);
//	            	campaignFromQ.setName(newCampName);
//	                campaignFromQ.save();

//	              Collection<CfgConnInfo>listApps =cfgAppNew.getAppServers();
//	              System.out.println("Jumlah Conn : "+ cfgAppNew.size());
//	              	Iterator<CfgConnInfo>iListApps = listApps.iterator();
//	              	while (iListApps.hasNext()) {
//	              		System.out.println("Iterator "+i+" : "+iListApps.next().toString());
//		   				i++;
//					}
//	            	System.out.println("DBID "+cfgApp.get));
	              
	              
	            }
	            catch (Exception e)
	            {
	                System.out.println("Exception Read Object : "+e.getMessage());
	            }
     } else {
     	System.out.println("INI NGGA KONEK");
     }
	 }
  

}
