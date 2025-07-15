package com.viwath.music_player.data.data_source

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.viwath.music_player.domain.model.FavoriteMusic
import com.viwath.music_player.domain.model.Playlist
import com.viwath.music_player.domain.model.PlaylistSong

@Database(
    entities = [FavoriteMusic::class, Playlist::class, PlaylistSong::class],
    version = 2
)
abstract class MusicDatabase: RoomDatabase() {

    abstract val favoriteMusicDao: FavoriteMusicDao
    abstract val playlistDao: PlaylistDao

    companion object{
        const val DATABASE_NAME = "favorite_music_db"

//        val MIGRATION_1_2 = object : Migration(1, 2) {
//            override fun migrate(db: SupportSQLiteDatabase) {
//                db.execSQL("""
//                    CREATE TABLE IF NOT EXISTS `playlist` (
//                        `playlistId` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
//                        `name` TEXT NOT NULL,
//                        `createAt` INTEGER NOT NULL,
//                        `description` TEXT,
//                        `thumbnail` TEXT
//                    )
//                """.trimIndent())
//                db.execSQL("""
//                    CREATE TABLE IF NOT EXISTS `playlist_song` (
//                        `playlistId` INTEGER NOT NULL,
//                        `musicId` TEXT NOT NULL,
//                        `musicUri` TEXT NOT NULL,
//                        PRIMARY KEY(`playlistId`, `musicId`),
//                        FOREIGN KEY(`playlistId`) REFERENCES `playlist`(`playlistId`) ON DELETE CASCADE
//                    )
//                """.trimIndent())
//            }
//        }


    }

}