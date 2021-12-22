package io.github.h4tiel.able.models.spotifyplaylist

import com.google.gson.annotations.SerializedName

data class Items (
	@SerializedName("added_at") val added_at : String,
	@SerializedName("added_by") val AddedBy : AddedBy,
	@SerializedName("is_local") val is_local : Boolean,
	@SerializedName("primary_color") val primary_color : String,
	@SerializedName("sharing_info") val sharing_info : SharingInfo,
	@SerializedName("track") val track : Track,
	@SerializedName("video_thumbnail") val video_thumbnail : VideoThumbnail
)