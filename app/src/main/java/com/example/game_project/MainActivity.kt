package com.example.game_project

import android.os.Bundle
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.RelativeLayout
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {

    val car = ImageView(this)
    private lateinit var lanes: Array<RelativeLayout>
    private lateinit var player: ImageView
    private var playerPos=1; ///  0 - left lane 1- middle lane 2- right lane

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val btnLeft: ImageButton = findViewById(R.id.left_button)
        val btnRight: ImageButton = findViewById(R.id.right_button)
        player = findViewById(R.id.player)

        lanes = arrayOf(findViewById(R.id.left_lane)  ,  findViewById(R.id.center_lane)  ,  findViewById(R.id.right_lane))
        btnLeft.setOnClickListener { moveLeft() }
        btnRight.setOnClickListener { moveRight() }

        car.setImageResource(R.drawable.car)
        car.layoutParams = FrameLayout.LayoutParams(120, 120)

        player.post { updatePlayerPosition() }

    }
    private fun moveLeft()
    {
        if(playerPos >0)
        {
            playerPos--
            updatePlayerPosition()
        }
    }

    private fun moveRight()
    {
        if(playerPos < 2)
        {
            playerPos++
            updatePlayerPosition()
        }
    }
    private fun updatePlayerPosition()
    {
        val parent = player.parent as ViewGroup
        parent.removeView(player)
        lanes[playerPos].addView(player);
    }
}