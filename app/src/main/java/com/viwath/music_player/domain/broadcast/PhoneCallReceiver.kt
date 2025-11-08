package com.viwath.music_player.domain.broadcast

import android.os.Build
import android.telephony.TelephonyCallback
import android.telephony.TelephonyManager
import androidx.annotation.RequiresApi

@RequiresApi(Build.VERSION_CODES.S)
class PhoneCallReceiver(
    private val onCallStart: () -> Unit,
    private val onCallEnded: () -> Unit,
): TelephonyCallback(), TelephonyCallback.CallStateListener {

    private var isInCall = false

    override fun onCallStateChanged(state: Int) {
        when(state){
            TelephonyManager.CALL_STATE_IDLE -> {
                if (isInCall){
                    isInCall = false
                    onCallEnded()
                }
            }
            TelephonyManager.CALL_STATE_RINGING, TelephonyManager.CALL_STATE_OFFHOOK -> {
                if (!isInCall){
                    isInCall = true
                    onCallStart()
                }
            }
        }
    }

}