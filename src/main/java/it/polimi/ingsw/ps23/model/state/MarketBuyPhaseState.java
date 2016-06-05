package it.polimi.ingsw.ps23.model.state;

import it.polimi.ingsw.ps23.model.Game;
import it.polimi.ingsw.ps23.model.Player;
import it.polimi.ingsw.ps23.model.actions.Action;
import it.polimi.ingsw.ps23.model.actions.MarketAction;
import it.polimi.ingsw.ps23.model.market.Market;
import it.polimi.ingsw.ps23.model.market.MarketObject;
import it.polimi.ingsw.ps23.view.visitor.ViewVisitor;

public class MarketBuyPhaseState implements State{
	
	private Market market;
	private Player currentPlayer;
	
	@Override
	public void changeState(Context context, Game game) {
		market = game.getMarket();	
		currentPlayer = game.getCurrentPlayer();
	}
	
	public String getCurrentPlayer() {
		return currentPlayer.getName() + " coins: " + currentPlayer.getCoins();
	}
	
	public boolean canBuy() {
		for(MarketObject marketObject : market.getMarketObject()) {
			if(marketObject.getPlayer() != currentPlayer && marketObject.getCost() == currentPlayer.getCoins()) {
				return true;
			}
		}
		return false;
	}
	
	public String getAvaiableOffers() {
		String avaiableOffers = new String();
		for(MarketObject marketObject : market.getMarketObject()) {
			if(marketObject.getPlayer() != currentPlayer && marketObject.getCost() == currentPlayer.getCoins()) {
				avaiableOffers += marketObject.toString() + "\n";
			}
		}
		return avaiableOffers;
	}

	@Override
	public void acceptView(ViewVisitor view) {
		view.visit(this);
	}
	
	public Action updateMarket(int selectedItem) {
		int i = 0;
		for(MarketObject marketObject : market.getMarketObject()) {
			if(marketObject.getPlayer() != currentPlayer && marketObject.getCost() == currentPlayer.getCoins()) {
				if(i == selectedItem) {
					return new MarketAction(marketObject);
				}
				else {
					i++;
				}
			}
		}
		return null;
	}

}
