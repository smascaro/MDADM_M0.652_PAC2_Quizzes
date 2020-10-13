package uoc.quizz

import android.app.Application
import uoc.quizz.common.QuizzProgressManager
import uoc.quizz.repository.DatabaseInstanceProvider

class QuizzApplication : Application() {
    lateinit var progressManager: QuizzProgressManager
    override fun onCreate() {
        super.onCreate()
        progressManager = QuizzProgressManager(this)
        DatabaseInstanceProvider.getInstance(applicationContext)
    }
}