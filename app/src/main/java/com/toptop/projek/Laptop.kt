package com.toptop.projek

import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class Laptop(
    val id: String? = null,
    val modelName: String? = null,
    val brand: String? = null,
    val operatingSystem: String? = null,
    val price: Long? = null, // Gunakan Long untuk harga
    val cpu: String? = null,
    val ram: String? = null,
    val storage: String? = null,
    val screenSize: Double? = null,
    val gpu: String? = null,
    val description: String? = null,
    val imageUrl: String? = null,
    val createdAt: String? = null
){
    // Konstruktor kosong untuk Firebase
    constructor() : this(null, null, null, null, null, null, null, null, null, null, null, null, null)
}