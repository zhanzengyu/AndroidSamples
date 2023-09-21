package com.zengyu.sample;

import android.app.Instrumentation;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import java.lang.reflect.Field;

public class MainActivity2 extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        replaceActivityInstrumentation();
        Intent intent = new Intent(this, MainActivity3.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        getApplicationContext().startActivity(intent);
    }

    public void replaceActivityInstrumentation() {
        Class<?> activityThreadClazz = null;
        try {
            activityThreadClazz = Class.forName("android.app.ActivityThread");
            Field activityThreadField = activityThreadClazz.getDeclaredField("sCurrentActivityThread");
            activityThreadField.setAccessible(true);
            Object currentActivityThread = activityThreadField.get(null);
            Field mInstrumentationField = activityThreadClazz.getDeclaredField("mInstrumentation");
            mInstrumentationField.setAccessible(true);
            Instrumentation mInstrumentation = (Instrumentation) mInstrumentationField.get(currentActivityThread);
            Instrumentation mInstrumentationProxy = new InstrumentationProxy(mInstrumentation);
            mInstrumentationField.set(currentActivityThread, mInstrumentationProxy);
        } catch (ClassNotFoundException | NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}