package uoc.quizz.common

import android.content.Context
import android.content.SharedPreferences

private const val QUIZZ_QUESTIONS_SHARED_PREFS_NAME = "QUIZZ_QUESTIONS_SHARED_PREFS"
private const val QUIZZ_QUESTIONS_SHARED_PREFS_QUESTION_INDEX = "QUIZZ_QUESTIONS_SHARED_PREFS"

class QuizzProgressManager(private val context: Context) {
    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences(QUIZZ_QUESTIONS_SHARED_PREFS_NAME, Context.MODE_PRIVATE)

    fun getCurrentQuestionIndex(): Int {
        return sharedPreferences.getInt(QUIZZ_QUESTIONS_SHARED_PREFS_QUESTION_INDEX, 0)
    }

    fun next(): Int {
        val nextIndex = getCurrentQuestionIndex() + 1
        sharedPreferences.edit().putInt(QUIZZ_QUESTIONS_SHARED_PREFS_QUESTION_INDEX, nextIndex)
            .apply()
        return nextIndex
    }

    fun reset() {
        sharedPreferences.edit().putInt(QUIZZ_QUESTIONS_SHARED_PREFS_QUESTION_INDEX, 0).apply()
    }
}