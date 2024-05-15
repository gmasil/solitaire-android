package de.gmasil.solitaire.game.ui

import android.graphics.Point
import kotlin.math.pow
import kotlin.math.sqrt

class DistanceComparator(private val refX: Float, private val refY: Float) : Comparator<Point> {

    override fun compare(p1: Point, p2: Point): Int {
        val d1 = distance(p1)
        val d2 = distance(p2)
        return d1.compareTo(d2)
    }

    private fun distance(p: Point): Double {
        val dx = p.x - refX
        val dy = p.y - refY
        return sqrt(dx.toDouble().pow(2.0) + dy.toDouble().pow(2.0))
    }
}
