package subsonic

import androidx.room.Entity
import androidx.room.PrimaryKey

@kotlinx.serialization.Serializable
@Entity
data class SubsonicArtist(@PrimaryKey val id: String, val name: String, val coverArt: String? = null,
                          val albumCount: Int = 0, val artistImageUrl: String? = null) {
}