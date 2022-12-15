package com.omelan.cofi.share.model

data class License(val license: String, val license_url: String)

data class Dependency(
    val project: String,
    val description: String,
    val version: String,
    val developers: List<String>,
    val url: String?,
    val year: String?,
    val licenses: List<License>,
)
