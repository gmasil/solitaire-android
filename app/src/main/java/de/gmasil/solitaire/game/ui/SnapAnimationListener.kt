package de.gmasil.solitaire.game.ui

import android.view.View
import android.view.animation.Animation
import java.util.function.BiConsumer

class SnapAnimationListener(
    private val view: View,
    private val x: Float,
    private val y: Float,
    private val callback: BiConsumer<Float, Float>
) : Animation.AnimationListener {

    override fun onAnimationStart(animation: Animation?) {}

    override fun onAnimationEnd(animation: Animation?) {
        view.clearAnimation()
        view.x = x
        view.y = y
        callback.accept(x, y)
    }

    override fun onAnimationRepeat(animation: Animation?) {}
}
