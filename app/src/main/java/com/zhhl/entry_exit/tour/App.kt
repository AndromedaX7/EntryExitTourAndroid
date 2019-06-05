package com.zhhl.entry_exit.tour

import android.app.Activity
import android.app.Application
import android.os.Bundle
import android.util.Log
import c.feature.autosize.AutoSizeManager
import com.uuzuche.lib_zxing.activity.ZXingLibrary

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        AutoSizeManager.instance(this).init()
        ZXingLibrary.initDisplayOpinion(this)
        registerActivityLifecycleCallbacks(object  :Application.ActivityLifecycleCallbacks{
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
                Log.e("activityCreated",activity!!::class.java.simpleName)
            }

        })
    }
}