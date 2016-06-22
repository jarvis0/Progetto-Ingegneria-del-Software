package it.polimi.ingsw.ps23.server;

import java.io.IOException;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.rmi.AlreadyBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Timer;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

import it.polimi.ingsw.ps23.client.rmi.ClientInterface;
import it.polimi.ingsw.ps23.server.commons.exceptions.ViewNotFoundException;

class Server implements ServerInterface {
	
	private static final int SOCKET_PORT_NUMBER = 12345;
	private static final int RMI_PORT_NUMBER = 1099;
	private static final String POLICY_NAME = "cofRegistry";
	private static final int MINIMUM_PLAYERS_NUMBER = 2;
	private static final int LAUNCH_TIMEOUT = 1;
	private static final String LAUNCH_PRINT = "A new game is starting in ";
	private static final int CONNECTION_TIMEOUT = 9000;
	private static final String SECONDS_PRINT =  " seconds...";
	private static final String NO_INPUT = "NOINPUTNEEDED";
	
	private ExecutorService executor;
	
	private ServerSocket serverSocket;

	private Map<String, Connection> socketWaitingConnections;
	private Map<String, ClientInterface> rmiWaitingConnections;
	
	private boolean launchingGame;
	private boolean socketActive;

	private GameInstancesSet gameInstances;
	
	private PrintStream output;
	
	private String doubleName;

	private Server() {
		output = new PrintStream(System.out, true);
		gameInstances = new GameInstancesSet(CONNECTION_TIMEOUT);
		socketActive = false;
		executor = Executors.newCachedThreadPool();
		socketWaitingConnections = new HashMap<>();
		rmiWaitingConnections = new HashMap<>();
		launchingGame = false;
	}

	synchronized void initializeGame() {
		gameInstances.newGame(socketWaitingConnections, rmiWaitingConnections);
		launchingGame = false;
		socketWaitingConnections.clear();
		rmiWaitingConnections.clear();
		output.println("A new game is started.");
	}

	synchronized void setRMITimerEnd(Timer timer) {
		timer.cancel();
		for(Connection connection : socketWaitingConnections.values()) {
			connection.setStarted();
		}
		initializeGame();
	}

	private synchronized void startCountdownFromRMI() {
		if(socketWaitingConnections.size() + rmiWaitingConnections.size() == MINIMUM_PLAYERS_NUMBER) {
			launchingGame = true;
			output.println(LAUNCH_PRINT + LAUNCH_TIMEOUT + SECONDS_PRINT);
			String message = LAUNCH_PRINT + LAUNCH_TIMEOUT + SECONDS_PRINT + "\n";
			for(Connection connection : socketWaitingConnections.values()) {
				connection.send(message);
			}
			for(ClientInterface client : rmiWaitingConnections.values()) {
				infoMessage(client, message);
			}
			Timer timer = new Timer();
			timer.schedule(new LaunchingGameTask(timer, this, LAUNCH_TIMEOUT), LAUNCH_TIMEOUT, 1000L);
		}
	}

