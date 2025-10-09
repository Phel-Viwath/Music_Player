package com.viwath.music_player.core.util

import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.core.content.FileProvider
import java.io.File

fun shareIntent(
    context: Context,
    filePath: String
){
    try {
        val file = File(filePath)
        if (file.exists()){
            val uri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.provider",
                file
            )
            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "audio/*"
                putExtra(Intent.EXTRA_STREAM, uri)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            context.startActivity(
                Intent.createChooser(shareIntent, "Share music via")
            )

        }
        else{
            Toast.makeText(context, "File not found", Toast.LENGTH_SHORT).show()
        }

    }catch (e: Exception){
        e.printStackTrace()
        Toast.makeText(context, e.message, Toast.LENGTH_SHORT).show()
    }
}