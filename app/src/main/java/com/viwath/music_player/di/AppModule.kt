package com.viwath.music_player.di

import android.app.Application
import android.content.Context
import androidx.media3.exoplayer.ExoPlayer
import androidx.room.Room
import com.viwath.music_player.data.data_source.FavoriteMusicDatabase
import com.viwath.music_player.data.repository.MusicRepositoryImp
import com.viwath.music_player.domain.repository.MusicRepository
import com.viwath.music_player.domain.use_case.AddFavorUseCase
import com.viwath.music_player.domain.use_case.FavoriteUseCase
import com.viwath.music_player.domain.use_case.GetFavorUseCase
import com.viwath.music_player.domain.use_case.GetMusicsUseCase
import com.viwath.music_player.domain.use_case.RemoveFavorUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ServiceScoped
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideFavoriteMusicDatabase(app: Application): FavoriteMusicDatabase {
        return Room.databaseBuilder(
            app,
            FavoriteMusicDatabase::class.java,
            FavoriteMusicDatabase.DATABASE_NAME
        ).build()
    }

    @Provides
    @Singleton
    fun provideMusicRepository(
        @ApplicationContext context: Context,
        db: FavoriteMusicDatabase
    ): MusicRepository {
        return MusicRepositoryImp(context, db.favoriteMusicDao)
    }

    @Provides
    @Singleton
    fun provideGetMusicsUseCase(
        repository: MusicRepository
    ): GetMusicsUseCase = GetMusicsUseCase(repository)

    @Provides
    @Singleton
    fun provideExoPlayer(
        @ApplicationContext context: Context
    ): ExoPlayer = ExoPlayer.Builder(context).build().apply {
        repeatMode = ExoPlayer.REPEAT_MODE_ALL
    }

    // provide favor use-case
    @Provides
    @Singleton
    fun provideFavorUseCase(
        repository: MusicRepository
    ): FavoriteUseCase = FavoriteUseCase(
        addFavorUseCase = AddFavorUseCase(repository),
        removeFavorUseCase = RemoveFavorUseCase(repository),
        getFavorUseCase = GetFavorUseCase(repository)
    )

}