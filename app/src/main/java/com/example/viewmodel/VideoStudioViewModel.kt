package com.example.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.room.Room
import com.example.data.AppDatabase
import com.example.data.VideoProject
import com.example.data.VideoScene
import com.example.service.AiVideoGeneratorService
import com.example.service.FreeVoiceEngine
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlin.random.Random

data class GeneratorUiState(
    val isGenerating: Boolean = false,
    val progress: Int = 0,
    val statusMessage: String = "",
    val errorMessage: String? = null
)

class VideoStudioViewModel(application: Application) : AndroidViewModel(application) {

    private val db = Room.databaseBuilder(
        application,
        AppDatabase::class.java,
        "ai_video_studio.db"
    ).fallbackToDestructiveMigration().build()

    private val dao = db.videoProjectDao()
    val voiceEngine = FreeVoiceEngine(application)

    val allProjects: StateFlow<List<VideoProject>> = dao.getAllProjects()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    private val _currentProject = MutableStateFlow<VideoProject?>(null)
    val currentProject: StateFlow<VideoProject?> = _currentProject.asStateFlow()

    private val _currentScenes = MutableStateFlow<List<VideoScene>>(emptyList())
    val currentScenes: StateFlow<List<VideoScene>> = _currentScenes.asStateFlow()

    private val _generatorState = MutableStateFlow(GeneratorUiState())
    val generatorState: StateFlow<GeneratorUiState> = _generatorState.asStateFlow()

    // Video Player State
    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> = _isPlaying.asStateFlow()

    private val _currentSceneIndex = MutableStateFlow(0)
    val currentSceneIndex: StateFlow<Int> = _currentSceneIndex.asStateFlow()

    private val _sceneProgress = MutableStateFlow(0f)
    val sceneProgress: StateFlow<Float> = _sceneProgress.asStateFlow()

    private val _isVoiceMuted = MutableStateFlow(false)
    val isVoiceMuted: StateFlow<Boolean> = _isVoiceMuted.asStateFlow()

    private var playbackJob: Job? = null

    init {
        // Pre-populate a demo project if empty
        viewModelScope.launch {
            val existing = dao.getAllProjects()
            // Observe or check if initial sample is needed
        }
    }

    fun selectProject(project: VideoProject) {
        _currentProject.value = project
        stopPlayback()
        _currentSceneIndex.value = 0
        _sceneProgress.value = 0f

        viewModelScope.launch {
            val scenes = dao.getScenesForProjectList(project.id)
            if (scenes.isEmpty()) {
                // If project has no scenes yet, generate them
                generateVideoForProject(project)
            } else {
                _currentScenes.value = scenes
            }
        }
    }

    fun startGeneratingNewVideo(
        title: String,
        topicPrompt: String,
        durationMinutes: Int,
        aspectRatio: String,
        visualStyle: String,
        voicePersona: String,
        language: String,
        bgMusicVibe: String
    ) {
        viewModelScope.launch {
            _generatorState.value = GeneratorUiState(
                isGenerating = true,
                progress = 5,
                statusMessage = "Setting up 30-min AI Video Project..."
            )

            val calculatedScenes = when {
                durationMinutes <= 1 -> 6
                durationMinutes <= 3 -> 12
                durationMinutes <= 5 -> 18
                durationMinutes <= 10 -> 28
                durationMinutes <= 20 -> 40
                else -> 55 // Up to 30 mins
            }

            val project = VideoProject(
                title = title.ifBlank { "Epic $durationMinutes-Min: ${topicPrompt.take(25)}..." },
                topicPrompt = topicPrompt.ifBlank { "Journey Through Time & Space" },
                durationMinutes = durationMinutes.coerceIn(1, 30),
                aspectRatio = aspectRatio,
                visualStyle = visualStyle,
                voicePersona = voicePersona,
                language = language,
                bgMusicVibe = bgMusicVibe,
                totalScenes = calculatedScenes,
                status = "Generating"
            )

            val projectId = dao.insertProject(project)
            val savedProject = project.copy(id = projectId)
            _currentProject.value = savedProject

            try {
                val scenes = AiVideoGeneratorService.generateVideoScenes(
                    project = savedProject,
                    onProgress = { prog, msg ->
                        _generatorState.value = _generatorState.value.copy(
                            progress = prog,
                            statusMessage = msg
                        )
                    }
                )

                dao.insertScenes(scenes)
                val updatedProject = savedProject.copy(status = "Ready", totalScenes = scenes.size)
                dao.updateProject(updatedProject)

                _currentProject.value = updatedProject
                _currentScenes.value = scenes
                _currentSceneIndex.value = 0
                _sceneProgress.value = 0f

                _generatorState.value = GeneratorUiState(
                    isGenerating = false,
                    progress = 100,
                    statusMessage = "Video Generated Successfully!"
                )
            } catch (e: Exception) {
                _generatorState.value = GeneratorUiState(
                    isGenerating = false,
                    errorMessage = "Error generating video: ${e.localizedMessage}"
                )
            }
        }
    }

