package com.whoisyari.freefallingdetector

import android.app.PendingIntent

/*
 *
 * Create by hoanle@xtaypro.com
 * Created at 15/3/20
 *
 */

/**
 * Define the options that the lib will use
 * @param interval:
 * @param minimumFallingSpeed
 * @param allowForeground: On Android 8 and above, background service is very limited. Running foreground to keep it alive
 */
class FreeFallingOptions(val allowForeground: Boolean) {

    // The interval time, in milliseconds, that the lib will detect motion between activities
    var interval = 100

    // The minimum speed to decide this is a free falling
    var minimumFallingSpeed = 1500
}