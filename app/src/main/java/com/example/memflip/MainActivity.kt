@file:Suppress("DEPRECATION")

package com.example.memflip

import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.CheckBox
import android.widget.FrameLayout
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat

class MainActivity : AppCompatActivity() {

    // Handler to manage delay
    private val handler = Handler(Looper.getMainLooper())

    // Runnable to hide the system UI after 3 seconds
    private val hideRunnable = Runnable {
        hideSystemUI()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT



        // Request full-screen mode and hide title
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )

        // Set the content view
        setContentView(R.layout.activity_main)

        // Load and display points from SharedPreferences
        loadPoints()

        // Play Button click listener
        val playButton = findViewById<ImageButton>(R.id.play_button)
        playButton.setOnClickListener {
            val intent = Intent(this, LevelActivity::class.java)
            startActivity(intent)
        }

        // Score Button click listener
        val scoreButton = findViewById<ImageButton>(R.id.score_button)
        scoreButton.setOnClickListener {
            val intent = Intent(this, ScoresActivity::class.java)
            startActivity(intent)
        }

        // Settings button and overlay handling
        val settingsButton = findViewById<ImageButton>(R.id.setting_button)
        val settingsOverlay = findViewById<FrameLayout>(R.id.settings_overlay)
        val closeSettingsButton = findViewById<ImageButton>(R.id.close_settings_button)
        settingsButton.setOnClickListener {
            // Toggle visibility of the overlay
            if (settingsOverlay.visibility == View.GONE) {
                settingsOverlay.visibility = View.VISIBLE
            } else {
                settingsOverlay.visibility = View.GONE
            }
        }
        closeSettingsButton.setOnClickListener {
            // Hide the settings overlay
            settingsOverlay.visibility = View.GONE
        }

        // Music and SFX toggle buttons
        val musicToggle = findViewById<CheckBox>(R.id.music_toggle)
        val sfxToggle = findViewById<CheckBox>(R.id.sfx_toggle)

        // Load initial toggle states from SharedPreferences
        val sharedPreferences = getSharedPreferences("SettingsPrefs", Context.MODE_PRIVATE)
        musicToggle.isChecked = sharedPreferences.getBoolean("musicEnabled", true)
        sfxToggle.isChecked = sharedPreferences.getBoolean("sfxEnabled", true)

        // Add listeners to handle state changes
        musicToggle.setOnCheckedChangeListener { _, isChecked ->
            val editor = sharedPreferences.edit()
            editor.putBoolean("musicEnabled", isChecked)
            editor.apply()
            if (isChecked) {
                // Code to turn on music
            } else {
                // Code to turn off music
            }
        }

        sfxToggle.setOnCheckedChangeListener { _, isChecked ->
            val editor = sharedPreferences.edit()
            editor.putBoolean("sfxEnabled", isChecked)
            editor.apply()
            if (isChecked) {
                // Code to turn on SFX
            } else {
                // Code to turn off SFX
            }
        }

        // Apply window insets listener to manage system UI padding (optional)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Hide the navigation bar and status bar after 3 seconds
        hideNavigationBarWithDelay()

        // Swipe gesture to reappear navigation bar
        window.decorView.setOnSystemUiVisibilityChangeListener { visibility ->
            if (visibility and View.SYSTEM_UI_FLAG_HIDE_NAVIGATION == 0) {
                // If the navigation bar is visible, hide it again after 3 seconds
                hideNavigationBarWithDelay()
            }
        }
    }

    // Function to hide the system UI (navigation bar and status bar)
    private fun hideSystemUI() {
        val windowInsetsController = WindowInsetsControllerCompat(window, window.decorView)
        windowInsetsController.hide(WindowInsetsCompat.Type.systemBars())
        windowInsetsController.systemBarsBehavior =
            WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
    }

    // Function to hide the navigation bar after a delay (3 seconds)
    private fun hideNavigationBarWithDelay() {
        handler.removeCallbacks(hideRunnable) // Remove any pending hides to prevent stacking
        handler.postDelayed(hideRunnable, 3000) // Schedule to hide after 3 seconds
    }

    private fun loadPoints() {
        val sharedPreferences = getSharedPreferences("MemoryCardPrefs", Context.MODE_PRIVATE)
        val savedPoints = sharedPreferences.getInt("points", 0)

        val pointsTextView: TextView = findViewById(R.id.points_number)
        pointsTextView.text = savedPoints.toString()
    }

    override fun onStop() {
        super.onStop()
        // Clean up to prevent memory leaks
        handler.removeCallbacks(hideRunnable)
    }

    override fun onResume() {
        super.onResume()
        // Resume hiding when activity is resumed
        hideNavigationBarWithDelay()
    }
}
