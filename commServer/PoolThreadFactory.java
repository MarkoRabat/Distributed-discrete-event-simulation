package commServer;

import java.net.Socket;

public class PoolThreadFactory {

	public PoolThreadFactory() {}
	
	public PoolThread createThreadForConn(Socket socket) {
		return new PoolThread(socket);
	}

}
