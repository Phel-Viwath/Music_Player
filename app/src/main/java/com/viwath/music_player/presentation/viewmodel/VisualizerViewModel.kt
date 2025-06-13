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
import com.viwath.music_player.domain.model.AudioLevels
import com.viwath.music_player.domain.service.AudioVisualizer
import dagger.hilt.android.lifecycle.HiltViewModel
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

    private val _waveformData = MutableStateFlow(ByteArray(128))
    val waveformData: StateFlow<ByteArray> = _waveformData.asStateFlow()

    private var isVisualizerStarted = false
    private var smoothedBands = FloatArray(64)
    private var smoothingFactor = 0.3f

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
                    startVisualizerWithRetry()
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
                        startVisualizerWithRetry()
                    }
                }

            }
        })
    }

    private fun updatePlayingState() {
        val isCurrentlyPlaying = exoPlayer.isPlaying &&
                exoPlayer.audioSessionId != C.AUDIO_SESSION_ID_UNSET

        Log.d("VisualizerViewModel", "updatePlayingState - ExoPlayer.isPlaying: ${exoPlayer.isPlaying}")
        Log.d("VisualizerViewModel", "updatePlayingState - AudioSessionId: ${exoPlayer.audioSessionId}")
        Log.d("VisualizerViewModel", "updatePlayingState - Final isPlaying: $isCurrentlyPlaying")
        _isPlaying.value = isCurrentlyPlaying
    }

    private fun startVisualizerWithRetry() {
        if (isVisualizerStarted) return

        viewModelScope.launch {
            var retryCount = 0
            val maxRetries = 10

            while (retryCount < maxRetries && !isVisualizerStarted) {
                if (exoPlayer.audioSessionId != C.AUDIO_SESSION_ID_UNSET) {
                    val success = audioVisualizer.start()
                    if (success) {
                        isVisualizerStarted = true
                        startAudioDataUpdates()
                        Log.d("VisualizerViewModel", "Visualizer started successfully")
                        break
                    }
                }
                retryCount++
                delay(100) // Wait 100ms before retry
            }

            if (!isVisualizerStarted) {
                Log.e("VisualizerViewModel", "Failed to start visualizer after $maxRetries attempts")
            }
        }
    }

    private fun startAudioDataUpdates() {
        viewModelScope.launch {
            while (isVisualizerStarted && _isPlaying.value == true) {
                // Get frequency bands
                val newBands = audioVisualizer.getFrequencyBand(64)

                // Apply smoothing to reduce jittery animations
                for (i in newBands!!.indices){
                    smoothedBands[i] = smoothedBands[i] * (1f - smoothingFactor) + newBands[i] * smoothingFactor
                }

                _frequencyBand.value = smoothedBands.copyOf()

                _audioLevel.value = audioVisualizer.getAudioLevels()

                val waveForm = audioVisualizer.getRawWaveform()
                if (waveForm != null && waveForm.isNotEmpty()){
                    _waveformData.value = waveForm
                }

                delay(16)
            }
        }
    }

    fun stopVisualizer() {
        isVisualizerStarted = false
        audioVisualizer.stop()

        smoothedBands.fill(0f)
        Log.d("VisualizerViewModel", "Visualizer stopped")
    }

    fun onPlaybackStarted(){
        if (!isVisualizerStarted && exoPlayer.isPlaying) {
            startVisualizerWithRetry()
        }
    }

    override fun onCleared() {
        super.onCleared()
        stopVisualizer()
    }
}