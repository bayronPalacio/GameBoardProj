package com.example.gameboardproj

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_game_board.*

class GameBoardActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game_board)

        // Switch Game Boards
        // False Board - Left
        falseLayout.setOnClickListener {
            val fragment = LeftBoardFragment.newInstance()
            val transaction =  supportFragmentManager.beginTransaction()
            transaction.replace(R.id.gameBoardFrameLayout, fragment)
            transaction.commit()
        }

        // True Board - Right
        trueLayout.setOnClickListener {
            val fragment = RightBoardFragment.newInstance()
            val transaction =  supportFragmentManager.beginTransaction()
            transaction.replace(R.id.gameBoardFrameLayout, fragment)
            transaction.commit()
        }
    }
}
