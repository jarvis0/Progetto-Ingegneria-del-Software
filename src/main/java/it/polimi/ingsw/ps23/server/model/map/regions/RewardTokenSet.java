package it.polimi.ingsw.ps23.server.model.map.regions;

import java.util.Collections;
import java.util.List;

public class RewardTokenSet {

	private List<RewardToken> rewardTokens;
	
	public RewardTokenSet(List<RewardToken> rewardTokenSet) {
		Collections.shuffle(rewardTokenSet);
		this.rewardTokens = rewardTokenSet;
	}

	public int rewardTokenSize() {
		return rewardTokens.size();
	}
	
	public RewardToken removeRewardToken(int index) {
		return rewardTokens.remove(index);
	}
	
}