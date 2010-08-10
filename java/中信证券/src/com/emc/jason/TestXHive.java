package com.emc.jason;

import java.util.Iterator;

import com.xhive.XhiveDriverFactory;
import com.xhive.core.interfaces.XhiveDriverIf;
import com.xhive.core.interfaces.XhiveSessionIf;
import com.xhive.dom.interfaces.XhiveLibraryIf;


public class TestXHive {
	
	private void run() {
		XhiveDriverIf xhiveDriver = XhiveDriverFactory.getDriver("xhive://dctm:1235");
		xhiveDriver.init(1024);
		XhiveSessionIf session = xhiveDriver.createSession();
		
		session.connect("Administrator", "demo", "DEMO");
		
		session.begin();
		XhiveLibraryIf library = session.getDatabase().getRoot();

		StringBuilder sb = new StringBuilder();
		sb.append("for $rec in //下线系统历史数据/明细 ");
		sb.append("where $rec/帐户号 = \"800100426508213001\" ");
		sb.append("return $rec");

		Iterator r = library.executeXQuery(sb.toString());
		
		while(r.hasNext()) {
			System.out.println(r.next().toString());
		}
		session.commit();
		
		session.disconnect();
		session.terminate();
	}

	public static void main(String[] args) {
		TestXHive test = new TestXHive();
		try {
			test.run();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
