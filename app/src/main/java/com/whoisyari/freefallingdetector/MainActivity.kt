package com.whoisyari.freefallingdetector

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        Log.d(FreeFallingSdk.TAG, "activity onCreate ${android.os.Process.myPid()}")
        val options = FreeFallingOptions(true)

        FreeFallingSdk.getInstance().startService(this, options, object : FreeFallingCallback {
            override fun onFallingDetected() {
                Log.d(FreeFallingSdk.TAG, "onFallingDetected")
            }

            override fun onSensorUnavailable() {
                Log.d(FreeFallingSdk.TAG, "onSensorUnavailable")
            }

            override fun onPossibleFalling() {

            }
        })
    }

    override fun onStop() {
        super.onStop()
        Log.d(FreeFallingSdk.TAG, "activity onStop ${android.os.Process.myPid()}")
    }
}
