package uoc.quizz.data.repository.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import uoc.quizz.data.repository.dao.QuestionsDao
import uoc.quizz.data.repository.dao.QuizzesDao
import uoc.quizz.data.repository.entity.AnswersConverter
import uoc.quizz.data.repository.entity.Question
import uoc.quizz.data.repository.entity.QuestionItemConverter
import uoc.quizz.data.repository.entity.Quiz

@Database(entities = arrayOf(Question::class, Quiz::class), version = 2)
@TypeConverters(AnswersConverter::class, QuestionItemConverter::class)
abstract class QuestionsDatabase : RoomDatabase() {
    abstract fun questionsDao(): QuestionsDao
    abstract fun quizzesDao(): QuizzesDao
}