package uoc.quizz

import android.app.Application
import uoc.quizz.data.database.DatabaseInstanceProvider

class QuizzApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        DatabaseInstanceProvider.create(applicationContext)
    }
}