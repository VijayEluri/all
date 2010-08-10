package com.lets.filepro;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;

public class FileInfo {
	private static int INTFILECOUNT = 100;
	private File sourceFile;
	private File[] fileList;
	private int fCount;
	private long fileBCount;

	public FileInfo(File f) {
		this.sourceFile = f;
		this.fCount = 0;
		this.fileList = new File[INTFILECOUNT];
	}

	public void ParseFile(File f) {
		if (f.isDirectory()) {
			File[] fl = f.listFiles();
			for (int i = 0; i < fl.length; i++)
				ParseFile(fl[i]);
		} else {
			if (fCount == fileList.length) {
				File[] newfl = new File[INTFILECOUNT + fileList.length];
				System.arraycopy(fileList, 0, newfl, 0, fileList.length);
				fileList = newfl;
			}
			this.fileBCount += f.length();
			fileList[fCount] = f;
			fCount++;
		}
	}

	public void Prepare() {
		this.fileBCount = 0;
		this.fCount = 0;
		ParseFile(sourceFile);
	}

	public File[] GetTaskForms(String path, int tc) throws IOException {
		long len = getOptBCount(tc);
		return GenTaskForms(path, tc, len);
	}

	public File[] GenTaskForms(String path, int tc, long tbc)
			throws IOException {
		File[] taskes = new File[tc];
		String pf = path;
		if (!pf.endsWith(File.separator))
			pf = pf + File.separator;

		PrintWriter[] pwList = new PrintWriter[tc];
		for (int i = 0; i < tc; i++) {
			File tf = new File(pf + "task" + i);
			tf.createNewFile();
			taskes[i] = tf;
			FileOutputStream os = new FileOutputStream(tf);
			pwList[i] = new PrintWriter(os);
		}

		for (int i = 0; i < fCount; i++) {
			long len = fileList[i].length() / tc + 1;

			long pos = 0;
			
			for (int j = 0; j < tc; j++) {
				
				long sl = len * (j + 1);

				if (sl >= fileList[i].length()) {
					pwList[j].println(fileList[i].toString() + " " + pos + " "
							+ fileList[i].length());
					pos = 0;
				} else {
					pwList[j].println(fileList[i].toString() + " " + pos + " "
							+ sl);
					pos = sl;
				}
			}
		}
		
		for(int i=0; i<tc; i++)
			pwList[i].close();

		return taskes;
	}

	public long getFileByteCount() {
		return this.fileBCount;
	}

	public long getOptBCount(int tc) {
		return this.fileBCount / tc + 1;
	}

}
