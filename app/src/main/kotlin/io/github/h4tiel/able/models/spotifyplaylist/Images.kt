package io.github.h4tiel.able.models.spotifyplaylist

import com.google.gson.annotations.SerializedName

data class Images (
	@SerializedName("height") val height : Int,
	@SerializedName("url") val url : String,
	@SerializedName("width") val width : Int
)