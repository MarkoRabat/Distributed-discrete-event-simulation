package worker;

import java.net.ConnectException;

import commClient.CommClient;
import commServer.ExecutorServer;
import commServer.Server;

public class Worker {
	
	private static final int defaultServerPort = 5000;
	private static final String defaultServerHost = "localhost";
	private String serverHost;
	private int serverPort;
	private static int nextAvailPort = 5001;
	private int port;
	private int id;
	private ExecutorServer server = null;
	
	public Worker(String serverHost, int serverPort) {
		this.serverHost = serverHost;
		this.serverPort = serverPort;
		this.port = nextAvailPort++;
		server = new ExecutorServer(this.port, new PoolWorkerThreadFactory());
	}

	public Worker(String serverHost) { this(serverHost, defaultServerPort); }
	public Worker() { this(defaultServerHost, defaultServerPort); }
	
	public void connect() {
		Boolean again = true;
		while (again) {
			again = false;
			try {
				String response = CommClient.makeRequest(this.serverHost, this.serverPort, new String[] {
					"Workstation\n", "WorkstationStarted\n", "NAvailThreads\n", "10\n", "WorkerPort\n", "" + this.port + "\n"});
				String[] data = CommClient.processResponse(response);
			}
			catch (ConnectException e) { 
				again = true;
				try { Thread.sleep(200); }
				catch (InterruptedException e1) { e1.printStackTrace(); }
				System.err.println("Server not reachable: retrying...");
			}
		}
	}
	
	public void serveRequests() { server.start(); }
	public void stopRequestServer() { server.stop(); }
	
	public static void main(String[] args) {
		Worker[] workers = new Worker[20];
		for (int i = 0; i < workers.length; ++i) {
			workers[i] = new Worker();
			workers[i].connect();
			workers[i].serveRequests();
		}
		Server.waitForUserConsoleQ();
		for (int i = 0; i < workers.length; ++i)
			workers[i].stopRequestServer();
	}

}
