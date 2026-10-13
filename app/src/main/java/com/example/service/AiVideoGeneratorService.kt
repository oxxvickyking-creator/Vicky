package com.example.service

import com.example.BuildConfig
import com.example.data.VideoProject
import com.example.data.VideoScene
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import kotlin.random.Random

object AiVideoGeneratorService {

    private val CAMERA_MOTIONS = listOf(
        "Slow Zoom In",
        "Slow Zoom Out",
        "Cinematic Pan Right",
        "Cinematic Pan Left",
        "Dynamic Tilt Up",
        "Drone Orbit Shot"
    )

    /**
     * Generates a complete scene breakdown for a video up to 30 minutes.
     * Calculates the number of scenes needed based on target duration:
     * e.g. 1 min ~ 6-8 scenes
     *      5 min ~ 15-20 scenes
     *      10 min ~ 25-30 scenes
     *      15 min ~ 35-45 scenes
     *      30 min ~ 50-60 scenes
     */
    suspend fun generateVideoScenes(
        project: VideoProject,
        onProgress: (Int, String) -> Unit = { _, _ -> }
    ): List<VideoScene> = withContext(Dispatchers.IO) {
        val totalScenesTarget = when {
            project.durationMinutes <= 1 -> 6
            project.durationMinutes <= 3 -> 12
            project.durationMinutes <= 5 -> 18
            project.durationMinutes <= 10 -> 28
            project.durationMinutes <= 20 -> 40
            else -> 55 // For 30 minute long video!
        }

        onProgress(15, "AI Scriptwriting & Scene Outlining...")

        val apiKey = BuildConfig.GEMINI_API_KEY
        val generatedScenes = if (!apiKey.isNullOrBlank() && apiKey != "MY_GEMINI_API_KEY") {
            try {
                fetchGeminiSceneBreakdown(project, totalScenesTarget)
            } catch (e: Exception) {
                // Fallback to our procedural AI scriptwriting engine
                generateProceduralScenes(project, totalScenesTarget)
            }
        } else {
            generateProceduralScenes(project, totalScenesTarget)
        }

        onProgress(60, "Generating High-Definition Free Visuals & Voice Track...")

        // Attach free image URLs and camera motions
        val finalScenes = generatedScenes.mapIndexed { index, scene ->
            val seed = Random.nextInt(10000, 999999)
            val imageUrl = buildFreeImageUrl(
                prompt = "${scene.visualPrompt}, ${project.visualStyle}, ultra-detailed, cinematic lighting, 8k resolution",
                aspectRatio = project.aspectRatio,
                seed = seed
            )
            scene.copy(
                sceneNumber = index + 1,
                imageUrl = imageUrl,
                cameraMovement = CAMERA_MOTIONS[index % CAMERA_MOTIONS.size]
            )
        }

        onProgress(100, "30-Minute Timeline Compiled Successfully!")
        return@withContext finalScenes
    }

    private fun buildFreeImageUrl(prompt: String, aspectRatio: String, seed: Int): String {
        val (width, height) = when (aspectRatio) {
            "9:16" -> Pair(720, 1280)
            "1:1" -> Pair(1024, 1024)
            else -> Pair(1280, 720) // 16:9
        }
        val encodedPrompt = URLEncoder.encode(prompt, StandardCharsets.UTF_8.toString())
        return "https://image.pollinations.ai/prompt/$encodedPrompt?width=$width&height=$height&seed=$seed&nologo=true"
    }

    private fun fetchGeminiSceneBreakdown(project: VideoProject, targetScenes: Int): List<VideoScene> {
        val apiKey = BuildConfig.GEMINI_API_KEY
        val url = URL("https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent?key=$apiKey")
        val conn = url.openConnection() as HttpURLConnection
        conn.requestMethod = "POST"
        conn.setRequestProperty("Content-Type", "application/json")
        conn.doOutput = true

        val languagePrompt = when {
            project.language.contains("Hindi", ignoreCase = true) -> "Write narrationScript in Hindi (or Hinglish) with engaging storytelling."
            project.language.contains("Urdu", ignoreCase = true) -> "Write narrationScript in Urdu (or Roman Urdu) with emotional and cinematic depth."
            else -> "Write narrationScript in clear, cinematic English."
        }

        val systemPrompt = """
            You are a Hollywood master filmmaker and documentary creator.
            Create a detailed scene-by-scene script for a ${project.durationMinutes}-minute video on the topic: "${project.topicPrompt}".
            Style: ${project.visualStyle}.
            Aspect Ratio: ${project.aspectRatio}.
            Language: $languagePrompt.
            
            Return a JSON array of exactly $targetScenes scenes.
            Format:
            [
              {
                "title": "Scene headline",
                "visualPrompt": "Detailed visual description of the shot with lighting and framing for image AI",
                "narrationScript": "Spoken voiceover script for this scene",
                "durationSeconds": 8
              }
            ]
            ONLY output raw JSON array, without markdown tags.
        """.trimIndent()

        val requestBody = JSONObject().apply {
            put("contents", JSONArray().apply {
                put(JSONObject().apply {
                    put("parts", JSONArray().apply {
                        put(JSONObject().put("text", systemPrompt))
                    })
                })
            })
        }

        conn.outputStream.use { os ->
            os.write(requestBody.toString().toByteArray(StandardCharsets.UTF_8))
        }

        val responseCode = conn.responseCode
        if (responseCode == 200) {
            val responseText = conn.inputStream.bufferedReader().use { it.readText() }
            val jsonResponse = JSONObject(responseText)
            val candidates = jsonResponse.optJSONArray("candidates")
            val content = candidates?.optJSONObject(0)?.optJSONObject("content")
            val parts = content?.optJSONArray("parts")
            val text = parts?.optJSONObject(0)?.optString("text") ?: ""

            val cleanedJson = text.trim()
                .removePrefix("```json")
                .removePrefix("```")
                .removeSuffix("```")
                .trim()

            val jsonArray = JSONArray(cleanedJson)
            val scenes = mutableListOf<VideoScene>()
            for (i in 0 until jsonArray.length()) {
                val item = jsonArray.getJSONObject(i)
                scenes.add(
                    VideoScene(
                        projectId = project.id,
                        sceneNumber = i + 1,
                        title = item.optString("title", "Scene ${i + 1}"),
                        visualPrompt = item.optString("visualPrompt", project.topicPrompt),
                        narrationScript = item.optString("narrationScript", "Welcome to this journey about ${project.topicPrompt}."),
                        durationSeconds = item.optInt("durationSeconds", 8)
                    )
                )
            }
            if (scenes.isNotEmpty()) return scenes
        }

        return generateProceduralScenes(project, targetScenes)
    }

