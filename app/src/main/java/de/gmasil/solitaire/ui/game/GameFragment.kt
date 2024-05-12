package de.gmasil.solitaire.ui.game

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import de.gmasil.solitaire.R
import de.gmasil.solitaire.databinding.FragmentGameBinding


class GameFragment : Fragment() {

    private var _binding: FragmentGameBinding? = null

    private val binding
        get() = _binding!!

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentGameBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val textView: TextView = binding.textGame
        textView.text = requireContext().getString(R.string.menu_game)

        val gameField = root.findViewById<RelativeLayout>(R.id.game_field)

        imageView = ImageView(requireContext())
        imageView!!.setImageResource(R.drawable.ace_of_spades);
        val layoutParams = RelativeLayout.LayoutParams(cw, ch)
        imageView!!.setLayoutParams(layoutParams)
        imageView!!.setBackgroundResource(R.drawable.border)
        imageView!!.x = cx
        imageView!!.y = cy

        gameField.addView(imageView)

        gameField.setOnTouchListener { view, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    startDragging(event.x, event.y)
                }

                MotionEvent.ACTION_UP -> {
                    Log.i("touch", "stop dragging")
                    dragging = false
                }

                MotionEvent.ACTION_MOVE -> {
                    if (dragging) {
                        updateCard(event.x, event.y)
                    }
                }

                else -> {
                    Log.i(
                        "touch", "got ${event.action} ${MotionEvent.actionToString(event.action)}"
                    )
                }
            }
            true
        }

        return root
    }

    private fun updateCard(x: Float, y: Float) {
        cx = x - dx
        cy = y - dy
        imageView!!.x = cx
        imageView!!.y = cy
    }

    private fun isTouchingCard(x: Float, y: Float): Boolean {
        return x >= cx && x <= cx + cw && y >= cy && y <= cy + ch
    }

    private fun startDragging(x: Float, y: Float) {
        if (!isTouchingCard(x, y)) {
            dragging = false
            return
        }
        dragging = true
        dx = x - cx
        dy = y - cy
        Log.i("touch", "start dragging $dx, $dy")
        updateCard(x, y)
    }

    var dragging = false
    var imageView: ImageView? = null

    // card position and size
    var cx: Float = 20.0f
    var cy: Float = 20.0f
    var cw: Int = 207
    var ch: Int = 300

    // delta x,y from dragging start to card position
    var dx: Float = 0.0f
    var dy: Float = 0.0f

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
