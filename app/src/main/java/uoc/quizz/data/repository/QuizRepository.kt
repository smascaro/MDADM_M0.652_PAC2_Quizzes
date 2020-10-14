package uoc.quizz.data.repository

import uoc.quizz.data.dao.QuizzesDao
import uoc.quizz.data.entity.Quiz

class QuizRepository(private val dao: QuizzesDao) {
    suspend fun getAll(): List<Quiz> {
        return dao.getAll()
    }

    suspend fun getUnfinished(): Quiz? {
        return dao.getUnfinished()
    }

    suspend fun insert(quiz: Quiz): Quiz {
        val generatedId = dao.insert(quiz)
        return quiz.apply {
            id = generatedId.toInt()
        }
    }

    suspend fun update(quiz: Quiz) {
        dao.update(quiz)
    }
}