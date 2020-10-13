package uoc.quizz.data.entity

import androidx.room.*
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import com.google.gson.reflect.TypeToken

@Entity(tableName = "quizzes")
@TypeConverters(QuestionItemConverter::class)
data class Quiz(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "quiz_id")
    var id: Int = 0,
    @ColumnInfo(name = "questions")
    val questions: List<QuestionItem>,
    @ColumnInfo(name = "finished")
    var finished: Boolean = false
) {
    fun hasQuizzFinished(): Boolean {
        return questions.all { it.completed }
    }

    fun getTotalAttempts(): Int {
        return questions.sumBy { it.attemptsCount }
    }
}

data class QuestionItem(
    @SerializedName("question_id")
    val id: Int,
    @SerializedName("attempts_count")
    var attemptsCount: Int,
    @SerializedName("completed")
    var completed: Boolean
)

class QuestionItemConverter {
    private val gson = Gson()

    @TypeConverter
    fun toString(questions: List<QuestionItem>): String {
        return gson.toJson(questions)
    }

    @TypeConverter
    fun fromJson(data: String): List<QuestionItem> {
        val type = object : TypeToken<List<QuestionItem>>() {}.type
        return gson.fromJson(data, type)
    }
}