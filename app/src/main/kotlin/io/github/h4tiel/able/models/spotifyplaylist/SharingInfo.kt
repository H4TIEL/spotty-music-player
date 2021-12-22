package io.github.h4tiel.able.models.spotifyplaylist

import com.google.gson.annotations.SerializedName

data class SharingInfo (
	@SerializedName("share_id") val share_id : String,
	@SerializedName("share_url") val share_url : String,
	@SerializedName("uri") val uri : String
)