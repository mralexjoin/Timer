package com.kalugin.timer

import android.content.Context
import android.content.Intent
import android.media.AudioAttributes
import android.os.Build
import android.os.VibrationAttributes
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import java.io.Serializable

fun defaultVibrator(context: Context): Vibrator? = when {
    Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
        val vibratorManager =
            context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
        vibratorManager?.defaultVibrator
    }

    else -> @Suppress("DEPRECATION") context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
}

fun Vibrator.vibrateOneShot(milliseconds: Long) = when {
    Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU -> vibrate(
        VibrationEffect.createOneShot(
            milliseconds,
            VibrationEffect.DEFAULT_AMPLITUDE
        ),
        VibrationAttributes.Builder()
            .setUsage(VibrationAttributes.USAGE_ALARM)
            .build()
    )

    Build.VERSION.SDK_INT >= Build.VERSION_CODES.O -> @Suppress("DEPRECATION") vibrate(
        VibrationEffect.createOneShot(
            milliseconds,
            VibrationEffect.DEFAULT_AMPLITUDE
        ),
        AudioAttributes.Builder()
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .setUsage(AudioAttributes.USAGE_ALARM)
            .build()
    )

    else -> @Suppress("DEPRECATION") vibrate(milliseconds)
}

inline fun <reified T : Serializable> Intent.serializable(key: String): T? = when {
    Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU -> getSerializableExtra(
        key,
        T::class.java
    )

    else -> @Suppress("DEPRECATION") getSerializableExtra(key) as? T
}
