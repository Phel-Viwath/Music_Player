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
import com.viwath.music_player.presentation.ui.screen.state.PlayingMode
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

/**
 * A foreground service for playing music in the background.
 *
 * This service manages music playback using [ExoPlayer], integrates with the Android system
 * through [MediaSessionCompat] for media controls (e.g., lock screen, Bluetooth),
 * and displays a notification with playback controls.
 *
 * It communicates with the UI layer (e.g., [MainActivity]) via a [Binder] and exposes its
 * playback state through a [StateFlow].
 *
 * @property exoPlayer The [ExoPlayer] instance for music playback. Injected by Hilt.
 * @property mediaSession The [MediaSessionCompat] instance for system integration. Injected by Hilt.
 * @property playbackState A [StateFlow] that emits the current [PlaybackState], allowing UI components to observe changes.
 */

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

    /**
     * Initializes the service components: notification channel, ExoPlayer listener,
     * media session, and the playback state updater.
     */
    private fun initializeService() {
        createNotificationChannel()
        setupExoPlayerListener()
        setupMediaSession()
        startPlaybackUpdater()
    }

    /**
     * Cleans up resources when the service is destroyed.
     * Cancels coroutines and releases the player and media session.
     */
    private fun cleanupService() {
        serviceScope.cancel()
        exoPlayer.release()
        mediaSession.release()
    }

    /**
     * Sets up a [Player.Listener] for [exoPlayer] to react to playback state changes.
     * This updates the service's internal state, media session, and notification.
     */
    private fun setupExoPlayerListener() {
        val listener = object : Player.Listener {
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
        }
        exoPlayer.addListener(listener)
    }

    /**
     * Updates the [currentMusic] property based on the current media item index in ExoPlayer.
     * This ensures the service's state is in sync with the player.
     */
    private fun updateCurrentMusic() {
        val currentIndex = exoPlayer.currentMediaItemIndex
        if (currentIndex in playlist.indices) {
            currentMusic = playlist[currentIndex]
        }
    }


    /**
     * Initializes and activates the [MediaSessionCompat].
     * It sets the callback for handling media control events from external sources
     * (like lock screen, Bluetooth) and updates the initial metadata.
     */
    private fun setupMediaSession() {
        mediaSession.setCallback(createMediaSessionCallback())
        mediaSession.isActive = true
        updateMediaMetadata()
    }

    /**
     * Creates and returns a [MediaSessionCompat.Callback] to handle media control events.
     */
    private fun createMediaSessionCallback(): MediaSessionCompat.Callback{
        return object : MediaSessionCompat.Callback() {
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
    }


    /**
     * Sets a new playlist for the player.
     *
     * This clears the existing playlist, adds the new list of music, and prepares the player.
     * @param musics The list of [Music] to be set as the new playlist.
     */
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

    /**
     * Checks if the current playlist is empty.
     * @return `true` if the playlist is empty, `false` otherwise.
     */
    fun isPlaylistEmpty(): Boolean = playlist.isEmpty()

    /**
     * Starts or resumes playback.
     * If a specific [Music] object is provided, it will play that track. Otherwise, it resumes the current track.
     * @param music The optional [Music] track to start playing. If null, resumes the current track.
     */
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

    /**
     * Adds a music track to be played next.
     * The track is inserted into the playlist immediately after the current track.
     * @param music The [Music] track to add.
     */
    fun addToPlayNext(music: Music){
        val currentIndex = exoPlayer.currentMediaItemIndex
        val mediaItem = MediaItem.fromUri(music.uri)

        exoPlayer.addMediaItem(currentIndex +1, mediaItem)
        if (playlist.isEmpty()){
            prepareAndSetSingleTrack(music)
        }
        playlist = playlist.toMutableList().apply {
            add(currentIndex + 1, music)
        }
    }

    /**
     * Adds a music track to the end of the playlist.
     *
     * @param music The [Music] track to add.
     */
    fun playLast(music: Music){
        val mediaItem = MediaItem.fromUri(music.uri)
        exoPlayer.addMediaItem(mediaItem)
        playlist = playlist.toMutableList().apply {
            add(music)
        }
    }

    /**
     * Pauses the current playback.
     */
    fun pauseMusic() {
        exoPlayer.pause()
        updateNotification(false)
    }

    /**
     * Resumes the paused playback.
     */
    fun resumeMusic(){
        exoPlayer.play()
    }

    /**
     * Skips to the next track in the playlist.
     */
    fun nextMusic() {
        if (exoPlayer.hasNextMediaItem()) {
            exoPlayer.seekToNextMediaItem()
        }
    }

    /**
     * Skips to the previous track in the playlist.
     */
    fun previousMusic() {
        if (exoPlayer.hasPreviousMediaItem()) {
            exoPlayer.seekToPreviousMediaItem()
        }
    }

    /**
     * Seeks to a specific position within the current track.
     * @param position The position to seek to, in milliseconds.
     */
    fun seekTo(position: Long) {
        exoPlayer.seekTo(position)
        updateNotification(exoPlayer.isPlaying)
    }

    /**
     * Jumps to a specific track in the playlist by its index.
     * @param index The index of the track to play.
     */
    fun seekToPosition(index: Int) {
        if (index in 0 until exoPlayer.mediaItemCount) {
            exoPlayer.seekToDefaultPosition(index)
            if (index in playlist.indices) {
                currentMusic = playlist[index]
            }
        }
    }

    /**
     * Sets the player to repeat the entire playlist.
     */
    fun repeatAll(){
        exoPlayer.repeatMode = Player.REPEAT_MODE_ALL
        updatePlaybackState()
        updateMediaMetadata()
    }

    /**
     * Sets the player to repeat the current track.
     */
    fun repeatOne(){
        exoPlayer.repeatMode = Player.REPEAT_MODE_ONE
        updatePlaybackState()
        updateMediaMetadata()
    }

    /**
     * Enables or disables shuffle mode.
     * @param isShuffle `true` to enable shuffle, `false` to disable.
     */
    fun shuffleMode(isShuffle: Boolean){
        exoPlayer.shuffleModeEnabled = isShuffle
        updatePlaybackState()
        updateMediaMetadata()
    }

    /**
     * Stops the music playback, removes the foreground notification, and stops the service.
     */
    @Suppress("DEPRECATION")
    fun stopService() {
        exoPlayer.stop()
        stopForeground(true)
        stopSelf()
        isServiceStarted = false
    }

    /**
     * Gets the current playing mode (shuffle, repeat all, repeat one).
     *
     * @return The current [PlayingMode].
     */
    fun getPlayingMode(): PlayingMode{
        return when{
            exoPlayer.shuffleModeEnabled -> PlayingMode.SHUFFLE
            exoPlayer.repeatMode == Player.REPEAT_MODE_ALL -> PlayingMode.REPEAT_ALL
            exoPlayer.repeatMode == Player.REPEAT_MODE_ONE -> PlayingMode.REPEAT_ONE
            else -> PlayingMode.REPEAT_ALL
        }
    }

    /**
     * Prepares and sets a single music track for playback.
     * Used when the playlist is initially empty.
     * @param music The [Music] track to prepare.
     */
    private fun prepareAndSetSingleTrack(music: Music) {
        val mediaItem = MediaItem.fromUri(music.uri)
        exoPlayer.setMediaItem(mediaItem)
        exoPlayer.prepare()
    }


    /**
     * Starts a coroutine that periodically updates the playback state and media metadata
     * while music is playing. This ensures the UI and media session are kept in sync
     * with the playback progress.
     */
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

    /**
     * Updates the [_playbackState] Flow with the latest information from [exoPlayer].
     * This includes playback status, position, duration, and the current track.
     * It also updates the [MediaSessionCompat]'s playback state.
     */
    private fun updatePlaybackState() {
        val duration = if (exoPlayer.duration == C.TIME_UNSET) 0L else exoPlayer.duration
        val position = exoPlayer.currentPosition

        val mediaSessionState = PlaybackStateCompat.Builder()
            .setActions(getPlaybackActions())
            .setState(getPlaybackStateCompat(), position, 1f)
            .build()

        _playbackState.value = PlaybackState(
            isPlaying = exoPlayer.isPlaying,
            isPaused = !exoPlayer.isPlaying && exoPlayer.playbackState == Player.STATE_READY,
            currentPosition = position,
            duration = duration,
            currentMusic = currentMusic?.toMusicDto(),
            playbackState = exoPlayer.playbackState,
            playingMode = getPlayingMode()
        )

        mediaSession.setPlaybackState(mediaSessionState)
    }

    /**
     * Defines the set of actions supported by the media session (e.g., play, pause, skip).
     * @return A bitmask of supported [PlaybackStateCompat] actions.
     */
    private fun getPlaybackActions(): Long {
        return PlaybackStateCompat.ACTION_PLAY or
                PlaybackStateCompat.ACTION_PAUSE or
                PlaybackStateCompat.ACTION_SKIP_TO_NEXT or
                PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS or
                PlaybackStateCompat.ACTION_STOP or
                PlaybackStateCompat.ACTION_SEEK_TO
    }

    /**
     * Maps the [ExoPlayer]'s state to the corresponding [PlaybackStateCompat] state.
     * @return The current playback state as a [PlaybackStateCompat] constant.
     */
    private fun getPlaybackStateCompat(): Int {
        return when {
            exoPlayer.isPlaying -> PlaybackStateCompat.STATE_PLAYING
            exoPlayer.playbackState == Player.STATE_BUFFERING -> PlaybackStateCompat.STATE_BUFFERING
            else -> PlaybackStateCompat.STATE_PAUSED
        }
    }

    /**
     * Updates the [MediaSessionCompat] metadata with information from the [currentMusic].
     * This includes title, artist, album, duration, and album art.
     */
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

    /**
     * Handles actions received from the notification controls.
     * @param action The string action to be performed (e.g., [ACTION_PLAY], [ACTION_PAUSE]).
     */
    private fun handleNotificationAction(action: String) {
        when (action) {
            ACTION_PLAY -> playMusic()
            ACTION_PAUSE -> pauseMusic()
            ACTION_NEXT -> nextMusic()
            ACTION_PREVIOUS -> previousMusic()
            ACTION_STOP -> stopService()
        }
    }

    /**
     * Starts the service in the foreground if it's not already started.
     * This displays the persistent notification required for background playback.
     */
    private fun startForegroundService() {
        if (!isServiceStarted) {
            val notification = buildNotification(exoPlayer.isPlaying)
            startForeground(NOTIFICATION_ID, notification)
            isServiceStarted = true
        }
    }

    /**
     * Updates the foreground notification with the current playback state (playing or paused).
     *
     * @param isPlaying `true` if music is currently playing, `false` otherwise.
     */
    private fun updateNotification(isPlaying: Boolean) {
        if (isServiceStarted) {
            val notification = buildNotification(isPlaying)
            val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.notify(NOTIFICATION_ID, notification)
        }
    }

    /**
     * Builds the notification with playback controls.
     *
     * @param isPlaying `true` if music is currently playing, which determines the play/pause icon.
     * @return The constructed [Notification].
     */
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

    /**
     * Creates a [PendingIntent] to open [MainActivity] when the notification is clicked.
     * @return The configured [PendingIntent].
     */
    private fun createMainActivityIntent(): PendingIntent {
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra("OPEN_MUSIC_DETAIL", true)
        }
        return PendingIntent.getActivity(
            this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }

    /**
     * Gets the title and artist of the current track for display in the notification.
     * Provides default values if the track information is not available.
     * @return A [Pair] containing the title and artist strings.
     */
    private fun getCurrentTrackInfo(): Pair<String, String> {
        val title = currentMusic?.title ?: "Unknown Music"
        val artist = currentMusic?.artist ?: "Unknown Artist"
        return title to artist
    }

    /**
     * Calculates the current playback progress as a percentage.
     */
    private fun calculateProgress(): Int {
        val currentPosition = exoPlayer.currentPosition
        val duration = if (exoPlayer.duration == C.TIME_UNSET) 0L else exoPlayer.duration
        return if (duration > 0) {
            ((currentPosition.toFloat() / duration.toFloat()) * 100).toInt()
        } else 0
    }

    /**
     * Creates the play/pause action for the notification.
     * @param isPlaying `true` to show a pause button, `false` to show a play button.
     * @return The configured [NotificationCompat.Action].
     */
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

    /**
     * Creates a [PendingIntent] for a notification action (e.g., play, pause, next).
     * @param action The string constant representing the action.
     * @return The configured [PendingIntent] that will send the action to this service.
     */
    private fun createActionIntent(action: String): PendingIntent {
        val intent = Intent(this, MusicService::class.java).apply {
            this.action = action
        }
        return PendingIntent.getService(
            this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }

    /**
     * Creates the notification channel required for Android Oreo (API 26) and above.
     * This is necessary for displaying the foreground service notification.
     */
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


    /**
     * Binder for the [MusicService].
     * Allows clients (like [MainActivity]) to get a direct instance of the service
     * to call its public methods.
     */
    inner class MusicBinder : Binder() {
        /** Returns the instance of [MusicService] so clients can call public methods. */
        fun getService(): MusicService = this@MusicService
    }
}