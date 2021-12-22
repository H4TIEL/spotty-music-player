package io.github.h4tiel.able.models.spotifyplaylist

import com.google.gson.annotations.SerializedName

data class Followers (
	@SerializedName("href") val href : String,
	@SerializedName("total") val total : Int
)