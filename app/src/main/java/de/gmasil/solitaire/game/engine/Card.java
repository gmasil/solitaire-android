package de.gmasil.solitaire.game.engine;

public class Card {
	private int stackNumber;
	private int cardNumber;
	private boolean revealed = false;

	public int getStackNumber() {
		return stackNumber;
	}

	public void setStackNumber(int stackNumber) {
		this.stackNumber = stackNumber;
	}

	public int getCardNumber() {
		return cardNumber;
	}

	public void setCardNumber(int cardNumber) {
		this.cardNumber = cardNumber;
	}

	public boolean isRevealed() {
		return revealed;
	}

	public void setRevealed(boolean revealed) {
		this.revealed = revealed;
	}
}
