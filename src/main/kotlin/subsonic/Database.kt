package subsonic

import androidx.room.Database
import androidx.room.RoomDatabase
import subsonic.dao.AlbumDao
import subsonic.dao.ArtistDao
import subsonic.dao.SongDao

@Database(entities = [SubsonicSong::class, SubsonicAlbum::class, SubsonicArtist::class], version = 1)
abstract class Database : RoomDatabase() {
    abstract fun songDao(): SongDao
    abstract fun albumDao(): AlbumDao
    abstract fun artistDao(): ArtistDao
}