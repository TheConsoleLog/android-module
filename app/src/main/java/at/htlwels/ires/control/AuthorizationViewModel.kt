package at.htlwels.ires.control

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import at.htlwels.bonfire.model.jwt.TokenRepository
import at.htlwels.bonfire.model.jwt.TokenType
import at.htlwels.ires.common.Constants
import at.htlwels.ires.model.Resource
import at.htlwels.ires.model.api.authService
import at.htlwels.ires.model.dto.auth.AuthRequest
import at.htlwels.ires.model.dto.auth.AuthResponse
import at.htlwels.ires.model.dto.auth.LoginRequest
import at.htlwels.ires.model.dto.auth.RenewRequest
import at.htlwels.ires.model.dto.auth.ResetPasswordRequest
import at.htlwels.ires.model.dto.auth.SignupRequest
import at.htlwels.ires.model.jwt.deleteTokens
import at.htlwels.ires.model.jwt.fetchToken
import at.htlwels.ires.model.jwt.storeTokens
import kotlinx.coroutines.launch

class AuthorizationViewModel(application: Application) : AndroidViewModel(application) {

    private val _sp: SharedPreferences = getApplication<Application>().getSharedPreferences(
        Constants.SHARED_PREF_FILE, Context.MODE_PRIVATE
    )

    private val _loginState: MutableState<Resource<AuthResponse>> = mutableStateOf(Resource.Ready)
    val loginState: State<Resource<AuthResponse>> = _loginState

    private val _signupState: MutableState<Resource<AuthResponse>> = mutableStateOf(Resource.Ready)
    val signupState: State<Resource<AuthResponse>> = _signupState


    //check if refresh token exists and is still valid, if yes set loginState to Success and therefore autologin
    init {
        viewModelScope.launch {
            _sp.fetchToken(TokenType.REFRESH)?.let { autoLogin(it) }
        }
    }


    private fun authorize(authReq: AuthRequest){

        val (authState, authorize) = when (authReq) {
            is LoginRequest -> _loginState to suspend { authService.login(authReq) }
            is SignupRequest -> _signupState to suspend { authService.signUp(authReq) }
        }

        viewModelScope.launch {

            ResponseHandler.callWithStateUpdate(
                state = authState,
                apiCall = authorize,
                onSuccess = { res ->
                    TokenRepository.refreshToken = res.refresh
                    println("Received Refresh: " + res.refresh)
                    TokenRepository.accessToken = res.access
                    println("Received Access: " + res.access)

                    _sp.storeTokens(refresh = res.refresh)
                }
            )
        }
    }

    fun signup(username: String, firstname: String, email: String, password: String) {
        authorize(SignupRequest("02.05.2005", email, firstname, "mustermann", password, username))
    }


    /**
     * Update login State
     */
    fun login(username: String, password: String) = authorize(LoginRequest(username, password))
    fun logout() = _sp.deleteTokens()


    private fun autoLogin(refreshToken: String) {

        viewModelScope.launch {

            println("Attempting AutoLogin with refresh Token: $refreshToken")

            val res = ResponseHandler.call { authService.renew(RenewRequest(refreshToken)) }

            if(res is Resource.Success){
                _loginState.value = Resource.Success(AuthResponse("", ""))
                TokenRepository.accessToken = res.data.access
                _sp.storeTokens(access = res.data.access)
            } else if (res is Resource.Error) {
                println("autologin failed: ")
                println(res.getMessage())
            }
        }
    }

    fun resetLoginState(){ _loginState.value = Resource.Ready }
    fun resetSignupState(){ _signupState.value = Resource.Ready }

    var resetPwUsernameState =  mutableStateOf("")


    private val _pwEmailSentState: MutableState<Resource<Unit>> = mutableStateOf(Resource.Ready)
    val pwEmailSentState: State<Resource<Unit>> = _pwEmailSentState

    fun sendPwEmail(username: String){
        viewModelScope.launch {
            ResponseHandler.callWithStateUpdate(
                state = _pwEmailSentState,
                apiCall = { authService.sendPwEmail(username) }
            )
        }
    }

    private val _passwordResetState: MutableState<Resource<Unit>> = mutableStateOf(Resource.Ready)
    val passwordResetState : State<Resource<Unit>> = _passwordResetState

    fun resetPassword(resetToken: String, newPW: String){
        viewModelScope.launch {
            ResponseHandler.callWithStateUpdate(
                state = _passwordResetState,
                apiCall = { authService.resetPassword(
                    ResetPasswordRequest(
                    token = resetToken,
                    userName = resetPwUsernameState.value,
                    updatedPassword = newPW
                )
                )}
            )
        }
    }

    fun resetPasswordResetStates(){
        _pwEmailSentState.value = Resource.Ready
        _passwordResetState.value = Resource.Ready
    }
}