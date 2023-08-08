package com.zengyu.sample;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.zengyu.sample.databinding.ActivityMainBinding;

import java.util.ArrayList;
import java.util.List;

import me.everything.android.ui.overscroll.OverScrollDecoratorHelper;

public class MainActivity extends AppCompatActivity {
    RecyclerView mRecyclerView;
    private RecyclerViewAdapter mAdapter;
    private List<Long> mItemList = new ArrayList<>();
    ActivityMainBinding mBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());
        initData();
        initRecyclerView();
    }

    final int ITEM_COUNT = 20;
    /**
     * 获取数据源
     */
    private void initData() {
        for (int i = 1; i <= ITEM_COUNT; i++) {
            mItemList.add(System.currentTimeMillis() + 3600 * 1000 * i);
        }
    }


    private void initRecyclerView() {
        mRecyclerView = mBinding.recyclerView;
        //设置子视图
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        //添加分割线
        mRecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        //设置适配器
        mAdapter = new RecyclerViewAdapter(this, mItemList);
        mRecyclerView.setAdapter(mAdapter);
        // Vertical
        OverScrollDecoratorHelper.setUpOverScroll(mRecyclerView, OverScrollDecoratorHelper.ORIENTATION_VERTICAL);
    }
}
