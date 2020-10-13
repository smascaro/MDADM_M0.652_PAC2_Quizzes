package uoc.quizz.data.database

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import uoc.quizz.common.QuizzQuestions
import uoc.quizz.data.dao.QuestionsDao
import uoc.quizz.data.dao.QuizzesDao
import uoc.quizz.data.entity.Answer
import uoc.quizz.data.entity.Question

object DatabaseInstanceProvider {
    private var instance: QuestionsDatabase? = null
    private val databaseCreationCallback = object : RoomDatabase.Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            prepopulateData()
        }
    }
    val io = CoroutineScope(Dispatchers.IO)
    fun create(context: Context): QuestionsDatabase {
        if (instance == null) {
            instance = Room.databaseBuilder(
                context,
                QuestionsDatabase::class.java,
                "questions-database"
            )
                .addCallback(databaseCreationCallback)
                .build()
            forceDatabaseCallbacks()
        }
        return instance!!
    }

    private fun forceDatabaseCallbacks() = io.launch {
        instance?.runInTransaction { }
    }

    fun getQuestionsDao(): QuestionsDao {
        ensureInstanceNotNull()
        return instance!!.questionsDao()
    }

    fun getQuizzesDao(): QuizzesDao {
        ensureInstanceNotNull()
        return instance!!.quizzesDao()
    }

    private fun ensureInstanceNotNull() {
        if (instance == null) {
            throw RuntimeException("Database instance has not yet been created")
        }
    }

    fun prepopulateData() = io.launch {
        val dao = instance?.questionsDao()
        if (dao != null) {
            QuizzQuestions.questions.forEach {
                val q = Question(
                    titleDefault = it.getLocalizedTitle("es"),
                    titleCa = it.getLocalizedTitle("ca"),
                    titleEn = it.getLocalizedTitle("en"),
                    answers = it.answers.map { answer -> Answer(answer.id, answer.text) },
                    imageUrl = it.imageUrl,
                    rightAnswerId = it.rightAnswerId
                )
                val questionId = dao.insert(q)
                println("Question inserted with id $questionId")
            }
        }
    }
}