// app/src/main/java/com/example/testapp2/utils/AvatarUploader.kt
package com.example.testapp2.utils

import java.io.File
import kotlinx.coroutines.*

object AvatarUploader {
    fun uploadAvatarAsync(file: File, userId: String, onSuccess: (String) -> Unit, onError: (Throwable) -> Unit) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                uploadAvatar(file, userId)
                val url = getAvatarUrl(userId)
                withContext(Dispatchers.Main) {
                    onSuccess(url)
                }
            } catch (e: Throwable) {
                withContext(Dispatchers.Main) {
                    onError(e)
                }
            }
        }
    }
}
