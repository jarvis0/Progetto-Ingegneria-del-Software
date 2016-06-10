package it.polimi.ingsw.ps23.model.bonus;

import java.util.List;

import javax.naming.InsufficientResourcesException;

import it.polimi.ingsw.ps23.model.Game;
import it.polimi.ingsw.ps23.model.Player;
import it.polimi.ingsw.ps23.model.TurnHandler;
import it.polimi.ingsw.ps23.model.map.PermissionCard;

public class RecycleBuildingPermitBonus extends Bonus implements SuperBonus {
	
	private static final int  VALUE_POSITION = 0;

	public RecycleBuildingPermitBonus(String name) {
		super(name);
	}

	@Override
	public void updateBonus(Game game, TurnHandler turnHandler) throws InsufficientResourcesException {
		turnHandler.addSuperBonus(this);		
	}

	@Override
	public String checkBonus(Player currentPlayer) {
		if(!currentPlayer.getPermissionTotalHandeck().getCards().isEmpty()){
		return  "You have encountred a Recycle Building Permit Bonus on Nobility Track \nchoose the used permit tile for take bonuses: " +currentPlayer.getPermissionTotalHandeck().toString();
		}
	return "Impossible using Recycle Building Permit Bonus because your Permission Hand Deck is empty (0 to skip)";
	}	

	@Override
	public void acquireSuperBonus(List<String> input, Game game, TurnHandler turnHandler) {
		if (!(Integer.parseInt(input.get(VALUE_POSITION)) == 0)) {
			((PermissionCard) game.getCurrentPlayer().getPermissionTotalHandeck().getCardInPosition(Integer.parseInt(input.get(VALUE_POSITION)) - 1)).useBonus(game, turnHandler);;
		}
	}

}
