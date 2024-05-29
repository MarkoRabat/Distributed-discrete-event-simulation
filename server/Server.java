package server;

import commServer.ExecutorServer;

public class Server {
	public static void main(String[] args) {
		System.out.println("Server started.");
		
		ExecutorServer server = new ExecutorServer(5000, new PoolServerThreadFactory());
		server.start();
		
		try {
			Thread.sleep(300000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		server.stop();
		
	}

}
