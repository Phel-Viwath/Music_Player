package com.viwath.music_player.domain.service

import android.annotation.SuppressLint
import android.media.audiofx.Visualizer
import android.util.Log
import androidx.media3.common.C
import androidx.media3.exoplayer.ExoPlayer
import com.viwath.music_player.core.util.AudioLevels
import kotlin.math.hypot
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

    /*fun getRawWaveform(): ByteArray? {
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
    }*/

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

    /*private fun processFftData(fftData: ByteArray, numberBand: Int): FloatArray {
        val band = FloatArray(numberBand)
        val n = fftData.size
        val dataSize = n / 2
        val bandSize = dataSize / numberBand
        for (i in 0 until numberBand){
            var sum = 0f
            val startIdx = i * bandSize
            val endIdx = minOf(startIdx + bandSize, dataSize - 1)

            // FFT data comes in Real/Imaginary pairs
            for (j in startIdx until endIdx){
                val real = fftData[2 * j].toFloat()
                val imag = fftData[2 * j + 1].toFloat()
                // Calculate magnitude
                val magnitude = hypot(real, imag)
                sum += magnitude
            }
            // Average and Logarithmic scaling (to make quiet sounds visible)
            // Multiplier 4f adds some visual "gain"
            val avg = (sum / bandSize) * 4f
            // Normalize roughly 0..1
            band[i] = (log10(1 + avg) / 2.5f).coerceIn(0f, 1f)
        }

        return band
    } */

    private fun processFftData(fft: ByteArray, targetSize: Int): FloatArray {
        val result = FloatArray(targetSize)
        val n = fft.size / 2 // We only use the first half of FFT
        val blockSize = n / targetSize

        for (i in 0 until targetSize) {
            val start = i * blockSize
            val end = minOf(start + blockSize, n)
            result[i] = calculateAverageMagnitude(fft, start, end)
        }
        return result
    }

    fun getAudioLevels(): AudioLevels {
        val fft = getRawFft() ?: return AudioLevels()

        // FFT size is usually 1024. The useful data is the first half (512).
        val n = fft.size / 2

        // Define ranges (approximate for 44.1kHz sample rate)
        // Bass: Lower 10% of frequencies
        // Mid: 10% to 40%
        // Treble: 40% to 100%
        val bassEnd = (n * 0.01).toInt()
        val midEnd = (n * 0.4).toInt()

        val bass = calculateAverageMagnitude(fft, 0, bassEnd)
        val mid = calculateAverageMagnitude(fft, bassEnd, midEnd)
        val treble = calculateAverageMagnitude(fft, midEnd, n)

        return AudioLevels(
            bass = bass,
            mid = mid,
            treble = treble
        )
    }

    /*fun getAudioLevels(): AudioLevels {
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

        val maxValue = 256f / (dataSize / 4) // Rough animation
        return AudioLevels(
            bass = (bass / maxValue ).coerceIn(0f, 1f),
            mid = (mid / maxValue).coerceIn(0f, 1f),
            treble = (treble / maxValue).coerceIn(0f, 1f)
        )
    } */


    private fun calculateAverageMagnitude(fft: ByteArray, start: Int, end: Int): Float {
        if (start >= end) return 0f

        var totalMagnitude = 0f
        val count = end - start

        // FFT data is stored as [Real, Imaginary, Real, Imaginary...]
        for (i in start until end) {
            val realIdx = i * 2
            val imagIdx = i * 2 + 1

            if (imagIdx < fft.size) {
                // Bytes are signed (-128 to 127). Convert to float.
                val real = fft[realIdx].toFloat()
                val imag = fft[imagIdx].toFloat()

                // Calculate magnitude
                val magnitude = hypot(real, imag)
                totalMagnitude += magnitude
            }
        }

        // Average magnitude for this range
        val avg = totalMagnitude / count

        // SCALING FACTOR: This is the magic number.
        // Raw FFT magnitudes usually range 0-150 depending on volume.
        // We log-scale it to make quiet sounds visible.
        // log10(1 + 50) ~= 1.7. log10(1 + 150) ~= 2.1.
        // Dividing by 2.5f keeps it usually under 1.0 without hard clipping.

        return (log10(1 + avg) / 2.2f).coerceIn(0f, 1f)
    }
    
}