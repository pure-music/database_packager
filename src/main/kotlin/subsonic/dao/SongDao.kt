package subsonic.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import subsonic.SubsonicSong

@Dao
interface SongDao {

    @Insert(onConflict = androidx.room.OnConflictStrategy.REPLACE)
    suspend fun insertAll(songs: List<SubsonicSong>)

    @Query("DELETE FROM SubsonicSong")
    suspend fun clear()
}