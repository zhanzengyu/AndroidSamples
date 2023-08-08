package com.zengyu.sample;

import android.app.Activity;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.zengyu.sample.databinding.ItemMainBinding;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

/**
 * @Author: zhanzengyu
 * @CreateDate: 2023/8/8 21:36
 * @Description:
 */
public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewViewHolder> {
    private Activity activity;
    private List<Long> itemList;
    private Set<RecyclerViewViewHolder> mHolders;
    private ScheduledExecutorService mExecutorService;

    public RecyclerViewAdapter(Activity activity, List<Long> itemList) {
        if (activity == null || itemList == null) {
            throw new IllegalArgumentException("params can't be null");
        }
        this.activity = activity;
        this.itemList = itemList;
        mHolders = new HashSet<>();
        mExecutorService = new ScheduledThreadPoolExecutor(1, new ThreadFactory() {
            @Override
            public Thread newThread(@NonNull Runnable r) {
                Thread thread = new Thread(r);
                thread.setName("countdown");
                return thread;
            }
        });
        mExecutorService.scheduleAtFixedRate(() -> activity.runOnUiThread(() -> {
            for (RecyclerViewViewHolder holder : mHolders) {
                updateTime(holder, holder.getTime());
            }
        }), 0, 1000, TimeUnit.MILLISECONDS);
    }

    @NonNull
    @Override
    public RecyclerViewViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemMainBinding itemMainBinding = ItemMainBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new RecyclerViewViewHolder(itemMainBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerViewViewHolder holder, int position) {
        holder.setTime(itemList.get(position));
        mHolders.add(holder);
        holder.mTvNum.setText(String.valueOf(position + 1));
        updateTime(holder, itemList.get(position));
    }

    private void updateTime(final RecyclerViewViewHolder holder, final long time) {
        String content;
        long remainTime = time - System.currentTimeMillis();
        remainTime /= 1000;
        if (remainTime <= 0) {
            content = "Time up";
            holder.mTxtTitle.setText(content);
            return;
        }

        content = "剩下"+remainTime+"秒";
        holder.mTxtTitle.setText(content);
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }
}
