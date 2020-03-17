package com.whoisyari.freefallingdetector

/*
 *
 * Create by hoanle@xtaypro.com
 * Created at 15/3/20
 *
 */

/**
 * Interface provides different callbacks of the sdk
 */
interface FreeFallingCallback {
    fun onFallingDetected()
    fun onPossibleFalling()
    fun onSensorUnavailable()
}