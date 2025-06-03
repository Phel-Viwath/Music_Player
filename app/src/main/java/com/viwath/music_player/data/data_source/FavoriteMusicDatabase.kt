package com.viwath.music_player.data.data_source

import androidx.room.Database
import androidx.room.RoomDatabase
import com.viwath.music_player.domain.model.FavoriteMusic

@Database(
    entities = [FavoriteMusic::class],
    version = 1
)
abstract class FavoriteMusicDatabase: RoomDatabase() {

    abstract val favoriteMusicDao: FavoriteMusicDao

    companion object{
        const val DATABASE_NAME = "favorite_music_db"
    }

}