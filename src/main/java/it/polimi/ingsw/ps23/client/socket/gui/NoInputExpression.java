package it.polimi.ingsw.ps23.client.socket.gui;

import java.io.PrintStream;

import it.polimi.ingsw.ps23.client.socket.Expression;
import it.polimi.ingsw.ps23.client.socket.Parser;
import it.polimi.ingsw.ps23.client.socket.RemoteGUIView;
import it.polimi.ingsw.ps23.client.socket.TerminalExpression;
import it.polimi.ingsw.ps23.client.socket.gui.interpreter.components.SocketSwingUI;
/**
 * Provides methods to print game asynchronous messages such as players disconnection or
 * reconnection into GUI text area.
 * @author Giuseppe Mascellaro
 *
 */
public class NoInputExpression implements Parser {

	private static final String PLAYER_NAME_TAG_OPEN = "<player_name>";
	private static final String PLAYER_NAME_TAG_CLOSE = "</player_name>";
	private static final String MAP_TYPE_TAG_OPEN = "<map_type>";
	private static final String MAP_TYPE_TAG_CLOSE = "</map_type>";
	
	private PrintStream output;
	
	private Expression expression;
	
	private RemoteGUIView remoteView;
	
	private SocketSwingUI swingUI;
	
	private PlayerNameExpression isPlayerName;
	private MapTypeExpression isMapType;
	/**
	 * Constructs the object initializing all the variables at the default values.
	 * @param remoteView - the current remote view of the user
	 * @param output - the references to the print stream
	 * @param expression - set of tags in witch the message is.
	 */
	public NoInputExpression(RemoteGUIView remoteView, PrintStream output, Expression expression) {
		this.output = output;
		this.expression = expression;
		this.remoteView = remoteView;
		isMapType = getMapTypeExpression();
		isPlayerName = getPlayerNameExpression();
	}
	
	private PlayerNameExpression getPlayerNameExpression() {
		Expression playerNameExpression = new TerminalExpression(PLAYER_NAME_TAG_OPEN, PLAYER_NAME_TAG_CLOSE);
		return new PlayerNameExpression(remoteView, playerNameExpression);
	}
	
	private MapTypeExpression getMapTypeExpression() {
		Expression mapTypeExpression = new TerminalExpression(MAP_TYPE_TAG_OPEN, MAP_TYPE_TAG_CLOSE);
		return new MapTypeExpression(remoteView, output, mapTypeExpression);
	}
	
	public void setSwingUI(SocketSwingUI swingUI) {
		this.swingUI = swingUI;
	}

	/**
	 * Prints a message from the server into the GUI text box.
	 * @param message - message to be printed to the GUI.
	 */
	public void infoMessage(String message) {
		if(expression.interpret(message)) {
			swingUI.appendConsoleText("\n" + expression.selectBlock(message));
		}
	}
	
	@Override
	public String parse(String message) {
		if(expression.interpret(message)) {
			String parsingMessage = expression.selectBlock(message);
			parsingMessage = isPlayerName.parse(parsingMessage);
			if(parsingMessage.contains("Invalid name format.")) {
				remoteView.setInvalidName();
			}
			String mapType = isMapType.parse(parsingMessage);
			if(mapType.equals(parsingMessage)) {
				output.println(parsingMessage);
			}
			return mapType;
		}
		return message;
	}

}
