package server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;

import commServer.PoolThread;

public class PoolServerThread extends PoolThread {

	public PoolServerThread(Socket client) { super(client); }
	
	protected void process_request(BufferedReader pin, PrintWriter outp) throws Exception {
		
		for (int i = 0; i < 20 && !pin.ready(); ++i) Thread.sleep(100);
		
		while (pin.ready()) System.out.println(pin.readLine()); 
		System.out.println("Done reading request.");
		
		outp.println("HTTP/1.1 200 OK");
		outp.println("Content-Type: text/html");
		outp.println("\r\n");
		outp.println("<h1>Hello World!</h1>");

	}

}
