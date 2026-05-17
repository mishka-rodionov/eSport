package com.competra.core.sync

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest

/**
 * Подписка на появление интернет-соединения через ConnectivityManager.
 * При каждом callback'е [onAvailable] системы вызывает [onNetworkAvailable].
 *
 * Регистрируется один раз при старте приложения (CompetraApp.onCreate).
 */
class NetworkAvailabilityObserver(private val context: Context) {

    private var registered = false

    fun start(onNetworkAvailable: () -> Unit) {
        if (registered) return
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager ?: return
        val request = NetworkRequest.Builder()
            .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            .build()
        cm.registerNetworkCallback(request, object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                onNetworkAvailable()
            }
        })
        registered = true
    }
}
