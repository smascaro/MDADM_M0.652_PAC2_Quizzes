package uoc.quizz.repository

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import uoc.quizz.repository.dao.QuestionsDao
import uoc.quizz.repository.entity.AnswersConverter
import uoc.quizz.repository.entity.Question

@Database(entities = arrayOf(Question::class), version = 1)
@TypeConverters(AnswersConverter::class)
abstract class QuestionsDatabase : RoomDatabase() {
    abstract fun questionsDao(): QuestionsDao
}