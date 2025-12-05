package com.viwath.music_player.presentation.viewmodel

import android.annotation.SuppressLint
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import com.viwath.music_player.core.util.AudioLevels
import com.viwath.music_player.domain.service.AudioVisualizer
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@SuppressLint("UnsafeOptInUsageError")
@HiltViewModel
class VisualizerViewModel @Inject constructor(
    private val audioVisualizer: AudioVisualizer,
    private val exoPlayer: ExoPlayer
): ViewModel(){

    private val _isPlaying = MutableLiveData(false)
    val isPlaying: LiveData<Boolean> = _isPlaying

    // Use frequency bands instead of raw waveform
    private val _frequencyBand = MutableStateFlow(FloatArray(64))
    val frequencyBand: StateFlow<FloatArray> = _frequencyBand.asStateFlow()

    // Audio levels for different frequency ranges
    private val _audioLevel = MutableStateFlow(AudioLevels())
    val audioLevel: StateFlow<AudioLevels> = _audioLevel.asStateFlow()

//    private val _waveformData = MutableStateFlow(ByteArray(128))
//    val waveformData: StateFlow<ByteArray> = _waveformData.asStateFlow()

//    private var isVisualizerStarted = false
    private var smoothedBands = FloatArray(64)
    private var smoothingFactor = 0.3f

    private var visualizerSetupJob: Job? = null
    private var visualizerUpdateJob: Job? = null

    init {
        setupPlaybackListener()
    }

    private fun setupPlaybackListener() {
        exoPlayer.addListener(object : Player.Listener {
            override fun onPlaybackStateChanged(playbackState: Int) {
                updatePlayingState()
            }

            override fun onIsPlayingChanged(isPlaying: Boolean) {
                updatePlayingState()
                if (isPlaying) {
                    startVisualizerSafe()
                } else {
                    stopVisualizer()
                }
            }

            override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
                super.onMediaItemTransition(mediaItem, reason)
                stopVisualizer()
                viewModelScope.launch {
                    delay(100)
                    if (exoPlayer.isPlaying){
                        startVisualizerSafe()
                    }
                }

            }
        })
    }

    private fun updatePlayingState() {
        val isCurrentlyPlaying = exoPlayer.isPlaying &&
                exoPlayer.audioSessionId != C.AUDIO_SESSION_ID_UNSET
        _isPlaying.value = isCurrentlyPlaying
    }

    private fun startVisualizerSafe() {
        // Cancel any existing setup attempt
        visualizerSetupJob?.cancel()

        visualizerSetupJob = viewModelScope.launch {
            // Wait a moment for the AudioSession to stabilize (Critical for Error -3)
            delay(200)

            // Double check we are still playing after the delay
            if (!exoPlayer.isPlaying) return@launch

            var retryCount = 0
            val maxRetries = 5
            var isStarted = false

            while (retryCount < maxRetries && !isStarted) {
                val sessionId = exoPlayer.audioSessionId

                if (sessionId != C.AUDIO_SESSION_ID_UNSET) {
                    // Try to start
                    val success = audioVisualizer.start()
                    if (success) {
                        isStarted = true
                        Log.d("VisualizerViewModel", "Visualizer started successfully on attempt ${retryCount + 1}")
                        startAudioDataUpdates() // Start the loop
                    } else {
                        // If failed, make sure we cleaned up
                        audioVisualizer.stop()
                    }
                }

                if (!isStarted) {
                    retryCount++
                    delay(300) // Increase delay between retries
                }
            }

            if (!isStarted) {
                Log.e("VisualizerViewModel", "Failed to start visualizer after $maxRetries attempts")
            }
        }
    }

    private fun startAudioDataUpdates() {
        // Cancel any existing update loop
        visualizerUpdateJob?.cancel()

        visualizerUpdateJob = viewModelScope.launch {
            Log.d("VisualizerViewModel", "startAudioDataUpdates: ${_isPlaying.value == true}")
            while (_isPlaying.value == true) {
                val newBands = audioVisualizer.getFrequencyBand(64)

                if (newBands != null) {
                    for (i in newBands.indices) {
                        smoothedBands[i] = smoothedBands[i] + (newBands[i] - smoothedBands[i]) * smoothingFactor
                    }
                    _frequencyBand.value = smoothedBands.copyOf()

                    // Only update levels if we got bands
                    _audioLevel.value = audioVisualizer.getAudioLevels()
                    Log.d("VisualizerViewModel", "startAudioDataUpdates: ${audioVisualizer.getAudioLevels()}")
                }

                // Check waveform only if needed (heavy operation)
                // val wave = audioVisualizer.getRawWaveform() ...

                delay(32)
            }
        }
    }

    fun stopVisualizer() {
        // Cancel coroutines first
        visualizerSetupJob?.cancel()
        visualizerUpdateJob?.cancel()

        // Then release native resources
        audioVisualizer.stop()

        smoothedBands.fill(0f)
        Log.d("VisualizerViewModel", "Visualizer stopped")
    }

    fun onPlaybackStarted() {
        if (exoPlayer.isPlaying) {
            startVisualizerSafe()
        }
    }

    override fun onCleared() {
        super.onCleared()
        stopVisualizer()
    }
}