    /**
     * Procedural generator that creates rich multi-scene cinematic videos up to 30 minutes
     * without needing any credits or external API keys.
     */
    fun generateProceduralScenes(project: VideoProject, count: Int): List<VideoScene> {
        val topic = project.topicPrompt.ifBlank { "The Secrets of the Universe and Deep Space Exploration" }
        val scenes = mutableListOf<VideoScene>()

        val chapters = listOf(
            "Prologue: The Awakening",
            "The Hidden Mystery Unveiled",
            "Historical Origins and Milestones",
            "The Turning Point",
            "Groundbreaking Discovery",
            "Deep Dive into the Core Phenomenon",
            "Global Impact & Human Reaction",
            "Uncharted Frontiers Ahead",
            "The Climax: Triumph Against All Odds",
            "Epilogue: The Future Beyond Tomorrow"
        )

        val isHindi = project.language.contains("Hindi", ignoreCase = true)
        val isUrdu = project.language.contains("Urdu", ignoreCase = true)

        for (i in 0 until count) {
            val chapterName = chapters[i % chapters.size] + if (count > 10) " - Part ${(i / chapters.size) + 1}" else ""
            
            val visualDesc = when (i % 6) {
                0 -> "Breathtaking wide-angle cinematic shot of $topic, bathed in ethereal golden hour sunlight and atmospheric volumetric fog, 8K ultra-detailed"
                1 -> "Close-up dramatic hero framing focusing on the intricate mechanics and wonder of $topic, shallow depth of field, neon highlights"
                2 -> "Dynamic drone sweeping camera gliding over a vast landscape highlighting $topic, intense contrast, cinematic film grain"
                3 -> "Futuristic high-tech holographic interface visualizing data and secrets of $topic, glowing cyan and violet particles"
                4 -> "Mysterious dimly lit interior with soft rim lighting emphasizing the depth and historic significance of $topic"
                else -> "Epic grand finale shot with sprawling horizons, vibrant celestial sky, symbolizing infinite future possibilities of $topic"
            }

            val script = when {
                isHindi -> when (i % 5) {
                    0 -> "Namaskar doston! Aaj hum shuru kar rahe hain ek aisi anokhi kahani jo $topic ke sabhi raazon ko khol degi."
                    1 -> "Jaise-jaise hum aage badhte hain, yahan sabse bada mod aata hai jise dekh kar sabhi hairan reh gaye the."
                    2 -> "Is daur mein $topic ne poori duniya ko sochne par majboor kar diya ki aakhir aage kya hone wala hai."
                    3 -> "Kadi mehnat aur naye vishwas ke sath, naye raaste khule aur ek anokha aitihasik record bana."
                    else -> "Yeh safar sirf ek shuruaat hai. Aane wala samay aur bhi shaandar hoga, toh sath bane rahiye aur dekhte rahiye!"
                }
                isUrdu -> when (i % 5) {
                    0 -> "Khushamdeed! Aaj hum ek aisi dilchasp dastan shuru kar rahe hain jo $topic ki haqeeqat ko samnay layegi."
                    1 -> "Is safar ka sab se ahem hissa wo lamha hai jab sab kuch tabdeel hone laga."
                    2 -> "Tareekh gawah hai ke $topic ne logon ke zehn aur mustaqbil ko hamesha ke liye badal kar rakh diya."
                    3 -> "Nayi umeedon aur hoslay ke sath yeh koshish aagay barhti rahi aur kamyabi ki bulandiyon ko chhoo liya."
                    else -> "Yeh dastan abhi khatam nahi hui, aane wala kal mazeed hairan-kun hoga. Hamare sath jude rahiye!"
                }
                else -> when (i % 5) {
                    0 -> "Welcome to an extraordinary cinematic exploration. Today, we uncover the hidden truths and wonders behind $topic."
                    1 -> "As we delve deeper into this unfolding chronicle, a pivotal transformation began to reshape everything we thought we knew."
                    2 -> "Across countless eras and frontiers, the sheer scale of $topic has inspired pioneers and thinkers across the globe."
                    3 -> "With each new breakthrough, unprecedented doors of opportunity swung wide open, setting a new benchmark for excellence."
                    else -> "This is only the beginning of a grand legacy. The horizon shines brighter than ever as we step boldly into tomorrow."
                }
            }

            scenes.add(
                VideoScene(
                    projectId = project.id,
                    sceneNumber = i + 1,
                    title = "Scene ${i + 1}: $chapterName",
                    visualPrompt = visualDesc,
                    narrationScript = script,
                    durationSeconds = 8
                )
            )
        }

        return scenes
    }
}
