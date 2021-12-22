

package io.github.h4tiel.able.services

import android.os.Bundle
import android.os.Handler
import android.os.ResultReceiver

class ServiceResultReceiver(handler: Handler?) : ResultReceiver(handler) {
    private var mReceiver: Receiver? = null

    fun setReceiver(receiver: Receiver?) {
        mReceiver = receiver
    }

    override fun onReceiveResult(resultCode: Int, resultData: Bundle?) {
        mReceiver?.onReceiveResult(resultCode)
    }

    interface Receiver {
        fun onReceiveResult(resultCode: Int)
    }
}