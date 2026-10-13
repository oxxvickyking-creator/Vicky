package com.example.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface VideoProjectDao {
    @Query("SELECT * FROM video_projects ORDER BY createdAt DESC")
    fun getAllProjects(): Flow<List<VideoProject>>

    @Query("SELECT * FROM video_projects WHERE id = :projectId LIMIT 1")
    suspend fun getProjectById(projectId: Long): VideoProject?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProject(project: VideoProject): Long

    @Update
    suspend fun updateProject(project: VideoProject)

    @Delete
    suspend fun deleteProject(project: VideoProject)

    @Query("SELECT * FROM video_scenes WHERE projectId = :projectId ORDER BY sceneNumber ASC")
    fun getScenesForProject(projectId: Long): Flow<List<VideoScene>>

    @Query("SELECT * FROM video_scenes WHERE projectId = :projectId ORDER BY sceneNumber ASC")
    suspend fun getScenesForProjectList(projectId: Long): List<VideoScene>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertScenes(scenes: List<VideoScene>)

    @Update
    suspend fun updateScene(scene: VideoScene)

    @Query("DELETE FROM video_scenes WHERE projectId = :projectId")
    suspend fun deleteScenesForProject(projectId: Long)
}
