package de.gmasil.solitaire.game.engine;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.IntStream;

public class GameField {
	private final List<List<Card>> stacks;

	public GameField(int stackCount) {
		stacks = new ArrayList<>();
		IntStream.range(0, stackCount).map(i -> i + 1).mapToObj(this::init).forEach(stacks::add);
	}

	private List<Card> init(int amount) {
		List<Card> stack = new LinkedList<>();
		IntStream.range(0, amount).mapToObj(x -> new Card()).forEach(stack::add);
		stack.get(stack.size() - 1).setRevealed(true);
		return stack;
	}

	public List<List<Card>> getStacks() {
		return stacks;
	}

	public int getStackCount() {
		return stacks.size();
	}

	public List<CardLocation> getLegalPlacements(CardLocation cardLocation) {
		return getLegalPlacements(cardLocation.getStackNumber(), cardLocation.getCardNumber());
	}

	public void move(CardLocation source, CardLocation target, Consumer<CardLocation> callback) {
		// TODO: check if legal move
		List<Card> sourceStack = stacks.get(source.getStackNumber());
		List<Card> cardsToMove = new LinkedList<>(sourceStack.subList(source.getCardNumber(), sourceStack.size()));
		List<Card> targetStack = stacks.get(target.getStackNumber());
		targetStack.addAll(cardsToMove);
		for (int i = 0; i < targetStack.size(); i++) {
			Card card = targetStack.get(i);
			card.setStackNumber(target.getStackNumber());
			card.setCardNumber(i);
		}
		for (int i = 0; i < cardsToMove.size(); i++) {
			sourceStack.remove(sourceStack.size() - 1);
		}
		if (source.getCardNumber() > 0 && sourceStack.size() == source.getCardNumber()) {
			// a new card can be revealed
			Card cardToFlip = sourceStack.get(sourceStack.size() - 1);
			if (!cardToFlip.isRevealed()) {
				cardToFlip.setRevealed(true);
				callback.accept(new CardLocation(source.getStackNumber(), source.getCardNumber() - 1));
			}
		}
	}

	public List<CardLocation> getLegalPlacements(int stackNumber, int cardNumber) {
		List<CardLocation> list = new LinkedList<>();
		// TODO: implement real placement
		for (int i = 0; i < getStackCount(); i++) {
			if (i == stackNumber) {
				list.add(new CardLocation(i, cardNumber));
			} else {
				list.add(new CardLocation(i, stacks.get(i).size()));
			}
		}
		return list;
	}

	public Card getCardAt(CardLocation cardLocation) {
		return getCardAt(cardLocation.getStackNumber(), cardLocation.getCardNumber());
	}

	public Card getCardAt(int stackNumber, int cardNumber) {
		if (cardNumber >= 0 && stackNumber >= 0 && stacks.size() > stackNumber) {
			List<Card> stack = stacks.get(stackNumber);
			if (stack.size() > cardNumber) {
				Card card = stack.get(cardNumber);
				card.setStackNumber(stackNumber);
				card.setCardNumber(cardNumber);
				return card;
			}
		}
		return null;
	}

	public List<Card> getCardStackAt(CardLocation cardLocation) {
		return getCardStackAt(cardLocation.getStackNumber(), cardLocation.getCardNumber());
	}

	public List<Card> getCardStackAt(int stackNumber, int cardNumber) {
		List<Card> cardStack = new LinkedList<>();
		if (cardNumber >= 0 && stackNumber >= 0 && stacks.size() > stackNumber) {
			List<Card> stack = stacks.get(stackNumber);
			while (stack.size() > cardNumber) {
				Card card = stack.get(cardNumber);
				if (!card.isRevealed()) {
					return cardStack;
				}
				card.setStackNumber(stackNumber);
				card.setCardNumber(cardNumber);
				cardStack.add(card);
				cardNumber++;
			}
		}
		return cardStack;
	}
}
