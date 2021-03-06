package it.polimi.ingsw.ps23.server.model.map;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Provides deck functionality to game cards such as cards shuffle and 
 * picking a parametric number of cards from the specified deck.
 * @author Giuseppe Mascellaro & Mirco Manzoni
 *
 */
public class Deck implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1360173791794831347L;
	
	private List<Card> cards;
	private List<Card> originalCards;
	
	/**
	 * Shuffles the specified deck.
	 * @param cards - list of card to be assumed as a deck to be shuffled
	 */
	public Deck(List<Card> cards) {
		originalCards = new ArrayList<>();
		originalCards.addAll(cards);
		Collections.shuffle(cards);
		this.cards = cards;
	}

	public List<Card> getCards() {
		return cards;
	}
	
	private void reinitializeDeck() {
		cards.addAll(originalCards);
		Collections.shuffle(cards);
	}
	
	/**
	 * Picks the specified parametric number of cards from the specified
	 * deck.
	 * <p>
	 * Removes the picked cards from the specified deck.
	 * @param cardsNumber - number of cards to be picked
	 * @return cards picked from the deck
	 */
	public List<Card> pickCards(int cardsNumber) {
		List<Card> pickedCards = new ArrayList<>();
		for(int i = 0; i < cardsNumber; i++) {
			if(cards.isEmpty()) {
				reinitializeDeck();
			}
			pickedCards.add(cards.remove(cards.size() - 1));
		}
		return pickedCards;
	}

	/**
	 * Picks the top card of the specified deck and removes it.
	 * @return card picked from the deck
	 */
	public Card pickCard() {
		if(cards.isEmpty()) {
			reinitializeDeck();
		}
		return cards.remove(cards.size() - 1);
	}
	
	@Override
	public String toString() {
		return getCards().toString();
	}
	
}
