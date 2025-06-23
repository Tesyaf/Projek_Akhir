package com.toptop.projek

import com.google.firebase.database.IgnoreExtraProperties

// Anotasi ini membantu Firebase mengabaikan field tambahan jika ada
@IgnoreExtraProperties
data class User(
    val id: String? = null,
    val displayName: String? = null,
    val email: String? = null,
    val password: String? = null, // WAJIB ADA untuk logika login & register
    val photoURL: String? = null,
    val preferredOperatingSystem: String? = null,
    val createdAt: String? = null
) {
    // Konstruktor kosong diperlukan oleh Firebase untuk deserialisasi
    constructor() : this(null, null, null, null, null, null, null)
}