	private void infoMessage(ClientInterface client, String message) {
		try {
			client.infoMessage(message);
		} catch (RemoteException e) {
			Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "Cannot register the new client to the registry.", e);
		}
	}
	
	@Override
	public void registerRMIClient(String name, ClientInterface client) {
		output.println("New RMI client connection received.");
		rmiWaitingConnections.put(name,  client);
		output.println("Player " + name + " has been added to the waiting list.");
		String message = "Connection established at " + new Date().toString() + "\nWaiting others players to connect...\n";
		if(launchingGame) {
			message += "\nA new game is starting in less than " + LAUNCH_TIMEOUT + SECONDS_PRINT + "\nThe new game is starting in a few seconds...\n";
		}
		infoMessage(client, message);
		startCountdownFromRMI();
	}

	private void startRMI() {
		try {
			Registry registry = LocateRegistry.createRegistry(RMI_PORT_NUMBER);
			ServerInterface stub = (ServerInterface) UnicastRemoteObject.exportObject(this, 0);
			registry.bind(POLICY_NAME, stub);
			output.println("Waiting for RMI connections...");
		} catch (RemoteException | AlreadyBoundException e) {
			socketActive = false;
			Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "Cannot create a new registry.", e);
		}
	}

	synchronized void deregisterSocketConnection(Connection connection) throws ViewNotFoundException {
		gameInstances.disconnectSocketPlayer(connection);
		Iterator<String> iterator = socketWaitingConnections.keySet().iterator();
		while(iterator.hasNext()) {
			if(socketWaitingConnections.get(iterator.next()) == connection) {
				iterator.remove();
			}
		}//TODO questo blocco while serve solo se un socketPlayer si connette prima di inserire il nome
		//output.println("The player " + disconnectedPlayer + " has been disconnected.");
	}

	synchronized void setSocketTimerEnd() {
		notifyAll();
	}

	private synchronized void startCountdownFromSocket() {
		if(socketWaitingConnections.size() + rmiWaitingConnections.size() == MINIMUM_PLAYERS_NUMBER) {
			launchingGame = true;
			output.println(LAUNCH_PRINT + LAUNCH_TIMEOUT + SECONDS_PRINT);
			String message = LAUNCH_PRINT + LAUNCH_TIMEOUT + SECONDS_PRINT + "\n";
			for(Connection connection : socketWaitingConnections.values()) {
				connection.send(NO_INPUT + "" + message);
			}
			for(ClientInterface client : rmiWaitingConnections.values()) {
				infoMessage(client, message);
			}
			Timer timer = new Timer();
			timer.schedule(new LaunchingGameTask(this, LAUNCH_TIMEOUT), LAUNCH_TIMEOUT, 1000L);
			try {
				wait();
			} catch (InterruptedException e) {
				socketActive = false;
				Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "Cannot wait new game countdown.", e);
				Thread.currentThread().interrupt();
			}
			timer.cancel();
			for(Connection connection : socketWaitingConnections.values()) {
				connection.setStarted();
			}
		}
	}
	
	private boolean isDouble(String name) {
		doubleName = new String();
		for(String playerName : socketWaitingConnections.keySet()) {
			if(name.equals(playerName)) {
				doubleName = playerName;
				return true;
			}
		}
		for(String playerName : rmiWaitingConnections.keySet()) {
			if(name.equals(playerName)) {
				doubleName = playerName;
				return true;
			}
		}
		if(gameInstances.checkIfAlreadyInGame(name)) {
			doubleName = name;
			return true;
		}
		return false;
	}

	private String solveDoubles(String playerName) {
		String newPlayerName = playerName;
		if(doubleName.contains(".")) {
			int n = doubleName.indexOf('.') + 1;
			String intermediary = doubleName.substring(n);
			int index = Integer.parseInt(intermediary) + 1;
			newPlayerName = doubleName.substring(0, doubleName.indexOf('.') + 1) + index;
		}
		else {
			newPlayerName += ".0";
		}
		if(isDouble(newPlayerName)) {
			newPlayerName = solveDoubles(playerName);
		}
		return newPlayerName;
	}

	synchronized void joinToWaitingList(String name, Connection connection) {
		String playerName = name;
		boolean formerPlayer = gameInstances.checkIfFormerPlayer(playerName);
		if(!formerPlayer && !playerName.matches("[a-zA-Z]")) {
			connection.send(NO_INPUT + "Invalid name.");
			connection.close();
			Thread.currentThread().interrupt();
		}
		else {
			if(!formerPlayer) {
				if(isDouble(name)) {
					playerName = solveDoubles(playerName);
					connection.send(NO_INPUT + "Your name is a double for a game, here you are a new one: ''" + playerName + "''.\nIn case of reconnection, use this name to rejoin your game.");
				}
				output.println("Player " + playerName + " has been added to the waiting list.");
				socketWaitingConnections.put(playerName, connection);
				startCountdownFromSocket();
			}
			else {
				output.println("Player " + playerName + " is being prompted to his previous game.");
				gameInstances.reconnectPlayer(playerName, connection);
				connection.send(NO_INPUT + "You have been prompted to your previous game, please wait your turn.");
			}
		}
	}

	private void newSocketConnection() {
		try {
			Socket newSocket = serverSocket.accept();
			output.println("New socket client connection received.");
			Connection connection = new Connection(this, newSocket, CONNECTION_TIMEOUT);
			String message = "Connection established at " + new Date().toString() + "\n";
			if(launchingGame) {
				message += "A new game is starting in less than " + LAUNCH_TIMEOUT + SECONDS_PRINT;
			}
			/*if(launchingGame) {
				connection.send("The new game is starting in a few" + SECONDS_PRINT + "\n");
			}*/
			connection.send(message);
			executor.submit(connection);
		} catch (IOException | NullPointerException e) {
			socketActive = false;
			Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "Cannot create a new connection socket.", e);
		}
	}
	
	private boolean isActive() {
		return socketActive;
	}

	private void startSocket() {
		try {
			serverSocket = new ServerSocket(SOCKET_PORT_NUMBER);
			socketActive = true;
			output.println("Waiting for socket connections...");
			while(isActive()) {
				newSocketConnection();
			}
		} catch (IOException e) {
			socketActive = false;
			Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "Cannot initialize the server connection socket.", e);
			Thread.currentThread().interrupt();
		}
	}

	public static void main(String[] args) {
		Server server = new Server();
		server.startRMI();
		server.startSocket();
	}

}
