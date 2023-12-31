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
    private var count = -1
        set(value) {
            field = value
            binding.timerCountdownTextView.text = when {
                count > 0 -> count.toString()
                count == 0 -> getString(R.string.timerDone)
                else -> ""
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        mainThreadHandler = Handler(Looper.getMainLooper())
        vibrator = defaultVibrator(applicationContext)

        val timerSettingEditText = binding.timerSettingEditText.apply {
            filters = arrayOf(EditTextInputFilter { value ->
                try {
                    value.toInt() in MIN_SETTING..MAX_SETTING
                } catch (_: NumberFormatException) {
                    false
                }
            })
        }
        binding.startTimerButton.setOnClickListener {
            val countText = timerSettingEditText.text.toString()
            if (countText.isNotBlank()) {
                count = countText.toInt()

                it.isEnabled = false
                binding.resetTimerButton.isEnabled = true
                mainThreadHandler?.postDelayed(
                    object : Runnable {
                        override fun run() {
                            count--
                            if (count > 0) {
                                mainThreadHandler?.postDelayed(this, ONE_SECOND)
                            } else {
                                it.isEnabled = true
                                binding.resetTimerButton.isEnabled = false
                                vibrator?.vibrateOneShot(VIBRATION_DURATION)
                            }
                        }
                    }, ONE_SECOND
                )
            }
        }

        binding.resetTimerButton.setOnClickListener {
            mainThreadHandler?.removeCallbacksAndMessages(null)
            count = -1

            binding.startTimerButton.isEnabled = true
            it.isEnabled = false
        }
    }

    private companion object {
        const val ONE_SECOND = 1_000L
        const val MIN_SETTING = 1
        const val MAX_SETTING = 3_600

        const val VIBRATION_DURATION = 500L
    }
}