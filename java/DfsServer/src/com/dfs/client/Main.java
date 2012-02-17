package com.dfs.client;

public class Main {

	public static void main(String[] args) {
		for (int i = 0; i < 10; ++i) {
			MinaClientThread thread = new MinaClientThread(i);
			thread.start();
		}
	}

}
