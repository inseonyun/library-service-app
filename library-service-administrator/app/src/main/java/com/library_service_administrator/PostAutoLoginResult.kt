package com.library_service_administrator

data class PostAutoLogin(
    var user_id: String? = null,
    var user_pw: String? = null
)

data class PostAutoLoginResult(
    var flag: Boolean? = null,
    var content: String? = null
)
