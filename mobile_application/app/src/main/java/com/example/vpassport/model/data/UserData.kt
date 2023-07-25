package com.example.vpassport.model.data

import com.google.gson.annotations.SerializedName

data class UserData(
    @SerializedName("wsId") val wsId: String,
    @SerializedName("proof") val proof: String
)