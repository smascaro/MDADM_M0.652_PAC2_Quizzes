package uoc.quizz.result

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_result.*
import uoc.quizz.QuizzApplication
import uoc.quizz.R
import uoc.quizz.common.Question
import uoc.quizz.common.QuizzProgressManager
import uoc.quizz.common.QuizzQuestions

class ResultActivity : AppCompatActivity() {
    private lateinit var progressManager: QuizzProgressManager
    private var currentQuestion: Question? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_result)
        progressManager = (application as QuizzApplication).progressManager
        val currentQuestionIndex = progressManager.getCurrentQuestionIndex()
        currentQuestion = QuizzQuestions.questions[currentQuestionIndex]
        val selectedAnswerId =
            intent.getIntExtra(QUESTION_RESULT_INTENT_EXTRA_SELECTED_ANSWER_ID, -1)
        if (selectedAnswerId != -1) {
            val selectedAnswer = currentQuestion?.answers?.first { it.id == selectedAnswerId }
            if (selectedAnswer?.id == currentQuestion?.rightAnswerId) {
                result_message.text = "RIGHT!!"
                result_image.setImageResource(R.drawable.ic_right_answer)
            }else{
                result_message.text = "WRONG!!"
                result_image.setImageResource(R.drawable.ic_wrong_answer)
            }

        }
    }
}