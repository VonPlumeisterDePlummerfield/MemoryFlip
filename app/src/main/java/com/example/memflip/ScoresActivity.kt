package com.example.memflip

import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat

class ScoresActivity : AppCompatActivity() {

    private lateinit var scoresRecyclerView: RecyclerView
    private lateinit var scoresAdapter: ScoresAdapter
    private val scoresList = mutableListOf<PlayerScore>() // Ensure PlayerScore is defined elsewhere

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)

        setContentView(R.layout.activity_scores) // Set the content view

        // Navigate back to MainActivity
        val backButton = findViewById<ImageButton>(R.id.back_button)
        backButton.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }

        // Initialize the RecyclerView
        scoresRecyclerView = findViewById(R.id.scores_recycler_view) // Reference to RecyclerView
        scoresRecyclerView.layoutManager = LinearLayoutManager(this)

        // Initialize the adapter with an empty list
        scoresAdapter = ScoresAdapter(scoresList) // Initialize adapter here

        // Set the adapter to the RecyclerView before loading scores
        scoresRecyclerView.adapter = scoresAdapter

        // Load scores from SharedPreferences
        loadScores()

        // Hide the system UI immediately
        hideSystemUI()

        // Listen for system UI visibility changes
        window.decorView.setOnSystemUiVisibilityChangeListener { visibility ->
            if (visibility and View.SYSTEM_UI_FLAG_HIDE_NAVIGATION == 0) {
                // If the navigation bar is visible, hide it again
                hideSystemUI()
            }
        }

        // Apply window insets listener to manage system UI padding (optional)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.scores_recycler_view)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun loadScores() {
        val sharedPreferences = getSharedPreferences("MemoryCardPrefs", Context.MODE_PRIVATE)
        val allEntries = sharedPreferences.all

        // Clear the list before loading scores
        scoresList.clear()

        val scoresWithTimestamps = mutableListOf<Pair<Long, PlayerScore>>() // To hold timestamps with scores

        for ((key, value) in allEntries) {
            if (key.endsWith("_name")) {
                val playerName = value as String
                val points = sharedPreferences.getInt("${key.replace("_name", "_points")}", 0)
                val moves = sharedPreferences.getInt("${key.replace("_name", "_moves")}", 0)
                val time = sharedPreferences.getInt("${key.replace("_name", "_time")}", 0)

                // Get the timestamp from the key
                val timestamp = key.substringBefore("_name").toLongOrNull() ?: 0L

                // Add the player score along with its timestamp
                scoresWithTimestamps.add(timestamp to PlayerScore(playerName, points, moves, time))
            }
        }

        // Sort the scores by timestamp in descending order (latest first)
        scoresWithTimestamps.sortByDescending { it.first }

        // Populate scoresList with sorted PlayerScores
        for ((_, playerScore) in scoresWithTimestamps) {
            scoresList.add(playerScore)
        }

        scoresAdapter.notifyDataSetChanged() // Notify the adapter after updating the list
    }

    // Function to hide the system UI (navigation bar and status bar)
    private fun hideSystemUI() {
        val windowInsetsController = WindowInsetsControllerCompat(window, window.decorView)
        windowInsetsController.hide(WindowInsetsCompat.Type.systemBars())
        windowInsetsController.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
    }

    override fun onStop() {
        super.onStop()
        // Clean up to prevent memory leaks if necessary
    }

    override fun onResume() {
        super.onResume()
        // Hide the system UI again when the activity resumes
        hideSystemUI()
    }
}
