package uoc.quizz.common

data class Question(val title:String, val answers: List<Answer>, val rightAnswerId:Int, val imageUrl:String)