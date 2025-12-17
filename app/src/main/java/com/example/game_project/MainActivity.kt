package com.example.game_project

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
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
import androidx.core.view.contains
import com.example.game_project.utilities.Constants
import com.example.game_project.utilities.SignalManager


class MainActivity : AppCompatActivity() {

    /// --------- LAYOUTS VARIABLES
    private lateinit var gameLayout: LinearLayout
    private lateinit var lanes: Array<RelativeLayout>
    /// --------- /LAYOUTS VARIABLES/

    /// ---------- PLAYER VARIABLES
    private lateinit var player: ImageView
    private var playerPos=1 ///  0 - left lane 1- middle lane 2- right lane
    private var hearts=3
    private lateinit var hearts_view: Array<ImageView>
    /// ---------- /PLAYER VARIABLES/

    /// --------- ENEMIES VARIABLES
    private val obstacles = mutableListOf<ImageView>() // list of the cars
    private val spawnHandler = Handler(Looper.getMainLooper())
    private val gameHandler = Handler(Looper.getMainLooper())

    /// --------- /ENEMIES VARIABLES/

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        findViews()
        initViews()
        SignalManager.init(applicationContext)

        startSpawning()
        gameLoop()
    }
    private fun findViews()
    {
        player = findViewById(R.id.player)
        gameLayout = findViewById(R.id.gameLayout)
        lanes = arrayOf(findViewById(R.id.left_lane)  ,  findViewById(R.id.center_lane)  ,  findViewById(R.id.right_lane))
        hearts_view = arrayOf( findViewById(R.id.heart1) , findViewById(R.id.heart2) , findViewById(R.id.heart3))
    }
    private fun initViews()
    {
        val btnLeft: ImageButton = findViewById(R.id.left_button)
        val btnRight: ImageButton = findViewById(R.id.right_button)
        btnLeft.setOnClickListener { moveLeft() }
        btnRight.setOnClickListener { moveRight() }
        player.post { updatePlayerPosition() }
    }


    /// ---------- BUTTONS FUNCTIONS
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
    /// ---------- /BUTTONS FUNCTIONS/

    /// ---------- UPDATE VIEWS

    private fun updatePlayerPosition()
    {
        val parent = player.parent as ViewGroup
        parent.removeView(player)
        lanes[playerPos].addView(player)
    }
    private fun updateHearts() {
        hearts_view[0].visibility = if (hearts >= 1) View.VISIBLE else View.GONE
        hearts_view[1].visibility = if (hearts >= 2) View.VISIBLE else View.GONE
        hearts_view[2].visibility = if (hearts >= 3) View.VISIBLE else View.GONE
    }
    /// ---------- /UPDATE VIEWS/


    /// ---------- SPAWN CARS FUNCTIONS

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
    private fun stopSpawning()
    {
        spawnHandler.removeCallbacksAndMessages(null)
    }
    private fun startSpawning() {
        spawnHandler.postDelayed(object : Runnable {
            override fun run() {
                spawnCar()
                spawnHandler.postDelayed(this, Constants.Car.SPAWN_DELAY) // spawn every 1.2s
            }
        }, Constants.Car.SPAWN_DELAY)
    }

    /// ---------- /SPAWN CARS FUNCTIONS/


    /// ---------- GAME FUNCTIONS

    private fun gameOver()
    {
        SignalManager
            .getInstance()
            .toast(
                "YOU DIED!",
                SignalManager.ToastLength.LONG)
        SignalManager
            .getInstance()
            .vibrate()
        stopSpawning() // stop spawning cars so we will restart the game
        val iterator = obstacles.iterator() // remove all cars
        while (iterator.hasNext())
        {
            val car = iterator.next()
            (car.parent as RelativeLayout).removeView(car)
            iterator.remove()
        }
        hearts =3 // restart lives
        updateHearts()
        playerPos=1 // restart player position
        updatePlayerPosition()

        startSpawning() // restart spawning
        gameLoop()  // restart game

    }
    private fun gameLoop() {
        gameHandler.post(object : Runnable {
            override fun run() {

                val iterator = obstacles.iterator()
                while (iterator.hasNext()) {
                    val car = iterator.next()
                    car.y += Constants.Car.SPEED

                    if (car.y > gameLayout.height) {
                        (car.parent as RelativeLayout).removeView(car)
                        iterator.remove()
                        continue
                    }


                    ///// need to make collission check
                    ///
                    if (car.y + car.layoutParams.height > player.y    // check if the car reached a point it can touch the player
                        &&
                        car.y < player.y+ car.layoutParams.height/2   /// check if car passed player yes the /2 is on purpose to make it easier
                        &&
                        lanes[playerPos].contains(car)) // check if car is on the same lane
                    {
                        hearts--
                        (car.parent as RelativeLayout).removeView(car)
                        iterator.remove()
                        updateHearts()
                        continue
                    }
                }
                if (hearts > 0)
                    gameHandler.postDelayed(this, Constants.Car.MOVE_DELAY)
                else
                    gameOver()
            }


        })
    }
}
/// ---------- /GAME FUNCTIONS/
