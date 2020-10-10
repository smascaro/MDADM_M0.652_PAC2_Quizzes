package uoc.quizz.common

/**
 * Returns either if the object is an Integer or not
 */
fun Any?.isInt(): Boolean {
    return this.toString().toIntOrNull() != null
}

/**
 * Returns current list casted as a List of Types
 */
inline fun <reified T> List<*>.toListOf(): List<T> {
    return if (all { it is T }) {
        @Suppress("UNCHECKED_CAST")
        this as List<T>
    } else {
        throw Exception("Not all elements are convertible to type")
    }
}