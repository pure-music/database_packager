package subsonic.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import subsonic.SubsonicAlbum
import subsonic.SubsonicArtist
import subsonic.SubsonicSong

@Dao
interface ArtistDao {

    @Insert(onConflict = androidx.room.OnConflictStrategy.REPLACE)
    suspend fun insertAll(songs: List<SubsonicArtist>)
    
    @Query("DELETE FROM SubsonicArtist")
    suspend fun clear()
}