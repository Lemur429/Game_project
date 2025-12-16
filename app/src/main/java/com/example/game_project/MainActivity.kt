package com.example.game_project

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.game_project.utilities.Constants
import java.util.Timer
import java.util.TimerTask

class MainActivity : AppCompatActivity() {

    private lateinit var gameLayout: LinearLayout
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
        gameLayout = findViewById(R.id.gameLayout)
        lanes = arrayOf(findViewById(R.id.left_lane)  ,  findViewById(R.id.center_lane)  ,  findViewById(R.id.right_lane))
        btnLeft.setOnClickListener { moveLeft() }
        btnRight.setOnClickListener { moveRight() }

        player.post { updatePlayerPosition() }
        startSpawning()
        gameLoop()
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

    private val obstacles = mutableListOf<ImageView>()
    private val spawnHandler = Handler(Looper.getMainLooper())

    private fun spawnCar() {
        val car = ImageView(this)
        car.setImageResource(R.drawable.car)
        car.layoutParams = FrameLayout.LayoutParams(
            player.width,
            player.height
        )

        // Choose random lane
        val laneIndex = (0..2).random()
        car.y = -player.height.toFloat()

        lanes[laneIndex].addView(car)
        obstacles.add(car)
    }
    private fun startSpawning() {
        spawnHandler.postDelayed(object : Runnable {
            override fun run() {
                spawnCar()
                spawnHandler.postDelayed(this, Constants.Car.SPAWN_DELAY) // spawn every 1.2s
            }
        }, Constants.Car.SPAWN_DELAY)
    }

    private val gameHandler = Handler(Looper.getMainLooper())
    private val speed = 12f

    private fun gameLoop() {
        gameHandler.post(object : Runnable {
            override fun run() {

                val iterator = obstacles.iterator()
                while (iterator.hasNext()) {
                    val car = iterator.next()
                    car.y += speed

                    if (car.y > gameLayout.height) {
                        gameLayout.removeView(car)
                        iterator.remove()
                        continue
                    }

                    ///// need to make collission check

                }

                gameHandler.postDelayed(this, Constants.Car.MOVE_DELAY)
            }
        })
    }
}