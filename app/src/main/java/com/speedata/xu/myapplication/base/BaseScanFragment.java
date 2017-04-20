package com.speedata.xu.myapplication.base;


import com.speedata.xu.myapplication.utils.ScanUtil;

public abstract class BaseScanFragment extends BaseFragment implements IBaseScanFragment {
	private ScanUtil scanUtil;
	public boolean isScan = true;

	@Override
	public void onResume() {
		super.onResume();
		scanUtil = new ScanUtil(mActivity);
		scanUtil.setOnScanListener(new ScanUtil.OnScanListener() {

			@Override
			public void getBarcode(String barcode) {
				onGetBarcode(barcode);
			}
		});
	}

	@Override
	public void onPause() {
		scanUtil.stopScan();
		super.onPause();
	}

}
