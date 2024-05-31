package worker;

import java.net.ConnectException;

import commClient.CommClient;

public class Worker {
	
	private int port = 5001;

	public static void main(String[] args) {
		// transforming serializable objects to/from string -- java
		// https://stackoverflow.com/questions/134492/how-to-serialize-an-object-into-a-string
		
		Worker w1 = new Worker();

		Boolean again = true;
		while (again) {
			again = false;
			try {
				System.out.println(CommClient.makeRequest("localhost", 5000, new String[] {
					"Workstation\n", "WorkstationStarted\n", "NAvailThreads\n", "10\n", "ServerPort\n", "" + w1.port}));
			}
			catch (ConnectException e) { 
				again = true;
				try { Thread.sleep(200); }
				catch (InterruptedException e1) { e1.printStackTrace(); }
				System.err.println("Server not reachable: retrying...");
			}
		}
	}

}
