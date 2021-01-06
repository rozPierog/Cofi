package com.omelan.burr

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MainActivityViewModel : ViewModel() {
    private val _pipState = MutableLiveData(false)
    val pipState: LiveData<Boolean> = _pipState

    private val _canGoToPiP = MutableLiveData(false)
    val canGoToPiP: LiveData<Boolean> = _canGoToPiP

    private val _statusBarHeight = MutableLiveData(0.dp)
    val statusBarHeight: LiveData<Dp> = _statusBarHeight

    private val _navBarHeight = MutableLiveData(0.dp)
    val navBarHeight: LiveData<Dp> = _navBarHeight

    fun setStatusBarHeight(newHeight: Dp) {
        _statusBarHeight.value = newHeight
    }

    fun setNavBarHeight(newHeight: Dp) {
        _navBarHeight.value = newHeight
    }

    fun setIsInPiP(newPiPState: Boolean) {
        _pipState.value = newPiPState
    }

    fun setCanGoToPiP(newCanGoToPiP: Boolean) {
        _canGoToPiP.value = newCanGoToPiP
    }
}