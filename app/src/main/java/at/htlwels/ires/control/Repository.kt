package at.htlwels.ires.control

import androidx.compose.runtime.MutableState
import at.htlwels.bonfire.model.jwt.TokenRepository
import at.htlwels.ires.model.Resource
import at.htlwels.ires.model.api.authService
import at.htlwels.ires.model.api.getMessage
import at.htlwels.ires.model.dto.auth.RenewRequest
import retrofit2.HttpException
import java.net.SocketTimeoutException

//TODO TESTING AUTOMATIC ACCESS TOKEN RENEWAL
class ResponseHandler{

    companion object{

        /**
         * Attempts Network call to API and displays the result by updating [state], also handles
         * access token Renewal on expiration.
         * @see call
         * @param apiCall The retrofit api call that should be handled
         */
        suspend fun <T> callWithStateUpdate(
            state: MutableState<Resource<T>>,
            apiCall: suspend () -> T,
            onSuccess: (T) -> Unit = {}
        ) {
            state.value = Resource.Loading

            val res = call { apiCall() }
            state.value = res

            if(res is Resource.Success) onSuccess(res.data)
        }

        /**
         * Executes a network API call with automatic handling of `401 Unauthorized` errors through token renewal.
         *
         * @param requestType Used to determine what kind of error message should be displayed upon HTTP 401 Error,
         * and to prevent infinite recursion if the token renewal results in a 401 Error
         * @param apiCall The retrofit api call that should be handled
         */
        suspend fun <T> call(
            requestType: RequestType = RequestType.Normal,
            apiCall: suspend () -> T
        ) : Resource<T> {

            try {
                val result = apiCall()
                return Resource.Success(result)

            } catch (e: HttpException){

                //if the http error code is 401 that means the access token might be expired, so
                //we try to get a new access token by using this same function with a different call
                if(e.code() == 401){

                    return when(requestType){
                        RequestType.TokenRenewal -> Resource.Error("Token Renewal resulted in 401 not authorized.")
                        RequestType.NormalWithNewToken -> Resource.Error("Still unauthorized with new access Token.")
                        RequestType.Normal -> {

                            //try to refresh the access token
                            val res = call(
                                requestType = RequestType.TokenRenewal
                            ) { authService.renew(RenewRequest(TokenRepository.getRefreshSafe())) }

                            //if the call succeeds, safe the token to the singleton and try to call again
                            if(res is Resource.Success){
                                TokenRepository.accessToken = res.data.access
                                println("Access Token automatically renewed at ${System.currentTimeMillis()}.")

                                return call (requestType = RequestType.NormalWithNewToken) { apiCall() }

                            } else {
                                return Resource.Error("Unauthorized and failed to refresh access token.")
                            }
                        }
                    }
                }

                return Resource.Error(e.getMessage())

            } catch(e: SocketTimeoutException){
                return Resource.Error("Ressource konnte nicht geladen werden: Timeout")
            } catch (e: Exception) {
                println("Unexpected error: ${e.localizedMessage}")
                return Resource.Error("An unexpected error occurred: ${e.localizedMessage}")
            }
        }
    }
}

enum class RequestType{
    Normal,
    NormalWithNewToken,
    TokenRenewal,
}

