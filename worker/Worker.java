package worker;

import java.net.ConnectException;

import commClient.CommClient;

public class Worker {
	
	private static final int defaultServerPort = 5000;
	private static final String defaultServerHost = "localhost";
	private String serverHost;
	private int serverPort;
	private static int nextAvailPort = 5001;
	private int port;
	private int id;
	
	public Worker(String serverHost, int serverPort) {
		this.serverHost = serverHost;
		this.serverPort = serverPort;
		this.port = nextAvailPort++;
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
				for (int i = 0; i < data.length; ++i)
					System.out.println(data[i] + "|");
			}
			catch (ConnectException e) { 
				again = true;
				try { Thread.sleep(200); }
				catch (InterruptedException e1) { e1.printStackTrace(); }
				System.err.println("Server not reachable: retrying...");
			}
		}
	}

	public static void main(String[] args) {
		Worker w1 = new Worker();
		w1.connect();
		
	}

}
