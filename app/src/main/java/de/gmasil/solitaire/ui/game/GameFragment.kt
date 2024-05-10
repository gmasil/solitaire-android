package de.gmasil.solitaire.ui.game

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
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
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentGameBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val textView: TextView = binding.textGame
        textView.text = requireContext().getString(R.string.menu_game)

        root.setOnTouchListener { view, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    Log.i("touch", "action down!!")
                }
                else -> {
                    Log.i("touch", "got ${event.action}")
                }
            }
            true
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
