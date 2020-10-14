package uoc.quizz.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import uoc.quizz.data.dao.QuestionsDao
import uoc.quizz.data.dao.QuizzesDao
import uoc.quizz.data.entity.AnswersConverter
import uoc.quizz.data.entity.Question
import uoc.quizz.data.entity.QuestionItemConverter
import uoc.quizz.data.entity.Quiz

@Database(entities = arrayOf(Question::class, Quiz::class), version = 2)
@TypeConverters(AnswersConverter::class, QuestionItemConverter::class)
abstract class QuestionsDatabase : RoomDatabase() {
    abstract fun questionsDao(): QuestionsDao
    abstract fun quizzesDao(): QuizzesDao
}