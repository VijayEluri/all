package com.emc.dctm.batchimport;

import java.io.ByteArrayInputStream;
import java.util.Enumeration;
import java.util.Properties;

import javax.swing.UIManager;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.*;

import com.documentum.com.DfClientX;
import com.documentum.com.IDfClientX;
import com.documentum.fc.client.*;
import com.documentum.fc.common.DfException;
import com.emc.dctm.batchimport.data.LoginDialogData;
import com.emc.dctm.batchimport.ui.ImportDialog;
import com.emc.dctm.batchimport.ui.LoginDialog;
import com.emc.dctm.service.SessionFactory;

public class App {

	public static void main(String[] args) {

		// systemTest();

		Display display = new Display();
		Shell shell = new Shell(display);
		shell.setMaximized(true);

		initMenu(shell);

		shell.open();

		LoginDialog loginDialog = new LoginDialog(shell);
		if (loginDialog.open().getButtonResult() != LoginDialogData.CANCEL_BUTTON) {

			try {
				testSomeAPI();
			} catch (DfException e) {
				e.printStackTrace();
			}

			while (!shell.isDisposed()) {
				if (!display.readAndDispatch())
					display.sleep();
			}
		}

		try {
			SessionFactory.getInstance().release();
		} catch (Exception e) {
			e.printStackTrace();
		}
		display.dispose();
	}

	private static void systemTest() {
		System.out.println(System.getProperty("java.library.path"));
		System.out.println(System.getProperty("java.class.path"));
		Properties properties = System.getProperties();
		Enumeration<Object> keys = properties.keys();
		while (keys.hasMoreElements()) {
			System.out.println(keys.nextElement());
		}
		UIManager.LookAndFeelInfo[] lafs = UIManager.getInstalledLookAndFeels();
		for (int i = 0; i < lafs.length; ++i) {
			System.out.println(lafs[i].getClassName());
		}
	}

	private static void testSomeAPI() throws DfException {
		IDfSession session = SessionFactory.getInstance().getDfSession();
		IDfFolder folder = session.getFolderByPath("/dmadmin");
		IDfClientX clientx = new DfClientX();

		String objectName = "test";
		String qualification = "dm_document where object_name='" + objectName
				+ "' and FOLDER (ID('" + folder.getObjectId().getId() + "'))";
		IDfSysObject object = (IDfSysObject) session
				.getObjectByQualification(qualification);
		if (object != null) {
			if (object.getObjectName().equals(objectName)) {
				object.destroyAllVersions();
			}
		}

		IDfDocument document = (IDfDocument) session.newObject("dm_document");

		document.setObjectName("test");
		document.setContentType("crtext");
		document.setFileEx("C:/temp/1.txt", "crtext", 0, null);
		document.appendFile("c:/temp/2.txt");
		document.appendFile("c:/temp/3.txt");

		document.link(folder.getObjectId().getId());

		document.save();
		
		System.out.print("Pages: ");
		System.out.println(document.getPageCount());
		ByteArrayInputStream bais = document.getContent();
		if (bais.available() > 0) {
			System.out.println(clientx.ByteArrayInputStreamToString(bais));
		}
	}

	private static void initMenu(final Shell shell) {
		Menu menu = new Menu(shell, SWT.BAR);
		shell.setMenuBar(menu);

		MenuItem file = new MenuItem(menu, SWT.CASCADE);
		file.setText("&File");

		Menu fileMenu = new Menu(shell, SWT.DROP_DOWN);
		file.setMenu(fileMenu);

		MenuItem importItem = new MenuItem(fileMenu, SWT.PUSH);
		importItem.setText("&Import");

		importItem.addListener(SWT.Selection, new Listener() {

			public void handleEvent(Event event) {
				ImportDialog dlg = new ImportDialog(shell);
				dlg.open();
			}

		});

		new MenuItem(fileMenu, SWT.SEPARATOR);

		MenuItem exitItem = new MenuItem(fileMenu, SWT.PUSH);
		exitItem.setText("E&xit");

		exitItem.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event event) {
				shell.close();
			}

		});
	}

}
