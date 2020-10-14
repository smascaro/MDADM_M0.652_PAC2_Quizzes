package uoc.quizz.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import uoc.quizz.data.entity.Quiz

@Dao
interface QuizzesDao {
    @Query("SELECT * FROM quizzes")
    suspend fun getAll(): List<Quiz>

    @Query("SELECT * FROM quizzes WHERE finished = 0")
    suspend fun getUnfinished(): Quiz?

    @Insert
    suspend fun insert(quiz: Quiz): Long

    @Update
    suspend fun update(quiz: Quiz)
}