package subsonic

import kotlinx.serialization.SerialName

@kotlinx.serialization.Serializable
data class SubsonicSearchListResponse(@SerialName("subsonic-response") val subsonicResponse: SubsonicSearchResponse? = null)
@kotlinx.serialization.Serializable
data class SubsonicSearchResponse(val status: String?, val version: String?, val searchResult3: SearchResult3? = null, val error: SubsonicError? = null)
@kotlinx.serialization.Serializable
data class SearchResult3(val song: List<SubsonicSong>? = null, val album: List<SubsonicAlbum>? = null, val artist: List<SubsonicArtist>? = null)


@kotlinx.serialization.Serializable
data class SubsonicError(val code: Int, val message: String)