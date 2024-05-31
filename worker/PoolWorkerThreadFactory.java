package worker;

import java.net.Socket;

import commServer.PoolThreadFactory;

public class PoolWorkerThreadFactory extends PoolThreadFactory {
	public PoolWorkerThreadFactory() {}
	
	public PoolWorkerThread createThreadForConn(Socket socket) {
		return new PoolWorkerThread(socket);
	}

}
