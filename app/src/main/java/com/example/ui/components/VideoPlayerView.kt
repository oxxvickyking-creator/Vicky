package com.example.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FastForward
import androidx.compose.material.icons.filled.FastRewind
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material.icons.filled.VolumeMute
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.data.VideoProject
import com.example.data.VideoScene
import com.example.ui.theme.VideoCyanLight
import com.example.ui.theme.VideoCyanSecondary
import com.example.ui.theme.VideoDarkBg
import com.example.ui.theme.VideoPurplePrimary

@Composable
fun VideoPlayerView(
    project: VideoProject,
    currentScene: VideoScene?,
    sceneIndex: Int,
    totalScenes: Int,
    isPlaying: Boolean,
    sceneProgress: Float,
    isVoiceMuted: Boolean,
    onTogglePlayPause: () -> Unit,
    onPrevious: () -> Unit,
    onNext: () -> Unit,
    onToggleVoiceMute: () -> Unit,
    modifier: Modifier = Modifier
) {
    val ratioFloat = when (project.aspectRatio) {
        "9:16" -> 9f / 16f
        "1:1" -> 1f
        else -> 16f / 9f // 16:9
    }

    // Dynamic Ken Burns Zoom & Pan effect calculation
    val cameraMovement = currentScene?.cameraMovement ?: "Slow Zoom In"
    val scale = when {
        cameraMovement.contains("Zoom In", ignoreCase = true) -> 1.0f + (sceneProgress * 0.14f)
        cameraMovement.contains("Zoom Out", ignoreCase = true) -> 1.15f - (sceneProgress * 0.12f)
        else -> 1.05f + (sceneProgress * 0.05f)
    }

    val translationX = when {
        cameraMovement.contains("Pan Right", ignoreCase = true) -> -35f + (sceneProgress * 70f)
        cameraMovement.contains("Pan Left", ignoreCase = true) -> 35f - (sceneProgress * 70f)
        cameraMovement.contains("Orbit", ignoreCase = true) -> kotlin.math.sin(sceneProgress * 3.14f) * 20f
        else -> 0f
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(VideoDarkBg)
            .border(1.dp, Color(0xFF2A2242), RoundedCornerShape(16.dp))
            .padding(10.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Video Screen Container
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(ratioFloat)
                .clip(RoundedCornerShape(12.dp))
                .background(Color.Black)
                .clickable { onTogglePlayPause() },
            contentAlignment = Alignment.Center
        ) {
            if (currentScene != null && currentScene.imageUrl.isNotBlank()) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(currentScene.imageUrl)
                        .crossfade(true)
                        .build(),
                    contentDescription = currentScene.visualPrompt,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxSize()
                        .graphicsLayer(
                            scaleX = scale,
                            scaleY = scale,
                            translationX = translationX
                        )
                )
            } else {
                // Fallback visual aesthetic background
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                listOf(Color(0xFF1E1035), Color(0xFF0D061C))
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.Videocam,
                            contentDescription = "Scene Video",
                            tint = VideoPurplePrimary,
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "AI Visual Scene Rendering...",
                            color = Color.White.copy(alpha = 0.8f),
                            fontSize = 13.sp
                        )
                    }
                }
            }

            // Top Badges Overlay
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.TopCenter)
                    .background(
                        Brush.verticalGradient(
                            listOf(Color.Black.copy(alpha = 0.75f), Color.Transparent)
                        )
                    )
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    color = Color.Black.copy(alpha = 0.6f),
                    shape = RoundedCornerShape(6.dp),
                    border = androidx.compose.foundation.BorderStroke(0.5.dp, VideoCyanSecondary)
                ) {
                    Text(
                        text = "Scene ${sceneIndex + 1} of $totalScenes  •  ${project.aspectRatio}",
                        color = VideoCyanLight,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }

                Surface(
                    color = Color(0xFF10B981).copy(alpha = 0.85f),
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        text = "0 CREDITS • FREE",
                        color = Color.White,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                    )
                }
            }

            // Bottom Subtitles & Captions Overlay
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
                    .background(
                        Brush.verticalGradient(
                            listOf(Color.Transparent, Color.Black.copy(alpha = 0.88f))
                        )
                    )
                    .padding(horizontal = 14.dp, vertical = 10.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (currentScene != null) {
                    Text(
                        text = currentScene.title,
                        color = Color.White.copy(alpha = 0.7f),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.height(3.dp))
                    Text(
                        text = currentScene.narrationScript,
                        color = Color.White,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        maxLines = 3,
                        overflow = TextOverflow.Ellipsis,
                        lineHeight = 17.sp,
                        modifier = Modifier
                            .background(Color.Black.copy(alpha = 0.5f), RoundedCornerShape(6.dp))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Progress Bar
        LinearProgressIndicator(
            progress = { sceneProgress },
            modifier = Modifier
                .fillMaxWidth()
                .height(4.dp)
                .clip(RoundedCornerShape(2.dp)),
            color = VideoPurplePrimary,
            trackColor = Color(0xFF231C38),
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Playback Controls Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Camera Motion Badge
            Text(
                text = currentScene?.cameraMovement ?: "Cinematic",
                color = VideoCyanLight,
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium
            )

            // Center Media buttons
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(
                    onClick = onPrevious,
                    enabled = sceneIndex > 0,
                    modifier = Modifier.testTag("prev_scene_btn")
                ) {
                    Icon(
                        imageVector = Icons.Default.FastRewind,
                        contentDescription = "Previous Scene",
                        tint = if (sceneIndex > 0) Color.White else Color.Gray
                    )
                }

                Surface(
                    onClick = onTogglePlayPause,
                    shape = CircleShape,
                    color = VideoPurplePrimary,
                    modifier = Modifier
                        .size(46.dp)
                        .testTag("play_pause_btn")
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                            contentDescription = if (isPlaying) "Pause" else "Play",
                            tint = Color.White,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                }

                IconButton(
                    onClick = onNext,
                    enabled = sceneIndex < totalScenes - 1,
                    modifier = Modifier.testTag("next_scene_btn")
                ) {
                    Icon(
                        imageVector = Icons.Default.FastForward,
                        contentDescription = "Next Scene",
                        tint = if (sceneIndex < totalScenes - 1) Color.White else Color.Gray
                    )
                }
            }

            // Voiceover Mute Toggle
            IconButton(
                onClick = onToggleVoiceMute,
                modifier = Modifier.testTag("voice_mute_btn")
            ) {
                Icon(
                    imageVector = if (isVoiceMuted) Icons.Default.VolumeMute else Icons.Default.VolumeUp,
                    contentDescription = if (isVoiceMuted) "Unmute Voice" else "Mute Voice",
                    tint = if (isVoiceMuted) Color.Gray else VideoCyanSecondary
                )
            }
        }
    }
}
