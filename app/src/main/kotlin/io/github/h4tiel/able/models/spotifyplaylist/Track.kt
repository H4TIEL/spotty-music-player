package io.github.h4tiel.able.models.spotifyplaylist

import com.google.gson.annotations.SerializedName

data class Track (
	@SerializedName("album") val album : Album,
	@SerializedName("artists") val artists : List<Artists>,
	@SerializedName("available_markets") val available_markets : List<String>,
	@SerializedName("disc_number") val disc_number : Int,
	@SerializedName("duration_ms") val duration_ms : Int,
	@SerializedName("episode") val episode : Boolean,
	@SerializedName("explicit") val explicit : Boolean,
	@SerializedName("external_ids") val external_ID : ExternalID,
	@SerializedName("external_urls") val external_URL : ExternalURL,
	@SerializedName("href") val href : String,
	@SerializedName("id") val id : String,
	@SerializedName("is_local") val is_local : Boolean,
	@SerializedName("name") val name : String,
	@SerializedName("popularity") val popularity : Int,
	@SerializedName("preview_url") val preview_url : String,
	@SerializedName("tags") val tags : List<String>,
	@SerializedName("track") val track : Boolean,
	@SerializedName("track_number") val track_number : Int,
	@SerializedName("type") val type : String,
	@SerializedName("uri") val uri : String
)