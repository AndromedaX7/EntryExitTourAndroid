package com.zhhl.entry_exit.tour

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import c.feature.autosize.AutoAdaptSize
import c.feature.autosize.ComplexUnit
import c.feature.extension.transparentStatus
import com.uuzuche.lib_zxing.activity.CaptureActivity
import com.uuzuche.lib_zxing.activity.CodeUtils
import kotlinx.android.synthetic.main.activity_scan2.*

class ScanQrActivity : AppCompatActivity(), AutoAdaptSize {

    private val requestClearQrCode = 1;
    private val requestScan = 2
    override fun complexUnit() = ComplexUnit.PT
    override fun designSize() = 320


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scan2)
        transparentStatus(Color.WHITE)

        scanResultContent.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                s?.let {
                    if (it.isEmpty()) {
                        scanResult.isErrorEnabled = true
                        scanResult.error = "请输入业务编码"
                    } else if (it.length != 15) {
                        scanResult.isErrorEnabled = true
                        scanResult.error="业务编码长度不正确"
                    } else {
                        scanResult.isErrorEnabled = false
                    }
                }
            }


            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

        })

        scan.setOnClickListener {
            startActivityForResult(Intent(this, CaptureActivity::class.java), requestScan);
        }
        mBack.setOnClickListener {
            finish()
        }
        mBack2.setOnClickListener {
            finish()
        }

        confirm.setOnClickListener {
            if (scanResultContent.text!!.isEmpty()) {
                scanResult.error = "请输入业务编码"
                scanResult.isErrorEnabled = true
                return@setOnClickListener
            }

            if (scanResultContent.text!!.length != 15) {
                scanResult.error = "业务编码长度不正确"
                scanResult.isErrorEnabled = true
                return@setOnClickListener
            }

            val intent = Intent(this, DetailsActivity::class.java)
            intent.putExtra(DetailsActivity.OPERATION_CODE, scanResultContent.text.toString())
            startActivityForResult(intent, requestClearQrCode)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
//        if (requestScan == requestCode && resultCode == Activity.RESULT_OK) {
//            data?.let {
//                val res = it.getStringExtra(CaptureActivity.OUTPUT_CODE)
//                scanResult.setText(res)
//                scanResult.setSelection(res.length)
//            }
//
//        }
        if (requestCode == requestScan) {
            //处理扫描结果（在界面上显示）
            if (null != data) {
                val bundle = data.extras ?: return
                if (bundle.getInt(CodeUtils.RESULT_TYPE) == CodeUtils.RESULT_SUCCESS) {
                    val result = bundle.getString(CodeUtils.RESULT_STRING)
                    scanResultContent.setText(result)
                    scanResultContent.setSelection(result.length)
                } else if (bundle.getInt(CodeUtils.RESULT_TYPE) == CodeUtils.RESULT_FAILED) {
                    Toast.makeText(this, "解析二维码失败", Toast.LENGTH_LONG).show()
                    scanResultContent.setText("")
                }
            }
        }
        if (requestCode == requestClearQrCode && resultCode == Activity.RESULT_OK) {
            scanResultContent.setText("")
        }
    }
}