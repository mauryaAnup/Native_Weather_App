package com.example.weatherapp

import android.content.Context

open class LoaderUtils {
    companion object {
        private var commonLoader: CommonLoader? = null

        fun showDialog(context: Context, isCancelable: Boolean) {
            hideDialog()
                try {
                    commonLoader = CommonLoader(context)
                    commonLoader?.let { commonLoader ->
                        commonLoader.setCanceledOnTouchOutside(false)
                        commonLoader.setCancelable(isCancelable)
                        commonLoader.show()
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
        }

        fun hideDialog() {
            if (commonLoader != null && commonLoader?.isShowing!!) {
                commonLoader = try {
                    commonLoader?.dismiss()
                    null
                } catch (e : Exception) {
                    null
                }
            }
        }
    }
}