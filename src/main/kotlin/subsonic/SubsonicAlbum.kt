package subsonic

import androidx.room.Entity
import androidx.room.PrimaryKey

@kotlinx.serialization.Serializable
@Entity
data class SubsonicAlbum(@PrimaryKey val id: String, val name: String? = null, val artist: String?= null, val songCount: Int = 0, val duration: Int = 0,
                         val playCount: Int = 0, val created: String? = null, val artistId: String? = null,
                         val coverArt: String? = null)
