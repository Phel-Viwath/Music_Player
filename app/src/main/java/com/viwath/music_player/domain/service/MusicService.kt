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
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.core.app.NotificationCompat
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import com.viwath.music_player.R
import com.viwath.music_player.core.util.Constant.ACTION_NEXT
import com.viwath.music_player.core.util.Constant.ACTION_PAUSE
import com.viwath.music_player.core.util.Constant.ACTION_PLAY
import com.viwath.music_player.core.util.Constant.ACTION_PREVIOUS
import com.viwath.music_player.core.util.Constant.ACTION_STOP
import com.viwath.music_player.core.util.Constant.CHANNEL_ID
import com.viwath.music_player.core.util.Constant.NOTIFICATION_ID
import com.viwath.music_player.core.util.GetImage.getImageBitMap
import com.viwath.music_player.domain.model.Music
import com.viwath.music_player.domain.model.dto.toMusicDto
import com.viwath.music_player.presentation.MainActivity
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
class MusicService : Service() {

    @Inject lateinit var exoPlayer: ExoPlayer
    @Inject lateinit var mediaSession: MediaSessionCompat

    // State management
    private var currentMusic: Music? = null
    private var playlist: List<Music> = emptyList()
    private var isServiceStarted = false

    private val _playbackState = MutableStateFlow(PlaybackState())
    val playbackState: StateFlow<PlaybackState> get() = _playbackState

    private val serviceScope = CoroutineScope(Dispatchers.Main + SupervisorJob())

    // ===========================================
    // Service Lifecycle
    // ===========================================

    override fun onCreate() {
        super.onCreate()
        initializeService()
    }

