package com.viwath.music_player.presentation

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import com.viwath.music_player.domain.model.Music
import com.viwath.music_player.domain.service.MusicService
import com.viwath.music_player.domain.service.MusicService.MusicBinder
import com.viwath.music_player.presentation.ui.screen.state.PlaybackState
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

class MusicPlayerManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private var musicService: MusicService? = null
    private var isServiceBound = false

    private val _icConnected = MutableStateFlow(false)
    val isConnected: StateFlow<Boolean> get() = _icConnected.asStateFlow()

    private val _playbackState = MutableStateFlow(PlaybackState())
    val playbackState: StateFlow<PlaybackState> get() = _playbackState.asStateFlow()

    private val serviceConnection = object: ServiceConnection{
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            val binder = service as MusicBinder
            musicService = binder.getService()
            isServiceBound = true
            _icConnected.value = true
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            musicService = null
            isServiceBound = false
            _icConnected.value = false
        }
    }

    fun bindService(){
        if (!isServiceBound){
            val intent = Intent(context, MusicService::class.java)
            context.bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE)
        }
    }

    fun unbindService(){
        if (isServiceBound){
            context.unbindService(serviceConnection)
            isServiceBound = false
            _icConnected.value = false
        }
    }

    fun getServicePlaybackState(): StateFlow<PlaybackState>? {
        return musicService?.playbackState
    }

    fun playMusic(music: Music, musics: List<Music> = emptyList()){
        musicService?.let { service ->
            if (musics.isNotEmpty()){
                service.setPlayList(musics)
                val selectedIndex = musics.indexOfFirst { it.id == music.id }
                if (selectedIndex >= 0) {
                    service.seekToPosition(selectedIndex)
                }
            }
            service.playMusic(music)
        }
    }
    fun pauseMusic(){
        musicService?.pauseMusic()
    }
    fun nextMusic() {
        musicService?.nextMusic()
    }
    fun previousMusic() {
        musicService?.previousMusic()
    }
    fun stopMusic() {
        musicService?.stopService()
    }
    fun seekTo(position: Long) {
        musicService?.seekTo(position)
    }



}