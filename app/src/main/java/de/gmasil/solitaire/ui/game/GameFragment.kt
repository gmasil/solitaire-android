package de.gmasil.solitaire.ui.game

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import de.gmasil.solitaire.R
import de.gmasil.solitaire.databinding.FragmentGameBinding
import de.gmasil.solitaire.game.engine.GameField
import de.gmasil.solitaire.game.ui.GameFieldView

class GameFragment : Fragment() {

    private var _binding: FragmentGameBinding? = null

    private val binding
        get() = _binding!!

    private var _gameField: GameField? = null

    private val gameField
        get() = _gameField!!

    private var _gameFieldView: GameFieldView? = null
    private val gameFieldView
        get() = _gameFieldView!!

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

        val relativeLayout = root.findViewById<RelativeLayout>(R.id.game_field)

        _gameField = GameField(7)
        _gameFieldView = GameFieldView(requireContext(), gameField, relativeLayout)

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
