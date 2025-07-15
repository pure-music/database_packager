import io.ktor.client.*
import io.ktor.client.engine.*
import io.ktor.client.engine.okhttp.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import subsonic.SubsonicPackager
import java.util.concurrent.TimeUnit


fun main(args: Array<String>) {
    val json = Json {
        ignoreUnknownKeys = true
        explicitNulls = false
    }
    val cio = OkHttp.config {
        config {
            followRedirects(true)
            followSslRedirects(true)
            writeTimeout(5 * 60000, TimeUnit.MILLISECONDS)
            readTimeout(5 * 60000, TimeUnit.MILLISECONDS)
            connectTimeout(5 * 60000, TimeUnit.MILLISECONDS)
        }
    }
    val client = HttpClient(cio) {
        install(ContentNegotiation) {
            json(json)
        }
        install(HttpTimeout) {
            requestTimeoutMillis = 5 * 60000 // 请求超时时间，单位：毫秒
            connectTimeoutMillis = 5* 60000 // 连接超时时间，单位：毫秒
            socketTimeoutMillis = 5 * 60000  // 套接字超时时间，单位：毫秒
        }
    }

    //输出args
    for (arg in args) {
        println(arg)
    }

    val type = args[0]

    runBlocking {
        if (type == "1") {
            val webdavHost = args[1]
            val username = args[2]
            val pwd = args[3]
            val dbDir = args[4]
            val subsonicPackager = SubsonicPackager(client, webdavHost,
                username, pwd, dbDir)
            try {
                subsonicPackager.pack()
            } catch (e: Exception) {
                println(e.message + " " + System.currentTimeMillis())
                throw e
            }
        }
    }
}



