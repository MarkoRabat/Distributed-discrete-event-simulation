package user;

import java.net.*;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.Scanner;
import java.io.*;
import commClient.CommClient;
import commServer.Server;

public class User {


	private static final int defaultServerPort = 5000;
	private static final String defaultServerHost = "localhost";
	private String serverHost;
	private int serverPort;
	
	public User(String serverHost, int serverPort) {
		this.serverHost = serverHost;
		this.serverPort = serverPort;
	}

	public User(String serverHost) { this(serverHost, defaultServerPort); }
	public User() { this(defaultServerHost, defaultServerPort); }
	

	public void testConnection() {
		for (int attempts = 3; attempts > 0; --attempts) {
			try {
				String response = CommClient.makeUserRequest(
					this.serverHost, this.serverPort, new String[] { "User", "TestConnect"});
				String[] data = CommClient.processResponse(response);
				System.out.print("attempt[" + (3 - attempts) + "]:\t");
				for (int i = 0; i < data.length; ++i)
					System.out.print(" " + data[i]);
				System.out.println();
			}
			catch (ConnectException e) { 
				try { Thread.sleep(200); }
				catch (InterruptedException e1) { e1.printStackTrace(); }
				System.err.println("Server not reachable: retrying...");
			}
		}
	}
	
	public void userSleep5sByServerRq() {
		for (int attempts = 3; attempts > 0; --attempts) {
			try {
				String response = CommClient.makeUserRequest(
					this.serverHost, this.serverPort, new String[] { "User", "UserBlock5s"});
				String[] data = CommClient.processResponse(response);
				System.out.print("attempt[" + (3 - attempts) + "]:\t");
				for (int i = 0; i < data.length; ++i)
					System.out.print(" " + data[i]);
				System.out.println();
				attempts = 0;
			}
			catch (ConnectException e) { 
				try { Thread.sleep(200); }
				catch (InterruptedException e1) { e1.printStackTrace(); }
				System.err.println("Server not reachable: retrying...");
			}
		}
	}

	public void sendJob(String jobName, String components, String connections) {
		for (int attempts = 3; attempts > 0; --attempts) {
			try {
				String[] params = new String[] {"User", "StartJob", "Components", "File"};
				params = CommClient.putFileInRequestParams(params, components);
				params = CommClient.mergeParams(params, new String[] { "Connections", "File"});
				params = CommClient.putFileInRequestParams(params, connections);
				params = CommClient.mergeParams(params, new String[] {
					"SimulationType", "SomeSimType", "logicalEndTime", "10", "JobName", jobName});
				String response = CommClient.makeUserRequest(this.serverHost, this.serverPort, params);
				String[] data = CommClient.processResponse(response);
				for (int i = 0; i < data.length; ++i)
					System.out.print(" " + data[i]);
				System.out.println();
				attempts = 0;
			}
			catch (ConnectException e) { 
				try { Thread.sleep(200); }
				catch (InterruptedException e1) { e1.printStackTrace(); }
				System.err.println("Server not reachable: retrying...");
			}
		}
	}
	
	public void abortJob(int jobId) {
		for (int attempts = 3; attempts > 0; --attempts) {
			try {
				String[] params = new String[] {"User", "Abort", "JobId", "" + jobId};
				String response = CommClient.makeUserRequest(this.serverHost, this.serverPort, params);
				String[] data = CommClient.processResponse(response);
				for (int i = 0; i < data.length; ++i)
					System.out.print(" " + data[i]);
				System.out.println();
				attempts = 0;
			}
			catch (ConnectException e) { 
				try { Thread.sleep(200); }
				catch (InterruptedException e1) { e1.printStackTrace(); }
				System.err.println("Server not reachable: retrying...");
			}
		}
		
	}
	
	public static void main(String[] args) {
		User user1 = new User();
		
		String components = CommClient.loadFile("src/komponente20-5000.txt");
		String connections = CommClient.loadFile("src/veze20-5000.txt");

		String[] result = CommClient.putFileInRequestParams(null, components);
		user1.testConnection();
		user1.sendJob("job1", components, connections);
		user1.sendJob("job2", components, connections);
		user1.sendJob("job3", components, connections);
		Server.waitForUserConsoleQ();
		user1.abortJob(0);
		
		//user1.userSleep5sByServerRq();
	}

}
