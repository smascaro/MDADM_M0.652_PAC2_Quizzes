package uoc.quizz.question

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import kotlinx.android.synthetic.main.activity_question.*
import kotlinx.android.synthetic.main.layout_toolbar.*
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
    private lateinit var currentQuestion: Question
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_question)

        this.setSupportActionBar(toolbar)
        progressManager = (application as QuizzApplication).progressManager

        val currentQuestionId = progressManager.getCurrentQuestionIndex()
        currentQuestion = QuizzQuestions.questions[currentQuestionId]
        initializeQuestionProgress()
        initializeQuestionTitle()
        initializeQuestionImage()
        initializeQuestionAnswers()
        initializeSendButton()
    }

    private fun initializeQuestionProgress() {
        val questionIndex = progressManager.getCurrentQuestionIndex()
        progress_questions.text =
            resources.getString(
                R.string.activity_question_progress_template,
                questionIndex + 1,
                QuizzQuestions.questions.size
            )
    }

    private fun initializeQuestionTitle() {
        title_question.text = currentQuestion.title
    }


    private fun initializeQuestionImage() {
        val questionImageView = findViewById<ImageView>(R.id.image_question)
        Glide
            .with(this)
            .load(currentQuestion.imageUrl)
            .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
            .into(questionImageView)
    }

    private fun initializeQuestionAnswers() {
        val questionsRadioGroup = findViewById<RadioGroup>(R.id.group_questions)
        currentQuestion.answers.shuffled().forEach {
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
            if (group_questions.checkedRadioButtonId == -1) {
                Toast.makeText(
                    this,
                    R.string.activity_question_no_answer_selected,
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                val resultScreenIntent = Intent(this, ResultActivity::class.java)
                resultScreenIntent.putExtra(
                    QUESTION_RESULT_INTENT_EXTRA_SELECTED_ANSWER_ID,
                    group_questions.checkedRadioButtonId
                )
                startActivity(resultScreenIntent)
                finish()
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.options_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.option_restart_quiz -> promptRestart()
            else -> false
        }
    }

    private fun promptRestart(): Boolean {
        AlertDialog.Builder(this).apply {
            this.setTitle(R.string.activity_question_restart_dialog_title)
            setPositiveButton(
                R.string.activity_question_restart_dialog_positive
            ) { _, _ -> restartQuizz() }
            setNegativeButton(R.string.activity_question_restart_dialog_negative) { dialog, _ ->
                dialog.dismiss()
            }
            this.setMessage(R.string.activity_question_restart_dialog_message)
        }.create().show()
        return true
    }

    private fun restartQuizz() {
        (application as QuizzApplication).progressManager.reset()
        val intent = Intent(this, QuestionActivity::class.java)
        startActivity(intent)
        finish()
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
    }
}