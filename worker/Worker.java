package worker;

import java.net.ConnectException;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import server.JobAccount;

import commClient.CommClient;
import commServer.ExecutorServer;
import commServer.Server;

public class Worker {
	
	private static final int defaultServerPort = 5000;
	private static final String defaultServerHost = "localhost";
	private static final int defaultAvailThreads = 10;
	private static final String defaultWorkerIp = "localhost";
	private static int jobSchedulerPeriod = 3;
	private static int jobSchedulerPhase = 0;
	private String serverHost;
	private int serverPort;
	private static int nextAvailPort = 5001;
	private int port;
	private ExecutorServer server = null;
	private ReentrantReadWriteLock rwLockJobAccount = null;
	private Dictionary<Integer, JobAccount> jobAccount = null;
	private ReentrantReadWriteLock rwLockJExecutorAccount = null;
	private Dictionary<Integer, JExecutor> jExecutorAccount = null;
	private JobScheduler jobScheduler = null;
	private int availThreads = -1;
	
	public Worker(String serverHost, int serverPort, int availThreads) {
		this.serverHost = serverHost;
		this.serverPort = serverPort;
		this.port = nextAvailPort++;
		this.rwLockJobAccount = new ReentrantReadWriteLock();
		this.jobAccount = new Hashtable<Integer, JobAccount>();
		this.rwLockJExecutorAccount = new ReentrantReadWriteLock();
		this.jExecutorAccount = new Hashtable<Integer, JExecutor>();
		this.availThreads = availThreads;
		this.server = new ExecutorServer(this.port, new PoolWorkerThreadFactory(
			this.jobAccount, this.rwLockJobAccount, 
			jExecutorAccount, rwLockJExecutorAccount, 
			this.availThreads, defaultWorkerIp, port
		));
		this.jobScheduler = new JobScheduler(
			jobSchedulerPeriod * 1000, jobSchedulerPhase * 1000, jobAccount, rwLockJobAccount, jExecutorAccount, rwLockJExecutorAccount);
	}

	public Worker(String serverHost, int availThreads) { this(serverHost, defaultServerPort, availThreads); }
	public Worker(String serverHost) { this(serverHost, defaultServerPort, defaultAvailThreads); }
	public Worker() { this(defaultServerHost, defaultServerPort, defaultAvailThreads); }
	
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
	
	//* send to server that job is done
	//* send to server that job is aborted
	
		//-- in both cases server should increase avail thread count by one

	public void serveRequests() { server.start(); jobScheduler.start(); }
	public void stopRequestServer() { server.stop(); jobScheduler.interrupt(); }
	
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

		
		
		while (true) {
			Server.waitForUserConsoleQ();
			for (int i = 0; i < workers.length; ++i) {
				System.out.println("============ worker[" + i + "] jobs==============");;
				Enumeration<Integer> keys = workers[i].jobAccount.keys();
				while (keys.hasMoreElements()) {
					int key = keys.nextElement();
					System.out.println(
						key + ": " + workers[i].jobAccount.get(key));
				}

			}
			System.out.println();
			for (int i = 0; i < workers.length; ++i) {
				System.out.println("============ worker[" + i + "] executors==============");;
				Enumeration<Integer> keys = workers[i].jExecutorAccount.keys();
				while (keys.hasMoreElements()) {
					int key = keys.nextElement();
					System.out.println(
						key + ": " + workers[i].jExecutorAccount.get(key).isAlive());
				}
			}
		}
		
		//for (int i = 0; i < workers.length; ++i)
			//workers[i].stopRequestServer();
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
