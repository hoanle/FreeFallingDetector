package com.whoisyari.freefallingdetector

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import androidx.viewpager.widget.PagerAdapter
import com.whoisyari.freefallingdetectorlibrary.data.model.SensorData
import java.text.SimpleDateFormat
import java.util.*

class FallDetailAdapter() : PagerAdapter() {

    private val fallingObjList: MutableList<FallingObject> = arrayListOf()
    fun setList(list: List<List<SensorData>>) {
        fallingObjList.clear()
        fallingObjList.addAll(list.map {
            FallingObject(it)
        })
        notifyDataSetChanged()
    }

    override fun isViewFromObject(view: View, theObject: Any): Boolean = view === theObject

    override fun getCount(): Int = fallingObjList.size

    override fun destroyItem(container: ViewGroup, position: Int, theObject: Any) {
        (theObject as? View)?.let(container::removeView)
    }

    override fun getPageWidth(position: Int): Float {
        return 0.95f
    }

    @SuppressLint("SetTextI18n")
    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val itemView = LayoutInflater.from(container.context)
            .inflate(R.layout.fall_item, container, false)
        container.addView(itemView)
        val txFallId = itemView.findViewById<AppCompatTextView>(R.id.tvFallId)
        val tvStartTime = itemView.findViewById<AppCompatTextView>(R.id.tvStartTime)
        val tvEndTime = itemView.findViewById<AppCompatTextView>(R.id.tvEndTime)
        val tvDuration = itemView.findViewById<AppCompatTextView>(R.id.tvDuration)
        val tvTotalZ = itemView.findViewById<AppCompatTextView>(R.id.tvTotalZ)
        val tvGravityZ = itemView.findViewById<AppCompatTextView>(R.id.tvGravityZ)
        val tvAccelerationZ = itemView.findViewById<AppCompatTextView>(R.id.tvAccelerationZ)

        val fallingObject = fallingObjList.get(position)

        val sensorDataFirstPosition = fallingObject.list.get(0)
        val sensorDataLastPosition = fallingObject.list.get(fallingObject.list.size - 1)

        txFallId.text = "${sensorDataFirstPosition.fallId}"
        tvStartTime.text = "${getStringTime(sensorDataFirstPosition.timeStamp)}"
        tvEndTime.text = "${getStringTime(sensorDataLastPosition.timeStamp)}"
        tvDuration.text = "${sensorDataLastPosition.timeStamp - sensorDataFirstPosition.timeStamp}"

        tvTotalZ.text = "${fallingObject.totalZ.toInt()}"
        tvAccelerationZ.text = "${fallingObject.accelerationZ.toInt()}"
        tvGravityZ.text = "${fallingObject.gravityZ.toInt()}"
        return itemView
    }

    private fun getStringTime(time: Long): String? {
        return SimpleDateFormat("yyyy-MMMM-dd HH:mm:ss").format(Date(time))
    }
}