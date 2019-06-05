package com.zhhl.entry_exit.tour

import android.app.Activity
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Bundle
import android.provider.MediaStore
import android.util.Base64
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import c.feature.autosize.AutoAdaptSize
import c.feature.autosize.ComplexUnit
import c.feature.dsl.okhttp.Method
import c.feature.dsl.okhttp.MimeType
import c.feature.dsl.okhttp.OkHttpDSL
import c.feature.extension.get
import c.feature.extension.transparentStatus
import kotlinx.android.synthetic.main.activity_details.*
import java.io.File
import java.io.FileOutputStream


class DetailsActivity : AppCompatActivity(), AutoAdaptSize {

    companion object {
        const val OPERATION_CODE = "1"
    }

    private var path: String = ""
    private var mOperationCode = ""
    private val camera = 1;
    private val commit = 2;
    override fun complexUnit() = ComplexUnit.PT
    override fun designSize() = 320
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_details)
        getIntentData()
        requestInformation()
        transparentStatus(Color.WHITE)
        val root = File(filesDir, "capture")
        if (!root.exists()) {
            root.mkdirs()
        }
        val photoFile = File(root, "${System.currentTimeMillis()}.png")
        path = photoFile.absolutePath
        cancel.setOnClickListener {
            finish()
        }
        mBack.setOnClickListener {
            finish()
        }
        mBack2.setOnClickListener {
            finish()
        }
        takePhoto.setOnClickListener {
            val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            val uri = FileProvider.getUriForFile(this, PhotoProvider.authentication, photoFile)
            Log.e("Uri", "$uri")
            cameraIntent.putExtra(
                MediaStore.EXTRA_OUTPUT, uri
            )
            startActivityForResult(cameraIntent, camera)
//            startActivity(Intent(this,PhotoCompareActivity::class.java))
        }
    }
    //http://10.106.54.118:25500/crjInterface/selecRyxx.do

    private fun requestInformation() {
        val okHttp = OkHttpDSL()
        okHttp {
            requestDescription {
                uri = "http://192.168.20.228:7098/crjInterface/selecRyxx.do"
                method = Method.POST
                body = "ywbh=$mOperationCode&sbid=232"//&sbid=232
                mimeType = MimeType.APPLICATION_X_FORM_URLENCODED
            }
            callType(DataInformation::class.java, {
                if (it.code == "1") {
                    setData(it)
                }
            }, {

            })
            callString({
                Log.e("Test", it);
            }, {
                it.printStackTrace()
            })
        }
    }

    private fun setData(information: DataInformation) {
        val data = information.data[0]
        name.text = data.name
        sex.text = data.xb
        birthday.text = data.csrq
        nation.text = "${data.mz}族"
        idCode.text = data.sfzh
        areaType.text = data.dylx
        registeredResidence.text = data.hkszd
        currentResidence.text = data.xjd
        phone.text = data.sjhm
        operationCode.text = data.ywbh
        operationType.text = data.ywlx
        getCertificatesTime.text = data.qzsj


        var photo = File(filesDir.absolutePath + "/capture", "$mOperationCode.jpg")
        var out = FileOutputStream(photo);
        out.write(Base64.decode(data.zzzp, Base64.DEFAULT));
        out.flush()
        out.close()
        mPhoto.setImageBitmap(BitmapFactory.decodeFile(photo.absolutePath))
//


    }

    private fun getIntentData() {
        val intentData = intent
        mOperationCode = (intentData[OPERATION_CODE, String::class.java] ?: "")
        operationCode.text = mOperationCode
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == camera && resultCode == Activity.RESULT_OK) {
            startActivityForResult(
                Intent(this, PhotoCompareActivity::class.java)
                    .putExtra("photoPath", path)
                    .putExtra("content", mOperationCode), commit
            )
        }

        if (requestCode == commit && resultCode == PhotoCompareActivity.commitSuccess) {
            setResult(Activity.RESULT_OK)
            finish()
        }
    }
}
