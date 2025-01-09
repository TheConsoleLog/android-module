package at.htlwels.ires.model.jwt

object TokenRepository {
    var refreshToken: String? = null
    var accessToken: String? = null

    fun logout(){
        refreshToken = null
        accessToken = null
    }

    private const val E = "No matching Token found in TokenRepository Singleton."

    fun getAccessSafe() = accessToken ?: throw RuntimeException(E)
    fun getRefreshSafe() = refreshToken ?: throw RuntimeException(E)
    fun getAccessWithBearer() = "Bearer " + getAccessSafe()
}