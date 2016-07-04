package it.polimi.ingsw.ps23.client.socket.gui;

import java.io.PrintStream;

import it.polimi.ingsw.ps23.client.socket.Expression;
import it.polimi.ingsw.ps23.client.socket.Parser;
import it.polimi.ingsw.ps23.client.socket.RemoteGUIView;
import it.polimi.ingsw.ps23.client.socket.TerminalExpression;

public class NoInputExpression implements Parser {

	private static final String PLAYER_NAME_TAG_OPEN = "<player_name>";
	private static final String PLAYER_NAME_TAG_CLOSE = "</player_name>";
	private static final String MAP_TYPE_TAG_OPEN = "<map_type>";
	private static final String MAP_TYPE_TAG_CLOSE = "</map_type>";
	
	private PrintStream output;
	
	private Expression expression;
	
	private PlayerNameExpression isPlayerName;
	private MapTypeExpression isMapType;
	
	public NoInputExpression(RemoteGUIView remoteView, PrintStream output, Expression expression) {
		this.output = output;
		this.expression = expression;
		isMapType = getMapTypeExpression(remoteView);
		isPlayerName = getPlayerNameExpression(remoteView);
	}
	
	private PlayerNameExpression getPlayerNameExpression(RemoteGUIView remoteView) {
		Expression playerNameExpression = new TerminalExpression(PLAYER_NAME_TAG_OPEN, PLAYER_NAME_TAG_CLOSE);
		return new PlayerNameExpression(remoteView, playerNameExpression);
	}
	
	private MapTypeExpression getMapTypeExpression(RemoteGUIView remoteView) {
		Expression mapTypeExpression = new TerminalExpression(MAP_TYPE_TAG_OPEN, MAP_TYPE_TAG_CLOSE);
		return new MapTypeExpression(remoteView, output, mapTypeExpression);
	}
	
	@Override
	public String parse(String message) {
		if(expression.interpret(message)) {
			String parsingMessage = expression.selectBlock(message);
			parsingMessage = isPlayerName.parse(parsingMessage);
			String mapType = isMapType.parse(parsingMessage);
			if(mapType.equals(parsingMessage)) {
				output.println(parsingMessage);
			}
			return mapType;
		}
		return message;
	}

}
