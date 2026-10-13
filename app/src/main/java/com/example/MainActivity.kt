package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material.icons.filled.FolderSpecial
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.screens.GeneratorScreen
import com.example.ui.screens.ProjectsLibraryScreen
import com.example.ui.screens.StudioPlayerScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.VideoAmberAccent
import com.example.ui.theme.VideoCyanLight
import com.example.ui.theme.VideoDarkBg
import com.example.ui.theme.VideoGreenAccent
import com.example.ui.theme.VideoPurpleDark
import com.example.ui.theme.VideoPurplePrimary
import com.example.ui.theme.VideoSurfaceDark
import com.example.ui.theme.VideoTextSecondary
import com.example.viewmodel.VideoStudioViewModel

enum class StudioTab(val label: String) {
    STUDIO("Studio & Player"),
    GENERATOR("New Video"),
    LIBRARY("Projects")
}

class MainActivity : ComponentActivity() {

    private val viewModel: VideoStudioViewModel by viewModels()

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            MyApplicationTheme {
                val allProjects by viewModel.allProjects.collectAsState()
                val currentProject by viewModel.currentProject.collectAsState()
                val currentScenes by viewModel.currentScenes.collectAsState()
                val generatorState by viewModel.generatorState.collectAsState()

                val isPlaying by viewModel.isPlaying.collectAsState()
                val currentSceneIndex by viewModel.currentSceneIndex.collectAsState()
                val sceneProgress by viewModel.sceneProgress.collectAsState()
                val isVoiceMuted by viewModel.isVoiceMuted.collectAsState()

                var currentTab by remember { mutableStateOf(StudioTab.GENERATOR) }

                // Auto bootstrap initial demo project if empty
                LaunchedEffect(allProjects) {
                    if (allProjects.isEmpty() && !generatorState.isGenerating) {
                        viewModel.startGeneratingNewVideo(
                            title = "Lost Civilizations of the Deep (30 Min)",
                            topicPrompt = "Secrets of Ancient Atlantis and Oceanic Mysteries",
                            durationMinutes = 30,
                            aspectRatio = "16:9",
                            visualStyle = "Cinematic 8K",
                            voicePersona = "Deep Cinematic Male",
                            language = "Hindi / Hinglish",
                            bgMusicVibe = "Epic Orchestral"
                        )
                    } else if (currentProject == null && allProjects.isNotEmpty()) {
                        viewModel.selectProject(allProjects.first())
                    }
                }

                // Switch to Studio tab once generation finishes
                LaunchedEffect(generatorState.progress, generatorState.isGenerating) {
                    if (!generatorState.isGenerating && generatorState.progress == 100 && currentProject != null) {
                        currentTab = StudioTab.STUDIO
                    }
                }

                Scaffold(
                    topBar = {
                        TopAppBar(
                            title = {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Surface(
                                            color = VideoPurplePrimary,
                                            shape = RoundedCornerShape(8.dp),
                                            modifier = Modifier.size(34.dp)
                                        ) {
                                            Box(contentAlignment = Alignment.Center) {
                                                Icon(
                                                    imageVector = Icons.Default.PlayCircle,
                                                    contentDescription = null,
                                                    tint = Color.White,
                                                    modifier = Modifier.size(20.dp)
                                                )
                                            }
                                        }
                                        Spacer(modifier = Modifier.width(10.dp))
                                        Column {
                                            Text(
                                                text = "AI Video Studio",
                                                color = Color.White,
                                                fontSize = 17.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                            Text(
                                                text = "Up to 30 Min • Free Unlimited AI",
                                                color = VideoCyanLight,
                                                fontSize = 11.sp
                                            )
                                        }
                                    }

                                    // Zero Credit Tag
                                    Surface(
                                        color = Color(0xFF163829),
                                        shape = RoundedCornerShape(8.dp),
                                        border = androidx.compose.foundation.BorderStroke(1.dp, VideoGreenAccent),
                                        modifier = Modifier.padding(end = 12.dp)
                                    ) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.ElectricBolt,
                                                contentDescription = null,
                                                tint = VideoAmberAccent,
                                                modifier = Modifier.size(13.dp)
                                            )
                                            Spacer(modifier = Modifier.width(3.dp))
                                            Text(
                                                text = "0 Credits",
                                                color = VideoGreenAccent,
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                    }
                                }
                            },
                            colors = TopAppBarDefaults.topAppBarColors(
                                containerColor = VideoSurfaceDark
                            )
                        )
                    },
                    bottomBar = {
                        NavigationBar(
                            containerColor = VideoSurfaceDark,
                            tonalElevation = 6.dp
                        ) {
                            NavigationBarItem(
                                selected = currentTab == StudioTab.STUDIO,
                                onClick = { currentTab = StudioTab.STUDIO },
                                icon = {
                                    Icon(imageVector = Icons.Default.PlayCircle, contentDescription = "Studio")
                                },
                                label = { Text("Studio Player", fontSize = 11.sp) },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = Color.White,
                                    selectedTextColor = VideoCyanLight,
                                    indicatorColor = VideoPurpleDark,
                                    unselectedIconColor = VideoTextSecondary,
                                    unselectedTextColor = VideoTextSecondary
                                ),
                                modifier = Modifier.testTag("nav_studio_tab")
                            )

                            NavigationBarItem(
                                selected = currentTab == StudioTab.GENERATOR,
                                onClick = { currentTab = StudioTab.GENERATOR },
                                icon = {
                                    Icon(imageVector = Icons.Default.AutoAwesome, contentDescription = "Generator")
                                },
                                label = { Text("New Video", fontSize = 11.sp) },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = Color.White,
                                    selectedTextColor = VideoCyanLight,
                                    indicatorColor = VideoPurpleDark,
                                    unselectedIconColor = VideoTextSecondary,
                                    unselectedTextColor = VideoTextSecondary
                                ),
                                modifier = Modifier.testTag("nav_generator_tab")
                            )

