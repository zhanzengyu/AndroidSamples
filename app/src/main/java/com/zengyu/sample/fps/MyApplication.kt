package com.zengyu.sample.fps

import android.app.Activity
import android.app.Application
import android.os.Bundle

class MyApplication : Application() {
    private var activityCount = 0

    override fun onCreate() {
        super.onCreate()
        registerActivityLifecycleCallbacks(object : ActivityLifecycleCallbacks {
            override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {}
            override fun onActivityStarted(activity: Activity) {
                if (activityCount == 0) {
                    // App moved to foreground
                    FpsMonitor.start()
                }
                activityCount++
            }
            override fun onActivityResumed(activity: Activity) {}
            override fun onActivityPaused(activity: Activity) {}
            override fun onActivityStopped(activity: Activity) {
                activityCount--
                if (activityCount == 0) {
                    // App moved to background
                    FpsMonitor.stop()
                }
            }
            override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {}
            override fun onActivityDestroyed(activity: Activity) {}
        })
    }

    override fun onTerminate() {
        super.onTerminate()
        FpsMonitor.stop()
    }
}

