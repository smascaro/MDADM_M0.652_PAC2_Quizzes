package uoc.quizz.common

data class Question(
    val localizedTitles: Map<String, String>,
    val answers: List<Answer>,
    val rightAnswerId: Int,
    val imageUrl: String,
) {
    val key = this.hashCode().toString()
    private val defaultLanguage = "es"
    fun getLocalizedTitle(iso6391Language: String): String {
        return if (localizedTitles.keys.any { it == iso6391Language }) {
            localizedTitles[iso6391Language]
        } else {
            localizedTitles[defaultLanguage]
        } ?: "ERROR fetching question title"
    }
}