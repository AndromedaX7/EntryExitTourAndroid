package com.zhhl.entry_exit.tour

import android.app.Activity
import android.app.AlertDialog
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
import kotlinx.android.synthetic.main.activity_details.*
import java.io.File
import java.io.FileOutputStream


class DetailsActivity : AppCompatActivity(), AutoAdaptSize {

    companion object {
        const val OPERATION_CODE = "1"
    }

    private var dialog: AlertDialog? = null
    private var path: String = ""
    private var mOperationCode = ""
    private val camera = 1;
    private val commit = 2;
    override fun complexUnit() = ComplexUnit.PT
    override fun designSize() = 320
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_details)
        AppCache.waterMark(this)
        getIntentData()
        dialog = DialogFactory.success(this, "数据加载中....")
        requestInformation()
        transparentStatus(Color.WHITE)
        val root = File(filesDir, "capture")
        if (!root.exists()) {
            root.mkdirs()
        }
        val photoFile = File(root, "xc$mOperationCode.jpg")
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
        dialog?.show()
        val okHttp = HttpDSL()
        okHttp {
            requestDescription {
                uri = "http://192.168.20.228:7098/crjInterface/selecHxRyxx.do"
                method = Method.POST
                body = "ywbh=$mOperationCode"//&sbid=232
                mimeType = MimeType.APPLICATION_X_FORM_URLENCODED
            }

            callType(DataInformation::class.java, {
                if (it.code == "1") {
                    setData(it)
                    dialog?.dismiss()
                } else {
                    DialogFactory.progressToFailed(dialog!!, "数据读取失败")
                    mBack.postDelayed({
                        DialogFactory.toProgress(dialog!!, "数据加载中....")
                        dialog?.dismiss()
                        finish()
                    }, 3000)
                }
            }, {
                DialogFactory.progressToFailed(dialog!!, "数据读取失败")
                mBack.postDelayed({
                    DialogFactory.toProgress(dialog!!, "数据加载中....")
                    dialog?.dismiss()
                    finish()
                }, 3000)
            })
        }
    }

    private fun setData(information: DataInformation) {
        val data = information.data[0]
        name.text = data.name
        sex.text = data.xb


        birthday.text = if (data.csrq.isEmpty()) {
            var id = ""
            if (data.sfzh.length == 18) {
                id = data.sfzh.subSequence(6, 6 + 8).toString()
//            }else{
//               id= data.sfzh.subSequence(6,6+6).toString()
            }
            id
        } else {
            data.csrq
        }
        nation.text = "${data.mz}族"
        idCode.text = data.sfzh
        areaType.text = data.dylx
        registeredResidence.text = data.hkszd
        currentResidence.text = data.xjd
        phone.text = data.sjhm
        operationCode.text = data.ywbh
        operationType.text = if (data.ywlx.isEmpty()) {
            "边境游业务"
        } else {
            data.ywlx
        }

        getCertificatesTime.text = if (data.qzsj.isEmpty()) {
            "未取证"
        } else {
            data.qzsj
        }

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
