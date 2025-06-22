package com.toptop.projek

import android.content.Context
import android.content.SharedPreferences

class SharedPrefManager private constructor(context: Context) {

    // Nama file SharedPreferences
    private val SHARED_PREF_NAME = "TopTopPrefs"

    // Kunci untuk data user
    private val KEY_USER_ID = "keyUserId"
    private val KEY_USERNAME = "keyUsername"
    private val KEY_IS_LOGGED_IN = "keyIsLoggedIn" // Untuk menandai apakah user sudah login

    private val sharedPreferences: SharedPreferences
    private val editor: SharedPreferences.Editor

    init {
        // Inisialisasi SharedPreferences
        // MODE_PRIVATE: Hanya aplikasi ini yang bisa mengakses SharedPreferences ini
        sharedPreferences = context.applicationContext.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE)
        editor = sharedPreferences.edit()
    }

    companion object {
        @Volatile
        private var instance: SharedPrefManager? = null

        // Metode untuk mendapatkan instance SharedPrefManager (Singleton)
        fun getInstance(context: Context): SharedPrefManager =
            instance ?: synchronized(this) {
                instance ?: SharedPrefManager(context).also { instance = it }
            }
    }

    /**
     * Menyimpan data pengguna setelah login berhasil.
     */
    fun userLogin(userId: Int, username: String) {
        editor.putInt(KEY_USER_ID, userId)
        editor.putString(KEY_USERNAME, username)
        editor.putBoolean(KEY_IS_LOGGED_IN, true) // Set status login menjadi true
        editor.apply() // Terapkan perubahan secara asynchronous
    }

    fun isLoggedIn(): Boolean {
        return sharedPreferences.getBoolean(KEY_IS_LOGGED_IN, false) // Defaultnya false
    }

    /**
     * Mendapatkan ID pengguna yang sedang login.
     * @return ID pengguna, atau -1 jika tidak ada pengguna login.
     */
    fun getUserId(): Int {
        return sharedPreferences.getInt(KEY_USER_ID, -1) // Defaultnya -1
    }

    /**
     * Mendapatkan username pengguna yang sedang login.
     * @return Username pengguna, atau null jika tidak ada pengguna login.
     */
    fun getUsername(): String? {
        return sharedPreferences.getString(KEY_USERNAME, null) // Defaultnya null
    }

    /**
     * Melakukan logout pengguna dengan menghapus semua data dari SharedPreferences.
     */
    fun logout() {
        editor.clear() // Hapus semua data
        editor.apply() // Terapkan perubahan
    }
}