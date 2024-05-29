package commServer;

import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class ExecutorServer extends Server {

	public static final int DEFAULT_THREADS_NUMBER = 200;

	protected ExecutorService pool;
	protected PoolThreadFactory threadFactory;

	public ExecutorServer(int port, PoolThreadFactory ptf) {
		this(DEFAULT_THREADS_NUMBER, port, ptf);
	}

	public ExecutorServer(int numOfThreads, int port, PoolThreadFactory ptf) {
		super(port);
		pool = Executors.newFixedThreadPool(numOfThreads);
		threadFactory = ptf;
	}

	@Override
	public void processRequest(Socket client) {
		pool.execute(threadFactory.createThreadForConn(client));
	}

	@Override
	public void stop() {
		super.close();
		pool.shutdown();
		try {
			if (!pool.awaitTermination(60, TimeUnit.SECONDS)) {
				pool.shutdownNow();
				if (!pool.awaitTermination(60, TimeUnit.SECONDS))
					System.err.println("Pool did not terminate");
			}
		} catch (InterruptedException ie) {
			pool.shutdownNow();
			Thread.currentThread().interrupt();
		}
	}
}
