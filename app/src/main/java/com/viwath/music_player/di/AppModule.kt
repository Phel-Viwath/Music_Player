package com.viwath.music_player.di

import android.app.Application
import android.content.Context
import androidx.media3.exoplayer.ExoPlayer
import androidx.room.Room
import com.viwath.music_player.core.util.MyPrefs
import com.viwath.music_player.data.data_source.MusicDatabase
import com.viwath.music_player.data.repository.MusicRepositoryImp
import com.viwath.music_player.domain.repository.MusicRepository
import com.viwath.music_player.domain.use_case.AddFavorUseCase
import com.viwath.music_player.domain.use_case.ClearCacheUseCase
import com.viwath.music_player.domain.use_case.FavoriteUseCase
import com.viwath.music_player.domain.use_case.GetFavorUseCase
import com.viwath.music_player.domain.use_case.GetMusicsUseCase
import com.viwath.music_player.domain.use_case.RemoveFavorUseCase
import com.viwath.music_player.domain.use_case.album_use_case.AlbumUseCase
import com.viwath.music_player.domain.use_case.album_use_case.GetAlbumMusicUseCase
import com.viwath.music_player.domain.use_case.album_use_case.GetAlbumsUseCase
import com.viwath.music_player.domain.use_case.playlist_use_case.AddPlaylistSongUseCase
import com.viwath.music_player.domain.use_case.playlist_use_case.DeletePlaylistUseCase
import com.viwath.music_player.domain.use_case.playlist_use_case.GetAllPlaylistUseCase
import com.viwath.music_player.domain.use_case.playlist_use_case.GetPlaylistSongsUseCase
import com.viwath.music_player.domain.use_case.playlist_use_case.GetPlaylistUseCase
import com.viwath.music_player.domain.use_case.playlist_use_case.NewPlaylistUseCase
import com.viwath.music_player.domain.use_case.playlist_use_case.PlaylistUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideFavoriteMusicDatabase(app: Application): MusicDatabase {
        return Room.databaseBuilder(
            app,
            MusicDatabase::class.java,
            MusicDatabase.DATABASE_NAME
            )
            //.addMigrations(MusicDatabase.MIGRATION_1_2)
            .build()
    }

    @Provides
    @Singleton
    fun provideMusicRepository(
        @ApplicationContext context: Context,
        db: MusicDatabase,
    ): MusicRepository {
        return MusicRepositoryImp(context, db.favoriteMusicDao, db.playlistDao)
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

    @Singleton
    @Provides
    fun providePlaylistUseCase(repository: MusicRepository): PlaylistUseCase{
        return PlaylistUseCase(
            newPlaylistUseCase = NewPlaylistUseCase(repository),
            getAllPlaylistUseCase = GetAllPlaylistUseCase(repository),
            deletePlaylistUseCase = DeletePlaylistUseCase(repository),
            getPlaylistSongUseCase = GetPlaylistSongsUseCase(repository),
            addPlaylistSongUseCase = AddPlaylistSongUseCase(repository),
            getPlaylistUseCase = GetPlaylistUseCase(repository)
        )
    }

    @Singleton
    @Provides
    fun provideAlbumUseCase(repository: MusicRepository): AlbumUseCase = AlbumUseCase(
        GetAlbumsUseCase(repository),
        GetAlbumMusicUseCase(repository)
    )

    @Provides
    @Singleton
    fun provideClearCacheUseCase(repository: MusicRepository): ClearCacheUseCase = ClearCacheUseCase(repository)


    @Singleton
    @Provides
    fun provideMyPrefs(
        @ApplicationContext context: Context
    ): MyPrefs = MyPrefs(context)

}