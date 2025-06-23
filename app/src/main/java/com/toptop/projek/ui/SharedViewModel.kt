package com.toptop.projek.ui // atau package lain yang Anda inginkan

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SharedViewModel : ViewModel() {
    val userId = MutableLiveData<String?>()

    fun setUserId(id: String?) {
        userId.value = id
    }
}