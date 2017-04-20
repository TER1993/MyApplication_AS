package com.speedata.xu.myapplication.base;

import android.view.View;


public interface IBaseFragment {

	/**
	 * 设置Fragment 布局文件
	 * @return
	 */
	public int setFragmentLayout();
	
	/**
	 * 获取控件
	 * @return
	 */
	public void findById(View view);
	
}
