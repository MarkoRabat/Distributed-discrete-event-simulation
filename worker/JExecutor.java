package worker;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.List;
import java.util.Random;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import rs.ac.bg.etf.sleep.simulation.Netlist;
import rs.ac.bg.etf.sleep.simulation.SimComponent;
import rs.ac.bg.etf.sleep.simulation.Simulator;
import rs.ac.bg.etf.sleep.simulation.SimulatorMultithread;
import rs.ac.bg.etf.sleep.simulation.SimulatorOptimistic;
import rs.ac.bg.etf.sleep.simulation.SimulatorSinglethread;
import server.JobAccount;

public class JExecutor extends Thread {
	
	private int key;
	private int subkey;
	private Dictionary<Integer,JobAccount> jobAccount = null;
	private ReentrantReadWriteLock rwLockJobAccount = null;
	private boolean requestedTermination = false;
	
	public JExecutor (int key, int subkey,
		Dictionary<Integer,JobAccount> jobAccount,
		ReentrantReadWriteLock rwLockJobAccount
	) {
		this.key = key;
		this.subkey = subkey;
		this.jobAccount = jobAccount;
		this.rwLockJobAccount = rwLockJobAccount;
	}
	
	public int getKey() { return key; }
	public void terminate() { requestedTermination = true; }
	
	@Override
	public void run() {
		try {
			String components = null;
			String connections = null;
			String rst = null;
			int ltime = 100;

			rwLockJobAccount.readLock().lock();
			
				components = jobAccount.get(key).components;
				connections = jobAccount.get(key).connections;
				rst = jobAccount.get(key).requestedSimType;
				ltime = jobAccount.get(key).logicalEndTime;

			rwLockJobAccount.readLock().unlock();
			
			
			Netlist<Object> netlist = createNetlist(components, connections);
			
			Simulator<Object> simulator = null;

			switch (rst) {
			case "SimulatorSinglethread":
				simulator = new SimulatorSinglethread<Object>(1); break;
			case "SimulatorMultithread":
				simulator = new SimulatorMultithread<Object>(1); break;
			case "SimulatorOptimistic":
				simulator = new SimulatorOptimistic<Object>(1); break;
			default:
				throw new IllegalArgumentException("Unknown simulation type: " + rst);
			}
			
			simulator.setNetlist(netlist);
			simulator.init();
			while (simulator.getlTime() < ltime && !requestedTermination) {
				simulator.execute();
				//System.out.println("Time{" + key + "}: " + simulator.getlTime());
			}
			if (requestedTermination) {
				System.err.println("\tExecutor for the job " + key + " has been terminated."); return; }
			System.out.println("==================================================");
			System.out.println("WORKER FINISHED");
			System.out.println("==================================================");
			try (PrintWriter out = new PrintWriter("job_" + key + "_" + subkey + ".txt")) {
			    out.println(netlisttoStr(netlist, "\n"));
			}
		}
		catch (Exception e) {
			// error in conf, abort whole job somehow
			e.printStackTrace();
		}
		// send results of job execution
	}
	
	public static String netlisttoStr(Netlist<Object> netlist, String sep) throws Exception {
		
		StringBuilder result = new StringBuilder();
		for (SimComponent<Object> c : netlist.getComponents().values()) {
			String[] context = c.getState();
			StringBuilder contextString = new StringBuilder();
			for (String s : context)
				contextString.append(s + " ");
			result.append(contextString.toString().trim() + sep);
		}
		return result.toString();

	}
	
	public static Netlist<Object> createNetlist(String components, String connections) throws Exception {
		Netlist<Object> netlist = new Netlist<Object>();

		String[] componentLines = components.split("\\|\\|");
		for (int i = 0; i < componentLines.length; ++i)
			netlist.addComponent(componentLines[i].split(" "));

		String[] connectionLines = connections.split("\\|\\|");
		String[][] connectionMatrix = new String[connectionLines.length - 1][];
		for (int i = 0; i < connectionLines.length - 1; ++i)
			connectionMatrix[i] = connectionLines[i + 1].split(" ");
		netlist.addConnection(connectionMatrix);
		
		return netlist;
	}

}
