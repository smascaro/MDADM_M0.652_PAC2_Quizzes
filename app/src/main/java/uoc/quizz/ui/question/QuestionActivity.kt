package uoc.quizz.ui.question

import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.RadioGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatRadioButton
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import kotlinx.android.synthetic.main.activity_question.*
import kotlinx.android.synthetic.main.layout_toolbar.toolbar
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import uoc.quizz.R
import uoc.quizz.data.questions.QuizzProgressManager
import uoc.quizz.data.questions.QuizzQuestions
import uoc.quizz.data.repository.QuestionRepository
import uoc.quizz.data.repository.QuizRepository
import uoc.quizz.data.repository.RepositoryProvider
import uoc.quizz.data.repository.entity.Quiz
import uoc.quizz.ui.result.ResultActivity

//region Region: Constants
const val QUESTION_RESULT_INTENT_EXTRA_SELECTED_ANSWER_ID =
    "QUESTION_RESULT_INTENT_EXTRA_SELECTED_ANSWER_ID"

//endregion
class QuestionActivity : AppCompatActivity() {
    private lateinit var currentQuestion: uoc.quizz.data.repository.entity.Question
    private lateinit var currentQuiz: Quiz
    private val io = CoroutineScope(Dispatchers.IO)
    private val ui = CoroutineScope(Dispatchers.Main)
    private var questionsRepository: QuestionRepository? = null
    private var quizRepository: QuizRepository? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_question)

        this.setSupportActionBar(toolbar)

        questionsRepository = RepositoryProvider.provideQuestionsRepository()
        quizRepository = RepositoryProvider.provideQuizRepository()

        initializeUi()
    }

    private fun initializeUi() = io.launch {
        initializeCurrentQuiz()
        initializeCurrentQuestion()
        ui.launch {
            initializeQuestionProgress()
            initializeQuestionTitle()
            initializeQuestionImage()
            initializeQuestionAnswers()
            initializeSendButton()
        }
    }

    private suspend fun initializeCurrentQuiz() {
        currentQuiz = QuizzProgressManager.getCurrentQuiz()
    }

    private suspend fun initializeCurrentQuestion() {
        currentQuestion = QuizzProgressManager.getCurrentQuestion(currentQuiz)
    }

    private fun initializeQuestionProgress() {
        val questionIndex = QuizzProgressManager.getQuestionIndex(currentQuiz, currentQuestion)
        progress_questions.text =
            resources.getString(
                R.string.activity_question_progress_template,
                questionIndex + 1,
                QuizzQuestions.questions.size
            )
        progress_seekbar.apply {
            max = QuizzQuestions.questions.size
            progress = questionIndex + 1
            setOnTouchListener { _, _ -> true }
            thumb = thumb.mutate().apply {
                alpha = 0
            }
        }
    }

    private fun initializeQuestionTitle() {
        title_question.text = currentQuestion.getLocalizedTitle(getDeviceLanguage())
    }

    private fun getDeviceLanguage(): String {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            resources.configuration.locales[0].language
        } else {
            resources.configuration.locale.language
        }
    }

    private fun initializeQuestionImage() {
        image_loading_progress_bar.visibility = View.VISIBLE
        Glide
            .with(this)
            .load(currentQuestion.imageUrl)
            .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
            .listener(object : RequestListener<Drawable> {
                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any?,
                    target: Target<Drawable>?,
                    isFirstResource: Boolean
                ): Boolean {
                    handleLoadingImageError()
                    return true
                }

                override fun onResourceReady(
                    resource: Drawable?,
                    model: Any?,
                    target: Target<Drawable>?,
                    dataSource: DataSource?,
                    isFirstResource: Boolean
                ): Boolean {
                    image_loading_progress_bar.visibility = View.GONE
                    return false
                }
            })
            .into(image_question)
    }

    private fun handleLoadingImageError() {
        image_loading_progress_bar.visibility = View.GONE
        image_question.setImageResource(R.drawable.ic_404_error)
        Toast.makeText(
            this@QuestionActivity,
            getString(R.string.question_activity_image_loading_error_message),
            Toast.LENGTH_SHORT
        ).show()
        image_question.setOnClickListener {
            it.setOnClickListener(null)
            initializeQuestionImage()
        }
    }

    private fun initializeQuestionAnswers() {
        val questionsRadioGroup = findViewById<RadioGroup>(R.id.group_questions)
        currentQuestion.answers.shuffled().forEach { answer ->
            questionsRadioGroup.addView(makeAnswerRadioButton(answer))
        }
    }

    private fun makeAnswerRadioButton(answer: uoc.quizz.data.repository.entity.Answer): AppCompatRadioButton {
        val params = RadioGroup.LayoutParams(
            RadioGroup.LayoutParams.WRAP_CONTENT,
            RadioGroup.LayoutParams.WRAP_CONTENT
        )
        return AppCompatRadioButton(this).apply {
            text = answer.text
            layoutParams = params
            id = answer.id
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                setTextAppearance(R.style.AppTextAppearance_Question_Answer)
            } else {
                setTextAppearance(this@QuestionActivity, R.style.AppTextAppearance_Question_Answer)
            }
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
        io.launch { QuizzProgressManager.reset(currentQuiz) }
        val intent = Intent(this, QuestionActivity::class.java)
        startActivity(intent)
        finish()
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
    }
}