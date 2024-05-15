package de.gmasil.solitaire.game.ui

import de.gmasil.solitaire.game.engine.CardLocation

class DraggingInfo {
    var dragging = false
    var cards = mutableListOf<CardView>()
    var cardLocation = CardLocation(0, 0)

    // delta x,y from dragging start to card position
    var deltaX: Float = 0.0f
    var deltaY: Float = 0.0f
}
