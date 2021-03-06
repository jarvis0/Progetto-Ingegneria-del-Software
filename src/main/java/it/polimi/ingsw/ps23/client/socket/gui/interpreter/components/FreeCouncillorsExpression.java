package it.polimi.ingsw.ps23.client.socket.gui.interpreter.components;

import java.util.ArrayList;
import java.util.List;

import it.polimi.ingsw.ps23.client.socket.Expression;
import it.polimi.ingsw.ps23.client.socket.gui.interpreter.GUIParser;

class FreeCouncillorsExpression extends GUIParser {

	private Expression expression;
	
	private List<String> freeCouncillors;
	
	FreeCouncillorsExpression(Expression expression) {
		this.expression = expression;
	}

	List<String> getFreeCouncillors() {
		return freeCouncillors;
	}

	@Override
	protected void parse(String message) {
		if(expression.interpret(message)) {
			String parsingMessage = expression.selectBlock(message);
			freeCouncillors = new ArrayList<>();
			do {
				parsingMessage = addField(freeCouncillors, parsingMessage);
			} while(!parsingMessage.isEmpty());
		}
	}

}
