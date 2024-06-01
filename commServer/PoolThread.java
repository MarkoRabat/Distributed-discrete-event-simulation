package commServer;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
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
	protected String clientIp = null;
	protected int clientPort = -1;

	@Override
	final public void run() { work(); }

	protected void work() {
		try (Socket client = this.client;
			PrintWriter outp = new PrintWriter(
				new OutputStreamWriter(client.getOutputStream()), true);
			BufferedReader pin = new BufferedReader(
				new InputStreamReader(client.getInputStream()));
		) {
			client.setKeepAlive(true);
			this.clientIp = client.getInetAddress().toString().substring(1);
			this.clientPort = client.getPort();
			process_connection_start(this.clientIp, this.clientPort);
			//String request = collect_request(pin);
			String request = collect_request_wMemFiles_supported(pin);
			String[] response = null;
			if (request != null && !request.equals("")) {

				String[] commands = request.split("&&");
				response = process_request(commands);
			}
			return_respone(response, outp);
			process_connection_end(this.clientIp, this.clientPort);
		} catch(Exception e) { e.printStackTrace(); }
	}
	
	protected void process_connection_start(String clientIp, int clientPort) throws Exception {
		System.out.println("The client (" + clientIp + "," + clientPort + ") connected.");
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
	
	protected String collect_request_wMemFiles_supported(BufferedReader pin) throws Exception {
		try {
			for (int i = 0; i < 30 && !pin.ready(); ++i) Thread.sleep(100);
			StringBuilder response = new StringBuilder();
			boolean fileMode = false; int fileChunksRem = -1;
			String inputLine = null;
			while ((inputLine = pin.readLine()) != null) {
				//System.out.println("\t" + inputLine + "=EndOfRequest: " + inputLine.equals("EndOfRequest"));
				if (inputLine.equals("EndOfRequest")) break;
				if (!response.toString().equals("") && !fileMode) response.append("&&");
				//String inputLine = pin.readLine();

				if (inputLine.equals("File") && !fileMode) { fileMode = true; continue; }
				if (fileMode && fileChunksRem == -1) {
					fileChunksRem = Integer.parseInt(inputLine); continue; }
				else if (fileMode && fileChunksRem > 0) --fileChunksRem;
				else if (fileMode && fileChunksRem == 0) {
					fileMode = false; fileChunksRem = -1; }
				response.append(inputLine);
			}
			return response.toString();
		} catch (Exception e) { e.printStackTrace(); }
		return null;
	}
	
	protected String[] process_request(String[] commands) throws Exception { return null; }
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
		System.out.println("The client (" + clientIp + "," + clientPort + ") disconnected.");
	}

}