    override fun onBind(intent: Intent?): IBinder = MusicBinder()

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        intent?.action?.let(::handleNotificationAction)
        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        cleanupService()
    }

    // ===========================================
    // Service Initialization
    // ===========================================

    private fun initializeService() {
        createNotificationChannel()
        setupExoPlayerListener()
        setupMediaSession()
        startPlaybackUpdater()
    }

    private fun cleanupService() {
        serviceScope.cancel()
        exoPlayer.release()
        mediaSession.release()
    }

    // ===========================================
    // ExoPlayer Setup and Listeners
    // ===========================================

    private fun setupExoPlayerListener() {
        exoPlayer.addListener(object : Player.Listener {
            override fun onIsPlayingChanged(isPlaying: Boolean) {
                updatePlaybackState()
                updateMediaMetadata()
                updateNotification(isPlaying)
            }

            override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
                updateCurrentMusic()
                updatePlaybackState()
                updateMediaMetadata()
                updateNotification(exoPlayer.isPlaying)
            }

            override fun onPlaybackStateChanged(playbackState: Int) {
                updatePlaybackState()
                updateMediaMetadata()
            }

            override fun onPositionDiscontinuity(
                oldPosition: Player.PositionInfo,
                newPosition: Player.PositionInfo,
                reason: Int
            ) {
                updatePlaybackState()
                updateMediaMetadata()
            }
        })
    }

    private fun updateCurrentMusic() {
        val currentIndex = exoPlayer.currentMediaItemIndex
        if (currentIndex in playlist.indices) {
            currentMusic = playlist[currentIndex]
        }
    }

    // ===========================================
    // MediaSession Setup
    // ===========================================

    private fun setupMediaSession() {
        mediaSession.setCallback(createMediaSessionCallback())
        mediaSession.isActive = true
        updateMediaMetadata()
    }

    private fun createMediaSessionCallback() = object : MediaSessionCompat.Callback() {
        override fun onPlay() = playMusic()
        override fun onPause() = pauseMusic()
        override fun onSkipToNext() = nextMusic()
        override fun onSkipToPrevious() = previousMusic()
        override fun onStop() = stopService()
        override fun onSeekTo(pos: Long) = seekTo(pos)

        override fun onSetRepeatMode(repeatMode: Int) {
            exoPlayer.repeatMode = when (repeatMode) {
                PlaybackStateCompat.REPEAT_MODE_ONE -> ExoPlayer.REPEAT_MODE_ONE
                PlaybackStateCompat.REPEAT_MODE_ALL,
                PlaybackStateCompat.REPEAT_MODE_GROUP -> ExoPlayer.REPEAT_MODE_ALL
                else -> ExoPlayer.REPEAT_MODE_OFF
            }
        }

        override fun onSetShuffleMode(shuffleMode: Int) {
            exoPlayer.shuffleModeEnabled = shuffleMode == PlaybackStateCompat.SHUFFLE_MODE_ALL
        }
    }

    // ===========================================
    // Playback Control Methods
    // ===========================================

    fun setPlaylist(musics: List<Music>) {
        playlist = musics
        val mediaItems = musics.map { MediaItem.fromUri(it.uri) }

        with(exoPlayer) {
            stop()
            clearMediaItems()
            setMediaItems(mediaItems)
            prepare()
        }
    }

    fun playMusic(music: Music? = null) {
        music?.let {
            currentMusic = it
            if (playlist.isEmpty()) {
                prepareAndSetSingleTrack(it)
            }
        }
        exoPlayer.play()
        startForegroundService()
    }

    fun pauseMusic() {
        exoPlayer.pause()
        updateNotification(false)
    }

    fun resumeMusic(){
        exoPlayer.play()
    }

    fun nextMusic() {
        if (exoPlayer.hasNextMediaItem()) {
            exoPlayer.seekToNextMediaItem()
        }
    }

    fun previousMusic() {
        if (exoPlayer.hasPreviousMediaItem()) {
            exoPlayer.seekToPreviousMediaItem()
        }
    }

    fun seekTo(position: Long) {
        exoPlayer.seekTo(position)
        updateNotification(exoPlayer.isPlaying)
    }

    fun seekToPosition(index: Int) {
        if (index in 0 until exoPlayer.mediaItemCount) {
            exoPlayer.seekToDefaultPosition(index)
            if (index in playlist.indices) {
                currentMusic = playlist[index]
            }
        }
    }

    fun repeatAll(){
        exoPlayer.repeatMode = Player.REPEAT_MODE_ALL
    }

    fun repeatOne(){
        exoPlayer.repeatMode = Player.REPEAT_MODE_ONE
    }

    fun shuffleMode(isShuffle: Boolean){
        exoPlayer.shuffleModeEnabled = isShuffle
    }

    @Suppress("DEPRECATION")
    fun stopService() {
        exoPlayer.stop()
        stopForeground(true)
        stopSelf()
        isServiceStarted = false
    }

    private fun prepareAndSetSingleTrack(music: Music) {
        val mediaItem = MediaItem.fromUri(music.uri)
        exoPlayer.setMediaItem(mediaItem)
        exoPlayer.prepare()
    }

    // ===========================================
    // State Updates
    // ===========================================

    private fun startPlaybackUpdater() {
        serviceScope.launch {
            while (true) {
                if (exoPlayer.isPlaying) {
                    updatePlaybackState()
                    updateMediaMetadata()
                }
                delay(1000)
            }
        }
    }

    private fun updatePlaybackState() {
        val duration = if (exoPlayer.duration == C.TIME_UNSET) 0L else exoPlayer.duration
        val position = exoPlayer.currentPosition

        val mediaSessionState = PlaybackStateCompat.Builder()
            .setActions(getPlaybackActions())
            .setState(getPlaybackStateCompat(), position, 1f)
            .build()

        _playbackState.value = PlaybackState(
            isPlaying = exoPlayer.isPlaying,
            currentPosition = position,
            duration = duration,
            currentMusic = currentMusic?.toMusicDto(),
            playbackState = exoPlayer.playbackState
        )

        mediaSession.setPlaybackState(mediaSessionState)
    }

    private fun getPlaybackActions(): Long {
        return PlaybackStateCompat.ACTION_PLAY or
                PlaybackStateCompat.ACTION_PAUSE or
                PlaybackStateCompat.ACTION_SKIP_TO_NEXT or
                PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS or
                PlaybackStateCompat.ACTION_STOP or
                PlaybackStateCompat.ACTION_SEEK_TO
    }

    private fun getPlaybackStateCompat(): Int {
        return when {
            exoPlayer.isPlaying -> PlaybackStateCompat.STATE_PLAYING
            exoPlayer.playbackState == Player.STATE_BUFFERING -> PlaybackStateCompat.STATE_BUFFERING
            else -> PlaybackStateCompat.STATE_PAUSED
        }
    }

    private fun updateMediaMetadata() {
        currentMusic?.let { music ->
            val metadata = MediaMetadataCompat.Builder()
                .putString(MediaMetadataCompat.METADATA_KEY_TITLE, music.title)
                .putString(MediaMetadataCompat.METADATA_KEY_ARTIST, music.artist)
                .putString(MediaMetadataCompat.METADATA_KEY_ALBUM, music.album)
                .putLong(MediaMetadataCompat.METADATA_KEY_DURATION, exoPlayer.duration)
                .putBitmap(MediaMetadataCompat.METADATA_KEY_ALBUM_ART, music.uri.getImageBitMap()?.asImageBitmap()?.asAndroidBitmap())
                .build()

            mediaSession.setMetadata(metadata)
        }
    }

    // ===========================================
    // Notification Handling
    // ===========================================

    private fun handleNotificationAction(action: String) {
        when (action) {
            ACTION_PLAY -> playMusic()
            ACTION_PAUSE -> pauseMusic()
            ACTION_NEXT -> nextMusic()
            ACTION_PREVIOUS -> previousMusic()
            ACTION_STOP -> stopService()
        }
    }

    private fun startForegroundService() {
        if (!isServiceStarted) {
            val notification = buildNotification(exoPlayer.isPlaying)
            startForeground(NOTIFICATION_ID, notification)
            isServiceStarted = true
        }
    }

    private fun updateNotification(isPlaying: Boolean) {
        if (isServiceStarted) {
            val notification = buildNotification(isPlaying)
            val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.notify(NOTIFICATION_ID, notification)
        }
    }

    private fun buildNotification(isPlaying: Boolean): Notification {
        val contentIntent = createMainActivityIntent()
        val (title, artist) = getCurrentTrackInfo()
        val progress = calculateProgress()

        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentIntent(contentIntent)
            .setContentTitle(title)
            .setContentText(artist)
            .setSmallIcon(android.R.drawable.ic_media_play)
            .setLargeIcon(currentMusic?.imagePath?.getImageBitMap())
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setOnlyAlertOnce(true)
            .setOngoing(isPlaying)
            .setShowWhen(false)
            .addAction(R.drawable.heart_outline, "Favor", createActionIntent(ACTION_PREVIOUS))
            .addAction(android.R.drawable.ic_media_previous, "Previous", createActionIntent(ACTION_PREVIOUS))
            .addAction(createPlayPauseAction(isPlaying))
            .addAction(android.R.drawable.ic_media_next, "Next", createActionIntent(ACTION_NEXT))
            .addAction(android.R.drawable.ic_menu_close_clear_cancel, "Stop", createActionIntent(ACTION_STOP))
            .setStyle(
                androidx.media.app.NotificationCompat.MediaStyle()
                    .setMediaSession(mediaSession.sessionToken)
                    .setShowActionsInCompactView(0, 1, 2)
            )
            .setProgress(100, progress, exoPlayer.duration == 0L)
            .build()
    }

    private fun createMainActivityIntent(): PendingIntent {
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        return PendingIntent.getActivity(
            this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }

    private fun getCurrentTrackInfo(): Pair<String, String> {
        val title = currentMusic?.title ?: "Unknown Music"
        val artist = currentMusic?.artist ?: "Unknown Artist"
        return title to artist
    }

    private fun calculateProgress(): Int {
        val currentPosition = exoPlayer.currentPosition
        val duration = if (exoPlayer.duration == C.TIME_UNSET) 0L else exoPlayer.duration
        return if (duration > 0) {
            ((currentPosition.toFloat() / duration.toFloat()) * 100).toInt()
        } else 0
    }

    private fun createPlayPauseAction(isPlaying: Boolean): NotificationCompat.Action {
        return if (isPlaying) {
            NotificationCompat.Action(
                android.R.drawable.ic_media_pause,
                "Pause",
                createActionIntent(ACTION_PAUSE)
            )
        } else {
            NotificationCompat.Action(
                android.R.drawable.ic_media_play,
                "Play",
                createActionIntent(ACTION_PLAY)
            )
        }
    }

    private fun createActionIntent(action: String): PendingIntent {
        val intent = Intent(this, MusicService::class.java).apply {
            this.action = action
        }
        return PendingIntent.getService(
            this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
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

    // ===========================================
    // Inner Classes
    // ===========================================

    inner class MusicBinder : Binder() {
        fun getService(): MusicService = this@MusicService
    }
}