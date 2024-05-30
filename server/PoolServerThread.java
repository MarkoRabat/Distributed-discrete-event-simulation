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
	
	protected String[] process_request(String request) throws Exception {
		System.out.println(request);
		String[] response = new String[1];
		response[0] = "<h1>Hello World!</h1>";
		return response;
	}

}
