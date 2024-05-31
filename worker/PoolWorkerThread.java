package worker;

import java.net.Socket;

import commServer.PoolThread;

public class PoolWorkerThread extends PoolThread {
	public PoolWorkerThread(Socket client) { super(client); }
	
	protected String[] process_request(String request) {
		String[] commands = request.split(" ");
		switch(commands[0]) {
		case "Server": return handleServerClient(commands);
		default:
			String[] response = new String[1];
			response[0] = "<h1>Succesful connection to Worker</h1>";
			return response;
		}
	}
	
	protected String[] handleServerClient(String[] commands) {
		switch (commands[1]) {
		case "PulseChk":
			return HandleServerCommands.checkPulse(commands);
		default:
			return null;
		}
	}

}