                            NavigationBarItem(
                                selected = currentTab == StudioTab.LIBRARY,
                                onClick = { currentTab = StudioTab.LIBRARY },
                                icon = {
                                    Icon(imageVector = Icons.Default.FolderSpecial, contentDescription = "Library")
                                },
                                label = { Text("Projects (${allProjects.size})", fontSize = 11.sp) },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = Color.White,
                                    selectedTextColor = VideoCyanLight,
                                    indicatorColor = VideoPurpleDark,
                                    unselectedIconColor = VideoTextSecondary,
                                    unselectedTextColor = VideoTextSecondary
                                ),
                                modifier = Modifier.testTag("nav_library_tab")
                            )
                        }
                    }
                ) { innerPadding ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                            .background(VideoDarkBg)
                    ) {
                        when (currentTab) {
                            StudioTab.STUDIO -> {
                                StudioPlayerScreen(
                                    project = currentProject,
                                    scenes = currentScenes,
                                    currentIndex = currentSceneIndex,
                                    isPlaying = isPlaying,
                                    sceneProgress = sceneProgress,
                                    isVoiceMuted = isVoiceMuted,
                                    onTogglePlayPause = { viewModel.togglePlayPause() },
                                    onPreviousScene = { viewModel.previousScene() },
                                    onNextScene = { viewModel.nextScene() },
                                    onSelectScene = { viewModel.seekToScene(it) },
                                    onToggleVoiceMute = { viewModel.toggleVoiceMute() },
                                    onUpdateScene = { viewModel.updateScene(it) },
                                    onRegenerateSceneImage = { viewModel.regenerateSceneVisual(it) },
                                    onTestSceneVoice = { viewModel.speakScene(it) },
                                    onExportScript = { viewModel.exportFullScript() },
                                    onExportSrt = { viewModel.exportSrtSubtitles() },
                                    onGoToGenerator = { currentTab = StudioTab.GENERATOR }
                                )
                            }
                            StudioTab.GENERATOR -> {
                                GeneratorScreen(
                                    generatorState = generatorState,
                                    onGenerate = { title, topic, durationMinutes, aspectRatio, visualStyle, voicePersona, language, bgMusicVibe ->
                                        viewModel.startGeneratingNewVideo(
                                            title = title,
                                            topicPrompt = topic,
                                            durationMinutes = durationMinutes,
                                            aspectRatio = aspectRatio,
                                            visualStyle = visualStyle,
                                            voicePersona = voicePersona,
                                            language = language,
                                            bgMusicVibe = bgMusicVibe
                                        )
                                    }
                                )
                            }
                            StudioTab.LIBRARY -> {
                                ProjectsLibraryScreen(
                                    projects = allProjects,
                                    currentProjectId = currentProject?.id,
                                    onSelectProject = { proj ->
                                        viewModel.selectProject(proj)
                                        currentTab = StudioTab.STUDIO
                                    },
                                    onDeleteProject = { viewModel.deleteProject(it) },
                                    onNewVideoClick = { currentTab = StudioTab.GENERATOR }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
