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
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
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
	private static final int MINIMUM_PLAYERS_NUMBER = 2;
	private static final int LAUNCH_TIMEOUT = 1;
	private static final int CONNECTION_TIMEOUT = 120;
	private static final String SECONDS_PRINT =  " seconds...";
	private static final String POLICY_NAME = "COFServer";
	
	private ExecutorService executor;
	
	private ServerSocket serverSocket;

	private List<Connection> socketAllConnections;
	private Map<String, Connection> socketWaitingConnections;
	
	private List<ClientInterface> rmiAllConnections;
	private Map<String, ClientInterface> rmiWaitingConnections;
	
	private boolean launchingGame;
	private boolean active;

	private GameInstancesSet gameInstances;
	
	private PrintStream output;
	private Logger logger;

	private Server() {
		output = new PrintStream(System.out, true);
		gameInstances = new GameInstancesSet();
		active = true;
		logger = Logger.getLogger(this.getClass().getName());
		executor = Executors.newCachedThreadPool();
		socketAllConnections = new ArrayList<>();
		socketWaitingConnections = new HashMap<>();
		rmiAllConnections = new ArrayList<>();
		rmiWaitingConnections = new HashMap<>();
		launchingGame = false;
	}
	
	synchronized void setSocketTimerEnd() {
		notifyAll();
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
			output.println("A new game is starting in " + LAUNCH_TIMEOUT + SECONDS_PRINT);
			String message = "A new game is starting in " + LAUNCH_TIMEOUT + SECONDS_PRINT + "\n";
			for(Connection connection : socketWaitingConnections.values()) {
				connection.send(message);
			}
			for(ClientInterface client : rmiWaitingConnections.values()) {
				infoMessage(client, message);
			}
			Timer timer = new Timer();
			timer.schedule(new RMILaunchingGame(timer, this, LAUNCH_TIMEOUT), LAUNCH_TIMEOUT, 1000L);
		}
	}

	private synchronized void startCountdownFromSocket() {
		if(socketWaitingConnections.size() + rmiWaitingConnections.size() == MINIMUM_PLAYERS_NUMBER) {
			launchingGame = true;
			output.println("A new game is starting in " + LAUNCH_TIMEOUT + SECONDS_PRINT);
			String message = "A new game is starting in " + LAUNCH_TIMEOUT + SECONDS_PRINT + "\n";
			for(Connection connection : socketWaitingConnections.values()) {
				connection.send(message);
			}
			for(ClientInterface client : rmiWaitingConnections.values()) {
				infoMessage(client, message);
			}
			Timer timer = new Timer();
			timer.schedule(new SocketLaunchingGame(this, LAUNCH_TIMEOUT), LAUNCH_TIMEOUT, 1000L);
			boolean loop = true;
			while(loop) {
				try {
					wait();
					loop = false;
				} catch (InterruptedException e) {
					active = false;
					logger.log(Level.SEVERE, "Cannot wait new game countdown.", e);
					Thread.currentThread().interrupt();
				}
			}
			timer.cancel();
			for(Connection connection : socketWaitingConnections.values()) {
				connection.setStarted();
			}
		}
	}
	
	void joinToWaitingList(Connection c, String name) {
		output.println("Player " + name + " has been added to the waiting list.");
		socketWaitingConnections.put(name, c);
		//TODO contorllare che questo player vuole rientrare in una partita precedentemente abbandonata
		startCountdownFromSocket();
	}
	
	synchronized void initializeGame() {
		gameInstances.newGame(socketWaitingConnections, rmiWaitingConnections);
		launchingGame = false;
		socketWaitingConnections.clear();
		rmiWaitingConnections.clear();
		output.println("A new game has started.");
	}
	
	private void newSocketConnection() {
		try {
			Socket newSocket = serverSocket.accept();
			output.println("I've received a new socket client connection.");
			Connection connection = new Connection(this, newSocket, CONNECTION_TIMEOUT);
			socketAllConnections.add(connection);
			String message = "Connection established at " + new Date().toString();
			if(launchingGame) {
				message += "\nA new game is starting in less than " + LAUNCH_TIMEOUT + SECONDS_PRINT;
			}
			connection.send(message + "\nWelcome, what's your name? ");
			if(launchingGame) {
				connection.send("The new game is starting in a few" + SECONDS_PRINT + "\n");
			}
			executor.submit(connection);
		} catch (IOException | NullPointerException e) {
			active = false;
			logger.log(Level.SEVERE, "Cannot create a new connection socket.", e);
		}
	}
	
	//TODO 2 giocatori minimo altrimenti deve terminare il game
	synchronized void deregisterConnection(Connection c) throws ViewNotFoundException {
		String disconnectedPlayer = gameInstances.disconnectPlayer(c);
		socketAllConnections.remove(c);
		Iterator<String> iterator = socketWaitingConnections.keySet().iterator();
		while(iterator.hasNext()){
			if(socketWaitingConnections.get(iterator.next()) == c){
				iterator.remove();
			}
		}
		output.println("The player " + disconnectedPlayer + " has disconnected.");
	}
	
	private boolean isActive() {
		return active;
	}

	private void startRMI() {
		try {
			Registry registry = LocateRegistry.createRegistry(RMI_PORT_NUMBER);
			ServerInterface stub = (ServerInterface) UnicastRemoteObject.exportObject(this, 0);
			registry.bind(POLICY_NAME, stub);
			output.println("Waiting for RMI connections...");
		} catch (RemoteException | AlreadyBoundException e) {
			active = false;
			logger.log(Level.SEVERE, "Cannot create a new registry.", e);
		}
	}

	private void infoMessage(ClientInterface client, String message) {
		try {
			client.infoMessage(message);
		} catch (RemoteException e) {
			logger.log(Level.SEVERE, "Cannot register the new client to the registry.", e);
		}
	}
	
	@Override
	public void registerClient(String name, ClientInterface client) {
		output.println("I've received a new RMI client connection.");
		rmiAllConnections.add(client);
		rmiWaitingConnections.put(name,  client);
		output.println("Player " + name + " has been added to the waiting list.");
		String message = "Connection established at " + new Date().toString();
		if(launchingGame) {
			message += "\nA new game is starting in less than " + LAUNCH_TIMEOUT + SECONDS_PRINT + "\nThe new game is starting in a few seconds...\n";
		}
		infoMessage(client, message);
		startCountdownFromRMI();
	}

	@Override
	public void notify(String message) {
		output.println(message);
	}

	public void startSocket() {
		try {
			serverSocket = new ServerSocket(SOCKET_PORT_NUMBER);
		} catch (IOException e) {
			active = false;
			logger.log(Level.SEVERE, "Cannot initialize the server connection socket.", e);
			Thread.currentThread().interrupt();
		}
		output.println("Waiting for socket connections...");
		while(isActive()) {
			newSocketConnection();
		}
	}

	public static void main(String[] args) {
		Server server;
		server = new Server();
		server.startRMI();
		server.startSocket();
	}

}
