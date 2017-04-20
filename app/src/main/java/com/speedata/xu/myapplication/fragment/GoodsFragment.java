package com.speedata.xu.myapplication.fragment;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.speedata.xu.myapplication.R;
import com.speedata.xu.myapplication.adapter.CommonAdapter;
import com.speedata.xu.myapplication.adapter.ViewHolder;
import com.speedata.xu.myapplication.application.CustomerApplication;
import com.speedata.xu.myapplication.base.BaseFragment;
import com.speedata.xu.myapplication.db.bean.BaseInfor;
import com.speedata.xu.myapplication.db.dao.BaseInforDao;
import com.speedata.xu.myapplication.utils.FileUtils;
import com.speedata.xu.myapplication.utils.ProgressDialogUtils;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by xu on 2016/4/5.
 */
public class GoodsFragment extends BaseFragment implements View.OnClickListener {

    private AlertDialog mDialog;
    private Context mContext;
    private BaseInforDao baseInforDao;

    @Override
    public int setFragmentLayout() {
        return R.layout.goods_frag;
    }

    private ListView lvgoods;
    private Button btnImport;
    private Button btnScan;
    private TextView tvNum;
    private CustomerApplication application;

    private List<BaseInfor> goodsDetialList;

    private EditText etTxtName;

    @Override
    public void findById(View view) {
        mContext = mActivity;
        goodsDetialList = new ArrayList<>();
        baseInforDao = new BaseInforDao(mContext);
        application = (CustomerApplication) mActivity.getApplication();
        btnImport = (Button) view.findViewById(R.id.goods_import_btn);
        btnImport.setOnClickListener(this);
        btnScan = (Button) view.findViewById(R.id.goods_scan_btn);
        btnScan.setOnClickListener(this);

        tvNum = (TextView) view.findViewById(R.id.goods_number_tv);

        lvgoods = (ListView) view.findViewById(R.id.goods_list_lv);

        goodsDetialList = application.getBaseInfor2();
        if (goodsDetialList == null) {
            return;
        }
        setAdapterMethod();


        lvgoods.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                BaseInfor bean = goodsDetialList.get(position);
                application.setAposition(position);
                String number = bean.getGoodsNum();
                String name = bean.getGoodsName();
                String price = bean.getGoodsPrice();
                String count = bean.getGoodsCount();
                GoodsMessageFragment goodsMessageFragment = new GoodsMessageFragment();
                Bundle bundle = new Bundle();

                bundle.putString("Gnumber", number);
                bundle.putString("Gname", name);
                bundle.putString("Gprice", price);
                bundle.putString("Gcount", count);
                goodsMessageFragment.setArguments(bundle);
                openFragment(goodsMessageFragment);


            }
        });
    }
    private void setAdapterMethod() {
        int a = goodsDetialList.size();
        String b = a + "";
        tvNum.setText(b);

        CommonAdapter<BaseInfor> adapter = new CommonAdapter<BaseInfor>(mActivity, goodsDetialList, R.layout
                .goods_item) {
            @Override
            public void convert(ViewHolder helper, BaseInfor item, int position) {
                helper.setText(R.id.goods_num_tv, item.getGoodsNum());
                helper.setText(R.id.goods_name_tv, item.getGoodsName());
                //    helper.setText(R.id.goods_price_tv, item.getGoodsPrice());
                //    helper.setText(R.id.goods_number_tv, item.getGoodsCount());
            }
        };

        lvgoods.setAdapter(adapter);
        if (application.getAposition() != -1) {
            lvgoods.setSelection(application.getAposition());
        }

    }


    private List<BaseInfor> getFirstGoodsData() {
        ProgressDialogUtils.showProgressDialog(mActivity, getString(R.string.good_is_loading));

        new Thread(new Runnable() {
            @Override
            public void run() {
                goodsDetialList = baseInforDao.imQueryList();

                application.setBaseInfo2(goodsDetialList);

                Message msg = new Message();
                msg.obj = 1;
                ahandler.sendMessage(msg);
            }
        }).start();


        return goodsDetialList;
    }

    private Handler ahandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            ProgressDialogUtils.dismissProgressDialog();
            int count = (int) msg.obj;
            if (count != 1) {
                new AlertDialog.Builder(mActivity).setTitle(R.string.good_wrong).setMessage(R.string.good_failed).show();
                return;
            }
            setAdapterMethod();
            if (application.getAposition() != -1) {
                lvgoods.setSelection(application.getAposition());
            }
        }
    };



    @Override
    public void onClick(View v) {
        if (v == btnImport) {
            DialogButtonOnClickListener dialogButtonOnClickListener = new DialogButtonOnClickListener();
            etTxtName = new EditText(mActivity);
            mDialog = new AlertDialog.Builder(mActivity)
                    .setTitle(R.string.good_input_filename)
                    .setView(etTxtName)
                    .setPositiveButton(R.string.sure, dialogButtonOnClickListener)
                    .setNegativeButton(R.string.miss, dialogButtonOnClickListener)
                    .show();

        } else if (v == btnScan) {
            goodsDetialList = getFirstGoodsData();
        }

    }

    /**
     * 退出时的对话框的按钮点击事件
     */
    private class DialogButtonOnClickListener implements DialogInterface.OnClickListener {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            switch (which) {
                case DialogInterface.BUTTON_POSITIVE: // 确定
                    String txtName = etTxtName.getText().toString();
                    mDialog.dismiss();
                    readFile(txtName);

                    break;
                case DialogInterface.BUTTON_NEGATIVE: // 取消
                    // 取消显示对话框
                    mDialog.dismiss();

                    break;
            }
        }
    }

    private void readFile(final String txtName) {

        ProgressDialogUtils.showProgressDialog(mActivity, getString(R.string.good_is_import));
        baseInforDao.imDeleteAll();
        new Thread(new Runnable() {
            @Override
            public void run() {
                final int acount = FileUtils.importBaseInforTxt(mContext, txtName);
                Message msg = new Message();
                msg.obj = acount;
                handler.sendMessage(msg);
            }
        }).start();


    }


    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            ProgressDialogUtils.dismissProgressDialog();
            int count = (int) msg.obj;
            if (count == -1) {
                new AlertDialog.Builder(mActivity).setTitle(getString(R.string.good_wrong)).setMessage(R.string.good_null_fail).show();
                return;
            }
            String c = count + "";
            tvNum.setText(c);
            Toast.makeText(mContext, getString(R.string.good_count) + count, Toast.LENGTH_SHORT).show();
        }
    };

}
