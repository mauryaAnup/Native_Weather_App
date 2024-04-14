package com.example.weatherapp

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build

open class NoInternetUtils {

    companion object {
        private var noInternet: NoInternet? = null

        fun isOnline(context: Context): Boolean {
            val connectivityManager =
                context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

            val networkCapabilities =
                connectivityManager.activeNetwork ?: return false
            val actNw =
                connectivityManager.getNetworkCapabilities(networkCapabilities) ?: return false

            return actNw.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
        }


        fun showNoInternet(context: Context) {
            hideNoInternet()
            try {
                noInternet = NoInternet(context)
                noInternet.let { noInternet ->
                    noInternet?.setCanceledOnTouchOutside(false)
                    noInternet?.setCancelable(false)
                    noInternet?.show()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        fun hideNoInternet() {
            if (noInternet != null && noInternet?.isShowing!!) {
                noInternet = try {
                    noInternet?.dismiss()
                    null
                } catch (e: Exception) {
                    null
                }
            }
        }

    }
}