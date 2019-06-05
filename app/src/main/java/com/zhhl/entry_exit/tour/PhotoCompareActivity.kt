package com.zhhl.entry_exit.tour

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Bundle
import android.provider.MediaStore
import android.util.Base64
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import c.feature.autosize.AutoAdaptSize
import c.feature.autosize.ComplexUnit
import c.feature.dsl.okhttp.Method
import c.feature.dsl.okhttp.MimeType
import c.feature.dsl.okhttp.OkHttpDSL
import c.feature.extension.transparentStatus
import kotlinx.android.synthetic.main.activity_photo_compare.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.io.File

class PhotoCompareActivity : AppCompatActivity(), AutoAdaptSize {
    companion object {
        const val commitSuccess = 255
        const val commitFailed = 254
    }

    override fun complexUnit() = ComplexUnit.PT
    val camera = 1
    private val options = BitmapFactory.Options()

    private var successDialog: AlertDialog? = null;
    private var path = ""
    private var content = "";
    override fun designSize() = 320
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        transparentStatus(Color.WHITE)
        setContentView(R.layout.activity_photo_compare)
        successDialog = DialogFactory.success(this, "信息提交中...")
        path = intent.getStringExtra("photoPath")
        content = intent.getStringExtra("content")
        options.inSampleSize = 2
        window.decorView.post {
            getImage()
        }




        mBack.setOnClickListener {
            finish()
        }
        mBack2.setOnClickListener {
            finish()
        }

        commit.setOnClickListener {
            successDialog?.let {
                it.show()
                mBack.postDelayed({
                    DialogFactory.progressToSuccess(it, "信息已提交")
                    mBack.postDelayed({
                        DialogFactory.toProgress(it, "信息提交中...")
                        it.dismiss()
                        setResult(commitSuccess)
                        finish()
                    }, 3000)
                }, 3000)

            }


        }

        takePhoto.setOnClickListener {

            val root = File(filesDir, "capture")
            if (!root.exists()) {
                root.mkdirs()
            }
            val photoFile = File(root, "${System.currentTimeMillis()}.png")
            this.path = photoFile.absolutePath
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

    private fun getImage() {

        val bitmap0 = BitmapFactory.decodeFile(path, options)
        val bitmap1 = BitmapFactory.decodeFile(filesDir.absolutePath + "/capture/$content.jpg", options)
        currentPhoto.setImageBitmap(bitmap0)
        remotePhoto.setImageBitmap(bitmap1)
        zipImage(bitmap0, bitmap1);
    }

    private fun compareFaces(face0: String, face1: String) {
        val okHttp = OkHttpDSL()
        okHttp {

            requestDescription {
                method = Method.POST
                mimeType = MimeType.APPLICATION_JSON
                uri = "http://20.25.0.16:8088/rlsb/rlsb"
                body =
                    " {\"func\":\"oneToOne\",\"image1\":\"$face0\",\"image2\":\"$face1\",\"key\":\"732e99787c834a34b01a68129671bb96\",\"token\":\"ab\"}"
            }
            callType(FaceResult::class.java, {
                Log.e("result", "${it.isSuccess}::${it.obj.msg}::${it.obj.data.sim}")
                indicator.visibility = View.GONE
                if (it.isSuccess)
                    if (it.obj.data.errorcode == 0)
                        sim.text = "${it.obj.data.sim.toString().substring(0..4)}%"
                    else {
                        sim.text = "---(${it.obj.data.errormsg})"
                    }
            }, {
                it.printStackTrace()
                indicator.visibility = View.GONE
            })

//            requestDescription {
//                uri = "http://192.168.20.228:7098/RXDB/RXBDServlet"
//                method = Method.POST
//                mimeType = MimeType.APPLICATION_X_FORM_URLENCODED
////                mimeType = MimeType.APPLICATION_JSON
//                body = "base64_1=$_face0&base64_2=$_face1"
////                body= " { \"base64_1\":\"$face0\",\"base64_2\":\"$face1\"}"
//            }
//            callType(FaceResult2::class.java, {
//                Log.e("result", "${it.status}::${it.msg}::${it.data.sim}")
//                indicator.visibility = View.GONE
//
//                if (it.data.errorcode == 0)
//                    sim.text = "${it.data.sim.toString().substring(0..4)}%"
//                else {
//                    sim.text = "---(${it.data.errormsg})"
//                }
//            }, {
//                it.printStackTrace()
//                indicator.visibility = View.GONE
//            })


        }
    }


    private fun zipImage(bface0: Bitmap, bface1: Bitmap) {
        sim.text = "---"
        indicator.visibility = View.VISIBLE
        GlobalScope.launch(Dispatchers.IO) {

            val out0 = ByteArrayOutputStream()
            val out1 = ByteArrayOutputStream()
            bface0.compress(Bitmap.CompressFormat.JPEG, 40, out0)
            bface1.compress(Bitmap.CompressFormat.JPEG, 40, out1)

            withContext(Dispatchers.Main) {
                remotePhoto.setImageBitmap(bface1)
            }

            val face0Buff = out0.toByteArray()
            val face1Buff = out1.toByteArray()
            Log.e("Buff", "Size:${out0.size()}")
            Log.e("====", "===================")
            Log.e("Buff", "Size:${out1.size()}")
            val face0 = String(Base64.encode(face0Buff, Base64.DEFAULT));
            val face1 = String(Base64.encode(face1Buff, Base64.DEFAULT));
            compareFaces(face0, face1)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == camera && resultCode == Activity.RESULT_OK) {
            getImage()
        }
    }
}



