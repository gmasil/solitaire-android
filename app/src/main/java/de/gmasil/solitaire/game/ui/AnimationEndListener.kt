package de.gmasil.solitaire.game.ui

import android.view.animation.Animation
import java.util.function.Consumer

class AnimationEndListener(private val callback: Consumer<Animation?>) :
    Animation.AnimationListener {

    override fun onAnimationStart(animation: Animation?) {}

    override fun onAnimationEnd(animation: Animation?) {
        callback.accept(animation)
    }

    override fun onAnimationRepeat(animation: Animation?) {}
}
