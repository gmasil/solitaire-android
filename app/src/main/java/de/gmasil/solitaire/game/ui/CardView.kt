package de.gmasil.solitaire.game.ui

import android.annotation.SuppressLint
import android.content.Context

@SuppressLint("ViewConstructor")
class CardView(context: Context, var stackNumber: Int, var cardNumber: Int, var revealed: Boolean) :
    androidx.appcompat.widget.AppCompatImageView(context) {}
