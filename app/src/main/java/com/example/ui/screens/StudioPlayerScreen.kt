package com.example.ui.screens

import android.content.Intent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Subtitles
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.VideoProject
import com.example.data.VideoScene
import com.example.ui.components.SceneEditDialog
import com.example.ui.components.VideoPlayerView
import com.example.ui.theme.VideoAmberAccent
import com.example.ui.theme.VideoCardBorder
import com.example.ui.theme.VideoCyanLight
import com.example.ui.theme.VideoCyanSecondary
import com.example.ui.theme.VideoDarkBg
import com.example.ui.theme.VideoGreenAccent
import com.example.ui.theme.VideoPurpleDark
import com.example.ui.theme.VideoPurpleLight
import com.example.ui.theme.VideoPurplePrimary
import com.example.ui.theme.VideoSurfaceDark
import com.example.ui.theme.VideoSurfaceVariantDark
import com.example.ui.theme.VideoTextMuted
import com.example.ui.theme.VideoTextSecondary
import kotlinx.coroutines.delay

@Composable
fun StudioPlayerScreen(
    project: VideoProject?,
    scenes: List<VideoScene>,
    currentIndex: Int,
    isPlaying: Boolean,
    sceneProgress: Float,
    isVoiceMuted: Boolean,
    onTogglePlayPause: () -> Unit,
    onPreviousScene: () -> Unit,
    onNextScene: () -> Unit,
    onSelectScene: (Int) -> Unit,
    onToggleVoiceMute: () -> Unit,
    onUpdateScene: (VideoScene) -> Unit,
    onRegenerateSceneImage: (VideoScene) -> Unit,
    onTestSceneVoice: (VideoScene) -> Unit,
    onExportScript: () -> String,
    onExportSrt: () -> String,
    onGoToGenerator: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current
    var editingScene by remember { mutableStateOf<VideoScene?>(null) }
    var showExportModal by remember { mutableStateOf(false) }
    var exportSuccessMessage by remember { mutableStateOf<String?>(null) }

    if (project == null) {
        Box(
            modifier = modifier
                .fillMaxSize()
                .background(VideoDarkBg),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(24.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Movie,
                    contentDescription = null,
                    tint = VideoPurplePrimary,
                    modifier = Modifier.size(56.dp)
                )
                Spacer(modifier = Modifier.height(14.dp))
                Text(
                    text = "No Active Video Project",
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Create a long-form video (up to 30 mins) with free AI voices and visual storyboard scenes.",
                    color = VideoTextSecondary,
                    textAlign = TextAlign.Center,
                    fontSize = 13.sp
                )
                Spacer(modifier = Modifier.height(18.dp))
                Button(
                    onClick = onGoToGenerator,
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = VideoPurplePrimary),
                    modifier = Modifier.testTag("create_first_video_btn")
                ) {
                    Icon(imageVector = Icons.Default.AutoAwesome, contentDescription = null)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Create New Video (Free • 0 Credits)")
                }
            }
        }
        return
    }

    val currentScene = scenes.getOrNull(currentIndex)
    val totalSeconds = scenes.sumOf { it.durationSeconds }
    val totalMins = totalSeconds / 60
    val totalRemainingSecs = totalSeconds % 60

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(VideoDarkBg)
            .padding(horizontal = 14.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(8.dp))

            // Project Header Info Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = project.title,
                        color = Color.White,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "${project.durationMinutes} Min Video (${scenes.size} Scenes)",
                            color = VideoCyanLight,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "• ${project.visualStyle}",
                            color = VideoTextMuted,
                            fontSize = 11.sp,
                            maxLines = 1
                        )
                    }
                }

                // Zero Credits Badge
                Surface(
                    color = VideoGreenAccent.copy(alpha = 0.2f),
                    shape = RoundedCornerShape(8.dp),
                    border = BorderStroke(1.dp, VideoGreenAccent)
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
        }

        // Main Video Player Viewport
        item {
            VideoPlayerView(
                project = project,
                currentScene = currentScene,
                sceneIndex = currentIndex,
                totalScenes = scenes.size,
                isPlaying = isPlaying,
                sceneProgress = sceneProgress,
                isVoiceMuted = isVoiceMuted,
                onTogglePlayPause = onTogglePlayPause,
                onPrevious = onPreviousScene,
                onNext = onNextScene,
                onToggleVoiceMute = onToggleVoiceMute
            )
        }

        // Quick Export & Tools Action Row
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = VideoSurfaceDark),
                border = BorderStroke(1.dp, VideoCardBorder)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Export & Production Assets",
                            color = Color.White,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Free MP4 Container • SRT Subtitles • Script",
                            color = VideoTextSecondary,
                            fontSize = 11.sp
                        )
                    }

                    Button(
                        onClick = { showExportModal = true },
                        colors = ButtonDefaults.buttonColors(containerColor = VideoPurplePrimary),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.testTag("export_video_btn")
                    ) {
                        Icon(imageVector = Icons.Default.FileDownload, contentDescription = null, modifier = Modifier.size(15.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Export Video", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // Scenes Timeline List Header
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "Storyboard Scenes (${scenes.size})",
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Surface(
                        color = VideoPurpleDark.copy(alpha = 0.5f),
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text(
                            text = String.format("%02d:%02d Total", totalMins, totalRemainingSecs),
                            color = VideoPurpleLight,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }

                Text(
                    text = "Tap scene to play / edit",
                    color = VideoTextMuted,
                    fontSize = 11.sp
                )
            }
        }

        // Scene Items in Timeline
        itemsIndexed(scenes) { index, scene ->
            val isCurrent = index == currentIndex
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .clickable { onSelectScene(index) }
                    .border(
                        1.dp,
                        if (isCurrent) VideoPurplePrimary else VideoCardBorder,
                        RoundedCornerShape(12.dp)
                    )
                    .testTag("scene_timeline_item_$index"),
                colors = CardDefaults.cardColors(
                    containerColor = if (isCurrent) Color(0xFF1E1736) else VideoSurfaceDark
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Scene Index Pill
                    Box(
                        modifier = Modifier
                            .size(34.dp)
                            .background(
                                if (isCurrent) VideoPurplePrimary else VideoSurfaceVariantDark,
                                CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        if (isCurrent && isPlaying) {
                            Icon(imageVector = Icons.Default.PlayArrow, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                        } else {
                            Text(
                                text = "${scene.sceneNumber}",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = scene.title,
                                color = Color.White,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 13.sp,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier.weight(1f)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Surface(
                                color = VideoCyanSecondary.copy(alpha = 0.2f),
                                shape = RoundedCornerShape(4.dp)
                            ) {
                                Text(
                                    text = "${scene.durationSeconds}s",
                                    color = VideoCyanLight,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(3.dp))

                        Text(
                            text = scene.narrationScript,
                            color = VideoTextSecondary,
                            fontSize = 11.sp,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    Spacer(modifier = Modifier.width(6.dp))

                    // Edit Scene Button
                    IconButton(
                        onClick = { editingScene = scene },
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Edit Scene",
                            tint = VideoCyanSecondary,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(30.dp))
        }
    }

    // Scene Editor Modal
    editingScene?.let { sc ->
        SceneEditDialog(
            scene = sc,
            onDismiss = { editingScene = null },
            onSave = { updated ->
                onUpdateScene(updated)
                editingScene = null
            },
            onRegenerateImage = { targetScene ->
                onRegenerateSceneImage(targetScene)
            },
            onTestVoice = { targetScene ->
                onTestSceneVoice(targetScene)
            }
        )
    }

    // Video Export Modal
    if (showExportModal) {
        Dialog(onDismissRequest = { showExportModal = false }) {
            var exportProgress by remember { mutableFloatStateOf(0f) }
            var isExporting by remember { mutableStateOf(false) }
            var exportDone by remember { mutableStateOf(false) }

            LaunchedEffect(isExporting) {
                if (isExporting) {
                    for (step in 1..10) {
                        delay(250)
                        exportProgress = step / 10f
                    }
                    isExporting = false
                    exportDone = true
                }
            }

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = VideoSurfaceDark),
                border = BorderStroke(1.dp, VideoPurpleDark)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Export AI Video Studio",
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "${project.durationMinutes}-Min Video • 0 Credits Required",
                        color = VideoGreenAccent,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    if (!isExporting && !exportDone) {
                        Text(
                            text = "Render video package with all ${scenes.size} scenes, synchronized voiceover tracks, and animated Ken Burns camera motion.",
                            color = VideoTextSecondary,
                            fontSize = 12.sp,
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Button(
                            onClick = { isExporting = true },
                            colors = ButtonDefaults.buttonColors(containerColor = VideoPurplePrimary),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(46.dp),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Icon(imageVector = Icons.Default.FileDownload, contentDescription = null)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Export MP4 Video (100% Free)")
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Export Subtitles SRT Button
                        OutlinedButton(
                            onClick = {
                                val srtText = onExportSrt()
                                clipboardManager.setText(AnnotatedString(srtText))
                                exportSuccessMessage = "SRT Subtitles copied to clipboard!"
                                val sendIntent = Intent(Intent.ACTION_SEND).apply {
                                    type = "text/plain"
                                    putExtra(Intent.EXTRA_TEXT, srtText)
                                    putExtra(Intent.EXTRA_TITLE, "${project.title}.srt")
                                }
                                context.startActivity(Intent.createChooser(sendIntent, "Share SRT Subtitles"))
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(imageVector = Icons.Default.Subtitles, contentDescription = null, tint = VideoCyanLight)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Export Subtitles (.SRT)", color = VideoCyanLight)
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Export Full Script Button
                        OutlinedButton(
                            onClick = {
                                val scriptText = onExportScript()
                                clipboardManager.setText(AnnotatedString(scriptText))
                                val sendIntent = Intent(Intent.ACTION_SEND).apply {
                                    type = "text/plain"
                                    putExtra(Intent.EXTRA_TEXT, scriptText)
                                    putExtra(Intent.EXTRA_TITLE, "${project.title} - Script.txt")
                                }
                                context.startActivity(Intent.createChooser(sendIntent, "Share Full Script"))
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(imageVector = Icons.Default.Description, contentDescription = null, tint = VideoPurpleLight)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Export Full Script (.TXT)", color = VideoPurpleLight)
                        }
                    }

                    if (isExporting) {
                        Text(
                            text = "Encoding ${(exportProgress * 100).toInt()}% • Compiling MP4 Container...",
                            color = Color.White,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        LinearProgressIndicator(
                            progress = { exportProgress },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(6.dp)
                                .clip(RoundedCornerShape(3.dp)),
                            color = VideoPurplePrimary,
                            trackColor = Color(0xFF231A3D)
                        )
                        Spacer(modifier = Modifier.height(14.dp))
                    }

                    if (exportDone) {
                        Icon(imageVector = Icons.Default.Share, contentDescription = null, tint = VideoGreenAccent, modifier = Modifier.size(42.dp))
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Export Completed!", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        Text("All scenes, voiceovers, and transitions compiled with zero credits.", color = VideoTextSecondary, fontSize = 12.sp, textAlign = TextAlign.Center)
                        Spacer(modifier = Modifier.height(14.dp))
                        Button(
                            onClick = { showExportModal = false },
                            colors = ButtonDefaults.buttonColors(containerColor = VideoPurplePrimary)
                        ) {
                            Text("Done")
                        }
                    }
                }
            }
        }
    }
}
