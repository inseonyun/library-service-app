package com.library_service_administrator

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class PostUser(
    var input: String? = null
)

data class PostUserResult(
    var UserID: String? = null,
    var BookName: String? = null,
    var BookLoanDate : String? = null,
    var BookReturnDate : String? = null
)