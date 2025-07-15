package subsonic

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Serializable
@Entity
data class SubsonicSong(@PrimaryKey val id: String, val parent: String? = null, val title: String, val artist: String ?= null,
                        val album: String? = null, val duration: Long = 0,
                        val track: Int = 0, val size: Long = 0, val suffix: String? = null, val contentType: String? = null,
                        val samplingRate: Int = 0, val isDir: Boolean = false, val path: String? = null, val albumId: String? = null,
                        val artistId: String ?= null, val created: String? = null, val bitRate: Int = 0,
                        val discNumber: Int = 1)
