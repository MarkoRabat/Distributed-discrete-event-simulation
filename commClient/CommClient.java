package commClient;

import java.net.*;
import java.util.Scanner;
import java.io.*;

public class CommClient {

	public static String makeRequest(String host, int port, String[] params) throws ConnectException {
		if (params == null || params.length == 0) {
			System.err.println("At least one param must be passed to makeRequest"); return null; }
		try (Socket clientSocket = new Socket(host, port);
			OutputStream os = clientSocket.getOutputStream();
			InputStream is = clientSocket.getInputStream();
		) {
			params = mergeParams(params, new String[] {"EndOfRequest\n"});
			for (int i = 0; i < params.length; os.write(params[i++].getBytes()));
			os.flush();
			StringBuilder response = new StringBuilder(); int ch;
			while( (ch = is.read())!= -1) response.append((char)ch);
			return response.toString();
		} 
		catch (ConnectException e) { throw e; }
		catch (Exception e) { e.printStackTrace(); }
		return null;
	}
	
	public static String[] processResponse(String request) {
		String[] r = request.split("\n");
		String[] result = new String[r.length - 5];
		for (int i = 0; i < r.length; ++i) r[i] = r[i].strip();
		int i = 0; for (int j = 4; j < r.length - 1; result[i++] = r[j++]);
		return result;
	}

	public static String makeWebRequest(String url) { 
		try { return makeRequest(url, 80, new String[] {"GET / HTTP/1.0\r\n\r"}); }
		catch (Exception e) { e.getStackTrace(); } return null;
	}
	public static void printResponseFromGoogle() { System.out.println(makeWebRequest("www.google.com")); }
	public static void printResponseFromYahoo() { System.out.println(makeWebRequest("www.yahoo.com")); }

	public static String makeUserRequest(String host, int port, String[] params) throws ConnectException {
		for (int i = 0; i < params.length; params[i++] += "\n");
		return makeRequest(host, port, params);
	}
	
	public static String loadFile(String path) {
		String loadedFile = null;
		try (Scanner scanner = new Scanner( new File(path), "UTF-8" )) {
			loadedFile = scanner.useDelimiter("\\A").next();
		} catch (FileNotFoundException e) { e.printStackTrace(); }
		loadedFile = loadedFile.replaceAll("[^a-zA-Z0-9 .\n]+", "");
		loadedFile = loadedFile.replaceAll("[\n]", "||");
		return loadedFile;
	}
	
	public static void printLoadedFile(String file) {
		System.out.println(file.replaceAll("\\|\\|", "\n"));
	}

	public static String[] mergeParams(String[] params1, String[] params2) {
		if (params1 == null || params2 == null) return null;
		int size = params1.length + params2.length;
		String[] result = new String[size];
		int i = 0;
		for (int j = 0; j < params1.length; result[i++] = params1[j++]);
		for (int j = 0; j < params2.length; result[i++] = params2[j++]);
		return result;
	}
	
	public static String[] putFileInRequestParams(String[] params, String file) {
		int size = file.length(); int chunkSize = 1024;
		int chunks = size / chunkSize + ((size % chunkSize > 0) ? 1 : 0);
		int paramsSize = 0; if (params != null) paramsSize += params.length;
		String[] result = new String[paramsSize + 1 + chunks];
		int i; for (i = 0; i < paramsSize; ++i) result[i] = params[i];
		result[i++] = "" + chunks;
		for(int j = 0; j < size; j += chunkSize, i++)
	        result[i] = file.substring(j, Math.min(size, j + chunkSize));
		return result;
	}

}
