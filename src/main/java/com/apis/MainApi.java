package com.apis;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import com.apis.controller.Genesys;
import com.apis.objects.ObjCfgFields;
import com.genesyslab.platform.applicationblocks.com.IConfService;
import com.genesyslab.platform.applicationblocks.com.Subscription;
import com.genesyslab.platform.applicationblocks.com.objects.CfgAccessGroup;
import com.genesyslab.platform.applicationblocks.com.objects.CfgAgentGroup;
import com.genesyslab.platform.applicationblocks.com.objects.CfgAgentLogin;
import com.genesyslab.platform.applicationblocks.com.objects.CfgCallingList;
import com.genesyslab.platform.applicationblocks.com.objects.CfgCallingListInfo;
import com.genesyslab.platform.applicationblocks.com.objects.CfgCampaign;
import com.genesyslab.platform.applicationblocks.com.objects.CfgFormat;
import com.genesyslab.platform.applicationblocks.com.objects.CfgID;
import com.genesyslab.platform.applicationblocks.com.objects.CfgPerson;
import com.genesyslab.platform.applicationblocks.com.objects.CfgRole;
import com.genesyslab.platform.applicationblocks.com.objects.CfgTableAccess;
import com.genesyslab.platform.applicationblocks.warmstandby.WarmStandbyService;
import com.genesyslab.platform.commons.protocol.ProtocolException;
import com.genesyslab.platform.commons.protocol.RegistrationException;
import com.genesyslab.platform.configuration.protocol.ConfServerProtocol;
import com.testing.App;

public class MainApi {
//	static String HOST="172.17.20.23";
	static String HOST="192.168.255.130";
	static int PORT=2020;
	static String BACKUP_HOST=HOST;
	static int BACKUP_PORT=PORT;
	static String CLIENT_NAME="AgentDesktop_Sample";
	static String USER_NAME="default";
//	static String USER_NAME="billie.eilish";
//	static String PASSWORD="";
	static String PASSWORD="password";
	static int SKILL_LEVEL = 5;
	static int TENANT_ID = 1;
	
	static ConfServerProtocol protocol;
    static WarmStandbyService  warmStandbyService;
    static IConfService confService;

