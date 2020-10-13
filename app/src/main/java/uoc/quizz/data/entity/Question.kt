package uoc.quizz.data.entity

import androidx.room.*
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

@Entity(tableName = "questions")
@TypeConverters(AnswersConverter::class)
data class Question(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Int = 0,
    @ColumnInfo(name = "title_default")
    val titleDefault: String,
    @ColumnInfo(name = "title_ca")
    val titleCa: String,
    @ColumnInfo(name = "title_en")
    val titleEn: String,
    @ColumnInfo(name = "answers")
    val answers: List<Answer>,
    @ColumnInfo(name = "right_answer_id")
    val rightAnswerId: Int,
    @ColumnInfo(name = "image_url")
    val imageUrl: String
) {
    fun getLocalizedTitle(iso6391Language: String): String {
        return when (iso6391Language) {
            "ca" -> titleCa
            "en" -> titleEn
            else -> titleDefault
        }
    }
}

class AnswersConverter {
    var gson = Gson()

    @TypeConverter
    fun fromJson(data: String): List<Answer> {
        val answerListType = object : TypeToken<List<Answer>>() {}.type
        return gson.fromJson(data, answerListType)
    }

    @TypeConverter
    fun toJson(answers: List<Answer>): String {
        return gson.toJson(answers)
    }
}