package uoc.quizz.common

import java.util.*

data class Question(
    val title: String,
    val answers: List<Answer>,
    val rightAnswerId: Int,
    val imageUrl: String,
    val key: String = UUID.randomUUID().toString(),
)