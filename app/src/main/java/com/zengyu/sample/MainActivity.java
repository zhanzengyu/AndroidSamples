package com.zengyu.sample;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.zengyu.sample.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    ActivityMainBinding mBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());
        mBinding.tvCenter.setText("Text Change");
    }
}
