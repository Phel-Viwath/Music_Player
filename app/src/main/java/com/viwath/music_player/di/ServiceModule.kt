package com.viwath.music_player.di

import android.content.Context
import androidx.media3.exoplayer.ExoPlayer
import com.viwath.music_player.presentation.MusicPlayerManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ServiceComponent
import dagger.hilt.android.qualifiers.ActivityContext
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ServiceScoped

@Module
@InstallIn(ServiceComponent::class)
object ServiceModule {

    @Provides
    @ServiceScoped
    fun provideExoPlayer(
        @ApplicationContext context: Context
    ): ExoPlayer = ExoPlayer.Builder(context).build().apply {
        repeatMode = ExoPlayer.REPEAT_MODE_ALL
    }


    @Provides
    @ServiceScoped
    fun provideMusicPlayerManager(
        @ActivityContext context: Context
    ): MusicPlayerManager = MusicPlayerManager(context)

}