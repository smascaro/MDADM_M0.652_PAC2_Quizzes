package uoc.quizz.data.repository

import uoc.quizz.data.repository.database.DatabaseInstanceProvider

/**
 * Provide repository instances
 */
object RepositoryProvider {
    private var questionRepository: QuestionRepository? = null
    private var quizRepository: QuizRepository? = null
    fun provideQuestionsRepository(): QuestionRepository {
        if (questionRepository == null) {
            val questionDao = DatabaseInstanceProvider.getQuestionsDao()
            questionRepository = QuestionRepository(questionDao)
        }
        return questionRepository!!
    }

    fun provideQuizRepository(): QuizRepository {
        if (quizRepository == null) {
            val quizzesDao = DatabaseInstanceProvider.getQuizzesDao()
            quizRepository = QuizRepository(quizzesDao)
        }
        return quizRepository!!
    }
}