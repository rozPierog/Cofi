package com.omelan.cofi.wearos.presentation

import com.google.android.gms.wearable.ChannelClient
import com.google.android.gms.wearable.Wearable
import com.google.android.gms.wearable.WearableListenerService
import com.omelan.cofi.share.model.AppDatabase

class CofiWearableListenerService: WearableListenerService() {
    override fun onChannelOpened(channel: ChannelClient.Channel) {
        super.onChannelOpened(channel)
        val db = AppDatabase.getInstance(this)
        val channelClient = Wearable.getChannelClient(this)
        ChannelHandler.getDataFromChannel(db, channelClient, channel)
    }
}
