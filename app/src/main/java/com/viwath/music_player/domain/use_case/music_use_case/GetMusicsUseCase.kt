package com.viwath.music_player.domain.use_case.music_use_case

import android.util.Log
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.viwath.music_player.core.util.Resource
import com.viwath.music_player.core.common.SortOrder
import com.viwath.music_player.domain.model.FavoriteMusic
import com.viwath.music_player.domain.model.Music
import com.viwath.music_player.domain.model.dto.MusicDto
import com.viwath.music_player.domain.repository.MusicRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class GetMusicsUseCase @Inject constructor(
    private val musicRepository: MusicRepository
) {
    operator fun invoke(sortOrder: SortOrder): Flow<Resource<List<MusicDto>>> = flow {
        emit(Resource.Loading())
        try {
            val musicFiles = musicRepository.getMusicFiles()
            val favoriteMusicList = musicRepository.getFavoriteMusic().first()
            val mergedList = mergeList(musicFiles, favoriteMusicList)
            Log.d("GetMusicUseCase", "invoke: $musicFiles")
            val musics = when (sortOrder) {
                SortOrder.TITLE -> mergedList.sortedBy { it.title }
                SortOrder.DURATION -> mergedList.sortedBy { it.duration }
                SortOrder.DATE -> mergedList.sortedBy { it.addDate }
            }
            emit(Resource.Success(musics))
        } catch (e: Exception) {
            FirebaseCrashlytics.getInstance().log("GetMusicListUseCase")
            FirebaseCrashlytics.getInstance().recordException(e)
            emit(Resource.Error(e.message ?: "Unknown error"))
        }
    }.flowOn(Dispatchers.IO)

    private fun mergeList(
        musicList: List<Music>,
        favoriteMusicList: List<FavoriteMusic>
    ): List<MusicDto>{
        val favoriteIds = favoriteMusicList.map { it.id }

        return musicList.map { music ->
            MusicDto(
                id = music.id,
                title = music.title,
                artist = music.artist,
                album = music.album,
                albumId = music.albumId,
                duration = music.duration,
                imagePath = music.imagePath,
                uri = music.uri,
                addDate = music.addDate,
                isFavorite = music.id in favoriteIds
            )
        }
    }
}