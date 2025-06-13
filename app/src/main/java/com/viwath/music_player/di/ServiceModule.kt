package com.viwath.music_player.di

import android.content.Context
import android.support.v4.media.session.MediaSessionCompat
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import com.viwath.music_player.domain.service.AudioVisualizer
import com.viwath.music_player.presentation.MusicPlayerManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ServiceComponent
import dagger.hilt.android.qualifiers.ActivityContext
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ServiceScoped
import javax.inject.Singleton

@Module
@InstallIn(ServiceComponent::class)
object ServiceModule {

    @Provides
    @ServiceScoped
    fun provideMediaSession(
        @ApplicationContext context: Context
    ): MediaSessionCompat = MediaSessionCompat(context, "MusicService").apply {
        isActive = true
    }


    @Provides
    @ServiceScoped
    fun provideMusicPlayerManager(
        @ActivityContext context: Context
    ): MusicPlayerManager = MusicPlayerManager(context)

}