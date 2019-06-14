package com.zhhl.entry_exit.tour

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.core.app.ActivityCompat
import c.feature.autosize.AutoAdaptSize
import c.feature.autosize.ComplexUnit
import kotlinx.android.synthetic.main.activity_splash.*

class SplashActivity : SplashActivityWrapper(), AutoAdaptSize {
    override fun login(token: String?) {
        val login = HttpDSL()
        login {
            requestDescription {
                usingOkHttp = false
                uri = "http://192.168.20.230:8081/uas/sso/singlesignoncontrol/checkbill.do"
                method = Method.POST
                body = "strBill=$token"
                mimeType = MimeType.APPLICATION_X_FORM_URLENCODED
            }
            callType(LoginBean::class.java, {
                AppCache.loginBean = it
                AppCache.waterMark(this@SplashActivity)
            }, {

            })
        }
    }

    override fun uaacApiError(error: String?) {
        Log.e("err", error)
    }

    override fun complexUnit(): ComplexUnit = ComplexUnit.PT
    override fun designSize() = 320
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        transparentStatus(Color.WHITE)
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), 100)
        }

        scan.setOnClickListener {
            startActivity(Intent(this, ScanQrActivity::class.java))
        }
        query.setOnClickListener {
            startActivity(Intent(this, HistoryActivity::class.java))
        }
    }


    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }
}