package com.example.weatherapp

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.widget.LinearLayout

class CommonLoader(context: Context): Dialog(context) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.loading_layout)
        window?.setLayout(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.MATCH_PARENT
        )
        window?.setBackgroundDrawableResource(R.color.transparent)
    }
}