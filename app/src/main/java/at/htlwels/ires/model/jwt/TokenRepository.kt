package at.htlwels.bonfire.model.jwt

object TokenRepository {
    var refreshToken: String? = null
    var accessToken: String? = null

    fun logout(){
        refreshToken = null
        accessToken = null
    }

    private const val E = "No matching Token found in TokenRepository Singleton."

    fun getAccessSafe() : String = accessToken ?: throw RuntimeException(E)
    fun getRefreshSafe() : String = refreshToken ?: throw RuntimeException(E)
}