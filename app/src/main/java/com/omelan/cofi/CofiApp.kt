package com.omelan.cofi

import android.app.Application
import com.kieronquinn.monetcompat.core.MonetCompat

class CofiApp: Application() {

    override fun onCreate() {
        super.onCreate()
        MonetCompat.enablePaletteCompat()
    }
}