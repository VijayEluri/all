package com.emc.jason;
import com.documentum.fc.client.IDfACL;
import com.documentum.fc.client.IDfFolder;
import com.documentum.fc.client.IDfGroup;
import com.documentum.fc.client.IDfPermit;
import com.documentum.fc.client.IDfSession;
import com.documentum.fc.common.DfException;
import com.documentum.fc.common.IDfList;


public class ProjectInitializer {

	private static String PROJECT_NAME;
	private static String PROJECT_NAME_EN;

	private static String[] GROUP_NAMES = {
		"members", 
		"onsite-owner", 
		"owner", 
		"rep", 
		"managers", 
		"compliance"
	};

	private static String[] GROUP_DESCS = {
		"项目组成员", 
		"项目现场负责人", 
		"项目负责人（主半办人）", 
		"保荐代表人", 
		"行业组和投行委领导", 
		"内核风控"
	};

	private static int[] GROUP_PERMS = {
		IDfACL.DF_PERMIT_WRITE, 
		IDfACL.DF_PERMIT_WRITE, 
		IDfACL.DF_PERMIT_WRITE, 
		IDfACL.DF_PERMIT_WRITE, 
		IDfACL.DF_PERMIT_RELATE, 
		IDfACL.DF_PERMIT_RELATE
	};
	
	private static void log(String str) {
		System.out.println(str);
	}
	
	private String getACLName() {
		return PROJECT_NAME_EN + "-ACL";
	}
	
	private String getGroupName(String baseName) {
		return PROJECT_NAME_EN + "-" + baseName;
	}

	private String getGroupDesc(String baseName) {
		return PROJECT_NAME + "-" + baseName;
	}
	
	private MySessionManager createSessionManager() throws DfException {
		return new MySessionManager(DemoLoginConsts.DOCBASE, DemoLoginConsts.USERNAME, DemoLoginConsts.PASSWORD);
	}
	
	private IDfFolder createCabinet(IDfSession session, String cabinetName, IDfACL acl) throws DfException {
		log("\n正在处理文件柜...");
		IDfFolder cabinet = session.getFolderByPath("/" + cabinetName);
		if (cabinet != null) {
			log("\t文件柜已经存在: " + cabinetName);
		} else {
			log("\t正在创建文件柜: /" + cabinetName);
			cabinet = (IDfFolder) session.newObject("dm_cabinet");
			cabinet.setObjectName(cabinetName);
		}
		cabinet.setBoolean("is_private", false);

		log("\t设置文件柜权限列表");
		cabinet.setACL(acl);
		cabinet.save();
		log("文件柜处理完成。\n");
		return cabinet;
	}
	
	private IDfACL createACL(IDfSession session, String ACLName) throws DfException {
		log("\n正在处理访问控制列表...");
		IDfACL acl = session.getACL(DemoLoginConsts.USERNAME, ACLName);
		if (acl != null) {
			log("\t访问控制列表已经存在: " + ACLName);
			log("\t正在更新权限");
			IDfList perms = acl.getPermissions();
			for (int i = 0; i < perms.getCount(); ++i) {
				IDfPermit perm = (IDfPermit) perms.get(i);
				acl.revokePermit(perm);
			}
		} else {
			log("\t正在创建访问控制列表: " + ACLName);
			acl = (IDfACL) session.newObject("dm_acl");
			acl.setObjectName(ACLName);
		}
		
		for (int i = 0; i < GROUP_NAMES.length; ++i) {
			String groupName = getGroupName(GROUP_NAMES[i]);
			int perm = GROUP_PERMS[i];
			acl.grant(groupName, perm, null);
		}
		acl.grant("dmadmin", IDfACL.DF_PERMIT_DELETE, null);
		
		acl.save();
		log("访问控制列表处理完成。\n");
		
		return acl;
	}
	
	private void createGroup(IDfSession session, String groupName, String desc) throws DfException {
		IDfGroup group = session.getGroup(groupName);
		if (group != null) {
			log("\t用户组: " + groupName + "已经存在。");
			return;
		}
		log("\t正在创建用户组: " + groupName);
		group = (IDfGroup) session.newObject("dm_group");
		group.setGroupName(groupName);
		group.setDescription(desc);
		group.save();
	}
	
	private void createGroups(IDfSession session) throws DfException {
		log("\n开始创建用户组...");
		for (int i = 0; i < GROUP_NAMES.length; ++i) {
			String groupName = getGroupName(GROUP_NAMES[i]);
			createGroup(session, groupName, getGroupDesc(GROUP_DESCS[i]));
		}
		log("用户组创建完成。\n");
	}
	
	private void run() throws DfException {
		MySessionManager sessionManager = createSessionManager();
		IDfSession session = sessionManager.getSession();
		
		log("开始处理项目: " + PROJECT_NAME + "...");
		
		createGroups(session);
		IDfACL acl = createACL(session, getACLName());
		createCabinet(session, PROJECT_NAME + "文档库", acl);
		
		sessionManager.releaseSession(session);
		
		log("项目创建完成。");
	}
	
	public static void main(String[] args) {
		if (args.length != 5) {
			log("程序参数为: <docbase> <username> <password> <项目名称> <项目英文名称>");
			System.exit(1);
		}
		
		DemoLoginConsts.DOCBASE = args[0];
		DemoLoginConsts.USERNAME = args[1];
		DemoLoginConsts.PASSWORD = args[2];
		PROJECT_NAME = args[3];
		PROJECT_NAME_EN = args[4];
		
		log("文档库: " + DemoLoginConsts.DOCBASE);
		log("用户名: " + DemoLoginConsts.USERNAME);
		log("密码: " + DemoLoginConsts.PASSWORD);
		log("项目名称: " + PROJECT_NAME);
		log("项目英文名称: " + PROJECT_NAME_EN);
		log("------------------------------------\n");
		
		ProjectInitializer prjInit = new ProjectInitializer();
		try {
			prjInit.run();
		} catch (DfException e) {
			e.printStackTrace();
		}
		
	}

}
