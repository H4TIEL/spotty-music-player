package io.github.h4tiel.able.models.spotifyplaylist

import com.google.gson.annotations.SerializedName

data class SpotifyPlaylist (

	@SerializedName("collaborative") val collaborative : Boolean,
	@SerializedName("description") val description : String,
	@SerializedName("external_urls") val external_URL : ExternalURL,
	@SerializedName("followers") val followers : Followers,
	@SerializedName("href") val href : String,
	@SerializedName("id") val id : String,
	@SerializedName("images") val images : List<Images>,
	@SerializedName("name") val name : String,
	@SerializedName("owner") val owner : Owner,
	@SerializedName("primary_color") val primary_color : String,
	@SerializedName("public") val public : Boolean,
	@SerializedName("sharing_info") val sharing_info : SharingInfo,
	@SerializedName("snapshot_id") val snapshot_id : String,
	@SerializedName("tracks") val tracks : Tracks,
	@SerializedName("type") val type : String,
	@SerializedName("uri") val uri : String
)