package subsonic.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import subsonic.SubsonicAlbum
import subsonic.SubsonicSong

@Dao
interface AlbumDao {

    @Insert(onConflict = androidx.room.OnConflictStrategy.REPLACE)
    suspend fun insertAll(songs: List<SubsonicAlbum>)
    
    @Query("DELETE FROM SubsonicAlbum")
    suspend fun clear()
}