package client;

import java.net.*;
import java.io.*;
import commClient.CommClient;

public class Client {
	

	public static void main(String[] args) {
		try {
			System.out.println(CommClient.makeUserRequest("127.0.0.1", 5000, new String[] {"hello1", "hello2", "hello3"}));
			System.out.println(CommClient.makeUserRequest("localhost", 5000, new String[] {"hello1", "hello2", "hello3"}));
		} catch (Exception e) { System.err.println("Server not reachable."); }
	}

}
