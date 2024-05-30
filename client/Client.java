package client;

import java.net.*;
import java.io.*;

public class Client {
	
	public static String makeRequest(String host, int port, String[] params) {
		if (params == null || params.length == 0) {
			System.err.println("At least one param must be passed to makeRequest"); return null; }
		try (Socket clientSocket = new Socket(host, port);
			OutputStream os = clientSocket.getOutputStream();
			InputStream is = clientSocket.getInputStream();
		) {
			for (int i = 0; i < params.length; os.write(params[i++].getBytes()));
			os.flush();
			StringBuilder response = new StringBuilder(); int ch;
			while( (ch = is.read())!= -1) response.append((char)ch);
			return response.toString();
		} catch (Exception e) { e.printStackTrace(); }
		return null;
	}
	
	public static String makeWebRequest(String url) { return makeRequest(url, 80, new String[] {"GET / HTTP/1.0\r\n\r\n"}); }
	public static void printResponseFromGoogle() { System.out.println(makeWebRequest("www.google.com")); }
	public static void printResponseFromYahoo() { System.out.println(makeWebRequest("www.yahoo.com")); }

	public static void main(String[] args) {
		System.out.println(makeRequest("localhost", 5000, new String[] {"hello1\n", "hello2\n", "hello3\n"}));
		System.out.println(makeRequest("localhost", 5000, new String[] {"hello1\n", "hello2\n", "hello3\n"}));
	}

}
