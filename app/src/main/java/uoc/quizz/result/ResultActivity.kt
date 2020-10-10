package uoc.quizz.result

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_result.*
import uoc.quizz.QuizzApplication
import uoc.quizz.R
import uoc.quizz.common.Question
import uoc.quizz.common.QuizzProgressManager
import uoc.quizz.common.QuizzQuestions
import uoc.quizz.question.QuestionActivity

class ResultActivity : AppCompatActivity() {
    private enum class ResultState {
        UNKOWN, RIGHT_ANSWER, WRONG_ANSWER, FINISHED_QUIZZ
    }

    private sealed class State {
        class RightAnswer : State()
        class WrongAnswer : State()
        class FinishedQuizz : State()
    }

    private lateinit var progressManager: QuizzProgressManager
    private lateinit var currentQuestion: Question
    private lateinit var state: State

    override fun onCreate(savedInstanceState: Bundle?) {
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_result)
        progressManager = (application as QuizzApplication).progressManager
        initializeCurrentQuestion()
        handleAnswer()
    }

    private fun initializeCurrentQuestion() {
        val currentQuestionIndex = progressManager.getCurrentQuestionIndex()
        currentQuestion = QuizzQuestions.questions[currentQuestionIndex]
    }

    private fun handleAnswer() {
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

    private fun handleRightAnswer() {
        progressManager.registerAttempt(currentQuestion)
        val nextQuestionIndex = progressManager.next()
        if (nextQuestionIndex >= QuizzQuestions.questions.size) {
            state = State.FinishedQuizz()
        } else {
            state = State.RightAnswer()
        }
        initializeUi()
        if (state is State.FinishedQuizz) {
            progressManager.reset()
        }
    }

    private fun handleWrongAnswer() {
        state = State.WrongAnswer()
        progressManager.registerAttempt(currentQuestion)
        initializeUi()
    }

    private fun initializeUi() {
        when (state) {
            is State.RightAnswer -> initializeViewsRightAnswer()
            is State.WrongAnswer -> initializeViewsWrongAnswer()
            is State.FinishedQuizz -> initializeViewsQuizzFinished()
        }
    }

    private fun initializeViewsQuizzFinished() {
        result_message.setText(R.string.activity_result_message_finished_quizz_text)
        result_image.setImageResource(R.drawable.ic_finished_quiz)
        val attempts = progressManager.getTotalAttempts()
        val accuracy = (QuizzQuestions.questions.size.toDouble() / attempts.toDouble()) * 100
        result_attempts.text = resources.getString(
            R.string.activity_result_attempts_count_finished_quizz_text,
            attempts,
            accuracy
        )
        result_button_start_again.visibility = View.VISIBLE
        result_button_start_again.setOnClickListener { goToQuestion() }
    }

    private fun initializeViewsRightAnswer() {
        result_message.setText(R.string.activity_result_message_right_answer_text)
        result_image.setImageResource(R.drawable.ic_right_answer)
        val attempts = progressManager.getAttemptsCount(currentQuestion)
        result_attempts.text =
            resources.getQuantityString(
                R.plurals.activity_result_attempts_count_right_answer_text,
                attempts,
                attempts
            )
        result_button_next.visibility = View.VISIBLE
        result_button_next.setOnClickListener { goToQuestion() }
    }

    private fun initializeViewsWrongAnswer() {
        result_message.setText(R.string.activity_result_message_wrong_answer_text)
        result_image.setImageResource(R.drawable.ic_wrong_answer)
        val attempts = progressManager.getAttemptsCount(currentQuestion)
        result_attempts.text =
            resources.getString(R.string.activity_result_attempts_count_wrong_answer_text, attempts)
        result_button_try_again.visibility = View.VISIBLE
        result_button_try_again.setOnClickListener { goToQuestion() }
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