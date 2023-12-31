package com.kalugin.timer

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Vibrator
import androidx.appcompat.app.AppCompatActivity
import com.kalugin.timer.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private var mainThreadHandler: Handler? = null
    private var vibrator: Vibrator? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        mainThreadHandler = Handler(Looper.getMainLooper())
        vibrator = defaultVibrator(applicationContext)

        binding.apply {
            timerSettingEditText.apply {
                filters = arrayOf(EditTextInputFilter { value ->
                    try {
                        value.toInt() in MIN_SETTING..MAX_SETTING
                    } catch (_: NumberFormatException) {
                        false
                    }
                })
            }
            startTimerButton.setOnClickListener {
                val countText = binding.timerSettingEditText.text.toString()
                if (countText.isNotBlank()) {
                    val startTime = System.currentTimeMillis()
                    val duration = countText.toLong() * ONE_SECOND

                    setTimerButtons(true)
                    startUpdateTimer(startTime, duration)
                }
            }
            resetTimerButton.setOnClickListener {
                resetTimer()
            }
        }
    }

    private fun startUpdateTimer(startTime: Long, duration: Long) {
        val elapsedTime = System.currentTimeMillis() - startTime
        val remainingTime = duration - elapsedTime
        if (remainingTime > 0) {
            val seconds = remainingTime / ONE_SECOND
            binding.timerCountdownTextView.text =
                getString(R.string.timeFormat, seconds / 60, seconds % 60)
            mainThreadHandler?.postDelayed(
                { startUpdateTimer(startTime, duration) }, DELAY
            )
        } else {
            binding.timerCountdownTextView.text = getString(R.string.timerDone)
            vibrator?.vibrateOneShot(VIBRATION_DURATION)
            setTimerButtons(false)
        }
    }

    private fun resetTimer() {
        mainThreadHandler?.removeCallbacksAndMessages(null)
        binding.timerCountdownTextView.text = ""

        setTimerButtons(false)
    }

    private fun setTimerButtons(timerActive: Boolean) {
        binding.startTimerButton.isEnabled = !timerActive
        binding.resetTimerButton.isEnabled = timerActive
    }

    private companion object {
        const val ONE_SECOND = 1_000L
        const val DELAY = ONE_SECOND
        const val MIN_SETTING = 1
        const val MAX_SETTING = 3_599

        const val VIBRATION_DURATION = 500L
    }
}