    fun generateVideoForProject(project: VideoProject) {
        viewModelScope.launch {
            _generatorState.value = GeneratorUiState(isGenerating = true, progress = 10, statusMessage = "Compiling scenes...")
            try {
                val scenes = AiVideoGeneratorService.generateVideoScenes(project) { p, m ->
                    _generatorState.value = _generatorState.value.copy(progress = p, statusMessage = m)
                }
                dao.insertScenes(scenes)
                _currentScenes.value = scenes
                _generatorState.value = GeneratorUiState(isGenerating = false, progress = 100)
            } catch (e: Exception) {
                _generatorState.value = GeneratorUiState(isGenerating = false, errorMessage = e.message)
            }
        }
    }

    // Playback Engine
    fun togglePlayPause() {
        if (_isPlaying.value) {
            pausePlayback()
        } else {
            startPlayback()
        }
    }

    private fun startPlayback() {
        val scenes = _currentScenes.value
        if (scenes.isEmpty()) return

        _isPlaying.value = true
        val project = _currentProject.value

        // Setup voice
        if (project != null) {
            voiceEngine.setVoicePersona(project.voicePersona, project.language)
        }

        playbackJob?.cancel()
        playbackJob = viewModelScope.launch {
            while (_isPlaying.value) {
                val idx = _currentSceneIndex.value
                val scene = scenes.getOrNull(idx) ?: break

                // Play scene voice if not muted
                if (!_isVoiceMuted.value) {
                    voiceEngine.speak(scene.narrationScript, "scene_$idx")
                }

                val durationMs = (scene.durationSeconds * 1000L).coerceAtLeast(3000L)
                val stepIntervalMs = 50L
                val totalSteps = (durationMs / stepIntervalMs).toInt()

                for (step in 0..totalSteps) {
                    if (!_isPlaying.value) break
                    _sceneProgress.value = step.toFloat() / totalSteps.toFloat()
                    delay(stepIntervalMs)
                }

                if (!_isPlaying.value) break

                // Next Scene
                if (idx < scenes.size - 1) {
                    _currentSceneIndex.value = idx + 1
                    _sceneProgress.value = 0f
                } else {
                    // Loop back or finish
                    _isPlaying.value = false
                    _currentSceneIndex.value = 0
                    _sceneProgress.value = 0f
                    voiceEngine.stop()
                    break
                }
            }
        }
    }

    fun pausePlayback() {
        _isPlaying.value = false
        playbackJob?.cancel()
        playbackJob = null
        voiceEngine.stop()
    }

    fun stopPlayback() {
        pausePlayback()
        _sceneProgress.value = 0f
    }

    fun seekToScene(index: Int) {
        val scenes = _currentScenes.value
        if (index in scenes.indices) {
            _currentSceneIndex.value = index
            _sceneProgress.value = 0f
            if (_isPlaying.value) {
                startPlayback()
            }
        }
    }

    fun nextScene() {
        val current = _currentSceneIndex.value
        val total = _currentScenes.value.size
        if (current < total - 1) {
            seekToScene(current + 1)
        }
    }

