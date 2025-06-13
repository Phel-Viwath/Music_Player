package com.viwath.music_player.di

import androidx.media3.exoplayer.ExoPlayer
import com.viwath.music_player.domain.service.AudioVisualizer
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)
object ViewModelModule {

    @Provides
    fun provideAudioVisualizer(
        exoPlayer: ExoPlayer
    ): AudioVisualizer = AudioVisualizer(exoPlayer)
}