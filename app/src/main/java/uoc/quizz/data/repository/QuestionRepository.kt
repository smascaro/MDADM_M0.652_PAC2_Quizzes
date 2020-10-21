package uoc.quizz.data.repository

import uoc.quizz.data.repository.dao.QuestionsDao
import uoc.quizz.data.repository.entity.Question

class QuestionRepository(private val dao: QuestionsDao) {
    suspend fun getAll(): List<Question> {
        return dao.getAll()
    }

    suspend fun insert(question: Question): Long {
        return dao.insert(question)
    }

    suspend fun getById(questionId: Int): Question {
        return dao.getById(questionId)
    }
}