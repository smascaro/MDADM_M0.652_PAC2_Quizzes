package uoc.quizz.result

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
import uoc.quizz.common.QuizzProgressManager
import uoc.quizz.data.entity.Quiz
import uoc.quizz.question.QuestionActivity

class ResultActivity : AppCompatActivity() {
    private sealed class State {
        object RightAnswer : State()
        object WrongAnswer : State()
        object FinishedQuizz : State()
    }

    private lateinit var currentQuestion: uoc.quizz.data.entity.Question
    private lateinit var currentQuiz: Quiz
    private lateinit var state: State
    private val io = CoroutineScope(Dispatchers.IO)
    private val ui = CoroutineScope(Dispatchers.Main)
    override fun onCreate(savedInstanceState: Bundle?) {
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_result)
        initialize()
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
}