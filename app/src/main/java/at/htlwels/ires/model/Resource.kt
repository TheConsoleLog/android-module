package at.htlwels.ires.model

import retrofit2.HttpException

sealed interface Resource<out T> {
    data object Ready : Resource<Nothing>
    data object Loading : Resource<Nothing>
    data class Success<out T>(val data: T) : Resource<T>

    data class Error(
        val details: Exception,
        private val preparedMessage: String? = null
    ) : Resource<Nothing> {

        /**
         * Return http error message in following order:
         * 1. [preparedMessage] if exists
         * 2. [HttpException.message] if the exception is of that type
         * 3. [java.lang.Exception.getLocalizedMessage] if exists
         * 4. Default String
         */
        fun getMessage() : String =
            preparedMessage ?:
            if(this.details is HttpException) this.details.message()
            else this.details.localizedMessage ?: "Unexpected Error without localized Message."
    }
}



