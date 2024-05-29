package server;

import java.net.Socket;

import commServer.PoolThreadFactory;

public class PoolServerThreadFactory extends PoolThreadFactory {
	
	public PoolServerThreadFactory() { super(); }
	
	@Override
	public PoolServerThread createThreadForConn(Socket socket) {
		return new PoolServerThread(socket);
	}

}
