package com.viwath.music_player.domain.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import com.viwath.music_player.domain.model.Music
import com.viwath.music_player.presentation.ui.screen.state.PlaybackState
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MusicService : Service(){

    @Inject
    lateinit var exoPlayer: ExoPlayer

    private var currentMusic: Music? = null
    private var musics: List<Music> = emptyList()
    private var isServiceStarted = false
    private val _playbackState = MutableStateFlow(PlaybackState())
    val playbackState: StateFlow<PlaybackState> get() = _playbackState

    private val serviceScope = CoroutineScope(Dispatchers.Main + SupervisorJob())

    override fun onCreate() {
        super.onCreate()
        createNotificationChanel()
        setupPlayerListener()
        startPlaybackStateUpdater()
    }

    override fun onBind(intent: Intent?): IBinder? = MusicBinder()

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return super.onStartCommand(intent, flags, startId)
        handleAction(intent?.action)
        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        exoPlayer.release()
        serviceScope.cancel()
    }

    private fun setupPlayerListener() {
        val listener = object : Player.Listener{
            override fun onIsPlayingChanged(isPlaying: Boolean) {
                super.onIsPlayingChanged(isPlaying)
                updatePlaybackState()
                updateNotification(isPlaying)
            }

            override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
                super.onMediaItemTransition(mediaItem, reason)
                val currentIndex = exoPlayer.currentMediaItemIndex
                if (currentIndex >= 0 && currentIndex < musics.size) {
                    currentMusic = musics[currentIndex]
                }
                updatePlaybackState()
                updateNotification(exoPlayer.isPlaying)
            }

            override fun onPlaybackStateChanged(playbackState: Int) {
                super.onPlaybackStateChanged(playbackState)
                updatePlaybackState()
            }
        }
        exoPlayer.addListener(listener)
    }


    private fun handleAction(action: String?){
        when(action){
            ACTION_PLAY -> playMusic()
            ACTION_PAUSE -> pauseMusic()
            ACTION_NEXT -> nextMusic()
            ACTION_PREVIOUS -> previousMusic()
            ACTION_STOP -> stopService()
        }
    }

    // Playback action
    private fun startPlaybackStateUpdater() {
        serviceScope.launch {
            while (true){
                updatePlaybackState()
                delay(100)
            }
        }
    }

    private fun updatePlaybackState() {
        val duration = if (exoPlayer.duration == C.TIME_UNSET) 0L else exoPlayer.duration
        _playbackState.value = PlaybackState(
            isPlaying = exoPlayer.isPlaying,
            currentPosition = exoPlayer.currentPosition,
            duration = duration,
            currentMusic = currentMusic,
            playbackState = exoPlayer.playbackState
        )
    }


    fun setPlayList(musics: List<Music>){
        this.musics = musics
        val mediaItems = musics.map {
            MediaItem.fromUri(it.uri)
        }
        exoPlayer.stop()
        exoPlayer.clearMediaItems()
        exoPlayer.setMediaItems(mediaItems)
        exoPlayer.prepare()
    }

    fun seekToPosition(index: Int){
        if (index >=0 && index < exoPlayer.mediaItemCount){
            exoPlayer.seekToDefaultPosition(index)
            if (index < musics.size)
                currentMusic = musics[index]
        }
    }

    fun seekTo(position: Long) {
        exoPlayer.seekTo(position)
    }

    fun playMusic(music: Music? = null){
        music?.let {
            currentMusic = it
            // If we have a single music item and no playlist, create one
            if (musics.isEmpty()) {
                val mediaItem = MediaItem.fromUri(it.uri)
                exoPlayer.setMediaItem(mediaItem)
                exoPlayer.prepare()
            }
        }
        exoPlayer.play()
        startForegroundService()
    }

    fun pauseMusic(){
        exoPlayer.pause()
        updateNotification(false)
    }

    fun nextMusic(){
        if (exoPlayer.hasNextMediaItem())
            exoPlayer.seekToNextMediaItem()
    }

    fun previousMusic(){
        if (exoPlayer.hasPreviousMediaItem())
            exoPlayer.seekToPreviousMediaItem()
    }

    @Suppress("DEPRECATION")
    fun stopService(){
        exoPlayer.stop()
        stopForeground(true)
        stopSelf()
        isServiceStarted = false
    }

    // start-stop service
    private fun startForegroundService(){
        if (!isServiceStarted){
            val notification = createNotification(exoPlayer.isPlaying)
            startForeground(NOTIFICATION_ID, notification)
            isServiceStarted = true
        }
    }

    // create-update notification
    private fun createNotification(isPlaying: Boolean): Notification {
        val intent = Intent(this, MusicService::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        val playPauseAction = if (isPlaying){
            NotificationCompat.Action(
                android.R.drawable.ic_media_pause,
                "Pause",
                createPendingIntent(ACTION_PAUSE)
            )
        }else{
            NotificationCompat.Action(
                android.R.drawable.ic_media_play,
                "Play",
                createPendingIntent(ACTION_PLAY)
            )
        }

        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle(currentMusic?.title ?: "Unknown Music")
            .setContentText(currentMusic?.artist ?: "Unknown Artist")
            .setSmallIcon(android.R.drawable.ic_media_play)
            .setContentIntent(pendingIntent)
            .setOngoing(isPlaying)
            .setShowWhen(false)
            .addAction(
                NotificationCompat.Action(
                    android.R.drawable.ic_media_previous,
                    "Previous",
                    createPendingIntent(ACTION_PREVIOUS)
                )
            )
            .addAction(
                playPauseAction
            )
            .addAction(
                NotificationCompat.Action(
                    android.R.drawable.ic_media_next,
                    "Next",
                    createPendingIntent(ACTION_NEXT)
                )
            )
            .addAction(
                NotificationCompat.Action(
                    android.R.drawable.ic_media_pause,
                    "Stop",
                    createPendingIntent(ACTION_STOP)
                )
            )
            .setStyle(
                androidx.media.app.NotificationCompat.MediaStyle()
                    .setShowActionsInCompactView(0, 1, 2)
            )
            .build()
    }

    private fun updateNotification(isPlaying: Boolean){
        if (isServiceStarted){
            val notification = createNotification(isPlaying)
            val notificationManager = this.getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.notify(NOTIFICATION_ID, notification)
        }
    }

    private fun createPendingIntent(action: String): PendingIntent? {
        val intent = Intent(this, MusicService::class.java).apply {
            this.action = action
        }
        return PendingIntent.getService(
            this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }

    private fun createNotificationChanel(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Music Playback",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Music playback controls"
                setShowBadge(false)
            }
            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }
    }

    inner class MusicBinder : Binder(){
        fun getService(): MusicService = this@MusicService
    }

    companion object {
        const val NOTIFICATION_ID = 1
        const val CHANNEL_ID = "music_playback_channel"
        const val ACTION_PLAY = "ACTION_PLAY"
        const val ACTION_PAUSE = "ACTION_PAUSE"
        const val ACTION_NEXT = "ACTION_NEXT"
        const val ACTION_PREVIOUS = "ACTION_PREVIOUS"
        const val ACTION_STOP = "ACTION_STOP"
    }
}