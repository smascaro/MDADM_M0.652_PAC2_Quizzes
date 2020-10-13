package uoc.quizz.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import uoc.quizz.data.entity.Question

@Dao
interface QuestionsDao {
    @Query("SELECT * FROM questions")
    suspend fun getAll(): List<Question>

    @Query("SELECT * FROM questions where id = :questionId")
    suspend fun getById(questionId: Int): Question

    @Insert
    suspend fun insert(question: Question): Long
}