    static Subscription subscriptionForAll;
    static Subscription subscriptionForObject;
	public static void main(String[] args) {
		
//################################### CONTOH PAKAI ###########################################
		Genesys apiGen = new Genesys(HOST, PORT, BACKUP_HOST, BACKUP_PORT, CLIENT_NAME, USER_NAME, 
				PASSWORD, SKILL_LEVEL, TENANT_ID,"Bowo");
		try {
			apiGen.InitializePSDKProtocolAndAppBlocks(apiGen);
			protocol=  apiGen.ConnectProtocol();
			if(protocol.getState() != null) {
	        	System.out.println(protocol.getState());
	        	System.out.println("###########################################");
	        	
//	        	System.out.println("########## Get All Fields #################");
//	        	ArrayList<ObjCfgFields> lCfgFields = apiGen.GetAllFileds();
//	        	for (ObjCfgFields objCfgFields : lCfgFields) {
//					System.out.println("id : "+objCfgFields.getDbID()+", name : "+ objCfgFields.getName()+"
//	        	("+objCfgFields.getType()+")");
//				}
	        	
//	        	System.out.println("########## Create New Format #################");
//	        	List<Integer> FiledsChoosed = new ArrayList<Integer>();
//	        	FiledsChoosed.add(241);FiledsChoosed.add(359);FiledsChoosed.add(145);FiledsChoosed.add(261);
//	        	Collection<Integer> filedsChoosed = FiledsChoosed;
//	        	apiGen.CreateNewFormat(apiGen, "INS_AIA_8989.Format", "Testings", filedsChoosed);
//	        	Thread.sleep(50);
//	        	apiGen.GetAllFormat();
	        	
//	        	System.out.println("########## Delete Format #################");
//	        	apiGen.DeleteFormat(155);
//	        	apiGen.GetAllFormat();
	        	
//	        	apiGen.GetAllRoles();
	        	
//	        	System.out.println("########## Create TableAccess #################");
////	        	156 Format
////	        	175 
//	        	apiGen.CreateNewTableAccess(apiGen, "TA_INS_AIA_01", "Testing", 175, 156, "TA_INS_AIA_01_TB",false);
//	        	apiGen.GetAllTableAccess();
////	        	
//	        	System.out.println("############### GET FIELD BY FORMAT ID ##################");
//	        	ArrayList<ObjCfgFields>objFieldBy = apiGen.GetAllFiledsByFormatID(156);
	        	
//	        	System.out.println("DB Name : "+apiGen.GetAllApplications("dap_ocs")); 
//	        	apiGen.GetAllApplications();
//	        	System.out.println(apiGen.GetAccessGroupByName("INS_Cigna").getMemberIDs().toString());
//	        	apiGen.GetPersons();
	        	
	        	
//################################################## INI BARU FORMAT######################################	        	
//	        	List<Integer> FiledsChoosed = new ArrayList<Integer>();
//	        	FiledsChoosed.add(102);FiledsChoosed.add(113);FiledsChoosed.add(114);FiledsChoosed.add(126);
//	        	Collection<Integer> filedsChoosed = FiledsChoosed;
//	        	CfgFormat resultFormat = apiGen.CreateNewFormat(apiGen, "INS_CGN_9091.Format", "Testings", filedsChoosed, "INS_Cigna");
//	        	System.out.println(resultFormat.toString());
	       
//################################################## INI BARU TABLE ACCESS ##########################	        	
//	        	Format id 106-107
//	        	DB Access ID 119 (Kalo di gua)
	        	
	        	
//	        	apiGen.getAllSkill();
	        	
	        	
//	        	CfgTableAccess result = apiGen.CreateNewTableAccess(apiGen, "INS_CGN_9091.TA", "Testing", 119, 106,
//	        			"INS_CGN_9091.TA_TB",false,"INS_Cigna");
//	        	System.out.println(result.toString());
	        	
	        	
//	        	CfgFormat myFormat = apiGen.getFormatByNameUpdateAccessGroup("INS_CGN_9090.Format", "INS_Cigna");
	        	
	        	
	        	
//	        	ArrayList<ObjCfgFields> listFild = apiGen.GetAllFileds();
//	        	apiGen.GetAllCallingList();
//	        	Kartu Kredit Visa 230320
//	        	apiGen.CreateNewCampaign(apiGen, "Campign Test 1", "Test Doank", "Kartu Kredit Visa 230320", "INS_Cigna");
//	        	CfgCampaign hasil= apiGen.UpdateCfgCampaign(apiGen, 0, "Campaign Cigna 280320", "Deskripsi Oncom Bandung", "Campaign Cigna 280320", "AccessGroup Kosong");
//	        	apiGen.GetAllCampaign();
	        	
//	        	Collection<Integer> filedsIds = Arrays.asList(121);
//	        	
//	        	CfgFormat result = apiGen.UpdateFormat(apiGen, 102, "Default_DoNotContact_List", "Bareng Dirga", filedsIds, "");
//	        	System.out.println("###########################################");
//	        	apiGen.getAllFormat();
//	        	apiGen.get
//	        	System.out.println("########################################### "+ result.getDescription());
//	        	CfgPerson result=  apiGen.CreateNewPerson("Memet2.Gorbacep", "mmt2@gmail.com", "Memet1", "Asep", true, "manage","mmt2@gmail.com", "100098870");
//	        	apiGen.getAllPersons(); 
//	        	135
	        	try {
	        		
//	        		apiGen.GetAccessGroup();
//	        		apiGen.GetFolders();
//	        		List<CfgAccessGroup> result = apiGen.GetAccessGroupByPrefix("INS");
//	        		for (CfgAccessGroup cfgAccessGroup : result) {
//	        			System.out.println(">>> : "+cfgAccessGroup.getGroupInfo().toString());
//					}
//	        		List<CfgPerson> result = apiGen.GetAccessGroupMembersByAccessGroupName("INS_AIA");
//	        		for (CfgPerson cfgPerson : result) {
//						System.out.println(">>>>>>>> "+cfgPerson.getUserName());
//					}
	        		
//	        		CfgAccessGroup result = apiGen.addAccessGroup("CCC_01");
//	        		
//	        	CfgPerson result = apiGen.GetPerson(111);
//	        	System.out.println("############################### "+ result.getUserName());
//	        	CfgAccessGroup AccessGroup = apiGen.delUserToAccessGroupByName("KKK",result);
//	        	CfgAgentGroup AgentGroup = apiGen.delUserToAgentGroupByName("CC", result);
//	        	System.out.println("################################# \n"+AccessGroup);
//	    	    System.out.println("################################# \n"+AgentGroup);
	        	
	        	
//	        	CfgRole resultRole = apiGen.removeUserFromRoleByName(result, "Manager");
	        	
	        	
//	        	CfgRole resultRole = apiGen.addUserToRoleByRoleName(result, "Supervisor", ""); //Karena sudah di lakukan sebelumnya harusnya
	        	
//	        	CfgPerson result3 = apiGen.CreateNewPersonSkill(result.getUserName(), "[Nama Skill]");//Skill tanya2 om Husain
//	        	
//	        	System.out.println("############################### "+ result.getDBID());
	        	
//	        		CfgPerson result2 = apiGen.getPersonByIdUpdateAccessGroup(result.getDBID(), "INS_AIA");
	        	
	        	
//	        		CfgAccessGroup accGrp = apiGen.GetAccessGroupByName("INS_AIA");
	        		
//	        		CfgID dbidObj = new Cfg;
	        		
//	        		accGrp.getMemberIDs().add(result.getDBID());
	        	
//	        		List<CfgAgentLogin> result = apiGen.getAllAgentLogin();
	        	
//	        		System.out.println("############################### "+ result2.getDBID());
	        	
//	        		CfgAgentGroup resultAG = apiGen.addUserToAgentGroupByName(result,"INS");
//	        		System.out.println("############################### "+ resultAG.toString());
	        	
//	        		Supervisor
//	        		CfgRole removeUserFromRole = apiGen.removeUserFromRoleByRoleName(result, "Supervisor", ""); 
//	        		System.out.println("############################### "+ removeUserFromRole.toString());
	        	
//	        		Thread.sleep(2000);
	        	
//		        	apiGen.GetAllRoles();
//		        	apiGen.GetAllAgentGroup();
//	        		Name Insurance Cigna 230320
	        		
//	        		apiGen.UpdateCallingList(apiGen, 102, "Name Insurance Cigna 230320", "Oncom", null, "11:30:00", "17:20:00", null);
//	        		List<CfgPerson> results =
//	        		apiGen.GetAllCallingList();
//	        		apiGen.updateAccessGroupToAgentGroup("INS", "INS_Cigna");
//	        		CfgAgentGroup result = apiGen.dellAccessGroupFromAgentGroup("INS", "INS_Cigna");
//	        		System.out.println("#################### done "+result.getGroupInfo().getName());
				} catch (Exception e) {
					// TODO: handle exception
					System.out.println("Error "+ e.getMessage());
				}
	        	
	        	
	        	
	        	

	        	
	            apiGen.disConnectProtocl(protocol);
		     } else {
		     	System.out.println("INI NGGA KONEK");
		     }
			System.exit(-1);
		} catch (RegistrationException e) {
			System.out.println("RegistrationException : "+e.getMessage());
		} catch (ProtocolException e) {
			System.out.println("ProtocolException : "+e.getMessage());
		} catch (IllegalStateException e) {
			System.out.println("IllegalStateException : "+e.getMessage());
		} catch (InterruptedException e) {
			System.out.println("InterruptedException : "+e.getMessage());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
}
