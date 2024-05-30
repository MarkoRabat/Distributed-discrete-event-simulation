package server;

import commServer.ExecutorServer;
import commServer.PoolThreadFactory;

public class Server {
	public static void main(String[] args) {
		System.out.println("Server started.");
		
		ExecutorServer server = new ExecutorServer(5000, new PoolServerThreadFactory());
		//ExecutorServer server = new ExecutorServer(5000, new PoolThreadFactory());
		server.start();
		
		/*try { Thread.sleep(300000);
		} catch (InterruptedException e) { e.printStackTrace(); }*/
		//server.stop();
		
	}

}
