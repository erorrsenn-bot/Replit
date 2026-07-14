package com.nova.client

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import com.nova.client.data.repository.ConfigRepository

class NovaApplication : Application() {

    lateinit var configRepository: ConfigRepository
        private set

    override fun onCreate() {
        super.onCreate()
        instance = this
        configRepository = ConfigRepository(this)
        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        val channel = NotificationChannel(
            OVERLAY_CHANNEL_ID,
            "Nova HUD Overlay",
            NotificationManager.IMPORTANCE_LOW
        ).apply {
            description = "Keeps the HUD overlay running over Minecraft"
            setShowBadge(false)
        }
        val manager = getSystemService(NotificationManager::class.java)
        manager.createNotificationChannel(channel)
    }

    companion object {
        const val OVERLAY_CHANNEL_ID = "nova_overlay"
        lateinit var instance: NovaApplication
            private set
    }
}
