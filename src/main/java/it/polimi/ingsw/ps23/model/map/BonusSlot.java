package it.polimi.ingsw.ps23.model.map;

import it.polimi.ingsw.ps23.model.bonus.Bonus;

public interface BonusSlot {
		
	public void addBonus(Bonus bonus);
	
	@Override
	public String toString();

}