package com.example.mangiaebasta.modal

import kotlinx.serialization.Serializable

@Serializable
data class UserResponse(
    val sid: String,
    val uid: Int
)

@Serializable
data class Location(
    val lat: Double,
    val lng: Double
)

@Serializable
data class Menu(
    val mid: Int,
    val name: String,
    val price: Double,
    val location: Location,
    val imageVersion: Int,
    val shortDescription: String,
    val deliveryTime: Int,
)

@Serializable
data class MenuImg(
    val base64: String
)

@Serializable
data class MenuDetails(
    val mid: Int,
    val name: String,
    val price: Double,
    val location: Location,
    val imageVersion: Int,
    val shortDescription: String,
    val deliveryTime: Int,
    val longDescription: String
)

@Serializable
data class User(
    val firstName: String? = null,
    val lastName: String? = null,
    val cardFullName: String? = null,
    val cardNumber: String? = null,
    val cardExpireMonth: Int? = null,
    val cardExpireYear: Int? = null,
    val cardCVV: String? = null,
    val uid: Int,
    val lastOid: Int? = null,
    val orderStatus: String? = null
)

 @Serializable
data class upUser(
    val firstName: String? = null,
    val lastName: String? = null,
    val cardFullName: String? = null,
    val cardNumber: String? = null,
    val cardExpireMonth: Int? = null,
    val cardExpireYear: Int? = null,
    val cardCVV: String? = null,
    val sid: String
)

@Serializable
data class Order(
    val oid: Int,
    val mid: Int,
    val uid: Int,
    val creationTimestamp: String,
    val status: String,
    val deliveryLocation: Location,
    val currentPosition: Location,
    val deliveryTimestamp: String? = null,
    val expectedDeliveryTimestamp : String? = null
)

@Serializable
data class DeliveryLocation(
    val lat: Double,
    val lng: Double
)

@Serializable
data class BuyMenuRequest(
    val sid: String,
    val deliveryLocation: DeliveryLocation
)