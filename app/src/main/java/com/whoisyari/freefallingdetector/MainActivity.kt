package com.whoisyari.freefallingdetector

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager.widget.ViewPager
import com.whoisyari.freefallingdetectorlibrary.data.model.SensorData


class MainActivity : AppCompatActivity() {

    val viewPager: ViewPager by lazy { findViewById<ViewPager>(R.id.vpFalls) }
    val adapter: FallDetailAdapter by lazy { FallDetailAdapter() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setUpTheSdk()

        viewPager.adapter = adapter
    }

    private fun setUpTheSdk() {
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

    private fun displayTheData() {
        val repository = FreeFallingSdk.getInstance().getFreeFallRepository(this)
        val fallIds = repository.getFallIds()

        val allFalls = arrayListOf<List<SensorData>>()
        for (fallId in fallIds) {
            allFalls.add(repository.getFallTrailById(fallId))
        }
        adapter.setList(allFalls)
    }

    override fun onResume() {
        super.onResume()
        displayTheData()
    }


    override fun onStop() {
        super.onStop()
        Log.d(FreeFallingSdk.TAG, "activity onStop ${android.os.Process.myPid()}")
    }
}
