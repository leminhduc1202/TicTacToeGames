package com.mdapp.tictactoe

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.GridLayout
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.android.gms.ads.*
import com.mdapp.tictactoe.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val boardCells = Array(3) { arrayOfNulls<ImageView>(3) }
    var board = Board()

    lateinit var mAdView: AdView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        MobileAds.initialize(this){}
        mAdView = binding.adviewBanner
        val adRequest = AdRequest.Builder().build()
        mAdView.loadAd(adRequest)
        mAdView.adListener = object : AdListener(){
            override fun onAdLoaded() {
                // Code to be executed when an ad finishes loading.
                Log.d("====", "onAdLoaded")
            }

            override fun onAdFailedToLoad(adError : LoadAdError) {
                // Code to be executed when an ad request fails.
                Log.d("====", adError.message)
            }

            override fun onAdOpened() {
                // Code to be executed when an ad opens an overlay that
                // covers the screen.
                Log.d("====", "onAdOpened")
            }

            override fun onAdClicked() {
                // Code to be executed when the user clicks on an ad.
                Log.d("====", "onAdClicked")
            }

            override fun onAdClosed() {
                // Code to be executed when the user is about to return
                // to the app after tapping on an ad.
                Log.d("====", "onAdClosed")
            }
        }

        loadBoard()

        binding.btRestart.setOnClickListener {
            board = Board()

            binding.tvResult.text = " "

            mapBoardToUi()
        }

    }

    private fun mapBoardToUi() {
        for (i in board.board.indices) {
            for (j in board.board.indices) {
                when (board.board[i][j]) {
                    Board.PLAYER -> {
                        boardCells[i][j]?.setImageResource(R.drawable.ic_o)
                        boardCells[i][j]?.isEnabled = false
                    }
                    Board.COMPUTER -> {
                        boardCells[i][j]?.setImageResource(R.drawable.ic_x)
                        boardCells[i][j]?.isEnabled = false
                    }
                    else -> {
                        boardCells[i][j]?.setImageResource(0)
                        boardCells[i][j]?.isEnabled = true
                    }
                }
            }
        }
    }

    inner class CellClickListener(
        val i: Int,
        val j: Int
    ) : View.OnClickListener {
        override fun onClick(v: View?) {
            if (!board.isGameOver) {
                val cell = Cell(i, j)

                board.placeMove(cell, Board.PLAYER)

                board.minimax(0, Board.COMPUTER)

                board.computersMove?.let {
                    board.placeMove(it, Board.COMPUTER)
                }
                mapBoardToUi()
            }
            when {
                board.hasComputerWon() -> binding.tvResult.text = getString(R.string.computer_won)
                board.hasPlayerWon() -> binding.tvResult.text = getString(R.string.you_won)
                board.isGameOver -> binding.tvResult.text = getString(R.string.game_draw)
            }
        }
    }

    private fun loadBoard() {
        for (i in boardCells.indices) {
            for (j in boardCells.indices) {
                boardCells[i][j] = ImageView(this)
                boardCells[i][j]?.layoutParams = GridLayout.LayoutParams().apply {
                    rowSpec = GridLayout.spec(i)
                    columnSpec = GridLayout.spec(j)
                    width = 250
                    height = 230
                    bottomMargin = 5
                    topMargin = 5
                    leftMargin = 5
                    rightMargin = 5
                }
                boardCells[i][j]?.setBackgroundColor(ContextCompat.getColor(this, R.color.primrose))
                binding.glBoard.addView(boardCells[i][j])
                boardCells[i][j]?.setOnClickListener(CellClickListener(i, j))
            }
        }
    }
}