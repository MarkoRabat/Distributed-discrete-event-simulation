package commServer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;

public class PoolThread extends Thread {

	protected Socket client;

	public PoolThread(Socket client) {
			this.client = client;
	}
	
	@Override
	final public void run() {
		try (Socket client = this.client;
			BufferedReader pin = new BufferedReader(
				new InputStreamReader(client.getInputStream())); 
			PrintWriter outp = new PrintWriter(
				new OutputStreamWriter(client.getOutputStream()), true);) {
			System.out.println("The Client "
				+ client.getInetAddress() + ":"
				+ client.getPort() + " connected.");
			work(pin, outp);
		} catch (IOException e) { System.out.println("Hi from here! -- PoolThread"); }
	}

	protected void work(BufferedReader pin, PrintWriter outp) {}

}
