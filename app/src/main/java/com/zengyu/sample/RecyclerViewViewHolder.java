package com.zengyu.sample;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.zengyu.sample.databinding.ItemMainBinding;

import java.util.Objects;

/**
 * @Author: zhanzengyu
 * @CreateDate: 2023/8/8 21:26
 * @Description:
 */
public class RecyclerViewViewHolder extends RecyclerView.ViewHolder {
    TextView mTxtTitle;
    TextView mTvNum;

    public RecyclerViewViewHolder(ItemMainBinding itemMainBinding) {
        super(itemMainBinding.getRoot());
        mTxtTitle = itemMainBinding.txtTitle;
        mTvNum = itemMainBinding.tvNum;
    }

    private long time;

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RecyclerViewViewHolder that = (RecyclerViewViewHolder) o;
        return Objects.equals(mTxtTitle, that.mTxtTitle) && Objects.equals(mTvNum, that.mTvNum);
    }

    @Override
    public int hashCode() {
        return Objects.hash(mTxtTitle, mTvNum);
    }
}
