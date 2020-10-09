package uoc.quizz

import android.app.Application
import uoc.quizz.common.QuizzProgressManager

class QuizzApplication : Application() {
    lateinit var progressManager: QuizzProgressManager
    override fun onCreate() {
        super.onCreate()
        progressManager = QuizzProgressManager(this)
    }
}