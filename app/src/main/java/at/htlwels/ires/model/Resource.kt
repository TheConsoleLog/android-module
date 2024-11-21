package at.htlwels.ires.model

sealed interface Resource<out T> {
    data object Ready : Resource<Nothing>
    data object Loading : Resource<Nothing>
    data class Success<out T>(val data: T) : Resource<T>
    data class Error(val message: String) : Resource<Nothing>
}