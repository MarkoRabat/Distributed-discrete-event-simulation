package server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

import commServer.PoolThread;

public class PoolServerThread extends PoolThread {

	public PoolServerThread(Socket client) { super(client); }
	
	@Override
	protected void work(BufferedReader pin, PrintWriter outp) {
		try { 
			while (pin.ready())
				System.out.println(pin.readLine()); 
		} catch (IOException e) {}
		System.out.println("Done reading request.");
		
		outp.println("HTTP/1.1 200 OK");
		outp.println("Content-Type: text/html");
		outp.println("\r\n");
		outp.println("<h1>Hello World!</h1>");
		// outp.flush();			
		
		System.out.println("Connection processing end.");
	}

}
