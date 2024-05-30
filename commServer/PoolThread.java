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
	public PoolThread(Socket client) { this.client = client; }

	@Override
	final public void run() { work(); }

	protected void work() {
		try (Socket client = this.client;
			PrintWriter outp = new PrintWriter(
				new OutputStreamWriter(client.getOutputStream()), true);
			BufferedReader pin = new BufferedReader(
				new InputStreamReader(client.getInputStream()));
		) {
			process_connection_start(client.getInetAddress().toString(), client.getPort());
			String request = collect_request(pin);
			String[] response = null;
			if (!request.equals(" ")) response = process_request(request);
			return_respone(response, outp);
			process_connection_end(client.getInetAddress().toString(), client.getPort());

		} catch(Exception e) { e.printStackTrace(); }
	}
	
	protected void process_connection_start(String clientIp, int clientPort) throws Exception {
		System.out.println("The Client (" + client.getInetAddress() + "," + client.getPort() + ") connected.");
	}
	protected String collect_request(BufferedReader pin) throws Exception {
		for (int i = 0; i < 30 && !pin.ready(); ++i) Thread.sleep(100);
		StringBuilder response = new StringBuilder();
		while (pin.ready()) {
			if (!response.toString().equals("")) response.append(" ");
			response.append(pin.readLine());
		}
		return response.toString();
	}
	protected String[] process_request(String request) throws Exception { return null; }
	protected void return_respone(String[] response, PrintWriter outp) throws Exception {
		// for browser connections
		outp.println("HTTP/1.1 200 OK");
		outp.println("Content-Type: text/html");
		outp.println("\r\n");
		if (response != null) for (int i = 0; i < response.length; outp.println(response[i++]));
		outp.println("End response.");
		outp.flush(); 
	}
	protected void process_connection_end(String clientIp, int clientPort) throws Exception {
		System.out.println("The client (" + client.getInetAddress() + "," + client.getPort() + ") disconnected.");
	}

}
