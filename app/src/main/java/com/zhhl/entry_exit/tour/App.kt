package com.zhhl.entry_exit.tour

import android.app.Activity
import android.app.Application
import android.os.Bundle
import android.util.Log
import c.feature.autosize.AutoSizeManager
import com.uuzuche.lib_zxing.activity.ZXingLibrary
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.File

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        clearFileCache();
        AutoSizeManager.instance(this).init()
        ZXingLibrary.initDisplayOpinion(this)
        registerActivityLifecycleCallbacks(object : Application.ActivityLifecycleCallbacks {
            override fun onActivityPaused(activity: Activity?) {
            }

            override fun onActivityResumed(activity: Activity?) {
            }

            override fun onActivityStarted(activity: Activity?) {
            }

            override fun onActivityDestroyed(activity: Activity?) {
            }

            override fun onActivitySaveInstanceState(activity: Activity?, outState: Bundle?) {
            }

            override fun onActivityStopped(activity: Activity?) {
            }

            override fun onActivityCreated(activity: Activity?, savedInstanceState: Bundle?) {
                Log.e("activityCreated", activity!!::class.java.simpleName)
            }

        })
    }

    private fun clearFileCache() {
        GlobalScope.launch(Dispatchers.IO) {
            val root = File("${filesDir.absolutePath}/capture")
            if (getFileSize(root) > 10 * 1024 * 1024) {
                root.delete()
            }
        }

    }

    private fun getFileSize(dir: File): Long {
        if (dir.isDirectory) {
            var size = 0L;
            for (file in dir.listFiles()) {
                size += getFileSize(file)
            }
            return size
        } else {
            return dir.length();
        }
    }

}