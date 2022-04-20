package com.library_service_administrator

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class PostModel(
    var ISBN: String? = null
)

data class PostResult(
    var ISBN: String? = null,
    var Name: String? = null,
    var Writer: String? = null,
    var Quantity: String? = null
)