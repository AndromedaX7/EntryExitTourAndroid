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
import kotlinx.android.synthetic.main.activity_photo_compare.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.io.File
import java.net.URLEncoder

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
        AppCache.waterMark(this)
        successDialog = DialogFactory.success(this, "图片上传中...")
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
            upload()
        }

        takePhoto.setOnClickListener {
            val root = File(filesDir, "capture")
            if (!root.exists()) {
                root.mkdirs()
            }
            val photoFile = File(root, "xc$content.jpg")
            this.path = photoFile.absolutePath
            val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            val uri = FileProvider.getUriForFile(this, PhotoProvider.authentication, photoFile)
            Log.e("Uri", "$uri")
            cameraIntent.putExtra(
                MediaStore.EXTRA_OUTPUT, uri
            )
            startActivityForResult(cameraIntent, camera)
        }
    }

    private fun getImage() {
        val bitmap0 = BitmapFactory.decodeFile(filesDir.absolutePath + "/capture/xc$content.jpg", options)
        val bitmap1 = BitmapFactory.decodeFile(filesDir.absolutePath + "/capture/$content.jpg", options)
        currentPhoto.setImageBitmap(bitmap0)
        remotePhoto.setImageBitmap(bitmap1)
        zipImage(bitmap0, bitmap1);
    }

    private fun compareFaces(face0: String, face1: String) {
        val okHttp = HttpDSL()
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
        }
    }


    private fun upload() {;
        successDialog?.show()
        val out = ByteArrayOutputStream()
        val options = BitmapFactory.Options()
        options.inSampleSize = 4
        val bitmap = BitmapFactory.decodeFile(path, options)
        bitmap.compress(Bitmap.CompressFormat.JPEG, 40, out)
        val src = out.toByteArray()
        out.close()
        val base64_2 = String(Base64.encode(src, Base64.DEFAULT))
        val base64 = URLEncoder.encode(base64_2.replace("\n", ""), "utf-8");
        val dsl = HttpDSL()
        dsl {
            requestDescription {
                uri = "http://192.168.20.228:7098/crjInterface/uploudImghx.do"
                body = "ywbh=$content&xcrxxx=$base64&sbid=${AppCache.loginBean?.userInfo?.code}"
                method = Method.POST
                mimeType = MimeType.APPLICATION_X_FORM_URLENCODED
            }
            callString({
                Log.e("upload", it);//{"code":"0"}
                if (it.contains("1")) {
                    DialogFactory.progressToSuccess(successDialog!!, "图片已上传")
                    mBack.postDelayed({
                        DialogFactory.toProgress(successDialog!!, "图片上传中...")
                        successDialog?.dismiss()
                        setResult(commitSuccess)
                        finish()
                    }, 3000)

                } else {
                    DialogFactory.progressToFailed(successDialog!!, "图片上传失败")
                    mBack.postDelayed({
                        DialogFactory.toProgress(successDialog!!, "图片上传中...")
                        successDialog?.dismiss()
                    }, 3000)
                    successDialog?.dismiss()
                }
            }, {
                Log.e("upload", "Error", it)
                DialogFactory.progressToFailed(successDialog!!, "图片上传失败")
                mBack.postDelayed({
                    DialogFactory.toProgress(successDialog!!, "图片上传中...")
                    successDialog?.dismiss()
                }, 3000)
            })
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



