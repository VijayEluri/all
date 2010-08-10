package com.lets.filepro;

import java.io.File;
import java.io.IOException;

public class TaskManager {
	private int taskCount = 0;
	private String targetPath;
	private File sourceFile;
	public String exeCommand = "";
	public String dfCABINET = "myCabinet";

	public TaskManager(File f, String path, int tCount) {
		this.taskCount = tCount;
		this.sourceFile = f;
		this.targetPath = path;
	}

	public void Run() throws IOException, InterruptedException {
		FileInfo info = new FileInfo(sourceFile);
		info.Prepare();
		File[] fl = info.GetTaskForms(targetPath, taskCount);
		
		Process[] pl = new Process[fl.length];
		System.out.println("TaskCount: " + fl.length);
		for (int i = 0; i < fl.length; i++) {
			String cmd = "sh " + exeCommand + " " + fl[i].getCanonicalPath() + " " + dfCABINET + " " + i;
			Process p = Runtime.getRuntime().exec(cmd);
			System.out.println("ExecTaskFile: " + cmd);
			pl[i] = p;
		}
		
		for(int i=0; i<pl.length; i++)
		{
			pl[i].waitFor();			
		}
	}

	public static void main(String[] args) throws IOException, InterruptedException {
		if (args.length != 5) {
			System.out
					.println("usage: source targetPath taskcount execfile cabinet");
			return;
		}
		File file = new File(args[0]);
		if (!file.exists())
			return;

		TaskManager m = new TaskManager(file, args[1], Integer
				.parseInt(args[2]));
		m.exeCommand = args[3];
		m.dfCABINET = args[4];
		m.Run();

	}

}
