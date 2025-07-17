package subsonic

import Packager
import androidx.room.Room
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import korlibs.crypto.md5
import kotlinx.coroutines.*
import kotlinx.datetime.Clock
import java.io.File
import kotlin.text.toByteArray

class SubsonicPackager(private val httpClient: HttpClient, private val webdavHost: String,
                       private val username: String, private val pwd: String, dbPath: String): Packager {
    private val databasePath: File = File(dbPath)
    private val db: Database
    private val pageSize = 500

    init {
        databasePath.delete()
        if (!databasePath.parentFile.exists()) {
            databasePath.parentFile.mkdirs()
        }
        db = Room.databaseBuilder<Database>(
            name = databasePath.absolutePath)
            .setDriver(BundledSQLiteDriver())
            .setQueryCoroutineContext(Dispatchers.IO)
            .build()
    }

    private suspend fun requestAlbums() {
        var albumOffset = 0L
        var hasNextAlbums = true

        while (hasNextAlbums) {
            println("start albumOffset ${System.currentTimeMillis()} $albumOffset")
            val muAlbums = getAlbums(albumOffset, pageSize, 0)
            db.albumDao().insertAll(muAlbums)
            if (muAlbums.size > (pageSize  - 20)) {
                hasNextAlbums = true
            } else {
                hasNextAlbums = false
            }
            albumOffset += muAlbums.size
        }
    }

    private suspend fun requestSongs() {
        var hasNextSong = true
        var songOffset = 0L
        while (hasNextSong) {
            println("start songOffset ${System.currentTimeMillis()} $songOffset")
            val songs = getSongs(songOffset, pageSize, 0)
            db.songDao().insertAll(songs)
            if (songs.size > (pageSize  - 20)) {
                hasNextSong = true
            } else {
                hasNextSong = false
            }
            songOffset += songs.size
        }

    }

    private suspend fun requestArtists() {
        var hasNextArtist = true
        var artistOffset = 0L
        while (hasNextArtist) {
            println("start artistOffset ${System.currentTimeMillis()} $artistOffset")
            val artists = getArtists(artistOffset, pageSize, 0)
            db.artistDao().insertAll(artists)
            if (artists.size > (pageSize  - 20)) {
                hasNextArtist = true
            } else {
                hasNextArtist = false
            }
            artistOffset += artists.size
        }
    }

    override suspend fun pack() {
        coroutineScope {
            val job1 = async {
                requestArtists()
                requestAlbums()
            }
            val job2 = async { requestSongs() }
            // 等待所有任务完成
            awaitAll(job1, job2)
        }
    }

    private suspend fun getSongs(offsetSearch:Long, size: Int, retry: Int): List<SubsonicSong> {
        val getAlbumsUrl = webdavHost + "/rest/search3" + "?query=''&songCount=$size&songOffset=$offsetSearch&albumCount=0&artistCount=0&" + getComnParams()
        val searchResponse = httpClient.get(getAlbumsUrl)
        if (searchResponse.status.value in 200..299) {
            val searchListResponse = searchResponse.body<SubsonicSearchListResponse>()
            if (searchListResponse.subsonicResponse != null) {
                val subsonicResponse = searchListResponse.subsonicResponse
                if (subsonicResponse.status == "ok" && subsonicResponse.searchResult3 != null
                    && subsonicResponse.searchResult3.song != null) {
                    val subsonicSongs = subsonicResponse.searchResult3.song
                    return subsonicSongs
                } else if (subsonicResponse.status != "ok") {
                    throw IllegalStateException("${subsonicResponse.status} ${subsonicResponse.error?.code} ${subsonicResponse.error?.message}")
                }
            }
            return emptyList()
        } else {
            if (retry < 3) {
                println("retry getSongs $offsetSearch $size")
                return getSongs(offsetSearch, size, retry + 1)
            } else {
                throw IllegalStateException("http error ${searchResponse.status.value}")
            }
        }
    }

    private suspend fun getAlbums(albumOffset:Long, size: Int, retry: Int): List<SubsonicAlbum> {
        val getAlbumsUrl = webdavHost + "/rest/search3" + "?query=''&songCount=0&albumCount=${size}&artistCount=0&albumOffset=$albumOffset&" + getComnParams()
        val albumsResponse = httpClient.get(getAlbumsUrl)
        if (albumsResponse.status.value in 200..299) {
            val albumListResponse = albumsResponse.body<SubsonicSearchListResponse>()
            if (albumListResponse.subsonicResponse != null) {
                val subsonicResponse = albumListResponse.subsonicResponse
                if (subsonicResponse.status == "ok"
                    && subsonicResponse.searchResult3 != null
                    && subsonicResponse.searchResult3.album != null) {
                    val subsonicAlbums = subsonicResponse.searchResult3.album
                    return subsonicAlbums
                } else if (subsonicResponse.status != "ok") {
                    throw IllegalStateException("${subsonicResponse.status} ${subsonicResponse.error?.code} ${subsonicResponse.error?.message}")
                }
            }
            return emptyList()
        } else {
            if (retry < 3) {
                println("retry getAlbums $albumOffset $size")
                return getAlbums(albumOffset, size, retry + 1)
            } else {
                throw IllegalStateException("http error ${albumsResponse.status.value}")
            }
        }
    }

    private suspend fun getArtists(offset:Long, size: Int, retry: Int): List<SubsonicArtist> {
        val getAlbumsUrl = webdavHost + "/rest/search3" + "?query=''&songCount=0&artistOffset=$offset&albumCount=0&artistCount=$size&" + getComnParams()
        val searchResponse = httpClient.get(getAlbumsUrl)
        if (searchResponse.status.value in 200..299) {
            val searchListResponse = searchResponse.body<SubsonicSearchListResponse>()
            if (searchListResponse.subsonicResponse != null) {
                val subsonicResponse = searchListResponse.subsonicResponse
                if (subsonicResponse.status == "ok" && subsonicResponse.searchResult3 != null
                    && subsonicResponse.searchResult3.artist != null) {
                    val subsonicArtists = subsonicResponse.searchResult3.artist
                    return subsonicArtists
                } else if (subsonicResponse.status != "ok") {
                    throw IllegalStateException( "${subsonicResponse.status} ${subsonicResponse.error?.code} ${subsonicResponse.error?.message}")
                } else {
                    throw IllegalStateException("subsonicResponse")
                }
            } else {
                throw IllegalStateException("searchListResponse null")
            }
        } else {
            if (retry < 3) {
                println("retry getArtists $offset $size")
                return getArtists(offset, size, retry + 1)
            } else {
                throw IllegalStateException("http error ${searchResponse.status.value}")
            }
        }
    }

    private fun getComnParams(): String {
        val currentTime = Clock.System.now().toEpochMilliseconds()
        val ts = pwd + currentTime
        val md5 = ts.toByteArray().md5().hex
        return "u=$username&s=$currentTime&t=$md5&v=1.16.1&c=PureMusic&f=json"
    }
}