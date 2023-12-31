package com.happydroid.happytodo.data.network.model

import java.util.Date

/**
 * This object holds the current revision information.
 */
object RevisionHolder {
    var revision: Int = 0
    var deviceId: String = "Device-" + Date().time.toString()
}
