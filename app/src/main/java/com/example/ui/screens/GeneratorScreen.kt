package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material.icons.filled.HourglassTop
import androidx.compose.material.icons.filled.PlayCircleFilled
import androidx.compose.material.icons.filled.RecordVoiceOver
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.VideoAmberAccent
import com.example.ui.theme.VideoCardBorder
import com.example.ui.theme.VideoCyanLight
import com.example.ui.theme.VideoCyanSecondary
import com.example.ui.theme.VideoCyanDark
import com.example.ui.theme.VideoDarkBg
import com.example.ui.theme.VideoGreenAccent
import com.example.ui.theme.VideoPurpleDark
import com.example.ui.theme.VideoPurpleLight
import com.example.ui.theme.VideoPurplePrimary
import com.example.ui.theme.VideoSurfaceDark
import com.example.ui.theme.VideoSurfaceVariantDark
import com.example.ui.theme.VideoTextMuted
import com.example.ui.theme.VideoTextSecondary
import com.example.viewmodel.GeneratorUiState

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun GeneratorScreen(
    generatorState: GeneratorUiState,
    onGenerate: (
        title: String,
        topic: String,
        durationMinutes: Int,
        aspectRatio: String,
        visualStyle: String,
        voicePersona: String,
        language: String,
        bgMusicVibe: String
    ) -> Unit,
    modifier: Modifier = Modifier
) {
    var prompt by remember {
        mutableStateOf("Mysteries of the Deep Ocean and Lost Civilizations")
    }
    var title by remember { mutableStateOf("Deep Ocean Expedition") }
    var durationMinutes by remember { mutableFloatStateOf(30f) } // Default 30 min as requested!
    var selectedAspectRatio by remember { mutableStateOf("16:9") }
    var selectedStyle by remember { mutableStateOf("Cinematic 8K") }
    var selectedVoice by remember { mutableStateOf("Deep Cinematic Male") }
    var selectedLanguage by remember { mutableStateOf("Hindi / Hinglish") }
    var selectedMusic by remember { mutableStateOf("Epic Orchestral") }

    val presetIdeas = listOf(
        "Ancient Lost Civilizations & Hidden Temples" to 30,
        "Secret Mysteries of the Cosmos & Black Holes" to 30,
        "AI Revolution 2030: Future of Humanity" to 15,
        "Horror Night: Paranormal Whispers" to 20,
        "Motivational Success Story: From Zero to Legend" to 10,
        "Supercars Evolution & Hyper-Engineering" to 5
    )

    val durationPresets = listOf(1, 3, 5, 10, 15, 20, 25, 30)
    val aspectRatios = listOf("16:9" to "YouTube / Landscape", "9:16" to "Reels / TikTok", "1:1" to "Square")
    val styles = listOf("Cinematic 8K", "Anime Fantasy", "Cyberpunk 2077", "Documentary Noir", "3D Pixar Animation", "Epic Sci-Fi")
    val voices = listOf("Deep Cinematic Male", "Energetic Tech", "Storyteller Female", "Warm Explainer", "Epic Movie Trailer")
    val languages = listOf("Hindi / Hinglish", "Urdu / Roman Urdu", "English US", "Spanish")
    val musics = listOf("Epic Orchestral", "Cyberpunk Synth", "Acoustic Emotional", "Deep Space Ambient", "Lo-Fi Chill")

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(VideoDarkBg)
            .padding(horizontal = 16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Spacer(modifier = Modifier.height(12.dp))

        // Hero Zero-Credit Banner
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = VideoSurfaceDark),
            border = BorderStroke(1.dp, VideoPurpleDark)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.horizontalGradient(
                            listOf(Color(0xFF221142), Color(0xFF0F1E36))
                        )
                    )
                    .padding(14.dp)
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.ElectricBolt,
                                contentDescription = null,
                                tint = VideoAmberAccent,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "100% UNLIMITED & FREE",
                                color = VideoAmberAccent,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.ExtraBold
                            )
                        }

                        Surface(
                            color = VideoGreenAccent.copy(alpha = 0.2f),
                            shape = RoundedCornerShape(8.dp),
                            border = BorderStroke(1.dp, VideoGreenAccent)
                        ) {
                            Text(
                                text = "0 CREDITS REQUIRED",
                                color = VideoGreenAccent,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Generate Up to 30-Minute AI Videos",
                        color = Color.White,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Unlimited free voiceover, visual scene generation, and script outlines without any token limit or subscription.",
                        color = VideoTextSecondary,
                        fontSize = 12.sp,
                        lineHeight = 16.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Topic & Prompt Input
        Text(
            text = "Video Topic or Story Idea",
            color = Color.White,
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(6.dp))
        OutlinedTextField(
            value = prompt,
            onValueChange = { prompt = it },
            placeholder = { Text("Describe the story, documentary, or tutorial topic...", color = VideoTextMuted) },
            minLines = 3,
            maxLines = 5,
            modifier = Modifier
                .fillMaxWidth()
                .testTag("video_prompt_input"),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = VideoSurfaceDark,
                unfocusedContainerColor = VideoSurfaceDark,
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White,
                focusedIndicatorColor = VideoPurplePrimary,
                unfocusedIndicatorColor = VideoCardBorder
            ),
            shape = RoundedCornerShape(12.dp)
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Quick Pick Inspiration Chips
        Text(
            text = "Quick Inspiration Topics:",
            color = VideoTextSecondary,
            fontSize = 12.sp
        )
        Spacer(modifier = Modifier.height(6.dp))
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            presetIdeas.forEach { (presetPrompt, dur) ->
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = VideoSurfaceVariantDark,
                    border = BorderStroke(0.5.dp, VideoCardBorder),
                    modifier = Modifier.clickable {
                        prompt = presetPrompt
                        title = presetPrompt.take(28)
                        durationMinutes = dur.toFloat()
                    }
                ) {
                    Text(
                        text = "$presetPrompt (${dur}m)",
                        color = VideoCyanLight,
                        fontSize = 11.sp,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Target Duration Slider (Highlighting up to 30 Minutes!)
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = VideoSurfaceDark),
            border = BorderStroke(1.dp, VideoCardBorder)
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.HourglassTop,
                            contentDescription = null,
                            tint = VideoCyanSecondary,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Target Video Duration",
                            color = Color.White,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Surface(
                        color = VideoPurplePrimary.copy(alpha = 0.25f),
                        shape = RoundedCornerShape(6.dp),
                        border = BorderStroke(1.dp, VideoPurplePrimary)
                    ) {
                        Text(
                            text = "${durationMinutes.toInt()} MINUTES (Max 30m)",
                            color = VideoPurpleLight,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.ExtraBold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Slider(
                    value = durationMinutes,
                    onValueChange = { durationMinutes = it },
                    valueRange = 1f..30f,
                    steps = 29,
                    colors = SliderDefaults.colors(
                        thumbColor = VideoPurplePrimary,
                        activeTrackColor = VideoPurplePrimary,
                        inactiveTrackColor = Color(0xFF2E2648)
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("1 min (Short)", color = VideoTextMuted, fontSize = 11.sp)
                    Text("15 min", color = VideoTextMuted, fontSize = 11.sp)
                    Text("30 min (Full Feature)", color = VideoCyanLight, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Quick Duration Chips
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    durationPresets.forEach { mins ->
                        val isSel = durationMinutes.toInt() == mins
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = if (isSel) VideoPurplePrimary else VideoSurfaceVariantDark,
                            modifier = Modifier
                                .clickable { durationMinutes = mins.toFloat() }
                                .padding(2.dp)
                        ) {
                            Text(
                                text = "${mins}m",
                                color = if (isSel) Color.White else VideoTextSecondary,
                                fontSize = 11.sp,
                                fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Aspect Ratio Selector
        Text(
            text = "Aspect Ratio",
            color = Color.White,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(6.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            aspectRatios.forEach { (ratio, label) ->
                val isSelected = selectedAspectRatio == ratio
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .clickable { selectedAspectRatio = ratio },
                    shape = RoundedCornerShape(10.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isSelected) VideoPurpleDark.copy(alpha = 0.6f) else VideoSurfaceDark
                    ),
                    border = BorderStroke(
                        1.dp,
                        if (isSelected) VideoPurplePrimary else VideoCardBorder
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(10.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = ratio,
                            color = if (isSelected) Color.White else VideoTextSecondary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        )
                        Text(
                            text = label,
                            color = VideoTextMuted,
                            fontSize = 10.sp,
                            maxLines = 1
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Visual Style Selector
        Text(
            text = "Visual Art Style (Unlimited AI Images)",
            color = Color.White,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(6.dp))
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            styles.forEach { style ->
                FilterChip(
                    selected = selectedStyle == style,
                    onClick = { selectedStyle = style },
                    label = { Text(style, fontSize = 12.sp) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = VideoPurplePrimary,
                        selectedLabelColor = Color.White,
                        containerColor = VideoSurfaceDark,
                        labelColor = VideoTextSecondary
                    ),
                    border = BorderStroke(
                        0.5.dp,
                        if (selectedStyle == style) VideoPurplePrimary else VideoCardBorder
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Free Voice Persona Selector
        Text(
            text = "Free Voiceover Persona (On-Device TTS - 0 Credits)",
            color = Color.White,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(6.dp))
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            voices.forEach { voice ->
                FilterChip(
                    selected = selectedVoice == voice,
                    onClick = { selectedVoice = voice },
                    label = { Text(voice, fontSize = 12.sp) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = VideoCyanDark,
                        selectedLabelColor = Color.White,
                        containerColor = VideoSurfaceDark,
                        labelColor = VideoTextSecondary
                    ),
                    border = BorderStroke(
                        0.5.dp,
                        if (selectedVoice == voice) VideoCyanSecondary else VideoCardBorder
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Language Selector
        Text(
            text = "Language / Script",
            color = Color.White,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(6.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            languages.forEach { lang ->
                FilterChip(
                    selected = selectedLanguage == lang,
                    onClick = { selectedLanguage = lang },
                    label = { Text(lang, fontSize = 11.sp) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = VideoPurpleDark,
                        selectedLabelColor = Color.White,
                        containerColor = VideoSurfaceDark,
                        labelColor = VideoTextSecondary
                    ),
                    border = BorderStroke(
                        0.5.dp,
                        if (selectedLanguage == lang) VideoPurplePrimary else VideoCardBorder
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Generation In-Progress Indicator
        if (generatorState.isGenerating) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = VideoSurfaceDark),
                border = BorderStroke(1.dp, VideoCyanSecondary)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    CircularProgressIndicator(
                        color = VideoCyanSecondary,
                        modifier = Modifier.size(36.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = generatorState.statusMessage.ifBlank { "Compiling ${durationMinutes.toInt()}-Minute Video..." },
                        color = Color.White,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    LinearProgressIndicator(
                        progress = { generatorState.progress / 100f },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .clip(RoundedCornerShape(3.dp)),
                        color = VideoPurplePrimary,
                        trackColor = Color(0xFF231C38)
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "${generatorState.progress}% Completed • Free Zero-Credit Generation",
                        color = VideoCyanLight,
                        fontSize = 11.sp
                    )
                }
            }
            Spacer(modifier = Modifier.height(14.dp))
        }

        // Error message if any
        if (generatorState.errorMessage != null) {
            Text(
                text = generatorState.errorMessage,
                color = MaterialTheme.colorScheme.error,
                fontSize = 12.sp,
                modifier = Modifier.padding(vertical = 6.dp)
            )
        }

        // Big Main Generate Button
        Button(
            onClick = {
                onGenerate(
                    title.ifBlank { prompt.take(24) },
                    prompt,
                    durationMinutes.toInt(),
                    selectedAspectRatio,
                    selectedStyle,
                    selectedVoice,
                    selectedLanguage,
                    selectedMusic
                )
            },
            enabled = !generatorState.isGenerating,
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = VideoPurplePrimary,
                disabledContainerColor = Color(0xFF382956)
            ),
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp)
                .testTag("generate_full_video_btn")
        ) {
            Icon(
                imageVector = Icons.Default.AutoAwesome,
                contentDescription = null,
                tint = Color.White
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Generate ${durationMinutes.toInt()}-Min AI Video (Free)",
                fontSize = 16.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color.White
            )
        }

        Spacer(modifier = Modifier.height(30.dp))
    }
}