    fun previousScene() {
        val current = _currentSceneIndex.value
        if (current > 0) {
            seekToScene(current - 1)
        }
    }

    fun toggleVoiceMute() {
        val newMute = !_isVoiceMuted.value
        _isVoiceMuted.value = newMute
        if (newMute) {
            voiceEngine.stop()
        } else if (_isPlaying.value) {
            val scene = _currentScenes.value.getOrNull(_currentSceneIndex.value)
            if (scene != null) {
                voiceEngine.speak(scene.narrationScript)
            }
        }
    }

    fun speakScene(scene: VideoScene) {
        val project = _currentProject.value ?: return
        voiceEngine.setVoicePersona(project.voicePersona, project.language)
        voiceEngine.speak(scene.narrationScript, "preview_${scene.id}")
    }

    fun regenerateSceneVisual(scene: VideoScene) {
        viewModelScope.launch {
            val project = _currentProject.value ?: return@launch
            val seed = Random.nextInt(10000, 999999)
            val newUrl = "https://image.pollinations.ai/prompt/${java.net.URLEncoder.encode("${scene.visualPrompt}, ${project.visualStyle}, cinematic 8k masterpiece", "UTF-8")}?width=1280&height=720&seed=$seed&nologo=true"
            val updated = scene.copy(imageUrl = newUrl)
            dao.updateScene(updated)
            _currentScenes.value = _currentScenes.value.map { if (it.id == scene.id) updated else it }
        }
    }

    fun updateScene(scene: VideoScene) {
        viewModelScope.launch {
            dao.updateScene(scene)
            _currentScenes.value = _currentScenes.value.map { if (it.id == scene.id) scene else it }
        }
    }

    fun deleteProject(project: VideoProject) {
        viewModelScope.launch {
            dao.deleteScenesForProject(project.id)
            dao.deleteProject(project)
            if (_currentProject.value?.id == project.id) {
                _currentProject.value = null
                _currentScenes.value = emptyList()
                stopPlayback()
            }
        }
    }

    fun exportSrtSubtitles(): String {
        val scenes = _currentScenes.value
        val sb = StringBuilder()
        var currentSeconds = 0

        scenes.forEachIndexed { i, scene ->
            val startSec = currentSeconds
            val endSec = currentSeconds + scene.durationSeconds
            currentSeconds = endSec

            sb.append("${i + 1}\n")
            sb.append("${formatSrtTime(startSec)} --> ${formatSrtTime(endSec)}\n")
            sb.append("${scene.narrationScript}\n\n")
        }
        return sb.toString()
    }

    private fun formatSrtTime(seconds: Int): String {
        val hours = seconds / 3600
        val mins = (seconds % 3600) / 60
        val secs = seconds % 60
        return String.format("%02d:%02d:%02d,000", hours, mins, secs)
    }

    fun exportFullScript(): String {
        val proj = _currentProject.value ?: return ""
        val scenes = _currentScenes.value
        val sb = StringBuilder()
        sb.append("=== AI VIDEO PRODUCTION SCRIPT ===\n")
        sb.append("Title: ${proj.title}\n")
        sb.append("Duration: ${proj.durationMinutes} Minutes (Total Scenes: ${scenes.size})\n")
        sb.append("Style: ${proj.visualStyle} | Ratio: ${proj.aspectRatio} | Voice: ${proj.voicePersona}\n")
        sb.append("Zero Credits Used: 100% Free Unlimited Generation\n")
        sb.append("=====================================\n\n")

        scenes.forEach { scene ->
            sb.append("SCENE ${scene.sceneNumber}: ${scene.title} [${scene.durationSeconds}s]\n")
            sb.append("VISUAL PROMPT: ${scene.visualPrompt}\n")
            sb.append("CAMERA MOTION: ${scene.cameraMovement}\n")
            sb.append("VOICEOVER: \"${scene.narrationScript}\"\n\n")
        }
        return sb.toString()
    }

    override fun onCleared() {
        super.onCleared()
        playbackJob?.cancel()
        voiceEngine.shutdown()
    }
}
