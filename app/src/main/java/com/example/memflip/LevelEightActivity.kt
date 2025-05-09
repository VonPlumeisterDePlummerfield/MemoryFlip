package com.example.memflip

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.media.MediaPlayer
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView

class LevelEightActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var cardAdapter: CardAdapter
    private var cards: MutableList<Card> = mutableListOf()
    private var firstCard: Card? = null
    private var secondCard: Card? = null
    private var canClick = true
    private var matchedPairs = 0

    private lateinit var movesTextView: TextView
    private lateinit var pointsTextView: TextView
    private lateinit var timerTextView: TextView
    private lateinit var completionLayout: View
    private lateinit var finalMovesTextView: TextView

    private var moves = 0
    private var points = 0
    private var startTime = 0L
    private lateinit var timer: CountDownTimer

    private lateinit var playerNameInput: EditText
    private lateinit var submitButton: Button
    private var isNameSubmitted = false // Track submission status

    // Added for settings button functionality
    private lateinit var settingButton: ImageButton
    private lateinit var settingsOverlay: View

    // MediaPlayer for flip sound
    private lateinit var flipSoundPlayer: MediaPlayer
    // MediaPlayer for flipback sound (when cards don't match)
    private lateinit var flipBackSoundPlayer: MediaPlayer
    private lateinit var cardGoneSoundPlayer: MediaPlayer

    private lateinit var hintButton: ImageButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        setContentView(R.layout.activity_level_eight)

        val homeButton = findViewById<Button>(R.id.return_home)
        homeButton.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish() // Optionally finish the current activity
        }

        val restartButton = findViewById<Button>(R.id.restart_button)
        val returnHomeButton = findViewById<Button>(R.id.home_button)
        val continueButton = findViewById<Button>(R.id.continue_button)

        continueButton.setOnClickListener {
            continueLevel()
        }

        restartButton.setOnClickListener {
            restartGame()
        }

        returnHomeButton.setOnClickListener {
            returnToHome()
        }


        movesTextView = findViewById(R.id.moves_numbers)
        pointsTextView = findViewById(R.id.points_number_ingame)
        timerTextView = findViewById(R.id.timer)
        completionLayout = findViewById(R.id.completion_layout)
        finalMovesTextView = completionLayout.findViewById(R.id.final_moves)

        recyclerView = findViewById(R.id.level_5x5_grid)
        recyclerView.layoutManager = GridLayoutManager(this, 5)

        initializeCards()
        cardAdapter = CardAdapter(cards) { card -> onCardClicked(card) }
        recyclerView.adapter = cardAdapter

        startGameTimer()

        val closeSettingsButton = findViewById<ImageButton>(R.id.close_settings_button)
        closeSettingsButton.setOnClickListener {
            settingsOverlay.visibility = View.GONE
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main_level_eight)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        hideSystemUI()


        // Initialize and set up the settings button
        settingButton = findViewById(R.id.setting_button)
        settingsOverlay = findViewById(R.id.settings_overlay)
        settingsOverlay.visibility = View.GONE

        settingButton.setOnClickListener {
            if (settingsOverlay.visibility == View.GONE) {
                settingsOverlay.visibility = View.VISIBLE
            } else {
                settingsOverlay.visibility = View.GONE
            }
        }

        // Initialize the hint button
        hintButton = findViewById(R.id.hint_button)
        hintButton.setOnClickListener { showHint() }

        // Initialize MediaPlayer for flip card sound
        flipSoundPlayer = MediaPlayer.create(this, R.raw.turncard)
        // Initialize MediaPlayer for flipback sound (when cards don't match)
        flipBackSoundPlayer = MediaPlayer.create(this, R.raw.flipcard)
        cardGoneSoundPlayer = MediaPlayer.create(this, R.raw.cardgone)
    }

    private fun showHint() {
        // Ensure the user has selected a first card
        if (firstCard == null) {
            Toast.makeText(this, "Please select a card first!", Toast.LENGTH_SHORT).show()
            return
        }

        // Find the matching card for the selected first card
        val matchingCard = cards.find { it.imageResId == firstCard!!.imageResId && it != firstCard }

        if (matchingCard != null && !matchingCard.isMatched) {
            // Temporarily flip the matching card
            matchingCard.isFaceUp = true
            cardAdapter.notifyDataSetChanged()

            // Hide the hint after 2 seconds
            Handler().postDelayed({
                matchingCard.isFaceUp = false
                cardAdapter.notifyDataSetChanged()
            }, 2000)
        } else {
            Toast.makeText(this, "No matching card found or it is already matched!", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onBackPressed() {
        AlertDialog.Builder(this)
            .setMessage("Are you sure you want to quit?")
            .setPositiveButton("Yes") { _, _ -> super.onBackPressed() }
            .setNegativeButton("No") { dialog, _ -> dialog.dismiss() }
            .create()
            .show()
    }

    private fun initializeCards() {
        val imageResources = listOf(
            R.drawable.card_branch, R.drawable.card_pencil,
            R.drawable.card_poison, R.drawable.card_droplet,
            R.drawable.card_yl_reverse_spiral, R.drawable.card_yl_spiral,
            R.drawable.card_reverse_spiral, R.drawable.card_spiral,
            R.drawable.card_tool, R.drawable.card_door,
            R.drawable.card_bubbles, R.drawable.card_flame,
            R.drawable.card_cherry, R.drawable.card_bottle,
            R.drawable.card_avo, R.drawable.card_bl_spiral,
            R.drawable.card_bl_reverse_spiral, R.drawable.card_sign,
            R.drawable.card_sosig, R.drawable.card_cookie,
            R.drawable.card_blk_spiral, R.drawable.card_blk_reverse_spiral,
            R.drawable.card_zip, R.drawable.card_zip2,
            R.drawable.card_zip_reverse,
        )

        for (i in imageResources.indices) {
            cards.add(Card(id = i * 2, imageResId = imageResources[i]))
            cards.add(Card(id = i * 2 + 1, imageResId = imageResources[i]))
        }

        cards.shuffle()
    }

    private fun onCardClicked(card: Card) {
        if (!canClick || card.isFaceUp) return
        playFlipCardSound()
        card.isFaceUp = true
        cardAdapter.notifyDataSetChanged()

        if (firstCard == null) {
            firstCard = card
        } else {
            secondCard = card
            canClick = false
            moves++
            movesTextView.text = moves.toString()

            if (firstCard?.imageResId == secondCard?.imageResId) {
                matchedPairs++
                points++
                pointsTextView.text = points.toString()
                playCardGoneSound()
                val handler = android.os.Handler()
                handler.postDelayed({
                    firstCard?.isMatched = true
                    secondCard?.isMatched = true
                    resetCards()

                    if (matchedPairs == cards.size / 2) {
                        stopGameTimer()
                        showCompletionDialog()
                    }
                }, 100)
            } else {
                // Play flipback sound (cards do not match)
                playFlipBackCardSound()
                val handler = android.os.Handler()
                handler.postDelayed({
                    firstCard?.isFaceUp = false
                    secondCard?.isFaceUp = false
                    resetCards()
                }, 1000)
            }
        }
    }

    private fun resetCards() {
        firstCard = null
        secondCard = null
        canClick = true
        cardAdapter.notifyDataSetChanged()
    }

    private fun startGameTimer() {
        startTime = System.currentTimeMillis()
        timer = object : CountDownTimer(Long.MAX_VALUE, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val elapsedSeconds = (System.currentTimeMillis() - startTime) / 1000
                val minutes = elapsedSeconds / 60
                val seconds = elapsedSeconds % 60
                timerTextView.text = String.format("%d:%02d", minutes, seconds)
            }

            override fun onFinish() {}
        }
        timer.start()
    }

    private fun stopGameTimer() {
        timer.cancel()
        val elapsedSeconds = (System.currentTimeMillis() - startTime) / 1000
        val minutes = elapsedSeconds / 60
        val seconds = elapsedSeconds % 60
        timerTextView.text = String.format("%d:%02d", minutes, seconds)
    }

    private fun hideSystemUI() {
        val windowInsetsController = WindowInsetsControllerCompat(window, window.decorView)
        windowInsetsController.hide(WindowInsetsCompat.Type.systemBars())
        windowInsetsController.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
    }

    private fun showCompletionDialog() {
        completionLayout.visibility = View.VISIBLE
        val finalPointsTextView: TextView = completionLayout.findViewById(R.id.final_points)
        finalPointsTextView.text = points.toString()

        val finalTimeTextView: TextView = completionLayout.findViewById(R.id.final_time)
        finalTimeTextView.text = timerTextView.text.toString()

        finalMovesTextView.text = moves.toString()

        playerNameInput = completionLayout.findViewById(R.id.player_name_input)
        submitButton = completionLayout.findViewById(R.id.submit_name_button)

        submitButton.setOnClickListener {
            if (!isNameSubmitted) {
                val playerName = playerNameInput.text.toString().trim()
                if (playerName.isNotEmpty()) {
                    savePlayerScore(playerName, points, moves, parseTime(timerTextView.text.toString()))
                    updateTotalPoints(points) // Add this line to update points
                    Toast.makeText(this, "Scores Submitted", Toast.LENGTH_SHORT).show()
                    submitButton.isEnabled = false
                    submitButton.alpha = 0.5f
                    isNameSubmitted = true
                } else {
                    Toast.makeText(this, "Please enter a name", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Name has already been submitted", Toast.LENGTH_SHORT).show()
            }
        }
    }


    private fun savePlayerScore(name: String, points: Int, moves: Int, time: Int) {
        val sharedPreferences = getSharedPreferences("MemoryCardPrefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        val timestamp = System.currentTimeMillis()

        editor.putString("${timestamp}_name", name)
        editor.putInt("${timestamp}_points", points)
        editor.putInt("${timestamp}_moves", moves)
        editor.putInt("${timestamp}_time", time)

        editor.apply()
    }
    private fun updateTotalPoints(newPoints: Int) {
        val sharedPreferences = getSharedPreferences("MemoryCardPrefs", Context.MODE_PRIVATE)
        val currentPoints = sharedPreferences.getInt("points", 0)
        val updatedPoints = currentPoints + newPoints

        val editor = sharedPreferences.edit()
        editor.putInt("points", updatedPoints)
        editor.apply()
    }

    private fun parseTime(timeText: String): Int {
        val timeParts = timeText.split(":")
        return (timeParts[0].toInt() * 60) + timeParts[1].toInt()
    }

    private fun returnToHome() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun restartGame() {
        finish()
        startActivity(Intent(this, LevelEightActivity::class.java))
    }

    private fun continueLevel() {
        val intent = Intent(this, LevelNineActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun playFlipCardSound() {
        flipSoundPlayer.start()
    }

    private fun playFlipBackCardSound() {
        flipBackSoundPlayer.start()
    }

    private fun playCardGoneSound() {
        cardGoneSoundPlayer.start()
    }

    override fun onDestroy() {
        super.onDestroy()
        // Release the MediaPlayer resources
        flipSoundPlayer.release()
        flipBackSoundPlayer.release()
        cardGoneSoundPlayer.release()
    }
}
