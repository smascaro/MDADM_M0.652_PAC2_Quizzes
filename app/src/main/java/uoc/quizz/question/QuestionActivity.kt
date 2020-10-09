package uoc.quizz.question

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.RadioButton
import android.widget.RadioGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.activity_question.*
import uoc.quizz.QuizzApplication
import uoc.quizz.R
import uoc.quizz.common.Answer
import uoc.quizz.common.Question
import uoc.quizz.common.QuizzProgressManager
import uoc.quizz.common.QuizzQuestions
import uoc.quizz.result.QUESTION_RESULT_INTENT_EXTRA_SELECTED_ANSWER_ID
import uoc.quizz.result.ResultActivity

class QuestionActivity : AppCompatActivity() {
    private lateinit var progressManager: QuizzProgressManager
    private var currentQuestion: Question? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_question)
        progressManager = (application as QuizzApplication).progressManager

        val currentQuestionId = progressManager.getCurrentQuestionIndex()
        currentQuestion = QuizzQuestions.questions[currentQuestionId]
        initializeQuestionImage()
        initializeQuestionAnswers()
        initializeSendButton()
    }


    private fun initializeQuestionImage() {
        val questionImageView = findViewById<ImageView>(R.id.image_question)
        Glide
            .with(this)
            .load(currentQuestion?.imageUrl)
            .into(questionImageView)
    }

    private fun initializeQuestionAnswers() {
        val questionsRadioGroup = findViewById<RadioGroup>(R.id.group_questions)
        currentQuestion?.answers?.forEach {
            val answerButton = makeAnswerRadioButton(it)
            questionsRadioGroup.addView(answerButton)
        }
    }

    private fun makeAnswerRadioButton(answer: Answer): RadioButton {
        val params = RadioGroup.LayoutParams(
            RadioGroup.LayoutParams.WRAP_CONTENT,
            RadioGroup.LayoutParams.WRAP_CONTENT
        )
        return RadioButton(this).apply {
            text = answer.text
            layoutParams = params
            id = answer.id
        }
    }

    private fun initializeSendButton() {
        val sendBtn = findViewById<Button>(R.id.button_send_question)
        sendBtn.setOnClickListener {
            val resultScreenIntent = Intent(this, ResultActivity::class.java)
            resultScreenIntent.putExtra(QUESTION_RESULT_INTENT_EXTRA_SELECTED_ANSWER_ID, group_questions.checkedRadioButtonId)
            startActivity(resultScreenIntent)
        }
    }
}