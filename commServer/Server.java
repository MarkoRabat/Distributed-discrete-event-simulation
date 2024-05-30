package commServer;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

public abstract class Server implements Runnable {

	private static int id = 0;

	protected String host;
	protected int port;

	public Server(int port) {
		this.port = port;
		thread = null;
		running = false;
	}

	ServerSocket listener = null;

	@Override
	public void run() {
		try {
			listener = new ServerSocket(port);
			while (running) {
				Socket client = listener.accept();
				processRequest(client);
			}
		} catch (Exception ex) {
			//ex.printStackTrace();
		} finally { close(); }
	}

	public abstract void processRequest(Socket client);

	protected volatile Thread thread;
	protected volatile boolean running;

	public void start() {
		if (thread == null) {
			thread = new Thread(this, "Server");
			thread.setDaemon(true);
			running = true;
			thread.start();
		}
	}

	public void stop() {
		running = false; thread.interrupt(); close(); }

	public void close() {
		try {
			listener.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void waitForUserConsoleQ() {
		Scanner input = new Scanner(System.in);
		String cont = input.nextLine();
		while(!cont.equals("q")) cont = input.nextLine();
	}

	public boolean isRunning() { return running; }

	public static synchronized int nextId() { return id++; }
}
