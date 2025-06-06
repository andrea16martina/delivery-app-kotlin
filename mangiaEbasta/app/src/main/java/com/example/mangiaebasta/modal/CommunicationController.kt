package com.example.mangiaebasta.modal

import android.content.Context
import android.net.Uri
import android.util.Log
import com.mapbox.geojson.Point
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.android.Android
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json

object CommunicationController {
    private val BASE_URL = "https://develop.ewlab.di.unimi.it/mc/2425"
    private val TAG = CommunicationController::class.simpleName

    private val client = HttpClient(Android) {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
            })
        }
    }

    enum class HttpMethod {
        GET,
        POST,
        DELETE,
        PUT
    }

    private suspend fun genericRequest(url: String, method: HttpMethod,
                                       queryParameters: Map<String, Any> = emptyMap(),
                                       requestBody: Any? = null) : HttpResponse {

        val urlUri = Uri.parse(url)
        val urlBuilder = urlUri.buildUpon()
        queryParameters.forEach { (key, value) ->
            urlBuilder.appendQueryParameter(key, value.toString())
        }
        val completeUrlString = urlBuilder.build().toString()
        Log.d(TAG, completeUrlString)

        val request: HttpRequestBuilder.() -> Unit = {
            requestBody?.let {
                contentType(ContentType.Application.Json)
                setBody(requestBody)
            }
        }

        val result = when (method) {
            HttpMethod.GET -> client.get(completeUrlString, request)
            HttpMethod.POST -> client.post(completeUrlString, request)
            HttpMethod.DELETE -> client.delete(completeUrlString, request)
            HttpMethod.PUT -> client.put(completeUrlString, request)
        }
        return result
    }

    suspend fun createUser(): UserResponse {
        Log.d(TAG, "createUser")

        val url = BASE_URL + "/user"

        val httpResponse = genericRequest(url, HttpMethod.POST)
        // TODO: aggiungere il controllo degli errori
        val result: UserResponse = httpResponse.body()
        return result
    }

    suspend fun getMenu(context: Context, location: Point): List<Menu> {
        Log.d(TAG, "getMenu")

        val sid = runBlocking { StorageManager.getSid(context).first() }
            ?: throw IllegalStateException("SID not found in DataStore")

        val url = "$BASE_URL/menu"
        val queryParameters = mapOf(
            "lat" to location.latitude(),
            "lng" to location.longitude(),
            "sid" to sid
        )

        val httpResponse = genericRequest(url, HttpMethod.GET, queryParameters)
        return httpResponse.body()
    }

    suspend fun getMenuImg(context: Context, mid: Int): MenuImg {
        Log.d(TAG, "getMenuImg")

        val sid = runBlocking { StorageManager.getSid(context).first() }
            ?: throw IllegalStateException("SID not found in DataStore")

        val url = "$BASE_URL/menu/$mid/image"
        val queryParameters = mapOf(
            "sid" to sid
        )

        val httpResponse = genericRequest(url, HttpMethod.GET, queryParameters)
        return httpResponse.body()
    }

    suspend fun getMenuDetails(context: Context, menu: Menu): MenuDetails {
        Log.d(TAG, "getMenuDetails")

        val sid = runBlocking { StorageManager.getSid(context).first() }
            ?: throw IllegalStateException("SID not found in DataStore")

        val url = "$BASE_URL/menu/${menu.mid}"
        val queryParameters = mapOf(
            "lat" to menu.location.lat,
            "lng" to menu.location.lng,
            "sid" to sid
        )

        val httpResponse = genericRequest(url, HttpMethod.GET, queryParameters)
        return httpResponse.body()
    }

    suspend fun getUser(context: Context): User {
        Log.d(TAG, "getUser")

        val sid = runBlocking { StorageManager.getSid(context).first() }
            ?: throw IllegalStateException("SID not found in DataStore")
        val uid = runBlocking { StorageManager.getUid(context).first() }
            ?: throw IllegalStateException("UID not found in DataStore")

        val url = "$BASE_URL/user/$uid"
        val queryParameters = mapOf(
            "sid" to sid
        )

        val httpResponse = genericRequest(url, HttpMethod.GET, queryParameters)
        return httpResponse.body()
    }

    suspend fun updateUser(context: Context, user: upUser) {
        Log.d(TAG, "updateUser")

        val sid = runBlocking { StorageManager.getSid(context).first() }
            ?: throw IllegalStateException("SID not found in DataStore")
        val uid = runBlocking { StorageManager.getUid(context).first() }
            ?: throw IllegalStateException("UID not found in DataStore")

        val url = "$BASE_URL/user/$uid"
        val queryParameters = mapOf(
            "sid" to sid
        )

        val httpResponse = genericRequest(url, HttpMethod.PUT, queryParameters, user)
    }

    suspend fun buyMenu(context: Context, mid: Int, currentPoint: Point): Order {
        Log.d(TAG, "buyMenu")

        if(!isUserProfileComplete(context)) {
            throw IllegalStateException("profile not complete")
        }

        if(getLastOrder(context)?.status  == "ON_DELIVERY") {
            throw IllegalStateException("ON_DELIVERY")
        }

        val sid = runBlocking { StorageManager.getSid(context).first() }
            ?: throw IllegalStateException("SID not found in DataStore")

        val url = "$BASE_URL/menu/$mid/buy"

        val requestBody = BuyMenuRequest(
            sid = sid,
            deliveryLocation = DeliveryLocation(
                lat = currentPoint.latitude(),
                lng = currentPoint.longitude()
            )
        )
            val httpResponse = client.post(url) {
                contentType(ContentType.Application.Json)
                setBody(requestBody)
            }
        if (httpResponse.status.value == 403) {
            throw IllegalStateException("403")
        }


           return httpResponse.body()
    }

    private suspend fun isUserProfileComplete(context: Context): Boolean {
        val user = getUser(context)
        return user.firstName?.isNotEmpty() == true &&
                user.lastName?.isNotEmpty() == true &&
                user.cardFullName?.isNotEmpty() == true &&
                user.cardNumber?.isNotEmpty() == true &&
                user.cardExpireMonth != null &&
                user.cardExpireYear != null &&
                user.cardCVV?.isNotEmpty() == true
    }

    private suspend fun getOrderStatus (context: Context, oid: Int): Order {
        Log.d(TAG, "getOrderStatus")

        val sid = runBlocking { StorageManager.getSid(context).first() }
            ?: throw IllegalStateException("SID not found in DataStore")

        val url = "$BASE_URL/order/$oid"
        val queryParameters = mapOf(
            "sid" to sid
        )

        val httpResponse = genericRequest(url, HttpMethod.GET, queryParameters)
        return httpResponse.body()
    }

    suspend fun getLastOrder (context: Context): Order? {
        Log.d(TAG, "getLatOrder")

        val sid = runBlocking { StorageManager.getSid(context).first() }
            ?: throw IllegalStateException("SID not found in DataStore")

        val user = getUser(context)
        val oid = user.lastOid

       return oid?.let { getOrderStatus(context, it) }
    }

    suspend fun getMenuByMid(context: Context, mid: Int, deliveryLocation: Location): MenuDetails {
        Log.d(TAG, "getMenuByMid")

        val sid = runBlocking { StorageManager.getSid(context).first() }
            ?: throw IllegalStateException("SID not found in DataStore")

        val url = "$BASE_URL/menu/$mid"
        val queryParameters = mapOf(
            "lat" to deliveryLocation.lat,
            "lng" to deliveryLocation.lng,
            "sid" to sid
        )

        val httpResponse = genericRequest(url, HttpMethod.GET, queryParameters)
        return httpResponse.body()
    }
}