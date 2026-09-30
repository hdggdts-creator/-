package com.example.audio

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import android.media.ToneGenerator
import android.media.AudioManager
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.speech.tts.TextToSpeech
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Locale
import kotlin.math.sin

class SoundManager(private val context: Context) : TextToSpeech.OnInitListener {

    private var toneGenerator: ToneGenerator? = null
    private var tts: TextToSpeech? = null
    private var isTtsReady = false
    var isSoundEnabled = true
    var isTtsEnabled = true

    private val vibrator: Vibrator? by lazy {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
            vibratorManager?.defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
        }
    }

    init {
        try {
            toneGenerator = ToneGenerator(AudioManager.STREAM_MUSIC, 85)
        } catch (_: Exception) {
            toneGenerator = null
        }
        try {
            tts = TextToSpeech(context.applicationContext, this)
        } catch (_: Exception) {
            tts = null
        }
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            val result = tts?.setLanguage(Locale("ar"))
            isTtsReady = result != TextToSpeech.LANG_MISSING_DATA && result != TextToSpeech.LANG_NOT_SUPPORTED
            tts?.setPitch(1.05f)
            tts?.setSpeechRate(1.0f)
        }
    }

    fun speak(text: String) {
        if (!isTtsEnabled || !isTtsReady || text.isBlank()) return
        try {
            tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, "QUIZ_HOST")
        } catch (_: Exception) {
            // Ignore TTS errors safely
        }
    }

    fun stopSpeaking() {
        try {
            tts?.stop()
        } catch (_: Exception) {}
    }

    fun playCorrect() {
        if (!isSoundEnabled) return
        vibrateSuccess()
        CoroutineScope(Dispatchers.Default).launch {
            playToneChime(intArrayOf(523, 659, 784, 1046), 110) // C5, E5, G5, C6
        }
    }

    fun playWrong() {
        if (!isSoundEnabled) return
        vibrateError()
        CoroutineScope(Dispatchers.Default).launch {
            playToneChime(intArrayOf(220, 196, 174), 160) // Low buzzer descent
        }
    }

    fun playWhistle() {
        if (!isSoundEnabled) return
        CoroutineScope(Dispatchers.Default).launch {
            playToneChime(intArrayOf(2200, 2600, 2200), 120)
        }
    }

    fun playTick() {
        if (!isSoundEnabled) return
        try {
            toneGenerator?.startTone(ToneGenerator.TONE_PROP_BEEP2, 60)
        } catch (_: Exception) {}
    }

    fun playFanfare() {
        if (!isSoundEnabled) return
        vibrateSuccess()
        CoroutineScope(Dispatchers.Default).launch {
            playToneChime(intArrayOf(440, 554, 659, 880, 1108), 130)
        }
    }

    private fun playToneChime(freqs: IntArray, durationMs: Int) {
        try {
            val sampleRate = 22050
            val totalSamples = (sampleRate * (durationMs / 1000f) * freqs.size).toInt()
            val samplesPerTone = (sampleRate * (durationMs / 1000f)).toInt()
            val buffer = ShortArray(totalSamples)

            var sampleIdx = 0
            for (freq in freqs) {
                val step = 2.0 * Math.PI * freq / sampleRate
                var angle = 0.0
                for (i in 0 until samplesPerTone) {
                    val fadeFactor = if (i > samplesPerTone * 0.8) {
                        (samplesPerTone - i).toFloat() / (samplesPerTone * 0.2f)
                    } else 1.0f
                    buffer[sampleIdx++] = (sin(angle) * Short.MAX_VALUE * 0.45 * fadeFactor).toInt().toShort()
                    angle += step
                }
            }

            val audioTrack = AudioTrack.Builder()
                .setAudioAttributes(
                    AudioAttributes.Builder()
                        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                        .setUsage(AudioAttributes.USAGE_GAME)
                        .build()
                )
                .setAudioFormat(
                    AudioFormat.Builder()
                        .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                        .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                        .setSampleRate(sampleRate)
                        .build()
                )
                .setBufferSizeInBytes(buffer.size * 2)
                .setTransferMode(AudioTrack.MODE_STATIC)
                .build()

            audioTrack.write(buffer, 0, buffer.size)
            audioTrack.play()
            Thread.sleep(durationMs.toLong() * freqs.size + 50)
            audioTrack.release()
        } catch (_: Exception) {
            toneGenerator?.startTone(ToneGenerator.TONE_CDMA_PIP, 100)
        }
    }

    private fun vibrateSuccess() {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator?.vibrate(VibrationEffect.createOneShot(120, VibrationEffect.DEFAULT_AMPLITUDE))
            } else {
                @Suppress("DEPRECATION")
                vibrator?.vibrate(120)
            }
        } catch (_: Exception) {}
    }

    private fun vibrateError() {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val timings = longArrayOf(0, 80, 80, 120)
                vibrator?.vibrate(VibrationEffect.createWaveform(timings, -1))
            } else {
                @Suppress("DEPRECATION")
                vibrator?.vibrate(200)
            }
        } catch (_: Exception) {}
    }

    fun release() {
        stopSpeaking()
        try {
            tts?.shutdown()
            toneGenerator?.release()
        } catch (_: Exception) {}
    }
}
