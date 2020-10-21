package uoc.quizz.data.repository.database

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import uoc.quizz.data.questions.QuizzQuestions
import uoc.quizz.data.repository.dao.QuestionsDao
import uoc.quizz.data.repository.dao.QuizzesDao

/**
 * Provide database instance
 */
object DatabaseInstanceProvider {
    private var instance: QuestionsDatabase? = null
    private val io = CoroutineScope(Dispatchers.IO)
    private val databaseCreationCallback = object : RoomDatabase.Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            prepopulateData()
        }
    }

    /**
     * Initialize database instance that will later be provided to callers
     */
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

    /**
     * Force database empty transaction so creation callback is actually called
     */
    private fun forceDatabaseCallbacks() = io.launch {
        instance?.runInTransaction { }
    }

    /**
     * Provide [QuestionsDao] instance
     */
    fun getQuestionsDao(): QuestionsDao {
        ensureInstanceNotNull()
        return instance!!.questionsDao()
    }

    /**
     * Provide [QuizzesDao] instance
     */
    fun getQuizzesDao(): QuizzesDao {
        ensureInstanceNotNull()
        return instance!!.quizzesDao()
    }

    private fun ensureInstanceNotNull() {
        if (instance == null) {
            throw RuntimeException("Database instance has not yet been created")
        }
    }

    private fun prepopulateData() = io.launch {
        val dao = instance?.questionsDao()
        if (dao != null) {
            QuizzQuestions.questions.forEach {
                val questionId = dao.insert(it)
                println("Question inserted with id $questionId")
            }
        }
    }
}