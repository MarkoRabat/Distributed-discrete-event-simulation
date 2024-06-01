package worker;

import java.net.ConnectException;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import server.JobAccount;

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
	private ReentrantReadWriteLock rwLockJobAccount = null;
	private Dictionary<Integer, JobAccount> jobAccount = null;
	
	public Worker(String serverHost, int serverPort) {
		this.serverHost = serverHost;
		this.serverPort = serverPort;
		this.port = nextAvailPort++;
		this.rwLockJobAccount = new ReentrantReadWriteLock();
		this.jobAccount = new Hashtable<Integer, JobAccount>();
		this.server = new ExecutorServer(this.port, new PoolWorkerThreadFactory(
			this.jobAccount, this.rwLockJobAccount
		));
	}

	public Worker(String serverHost) { this(serverHost, defaultServerPort); }
	public Worker() { this(defaultServerHost, defaultServerPort); }
	
	public void connect() {
		Boolean again = true;
		while (again) {
			again = false;
			try {
				String response = CommClient.makeUserRequest(this.serverHost, this.serverPort, new String[] {
					"Workstation", "WorkstationStarted", "NAvailThreads", "10", "WorkerPort", "" + this.port});
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
		Worker[] workers = new Worker[5];
		for (int i = 0; i < workers.length; ++i) {
			workers[i] = new Worker();
			workers[i].connect();
			workers[i].serveRequests();
		}
		Server.waitForUserConsoleQ();
		for (int i = 0; i < workers.length; ++i)
			workers[i].stopRequestServer();

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


/** 
 * 
import java.util.*;
import java.io.*;
public class ToStringSample {

    public static void main( String [] args )  throws IOException,
                                                      ClassNotFoundException {
        String string = toString( new SomeClass() );
        System.out.println(" Encoded serialized version " );
        System.out.println( string );
        SomeClass some = ( SomeClass ) fromString( string );
        System.out.println( "\n\nReconstituted object");
        System.out.println( some );


    }

    // Read the object from Base64 string.
   private static Object fromString( String s ) throws IOException ,
                                                       ClassNotFoundException {
        byte [] data = Base64.getDecoder().decode( s );
        ObjectInputStream ois = new ObjectInputStream( 
                                        new ByteArrayInputStream(  data ) );
        Object o  = ois.readObject();
        ois.close();
        return o;
   }

    // Write the object to a Base64 string.
    private static String toString( Serializable o ) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream( baos );
        oos.writeObject( o );
        oos.close();
        return Base64.getEncoder().encodeToString(baos.toByteArray()); 
    }
}

// Test subject. A very simple class.
class SomeClass implements Serializable {

    private final static long serialVersionUID = 1; // See Nick's comment below

    int i    = Integer.MAX_VALUE;
    String s = "ABCDEFGHIJKLMNOP";
    Double d = new Double( -1.0 );
    public String toString(){
        return  "SomeClass instance says: Don't worry, " 
              + "I'm healthy. Look, my data is i = " + i  
              + ", s = " + s + ", d = " + d;
    }
}*/
