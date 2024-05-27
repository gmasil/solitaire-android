package de.gmasil.solitaire.game.ui

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Point
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener
import android.view.animation.ScaleAnimation
import android.view.animation.TranslateAnimation
import android.widget.RelativeLayout
import de.gmasil.solitaire.R
import de.gmasil.solitaire.game.engine.CardLocation
import de.gmasil.solitaire.game.engine.GameField
import java.util.Collections
import java.util.LinkedList
import java.util.stream.Collectors

@SuppressLint("ClickableViewAccessibility")
class GameFieldView(
    private val context: Context,
    private val gameField: GameField,
    private val parent: RelativeLayout
) : OnTouchListener {
    private val stacks: MutableList<MutableList<CardView>> = mutableListOf()
    private val draggingInfo = DraggingInfo()

    private val stackSpace = 15
    private val topSpace = stackSpace
    private val cardWidth = 1050 / gameField.stackCount - stackSpace
    private val cardHeight = 1740 * cardWidth / 1200
    private val cardSpace = cardHeight / 2

    init {
        setupGameField()
        parent.setOnTouchListener(this)
    }

    private fun getAllCardViews(): List<CardView> {
        return stacks.stream().flatMap { s -> s.stream() }.collect(Collectors.toList())
    }

    private fun setupGameField() {
        var stackNumber = 0
        gameField.stacks.forEach { stack ->
            stacks.add(mutableListOf())
            var cardNumber = 0
            stack.forEach { card ->
                val cardView = createCard(CardLocation(stackNumber, cardNumber), card.isRevealed)
                stacks[stackNumber].add(cardView)
                parent.addView(cardView)
                cardNumber++
            }
            stackNumber++
        }
    }

    private fun createCard(loc: CardLocation, revealed: Boolean): CardView {
        return CardView(context, loc.stackNumber, loc.cardNumber, true).apply {
            if (revealed) {
                setImageResource(R.drawable.ace_of_spades)
            } else {
                setImageResource(R.drawable.card_backside)
            }
            setBackgroundResource(R.drawable.border)
            setLayoutParams(RelativeLayout.LayoutParams(cardWidth, cardHeight))
            val pixelLocation = translateCardToPixel(loc.stackNumber, loc.cardNumber)
            this.x = pixelLocation.x.toFloat()
            this.y = pixelLocation.y.toFloat()
        }
    }

    private fun getCardViewStackSafe(cardLocation: CardLocation): List<CardView> {
        val cardViewStack = mutableListOf<CardView>()
        var cardStack = gameField.getCardStackAt(cardLocation.stackNumber, cardLocation.cardNumber)
        if (cardStack.isEmpty()) {
            cardLocation.cardNumber--
        }
        cardStack = gameField.getCardStackAt(cardLocation.stackNumber, cardLocation.cardNumber)
        if (cardStack.isEmpty()) {
            return cardViewStack
        }
        cardStack.forEach {
            cardViewStack += getCardViewFromLocation(CardLocation(it.stackNumber, it.cardNumber))!!
        }
        return cardViewStack
    }

    private fun startDragging(x: Float, y: Float) {
        draggingInfo.dragging = false
        val cardLocation = translatePixelToCard(x.toInt(), y.toInt())
        val cardViewStack = getCardViewStackSafe(cardLocation)
        if (cardViewStack.isEmpty()) {
            return
        }
        draggingInfo.dragging = true
        draggingInfo.cardLocation = cardLocation
        draggingInfo.deltaX = x - cardViewStack[0].x
        draggingInfo.deltaY = y - cardViewStack[0].y
        draggingInfo.cards.clear()
        draggingInfo.cards += cardViewStack
        updateCards(draggingInfo.cards, x, y)
        draggingInfo.cards.forEach { it.elevation += 100f }
    }

    private fun stopDragging(x: Float, y: Float) {
        if (!draggingInfo.dragging) {
            return
        }
        draggingInfo.dragging = false
        val legalPlacements =
            gameField.getLegalPlacements(
                draggingInfo.cardLocation.stackNumber, draggingInfo.cardLocation.cardNumber)
        // determine target location
        val legalPixels = mutableListOf<Point>()
        legalPlacements.forEach {
            val p = translateCardToPixel(it)
            p.x += cardWidth / 2
            p.y += cardHeight / 2
            legalPixels.add(p)
        }
        Collections.sort(legalPixels, DistanceComparator(x, y))
        val targetLocation =
            translatePixelToCard(
                legalPixels[0].x - cardWidth / 2, legalPixels[0].y - cardHeight / 2)
        // move game field cards
        gameField.move(draggingInfo.cardLocation, targetLocation) {
            // callback indicates a new revealed card
            stacks[it.stackNumber][it.cardNumber].apply {
                revealed = true
                flipCard(this) { setImageResource(R.drawable.ace_of_spades) }
            }
        }
        // move views
        val cardPixel = translateCardToPixel(targetLocation)
        snapCardsTo(draggingInfo.cards, cardPixel) { updateElevation() }
        // update card views
        move(draggingInfo.cardLocation, targetLocation)
    }

    private fun cancelDragging(x: Float, y: Float) {
        if (!draggingInfo.dragging) {
            return
        }
        draggingInfo.dragging = false
        val originalPixelLocation = translateCardToPixel(draggingInfo.cardLocation)
        snapCardsTo(draggingInfo.cards, originalPixelLocation) { updateElevation() }
    }

    private fun updateElevation() {
        getAllCardViews().forEach { cardView -> cardView.elevation = cardView.cardNumber.toFloat() }
    }

    private fun move(source: CardLocation, target: CardLocation) {
        val sourceStack = stacks[source.stackNumber]
        val cardsToMove = LinkedList(sourceStack.subList(source.cardNumber, sourceStack.size))
        val targetStack = stacks[target.stackNumber]
        targetStack.addAll(cardsToMove)
        for (i in targetStack.indices) {
            val card = targetStack[i]
            card.stackNumber = target.stackNumber
            card.cardNumber = i
        }
        for (i in cardsToMove.indices) {
            sourceStack.removeAt(sourceStack.size - 1)
        }
    }

    private fun getCardViewFromLocation(cardLocation: CardLocation): CardView? {
        if (cardLocation.stackNumber < 0 ||
            cardLocation.cardNumber < 0 ||
            cardLocation.stackNumber >= stacks.size ||
            cardLocation.cardNumber >= stacks[cardLocation.stackNumber].size) {
            return null
        }
        return stacks[cardLocation.stackNumber][cardLocation.cardNumber]
    }

    private fun flipCard(card: CardView, callbackHalfWay: () -> Unit) {
        // flip half way
        ScaleAnimation(1f, 0f, 1f, 1f, card.x + cardWidth / 2, 0f).apply {
            setDuration(100)
            fillAfter = false
            setAnimationListener(
                AnimationEndListener {
                    callbackHalfWay.invoke()
                    // flip completely
                    ScaleAnimation(0f, 1f, 1f, 1f, card.x + cardWidth / 2, 0f).apply {
                        setDuration(100)
                        fillAfter = false
                        card.startAnimation(this)
                    }
                })
            card.startAnimation(this)
        }
    }

    private fun snapCardTo(card: CardView, target: Point, callback: () -> Unit) {
        val animation = TranslateAnimation(0f, target.x - card.x, 0f, target.y - card.y)
        animation.setDuration(100)
        animation.fillAfter = false
        animation.setAnimationListener(
            SnapAnimationListener(card, target.x.toFloat(), target.y.toFloat()) { x, y ->
                card.x = x
                card.y = y
                callback.invoke()
            })
        card.startAnimation(animation)
    }

    private fun snapCardsTo(cards: List<CardView>, target: Point, callback: () -> Unit) {
        var yOffset = 0
        cards.forEach { card ->
            snapCardTo(card, Point(target.x, target.y + yOffset), callback)
            yOffset += cardSpace
        }
    }

    private fun updateCard(card: CardView, x: Float, y: Float) {
        card.x = x - draggingInfo.deltaX
        card.y = y - draggingInfo.deltaY
    }

    private fun updateCards(cards: List<CardView>, x: Float, y: Float) {
        var yOffset = 0
        cards.forEach { card ->
            updateCard(card, x, y + yOffset)
            yOffset += cardSpace
        }
    }

    private fun translateCardToPixel(cardLocation: CardLocation): Point {
        return translateCardToPixel(cardLocation.stackNumber, cardLocation.cardNumber)
    }

    private fun translateCardToPixel(stackNumber: Int, cardNumber: Int): Point {
        return Point(
            stackNumber * (cardWidth + stackSpace) + stackSpace, cardNumber * cardSpace + topSpace)
    }

    private fun translatePixelToCard(x: Int, y: Int): CardLocation {
        return CardLocation(
            (x - (stackSpace / 2)) / (cardWidth + stackSpace), (y - topSpace) / cardSpace)
    }

    override fun onTouch(view: View, event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                startDragging(event.x, event.y)
            }
            MotionEvent.ACTION_UP -> {
                stopDragging(event.x, event.y)
            }
            MotionEvent.ACTION_CANCEL -> {
                cancelDragging(event.x, event.y)
            }
            MotionEvent.ACTION_MOVE -> {
                if (draggingInfo.dragging) {
                    updateCards(draggingInfo.cards, event.x, event.y)
                }
            }
            else -> {
                Log.i("touch", "got ${event.action} ${MotionEvent.actionToString(event.action)}")
            }
        }
        return true
    }
}
