package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "video_projects")
data class VideoProject(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val topicPrompt: String,
    val durationMinutes: Int, // 1 to 30 minutes
    val aspectRatio: String = "16:9", // "16:9", "9:16", "1:1"
    val visualStyle: String = "Cinematic 8K",
    val voicePersona: String = "Deep Cinematic Male",
    val language: String = "Hindi / Urdu",
    val bgMusicVibe: String = "Epic Orchestral",
    val totalScenes: Int = 0,
    val createdAt: Long = System.currentTimeMillis(),
    val status: String = "Ready" // "Draft", "Generating", "Ready"
)

@Entity(tableName = "video_scenes")
data class VideoScene(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val projectId: Long,
    val sceneNumber: Int,
    val title: String,
    val visualPrompt: String,
    val narrationScript: String,
    val durationSeconds: Int = 8,
    val imageUrl: String = "",
    val cameraMovement: String = "Slow Zoom In", // "Slow Zoom In", "Pan Right", "Slow Zoom Out", "Cinematic Orbit"
    val transition: String = "Crossfade"
)
