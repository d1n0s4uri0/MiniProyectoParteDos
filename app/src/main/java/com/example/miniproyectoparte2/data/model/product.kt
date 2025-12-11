package com.example.miniproyectoparte2.data.model

data class Product(
    val id: String = "",
    val code: String = "",
    val name: String = "",
    val price: Double = 0.0,
    val quantity: Int = 0
) : java.io.Serializable {
    fun getTotal(): Double {
        return price * quantity
    }
}
