package com.zhhl.entry_exit.tour

import android.app.Activity
import android.util.TypedValue
import com.xdja.watermarklibrary.WaterMarkUtils

object AppCache {
    var loginBean: LoginBean? = null
    fun waterMark(context:Activity){
        loginBean?.let {
            WaterMarkUtils.addWaterMark(context,it.userInfo.name+"  "+it.userInfo.code, 315,0x22666666,TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_PT,16f,context.resources.displayMetrics))
        }
    }
}