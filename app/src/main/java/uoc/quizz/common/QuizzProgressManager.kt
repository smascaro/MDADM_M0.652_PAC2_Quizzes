package uoc.quizz.common

import android.content.Context
import android.content.SharedPreferences

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
class QuizzProgressManager(private val context: Context) {
    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences(QUIZZ_QUESTIONS_SHARED_PREFS_NAME, Context.MODE_PRIVATE)

    /**
     *  This separate SharedPreferences will let us keep track of attempts for each question.
     */
    private val attemptsSharedPreferences: SharedPreferences =
        context.getSharedPreferences(
            QUIZZ_QUESTIONS_ATTEMPTS_SHARED_PREFS_NAME,
            Context.MODE_PRIVATE
        )

    /**
     * Returns current question index
     */
    fun getCurrentQuestionIndex(): Int {
        return sharedPreferences.getInt(QUIZZ_QUESTIONS_SHARED_PREFS_QUESTION_INDEX, 0)
    }

    /**
     * Update current question index and returns it
     */
    fun next(): Int {
        val nextIndex = getCurrentQuestionIndex() + 1
        sharedPreferences.edit().putInt(QUIZZ_QUESTIONS_SHARED_PREFS_QUESTION_INDEX, nextIndex)
            .apply()
        return nextIndex
    }

    /**
     * Register an attempt (either a success or failure) for the question passed as argument.
     */
    fun registerAttempt(question: Question) {
        val currentAttemptsCount = attemptsSharedPreferences.getInt(question.key, 0)
        attemptsSharedPreferences.edit().putInt(question.key, currentAttemptsCount + 1).apply()
    }

    /**
     * Returns total count of attempts for the question passed as argument.
     */
    fun getAttemptsCount(question: Question): Int {
        return attemptsSharedPreferences.getInt(question.key, 0)
    }

    /**
     * Returns total count of attempts for all questions answered. That is the sum of all individual answers attempts.
     */
    fun getTotalAttempts(): Int {
        return attemptsSharedPreferences.all.values.filter { it != null && it.isInt() }
            .toListOf<Int>().reduce { acc, element ->
                acc + element
            }
    }

    /**
     * Restore state of the quizz to initial state.
     */
    fun reset() {
        sharedPreferences.edit().clear().apply()
        attemptsSharedPreferences.edit().clear().apply()
    }
}