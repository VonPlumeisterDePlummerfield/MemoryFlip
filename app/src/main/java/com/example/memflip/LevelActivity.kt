package com.example.memflip

import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.CheckBox
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat

class LevelActivity : AppCompatActivity() {

    // Handler to manage delay
    private val handler = Handler(Looper.getMainLooper())

    // Runnable to hide the system UI after 3 seconds
    private val hideRunnable = Runnable {
        hideSystemUI()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        setContentView(R.layout.activity_level)

        // Navigate back to MainActivity
        val backButton = findViewById<ImageButton>(R.id.back_button)
        backButton.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }

        // Select Level Buttons
        val level5x5Button = findViewById<ImageButton>(R.id.level_5x5_button)
        level5x5Button.setOnClickListener {
            val intent = Intent(this, LevelFiveActivity::class.java)
            startActivity(intent)
        }
        val level6x6Button = findViewById<ImageButton>(R.id.level_6x6_button)
        level6x6Button.setOnClickListener {
            val intent = Intent(this, LevelSixActivity::class.java)
            startActivity(intent)
        }

        val level7x7Button = findViewById<ImageButton>(R.id.level_7x7_button)
        level7x7Button.setOnClickListener {
            val intent = Intent(this, LevelSevenActivity::class.java)
            startActivity(intent)
        }

        val level8x8Button = findViewById<ImageButton>(R.id.level_8x8_button)
        level8x8Button.setOnClickListener {
            val intent = Intent(this, LevelEightActivity::class.java)
            startActivity(intent)
        }

        val level9x9Button = findViewById<ImageButton>(R.id.level_9x9_button)
        level9x9Button.setOnClickListener {
            val intent = Intent(this, LevelNineActivity::class.java)
            startActivity(intent)
        }

        val level10x10Button = findViewById<ImageButton>(R.id.level_10x10_button)
        level10x10Button.setOnClickListener {
            val intent = Intent(this, LevelTenActivity::class.java)
            startActivity(intent)
        }


        // Settings button functionality
        val settingsButton = findViewById<ImageButton>(R.id.setting_button)
        val settingsOverlay = findViewById<View>(R.id.settings_overlay)
        val closeSettingsButton = findViewById<ImageButton>(R.id.close_settings_button)
        val musicToggle = findViewById<CheckBox>(R.id.music_toggle)
        val sfxToggle = findViewById<CheckBox>(R.id.sfx_toggle)

        settingsButton.setOnClickListener {
            settingsOverlay.visibility = View.VISIBLE
        }

        closeSettingsButton.setOnClickListener {
            settingsOverlay.visibility = View.GONE
        }

        musicToggle.setOnCheckedChangeListener { _, isChecked ->
            // Handle music toggle logic
            if (isChecked) {
                // Turn on music
            } else {
                // Turn off music
            }
        }

        sfxToggle.setOnCheckedChangeListener { _, isChecked ->
            // Handle SFX toggle logic
            if (isChecked) {
                // Turn on SFX
            } else {
                // Turn off SFX
            }
        }

        // Hide the system UI immediately
        hideSystemUI()
        // Apply window insets listener to manage system UI padding (optional)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main_level)) { v, insets ->
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
        windowInsetsController.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
    }

    // Function to hide the navigation bar after a delay (3 seconds)
    private fun hideNavigationBarWithDelay() {
        handler.removeCallbacks(hideRunnable) // Remove any pending hides to prevent stacking
        handler.postDelayed(hideRunnable, 3000) // Schedule to hide after 3 seconds
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
