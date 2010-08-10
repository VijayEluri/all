package com.lets.filepro;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedList;

public class Loader {
	public static final int MAX_INPUT_THREAD_COUNT = 100;
	public static final int MAX_OUTPUT_THREAD_COUNT = 100;
	public static final int MIN_OUTPUT_POOL = 20;
	public static final long MIN_PART_LENGTH = 100 * 1024 * 1024; // 100M
	public static int ProcessID = 0;
	private Thread[] inputThreads;
	private Thread[] outputThreads;
	private LinkedList inputQueue = new LinkedList();
	private LinkedList outputQueue = new LinkedList();
	private String dfCABINET = "MyCabinet";

	private Runnable inputRunner = new Runnable() {
		public void run() {
			do {
				Runnable input;
				synchronized (inputQueue) {
					if (inputQueue.isEmpty())
						input = null;
					else
						input = (Runnable) inputQueue.removeFirst();
				}
				if (input == null)
					break;
				input.run();
			} while (true);
		}
	};

	public Loader(File inputFile, int inputThreadCount, int outputThreadCount, String cabinet)
			throws IOException {
		inputThreadCount = Math.max(1, Math.min(MAX_INPUT_THREAD_COUNT,
				inputThreadCount));
		outputThreadCount = Math.max(1, Math.min(MAX_OUTPUT_THREAD_COUNT,
				outputThreadCount));
		this.dfCABINET = cabinet;
		inputThreads = new Thread[inputThreadCount];
		outputThreads = new Thread[outputThreadCount];

		createTasks(inputFile);
	}
	
	private void createTasks(File tf)throws IOException
	{
		FileReader fReader = new FileReader(tf);
		BufferedReader reader = new BufferedReader(fReader);
		while (true) {
			String cmd = reader.readLine();
			if (cmd == null)
				break;
			String[] parp = cmd.split(" ");

			inputQueue.add(new SplitterInput(outputQueue, new File(parp[0]),
					Long.parseLong(parp[1]), Long.parseLong(parp[2])));
		}
	}

//	private void createTasks(File[] inputFiles, long partLength) {
//		if (partLength <= 0) {
//			for (int i = 0; i < inputFiles.length; i++)
//				inputQueue.add(new SplitterInput(outputQueue, inputFiles[i]));
//		} else {
//			partLength = Math.max(MIN_PART_LENGTH, partLength);
//
//			for (int i = 0; i < inputFiles.length; i++) {
//				long fl = inputFiles[i].length();
//				long n = (fl + partLength - 1) / partLength;
//				if (n <= 1) {
//					inputQueue
//							.add(new SplitterInput(outputQueue, inputFiles[i]));
//					continue;
//				}
//				long pl = fl / n;
//				long ps = 0;
//				for (int j = 1; j < n; j++) {
//					long pe = ps + pl;
//					inputQueue.add(new SplitterInput(outputQueue,
//							inputFiles[i], ps, pe));
//					ps = pe;
//				}
//				inputQueue.add(new SplitterInput(outputQueue, inputFiles[i],
//						ps, fl));
//			}
//		}
//	}

	private void createInputThreads() {
		for (int i = 0; i < inputThreads.length; i++)
			inputThreads[i] = new Thread(inputRunner);
	}

	private void startInputThreads() {
		for (int i = 0; i < inputThreads.length; i++)
			inputThreads[i].start();
	}

	private void createOutputThreads() {
		for (int i = 0; i < outputThreads.length; i++)
			outputThreads[i] = new Thread(new SplitterOutput(outputQueue, this.dfCABINET));
	}

	private void startOutputThreads() {
		for (int i = 0; i < outputThreads.length; i++)
			outputThreads[i].start();
	}

	private void waitForInputEnd() throws InterruptedException {
		for (int i = 0; i < inputThreads.length; i++)
			if (inputThreads[i] != null)
				inputThreads[i].join();
	}

	private void addEndSignal() throws InterruptedException {
		synchronized (outputQueue) {
			for (int i = 0; i < outputThreads.length; i++)
				outputQueue.addLast(SplitterOutput.END_SIGNAL);

			outputQueue.notifyAll();
		}
	}

	private void waitForOutputEnd() throws InterruptedException {
		for (int i = 0; i < outputThreads.length; i++)
			if (outputThreads[i] != null)
				outputThreads[i].join();
	}

	public void run() throws InterruptedException {
		createInputThreads();
		createOutputThreads();
		startOutputThreads();
		startInputThreads();
		waitForInputEnd();
		addEndSignal();
		waitForOutputEnd();
	}

	public static void main(String[] args) throws InterruptedException,
			NumberFormatException, IOException {
		if (args.length != 5) {
			System.out
					.println("usage: filename inputThreadCount outputThreadCount Cabinet processID");
			return;
		}
		File file = new File(args[0]);
		if ((!file.exists()) || file.isDirectory())
			return;

		Loader loader = new Loader(file, Integer.parseInt(args[1]), Integer
				.parseInt(args[2]), args[3]);
		Loader.ProcessID = Integer.parseInt(args[4]);
		loader.run();
	}
}
