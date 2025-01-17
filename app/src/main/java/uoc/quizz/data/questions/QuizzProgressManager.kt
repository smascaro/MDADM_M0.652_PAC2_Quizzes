package uoc.quizz.data.questions

import uoc.quizz.data.repository.QuestionRepository
import uoc.quizz.data.repository.QuizRepository
import uoc.quizz.data.repository.RepositoryProvider
import uoc.quizz.data.repository.entity.Question
import uoc.quizz.data.repository.entity.QuestionItem
import uoc.quizz.data.repository.entity.Quiz

// region Region: Constants
private const val QUIZZ_QUESTIONS_SHARED_PREFS_NAME = "QUIZZ_QUESTIONS_SHARED_PREFS"
private const val QUIZZ_QUESTIONS_SHARED_PREFS_QUESTION_INDEX =
    "QUIZZ_QUESTIONS_SHARED_PREFS_QUESTION_INDEX"
private const val QUIZZ_QUESTIONS_ATTEMPTS_SHARED_PREFS_NAME =
    "QUIZZ_QUESTIONS_ATTEMPTS_SHARED_PREFS_NAME"
// endregion
/**
 * Helper class that manages quizz progress state
 */
object QuizzProgressManager {
    private val quizzRepository: QuizRepository by lazy { RepositoryProvider.provideQuizRepository() }
    private val questionRepository: QuestionRepository by lazy { RepositoryProvider.provideQuestionsRepository() }

    /**
     * Return current question that must be answered from [quiz].
     */
    suspend fun getCurrentQuestion(quiz: Quiz): Question {
        val currentQuestionId = quiz.questions.first { !it.completed }.id
        return getQuestionById(currentQuestionId)
    }

    /**
     * Return current quiz that is being played or a new one if there is none.
     */
    suspend fun getCurrentQuiz(): Quiz {
        return getUnfinishedQuiz() ?: makeNewQuiz()
    }

    /**
     * Return the index of a [question] within a [quiz]
     */
    fun getQuestionIndex(quiz: Quiz, question: Question): Int {
        return quiz.questions.indexOfFirst { it.id == question.id }
    }

    /**
     * Retrieve the only quiz in database that is not marked as finished, null if there is none
     */
    private suspend fun getUnfinishedQuiz(): Quiz? {
        return quizzRepository.getUnfinished()
    }

    /**
     * Retrieve a Question object from its unique identifier
     */
    private suspend fun getQuestionById(questionId: Int): Question {
        return questionRepository.getById(questionId)
    }

    /**
     * Create and store a new Quiz object in database.
     * @return the new inserted [Quiz] object
     */
    private suspend fun makeNewQuiz(): Quiz {
        val questions = questionRepository.getAll()
        val quiz = Quiz(questions = questions.shuffled().map {
            QuestionItem(it.id, 0, false)
        })
        return quizzRepository.insert(quiz)
    }

    /**
     * Mark current question as completed
     */
    suspend fun next(quiz: Quiz) {
        quiz.questions.first { !it.completed }.completed = true
        quizzRepository.update(quiz)
    }

    /**
     * Register an attempt (either a success or failure) for the [question] in [currentQuiz]
     */
    suspend fun registerAttempt(currentQuiz: Quiz, question: Question) {
        currentQuiz.questions.first { it.id == question.id }.attemptsCount++
        quizzRepository.update(currentQuiz)
    }

    /**
     * Returns total count of attempts for the [question] in [currentQuiz]
     */
    fun getAttemptsCount(
        currentQuiz: Quiz,
        question: Question
    ): Int {
        return currentQuiz.questions.first { it.id == question.id }.attemptsCount
    }

    /**
     * Restore state of the quizz to finished state.
     */
    suspend fun reset(currentQuiz: Quiz) {
        currentQuiz.finished = true
        quizzRepository.update(currentQuiz)
    }
}