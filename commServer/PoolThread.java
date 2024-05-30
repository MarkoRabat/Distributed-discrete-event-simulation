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
		work();
	}

	protected void work() {
		try (Socket client = this.client;
			PrintWriter outp = new PrintWriter(
				new OutputStreamWriter(client.getOutputStream()), true);
			BufferedReader pin = new BufferedReader(
				new InputStreamReader(client.getInputStream()));
		) {
			System.out.println("The Client " + client.getInetAddress()
				+ ":" + client.getPort() + " connected.");
			process_request(pin, outp); outp.flush();
			System.out.println("Connection processing end.");
		} catch(Exception e) { e.printStackTrace(); }
	}
	
	protected void process_request(BufferedReader pin, PrintWriter outp) throws Exception {}

}
