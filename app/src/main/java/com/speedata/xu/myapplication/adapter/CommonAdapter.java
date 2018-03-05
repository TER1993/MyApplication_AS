package com.speedata.xu.myapplication.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.List;

/**
 * Created by YJ on 15/8/3.
 */

public abstract class CommonAdapter<T> extends BaseAdapter {

    protected Context mContext;

    private List<T> mDatas;

    private int layoutId;
    //添加需要的部分



    protected CommonAdapter(Context context, List<T> mDatas, int layoutId) {
        this.mContext = context;
        LayoutInflater mInflater = LayoutInflater.from(mContext);
        this.mDatas = mDatas;
        this.layoutId = layoutId;
    }



    @Override
    public int getCount() {
        return mDatas.size();
    }

    @Override
    public T getItem(int position) {
        return mDatas.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = ViewHolder.get(mContext, convertView, parent, layoutId, position);
        convert(holder, getItem(position), position);
        return holder.getConvertView();
    }


    public abstract void convert(ViewHolder helper, T item, int position);

    public void addItem(T item) {
        mDatas.add(item);
    }



}


