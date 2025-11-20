package com.example.testapp2.utils

import android.util.Log
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.storage.storage
import java.io.File

val supabase = createSupabaseClient(
    supabaseUrl = "https://jkpwyupdtppenidbsgxr.supabase.co",
    supabaseKey = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6ImprcHd5dXBkdHBwZW5pZGJzZ3hyIiwicm9sZSI6ImFub24iLCJpYXQiOjE3NDgzMzMxMTcsImV4cCI6MjA2MzkwOTExN30.ZBIrg12Ihc33vchLq_MumIgiQGeoAUVEvzfxpBJrWhs"
) {
    install(io.github.jan.supabase.storage.Storage)
}

suspend fun uploadAvatar(file: File, userId: String) {
    try {
        val bucket = supabase.storage["avatars"]
        bucket.upload("$userId.jpg", file.readBytes())
    } catch (e: Exception) {
        Log.e("Supabase", "Upload failed: ${e.message}")
        throw e
    }
}

suspend fun getAvatarUrl(userId: String): String {
    val bucket = supabase.storage["avatars"]
    return bucket.publicUrl("$userId.jpg")
}