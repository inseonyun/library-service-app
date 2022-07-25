package com.library_service_administrator

data class PostLogin(
    var user_id: String? = null,
    var user_pw: String? = null
)

data class PostLoginResult(
    var flag: Boolean? = null,
    var content: String? = null
)
