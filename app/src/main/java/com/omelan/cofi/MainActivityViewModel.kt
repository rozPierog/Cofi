package com.omelan.cofi

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MainActivityViewModel : ViewModel() {
    private val _pipState = MutableLiveData(false)
    val pipState: LiveData<Boolean> = _pipState

    private val _canGoToPiP = MutableLiveData(false)
    val canGoToPiP: LiveData<Boolean> = _canGoToPiP

    fun setIsInPiP(newPiPState: Boolean) {
        _pipState.value = newPiPState
    }

    fun setCanGoToPiP(newCanGoToPiP: Boolean) {
        _canGoToPiP.value = newCanGoToPiP
    }
}