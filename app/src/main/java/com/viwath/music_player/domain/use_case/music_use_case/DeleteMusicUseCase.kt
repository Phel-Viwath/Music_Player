package com.viwath.music_player.domain.use_case.music_use_case

import android.os.Build
import android.util.Log
import com.viwath.music_player.core.util.DeleteResult
import com.viwath.music_player.core.util.Resource
import com.viwath.music_player.domain.model.Music
import com.viwath.music_player.domain.repository.MusicRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class DeleteMusicUseCase @Inject constructor(
    private val repository: MusicRepository
) {

     operator fun invoke(music: Music): Flow<Resource<DeleteResult>> = flow {
         emit(Resource.Loading())
         try {
             // Check if we need permission first (Android 11+)
             if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                 val intentSender = repository.getDeletePermissionIntent(music)
                 if (intentSender != null) {
                     Log.d("DeleteMusicUseCase", "invoke: need permission")
                     emit(Resource.Success(DeleteResult.NeedPermission(intentSender)))
                     return@flow
                 }
             }

             // Try to delete
             val deletedRows = repository.deleteMusic(music)
             if (deletedRows > 0) {
                 Log.d("DeleteMusicUseCase", "invoke: Delete Success")
                 emit(Resource.Success(DeleteResult.Success))
             } else {
                 Log.d("DeleteMusicUseCase", "invoke: Fail to delete")
                 emit(Resource.Error("Failed to delete music"))
             }
         }catch (e: Exception){
             Log.e("DeleteMusicUseCase", "invoke: ${e.message}")
             emit(Resource.Error(e.message ?: "Unknown error"))
         }
    }

    // Direct delete after permission granted (no permission check)
    fun executeDelete(music: Music): Flow<Resource<DeleteResult>> = flow {
        emit(Resource.Loading())
        try {
            val deletedRows = repository.deleteMusic(music)
            if (deletedRows > 0) {
                emit(Resource.Success(DeleteResult.Success))
            } else {
                emit(Resource.Error("Failed to delete music"))
            }
        } catch (e: SecurityException) {
            emit(Resource.Error("Permission denied: ${e.message}"))
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "Unknown error"))
        }
    }



}