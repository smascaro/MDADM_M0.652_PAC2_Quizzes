package uoc.quizz.ui.result

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_result.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import uoc.quizz.R
import uoc.quizz.data.questions.QuizzProgressManager
import uoc.quizz.data.repository.entity.QUESTION_SERIALIZE_EXTRA
import uoc.quizz.data.repository.entity.QUIZ_SERIALIZE_EXTRA
import uoc.quizz.data.repository.entity.Question
import uoc.quizz.data.repository.entity.Quiz
import uoc.quizz.ui.question.QuestionActivity
import java.io.Serializable

// region Region: Constants
const val RESULT_INTENT_ALREADY_HANDLED = "RESULT_INTENT_ALREADY_HANDLED"
const val RESULT_ANSWER_STATE = "RESULT_ANSWER_STATE"

// endregion
class ResultActivity : AppCompatActivity() {
    private sealed class State : Serializable {
        object RightAnswer : State()
        object WrongAnswer : State()
        object FinishedQuizz : State()
    }

    private lateinit var currentQuestion: Question
    private lateinit var currentQuiz: Quiz
    private lateinit var state: State
    private val io = CoroutineScope(Dispatchers.IO)
    private val ui = CoroutineScope(Dispatchers.Main)

    override fun onCreate(savedInstanceState: Bundle?) {
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_result)
        if (savedInstanceState == null) {
            initialize()
        } else {
            handleSavedState(savedInstanceState)
        }
    }

    /**
     * Handle state when orientation changes
     */
    private fun handleSavedState(savedInstanceState: Bundle) = ui.launch {
        println("Saved instance state: $savedInstanceState")
        val intentHandled = savedInstanceState.getBoolean(RESULT_INTENT_ALREADY_HANDLED, false)
        val currentQuestionFromSavedState = savedInstanceState.getSerializable(
            QUESTION_SERIALIZE_EXTRA
        ) as Question?
        val currentQuizFromSavedState = savedInstanceState.getSerializable(
            QUIZ_SERIALIZE_EXTRA
        ) as Quiz?
        val answerStateFromSavedState = savedInstanceState.getSerializable(
            RESULT_ANSWER_STATE
        ) as State?
        if (intentHandled && currentQuestionFromSavedState != null && currentQuizFromSavedState != null && answerStateFromSavedState != null) {
            currentQuiz = currentQuizFromSavedState
            currentQuestion = currentQuestionFromSavedState
            state = answerStateFromSavedState
            initializeUi()
        } else {
            Toast.makeText(
                this@ResultActivity,
                R.string.activity_result_unexpected,
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun initialize() = io.launch {
        initializeCurrentQuiz()
        initializeCurrentQuestion()
        handleAnswer()
    }

    private suspend fun initializeCurrentQuiz() {
        currentQuiz = QuizzProgressManager.getCurrentQuiz()
    }

    private suspend fun initializeCurrentQuestion() {
        currentQuestion = QuizzProgressManager.getCurrentQuestion(currentQuiz)
    }

    private suspend fun handleAnswer() {
        val selectedAnswerId =
            intent.getIntExtra(QUESTION_RESULT_INTENT_EXTRA_SELECTED_ANSWER_ID, -1)
        if (selectedAnswerId != -1) {
            val selectedAnswer = currentQuestion.answers.first { it.id == selectedAnswerId }
            if (selectedAnswer.id == currentQuestion.rightAnswerId) {
                handleRightAnswer()
            } else {
                handleWrongAnswer()
            }
        } else {
            Toast.makeText(
                this,
                R.string.activity_result_unexpected,
                Toast.LENGTH_LONG
            ).show()
        }
    }

    private suspend fun handleRightAnswer() {
        QuizzProgressManager.registerAttempt(currentQuiz, currentQuestion)
        QuizzProgressManager.next(currentQuiz)
        val finishedQuizz = currentQuiz.hasQuizzFinished()
        if (finishedQuizz) {
            state = State.FinishedQuizz
        } else {
            state = State.RightAnswer
        }
        initializeUi()
        if (state is State.FinishedQuizz) {
            QuizzProgressManager.reset(currentQuiz)
        }
    }

    private suspend fun handleWrongAnswer() {
        state = State.WrongAnswer
        QuizzProgressManager.registerAttempt(currentQuiz, currentQuestion)
        initializeUi()
    }

    private fun initializeUi() = ui.launch {
        when (state) {
            is State.RightAnswer -> initializeViewsRightAnswer()
            is State.WrongAnswer -> initializeViewsWrongAnswer()
            is State.FinishedQuizz -> initializeViewsQuizzFinished()
        }
    }

    private suspend fun initializeViewsQuizzFinished() = withContext(io.coroutineContext) {
        val attempts = currentQuiz.getTotalAttempts()
        val totalQuestions = currentQuiz.questions.size
        val accuracy = (totalQuestions.toDouble() / attempts.toDouble()) * 100
        ui.launch {
            result_message.setText(R.string.activity_result_message_finished_quizz_text)
            result_image.setImageResource(R.drawable.ic_finished_quiz)
            result_attempts.text = resources.getString(
                R.string.activity_result_attempts_count_finished_quizz_text,
                attempts,
                accuracy
            )
            result_button_start_again.visibility = View.VISIBLE
            result_button_start_again.setOnClickListener { goToQuestion() }
        }
    }

    private suspend fun initializeViewsRightAnswer() = withContext(io.coroutineContext) {
        val attempts = QuizzProgressManager.getAttemptsCount(currentQuiz, currentQuestion)
        ui.launch {
            result_message.setText(R.string.activity_result_message_right_answer_text)
            result_image.setImageResource(R.drawable.ic_right_answer)
            result_attempts.text =
                resources.getQuantityString(
                    R.plurals.activity_result_attempts_count_right_answer_text,
                    attempts,
                    attempts
                )
            result_button_next.visibility = View.VISIBLE
            result_button_next.setOnClickListener { goToQuestion() }
        }
    }

    private suspend fun initializeViewsWrongAnswer() = withContext(io.coroutineContext) {
        val attempts = QuizzProgressManager.getAttemptsCount(currentQuiz, currentQuestion)
        ui.launch {
            result_message.setText(R.string.activity_result_message_wrong_answer_text)
            result_image.setImageResource(R.drawable.ic_wrong_answer)
            result_attempts.text =
                resources.getString(
                    R.string.activity_result_attempts_count_wrong_answer_text,
                    attempts
                )
            result_button_try_again.visibility = View.VISIBLE
            result_button_try_again.setOnClickListener { goToQuestion() }
        }
    }

    private fun goToQuestion() {
        val intent = Intent(this, QuestionActivity::class.java)
        startActivity(intent)
        finish()
        handleTransitions()
    }

    private fun handleTransitions() {
        when (state) {
            is State.RightAnswer -> overridePendingTransition(
                R.anim.slide_in_right,
                R.anim.slide_out_left
            )
            is State.WrongAnswer -> overridePendingTransition(
                R.anim.slide_in_left,
                R.anim.slide_out_right
            )
            is State.FinishedQuizz -> overridePendingTransition(
                android.R.anim.fade_in,
                android.R.anim.fade_out
            )
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putSerializable(QUESTION_SERIALIZE_EXTRA, currentQuestion)
        outState.putSerializable(QUIZ_SERIALIZE_EXTRA, currentQuiz)
        outState.putBoolean(RESULT_INTENT_ALREADY_HANDLED, true)
        outState.putSerializable(RESULT_ANSWER_STATE, state)
    }
}