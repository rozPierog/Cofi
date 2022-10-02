package com.omelan.cofi

import android.app.Application
import android.os.Build
import com.kieronquinn.monetcompat.core.MonetCompat

class CofiApp : Application() {

    override fun onCreate() {
        super.onCreate()
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S) {
            MonetCompat.enablePaletteCompat()
        }
    }
}
