package at.htlwels.ires.model.jwt

import android.content.SharedPreferences

fun SharedPreferences.storeTokens(
    refresh: String? = null,
    access: String? = null
){
    //.edit() returns a SharedPreferencesEditor
    //.apply() is used to commit the changes

    edit().apply {
        refresh?.let { putString(TokenType.REFRESH.sp_key, it); println("storing refresh token") }
        access?.let { putString(TokenType.ACCESS.sp_key, it); println("storing access token") }
        apply()
    }
}

fun SharedPreferences.fetchToken(tt: TokenType) : String?{

    println("fetching ${tt.name} token: ${getString(tt.sp_key, null)}")
    return getString(tt.sp_key, null)
}

fun SharedPreferences.deleteTokens(){
    println("deleting both tokens")
    edit().apply{
        remove(TokenType.REFRESH.sp_key)
        remove(TokenType.ACCESS.sp_key)
        apply()
    }
}
