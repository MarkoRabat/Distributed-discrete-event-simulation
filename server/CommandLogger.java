package server;

public class CommandLogger {
	
	protected static String[] mergeLoggerCommands(String[] commands1, String[] commands2) {
		if (commands1 == null || commands2 == null) return null;
		int size = commands1.length + commands2.length;
		String[] result = new String[size];
		int i = 0;
		for (int j = 0; j < commands1.length; result[i++] = commands1[j++]);
		for (int j = 0; j < commands2.length; result[i++] = commands2[j++]);
		return result;
	}
	
	protected static void logToConsole(String logText, String[] command) {
		System.out.print("\t" + logText + ":");
		if (command == null) return;
		for (int i = 0; i < command.length; System.out.print(" " + command[i++]));
		System.out.println();
	}

}
