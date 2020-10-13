package uoc.quizz.repository.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import uoc.quizz.repository.entity.Question

@Dao
interface QuestionsDao{
    @Query("SELECT * FROM questions")
    fun getAll():List<Question>

    @Query("SELECT * FROM questions where id = :questionId")
    fun getById(questionId:Int):Question

    @Insert
    suspend fun insert(question: Question):Long
}