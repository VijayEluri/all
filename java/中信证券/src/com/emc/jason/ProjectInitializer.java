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
		"��Ŀ���Ա", 
		"��Ŀ�ֳ�������", 
		"��Ŀ�����ˣ�������ˣ�", 
		"����������", 
		"��ҵ���Ͷ��ί�쵼", 
		"�ں˷��"
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
		log("\n���ڴ����ļ���...");
		IDfFolder cabinet = session.getFolderByPath("/" + cabinetName);
		if (cabinet != null) {
			log("\t�ļ����Ѿ�����: " + cabinetName);
		} else {
			log("\t���ڴ����ļ���: /" + cabinetName);
			cabinet = (IDfFolder) session.newObject("dm_cabinet");
			cabinet.setObjectName(cabinetName);
		}
		cabinet.setBoolean("is_private", false);

		log("\t�����ļ���Ȩ���б�");
		cabinet.setACL(acl);
		cabinet.save();
		log("�ļ�������ɡ�\n");
		return cabinet;
	}
	
	private IDfACL createACL(IDfSession session, String ACLName) throws DfException {
		log("\n���ڴ�����ʿ����б�...");
		IDfACL acl = session.getACL(DemoLoginConsts.USERNAME, ACLName);
		if (acl != null) {
			log("\t���ʿ����б��Ѿ�����: " + ACLName);
			log("\t���ڸ���Ȩ��");
			IDfList perms = acl.getPermissions();
			for (int i = 0; i < perms.getCount(); ++i) {
				IDfPermit perm = (IDfPermit) perms.get(i);
				acl.revokePermit(perm);
			}
		} else {
			log("\t���ڴ������ʿ����б�: " + ACLName);
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
		log("���ʿ����б�����ɡ�\n");
		
		return acl;
	}
	
	private void createGroup(IDfSession session, String groupName, String desc) throws DfException {
		IDfGroup group = session.getGroup(groupName);
		if (group != null) {
			log("\t�û���: " + groupName + "�Ѿ����ڡ�");
			return;
		}
		log("\t���ڴ����û���: " + groupName);
		group = (IDfGroup) session.newObject("dm_group");
		group.setGroupName(groupName);
		group.setDescription(desc);
		group.save();
	}
	
	private void createGroups(IDfSession session) throws DfException {
		log("\n��ʼ�����û���...");
		for (int i = 0; i < GROUP_NAMES.length; ++i) {
			String groupName = getGroupName(GROUP_NAMES[i]);
			createGroup(session, groupName, getGroupDesc(GROUP_DESCS[i]));
		}
		log("�û��鴴����ɡ�\n");
	}
	
	private void run() throws DfException {
		MySessionManager sessionManager = createSessionManager();
		IDfSession session = sessionManager.getSession();
		
		log("��ʼ������Ŀ: " + PROJECT_NAME + "...");
		
		createGroups(session);
		IDfACL acl = createACL(session, getACLName());
		createCabinet(session, PROJECT_NAME + "�ĵ���", acl);
		
		sessionManager.releaseSession(session);
		
		log("��Ŀ������ɡ�");
	}
	
	public static void main(String[] args) {
		if (args.length != 5) {
			log("�������Ϊ: <docbase> <username> <password> <��Ŀ����> <��ĿӢ������>");
			System.exit(1);
		}
		
		DemoLoginConsts.DOCBASE = args[0];
		DemoLoginConsts.USERNAME = args[1];
		DemoLoginConsts.PASSWORD = args[2];
		PROJECT_NAME = args[3];
		PROJECT_NAME_EN = args[4];
		
		log("�ĵ���: " + DemoLoginConsts.DOCBASE);
		log("�û���: " + DemoLoginConsts.USERNAME);
		log("����: " + DemoLoginConsts.PASSWORD);
		log("��Ŀ����: " + PROJECT_NAME);
		log("��ĿӢ������: " + PROJECT_NAME_EN);
		log("------------------------------------\n");
		
		ProjectInitializer prjInit = new ProjectInitializer();
		try {
			prjInit.run();
		} catch (DfException e) {
			e.printStackTrace();
		}
		
	}

}
