package com.zengyu.sample;

import android.app.Activity;
import android.app.Instrumentation;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Created by zhanzengyu on 2023/9/21.
 * Description: Instrumentation 代理类
 */
public class InstrumentationProxy extends Instrumentation {
    private static final String TAG = InstrumentationProxy.class.getSimpleName();
    Instrumentation mInstrumentation;

    public InstrumentationProxy(Instrumentation mInstrumentation) {
        this.mInstrumentation = mInstrumentation;
    }

    public ActivityResult execStartActivity(
            Context who, IBinder contextThread, IBinder token, Activity target,
            Intent intent, int requestCode, Bundle options) {
        Log.d(TAG, "Hook success, who: " + who);
        try {
            Method execStartActivity = Instrumentation.class.
                    getDeclaredMethod("execStartActivity",
                    Context.class, IBinder.class, IBinder.class,
                    Activity.class, Intent.class, int.class, Bundle.class);
            return (ActivityResult) execStartActivity.invoke(mInstrumentation, who,
                    contextThread, token, target, intent, requestCode, options);
        } catch (NoSuchMethodException |
                 InvocationTargetException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

}
