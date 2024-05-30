package client;

import java.net.*;
import java.io.*;
import commClient.CommClient;

public class Client {
	

	public static void main(String[] args) {
		try {
			System.out.println(CommClient.makeRequest("localhost", 5000, new String[] {"hello1\n", "hello2\n", "hello3\n"}));
			System.out.println(CommClient.makeRequest("localhost", 5000, new String[] {"hello1\n", "hello2\n", "hello3\n"}));
		} catch (Exception e) { System.err.println("Server not reachable."); }
	}

}
