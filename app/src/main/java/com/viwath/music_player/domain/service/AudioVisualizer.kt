package com.viwath.music_player.domain.service

import android.annotation.SuppressLint
import android.media.audiofx.Visualizer
import android.util.Log
import androidx.media3.common.C
import androidx.media3.exoplayer.ExoPlayer
import com.viwath.music_player.domain.model.AudioLevels
import kotlin.math.log10
import kotlin.math.sqrt

@SuppressLint("UnsafeOptInUsageError")
class AudioVisualizer(
    private val exoPlayer: ExoPlayer
){
    private var visualizer: Visualizer? = null

    fun start(): Boolean {
        val sessionId = exoPlayer.audioSessionId
        if (sessionId == C.AUDIO_SESSION_ID_UNSET) {
            Log.e("AudioVisualizer", "Audio session ID is not yet available")
            return false
        }

        try {
            // Release any existing visualizer first
            stop()

            visualizer = Visualizer(sessionId).apply {
                captureSize = Visualizer.getCaptureSizeRange()[1]
                enabled = true
            }
            Log.d("AudioVisualizer", "Visualizer initialized with session ID: $sessionId")
            return true
        } catch (e: RuntimeException) {
            Log.e("AudioVisualizer", "Failed to initialize visualizer: ${e.message}")
            return false
        } catch (e: UnsupportedOperationException) {
            Log.e("AudioVisualizer", "Visualizer not supported on this device: ${e.message}")
            return false
        } catch (e: Exception) {
            Log.e("AudioVisualizer", "Unexpected error initializing visualizer: ${e.message}")
            return false
        }
    }

    fun stop() {
        try {
            visualizer?.let {
                if (it.enabled) {
                    it.enabled = false
                }
                it.release()
            }
        } catch (e: Exception) {
            Log.e("AudioVisualizer", "Error stopping visualizer: ${e.message}")
        } finally {
            visualizer = null
        }
    }

    fun getRawWaveform(): ByteArray? {
        return try {
            val viz = visualizer ?: return null
            if (!viz.enabled) return null

            val data = ByteArray(viz.captureSize)
            val result = viz.getWaveForm(data)

            if (result == Visualizer.SUCCESS) {
                data
            } else {
                Log.w("AudioVisualizer", "Failed to get waveform data, result: $result")
                null
            }
        } catch (e: Exception) {
            Log.e("AudioVisualizer", "Error getting waveform: ${e.message}")
            null
        }
    }

    fun getRawFft(): ByteArray? {
        return try {
            val viz = visualizer ?: return null
            if (!viz.enabled) return null
            val data = ByteArray(viz.captureSize)
            val result = viz.getFft(data)
            if (result == Visualizer.SUCCESS) {
                data
            } else {
                Log.w("AudioVisualizer", "Failed to get FFT data, result: $result")
                null
            }
        }catch (e: Exception){
            Log.e("AudioVisualizer", "Error getting FFT: ${e.message}")
            null
        }
    }

    fun getFrequencyBand(numberBand: Int = 64): FloatArray?{
        val fftData = getRawFft() ?: return null
        return processFftData(fftData, numberBand)
    }

    private fun processFftData(fftData: ByteArray, numberBand: Int): FloatArray{
        val band = FloatArray(numberBand)
        val dataSize = fftData.size
        val bandSize = dataSize / numberBand
        for (i in 0 until numberBand){
            var sum = 0f
            var  startIdx = i * bandSize
            var endIdx = minOf(startIdx + bandSize, dataSize - 1)
            for (j in startIdx until endIdx step 2){
                if (j + 1 < fftData.size){
                    val real = fftData[j].toFloat()
                    val imaginary = fftData[j + 1].toFloat()
                    val magnitude = sqrt(real * real + imaginary * imaginary)
                    val logMagnitude = log10(1 + magnitude)
                    sum += logMagnitude
                }
            }
            // Average and normalize
            band[i] = (sum / bandSize).coerceIn(0f, 255f) / 255f
        }

        return band
    }

    fun getAudioLevels(): AudioLevels {
        val fftData = getRawFft() ?: return AudioLevels()
        val dataSize = fftData.size / 2

        var bass = 0f
        var mid = 0f
        var treble = 0f

        // Bass: 0-250Hz (roughly first 1/8 of spectrum)
        val bassEnd = dataSize / 8
        for (i in 0 until bassEnd step 2){
            if (i + 1 < fftData.size){
                val real = fftData[i].toFloat()
                val imaginary = fftData[i + 1].toFloat()
                val magnitude = sqrt(real * real + imaginary * imaginary)
                val logMagnitude = log10(1 + magnitude)
                bass += logMagnitude
            }
        }

        // Mid: 250Hz-4kHz (roughly next 3/8 of spectrum)
        val midStart = bassEnd
        val midEnd =dataSize / 2
        for (i in midStart until midEnd step 2){
            if (i + 1 < fftData.size){
                val real = fftData[i].toFloat()
                val imaginary = fftData[i + 1].toFloat()
                val magnitude = sqrt(real * real + imaginary * imaginary)
                val logMagnitude = log10(1 + magnitude)
                mid += logMagnitude
            }
        }

        // Treble: 4kHz+ (remaining spectrum)
        val trebleStart = midEnd
        for (i in trebleStart until dataSize step 2){
            if (i + 1 < fftData.size ){
                val real = fftData[i].toFloat()
                val imaginary = fftData[i + 1].toFloat()
                val magnitude = sqrt(real * real + imaginary * imaginary)
                val logMagnitude = log10(1 + magnitude)
                treble += logMagnitude
            }
        }

        val maxValue = 255f / (dataSize / 4) // Rough animation
        return AudioLevels(
            bass = (bass / maxValue ).coerceIn(0f, 1f),
            mid = (mid / maxValue).coerceIn(0f, 1f),
            treble = (treble / maxValue).coerceIn(0f, 1f)
        )
    }
    
}