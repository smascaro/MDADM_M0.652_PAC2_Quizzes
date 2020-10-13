package uoc.quizz.repository

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import uoc.quizz.common.QuizzQuestions
import uoc.quizz.repository.entity.Answer
import uoc.quizz.repository.entity.Question

object DatabaseInstanceProvider {
    private var instance: QuestionsDatabase? = null
    val databaseCreationCallback = object : RoomDatabase.Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            prepopulateData()
        }
    }

    fun getInstance(context: Context): QuestionsDatabase {
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

    private fun forceDatabaseCallbacks(){
        GlobalScope.launch { instance?.runInTransaction {  } }
    }

    fun prepopulateData() {
        GlobalScope.launch {
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
}