package io.github.h4tiel.able.models.spotifyplaylist

import com.google.gson.annotations.SerializedName

data class ExternalURL (
	@SerializedName("spotify") val spotify : String
)