package com.example.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.VideoScene
import com.example.ui.theme.VideoCyanLight
import com.example.ui.theme.VideoCyanSecondary
import com.example.ui.theme.VideoDarkBg
import com.example.ui.theme.VideoPurplePrimary
import com.example.ui.theme.VideoSurfaceVariantDark

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SceneEditDialog(
    scene: VideoScene,
    onDismiss: () -> Unit,
    onSave: (VideoScene) -> Unit,
    onRegenerateImage: (VideoScene) -> Unit,
    onTestVoice: (VideoScene) -> Unit
) {
    var title by remember { mutableStateOf(scene.title) }
    var visualPrompt by remember { mutableStateOf(scene.visualPrompt) }
    var narrationScript by remember { mutableStateOf(scene.narrationScript) }
    var durationSeconds by remember { mutableStateOf(scene.durationSeconds.toString()) }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = VideoSurfaceVariantDark)
        ) {
            Column(
                modifier = Modifier
                    .padding(18.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = null,
                            tint = VideoCyanSecondary
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Edit Scene ${scene.sceneNumber}",
                            color = Color.White,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = Color.White
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Scene Title / Heading") },
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = VideoDarkBg,
                        unfocusedContainerColor = VideoDarkBg,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = narrationScript,
                    onValueChange = { narrationScript = it },
                    label = { Text("Spoken Voiceover Script") },
                    minLines = 3,
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = VideoDarkBg,
                        unfocusedContainerColor = VideoDarkBg,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Test Voice Button
                OutlinedButton(
                    onClick = {
                        val tempScene = scene.copy(narrationScript = narrationScript)
                        onTestVoice(tempScene)
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        imageVector = Icons.Default.VolumeUp,
                        contentDescription = null,
                        tint = VideoCyanLight
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Preview Voice (100% Free On-Device TTS)", color = VideoCyanLight)
                }

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = visualPrompt,
                    onValueChange = { visualPrompt = it },
                    label = { Text("Visual Scene Prompt (AI Image)") },
                    minLines = 3,
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = VideoDarkBg,
                        unfocusedContainerColor = VideoDarkBg,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Regenerate image button
                OutlinedButton(
                    onClick = {
                        val tempScene = scene.copy(visualPrompt = visualPrompt)
                        onRegenerateImage(tempScene)
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = null,
                        tint = VideoPurplePrimary
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Re-render Visual (0 Credits Free)", color = VideoPurplePrimary)
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    OutlinedButton(onClick = onDismiss) {
                        Text("Cancel", color = Color.White)
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Button(
                        onClick = {
                            val dur = durationSeconds.toIntOrNull() ?: 8
                            val updated = scene.copy(
                                title = title,
                                narrationScript = narrationScript,
                                visualPrompt = visualPrompt,
                                durationSeconds = dur.coerceIn(4, 30)
                            )
                            onSave(updated)
                            onDismiss()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = VideoPurplePrimary),
                        modifier = Modifier.testTag("save_scene_changes_btn")
                    ) {
                        Text("Save Changes", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
