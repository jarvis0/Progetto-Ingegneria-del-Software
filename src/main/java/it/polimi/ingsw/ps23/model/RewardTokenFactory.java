package it.polimi.ingsw.ps23.model;

import java.util.ArrayList;
import java.util.List;

import it.polimi.ingsw.ps23.model.BonusesFactory;
import it.polimi.ingsw.ps23.model.bonus.BonusCache;
import it.polimi.ingsw.ps23.model.map.RewardToken;
import it.polimi.ingsw.ps23.model.map.RewardTokens;

public class RewardTokenFactory {
	
	public RewardTokens makeRewardTokens(List<String[]> rawRewardTokens) {
		List<RewardToken> rewardTokens = new ArrayList<>();
		BonusCache.loadCache();
		String[] fields = rawRewardTokens.remove(rawRewardTokens.size() - 1);
		for(String[] rawRewardToken : rawRewardTokens) {
			rewardTokens.add((RewardToken) new BonusesFactory().makeBonuses(fields, rawRewardToken, new RewardToken()));
		}
		return new RewardTokens(rewardTokens);
	}
	
}