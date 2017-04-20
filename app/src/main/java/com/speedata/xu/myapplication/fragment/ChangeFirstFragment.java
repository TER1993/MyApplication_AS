package com.speedata.xu.myapplication.fragment;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.speedata.xu.myapplication.R;
import com.speedata.xu.myapplication.adapter.CommonAdapter;
import com.speedata.xu.myapplication.adapter.ViewHolder;
import com.speedata.xu.myapplication.application.CustomerApplication;
import com.speedata.xu.myapplication.base.BaseFragment;
import com.speedata.xu.myapplication.db.bean.CheckDetailInfor;
import com.speedata.xu.myapplication.db.bean.CheckInfor;
import com.speedata.xu.myapplication.db.bean.OutputTxt;
import com.speedata.xu.myapplication.db.dao.CheckDetailInforDao;
import com.speedata.xu.myapplication.db.dao.CheckInforDao;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by xu on 2016/4/22.
 */
public class ChangeFirstFragment extends BaseFragment implements View.OnClickListener {


    private ListView lvCheck;
    private TextView tvNumber;
    private TextView tvBluetooth;
    private CheckInforDao checkInforDao;
    private Context mContext;
    private List<CheckInfor> checkInforList;
    private Button btnNew;
    private AlertDialog mDialog;

    private CustomerApplication application;

    private CheckDetailInforDao checkDetailInforDao;

    private Button btnExplore;
    private TextView btnDel;

    @Override
    public int setFragmentLayout() {
        return R.layout.check_inventory_list;
    }

    @Override
    public void findById(View view) {
        lvCheck = (ListView) view.findViewById(R.id.inventory_list_lv);
        tvNumber = (TextView) view.findViewById(R.id.inventory_number_tv);
        btnNew = (Button) view.findViewById(R.id.inventory_new_btn);
        tvBluetooth = (TextView) view.findViewById(R.id.inventory_bluetooth_tv);
        mContext = mActivity;
        checkInforDao = new CheckInforDao(mContext);
        checkDetailInforDao = new CheckDetailInforDao(mContext);

        application = (CustomerApplication) mActivity.getApplication();
        btnNew.setOnClickListener(this);
        tvBluetooth.setOnClickListener(this);
        tvBluetooth.setText(R.string.change_print_button);
        btnNew.setText(R.string.change_new_button);

        btnExplore = (Button) view.findViewById(R.id.inventory_explore_btn);
        btnDel = (TextView) view.findViewById(R.id.inventory_del_tv);
        btnExplore.setOnClickListener(this);
        btnDel.setOnClickListener(this);
        btnExplore.setText(R.string.tip_explore);
        btnDel.setText(R.string.tip_del);


        checkInforList = getCheckListData();

        setAdapterMethod();



        lvCheck.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {


                CheckInfor bean = checkInforList.get(position);
                String checkTime = bean.getCheckTime();

                ChangeFragment changeFragment = new ChangeFragment();
                Bundle bundle = new Bundle();

                bundle.putString("C2checkTime", checkTime);
                bundle.putString("C2checkName", bean.getCheckName());

                changeFragment.setArguments(bundle);
                openFragment(changeFragment);


            }
        });


        lvCheck.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                CheckInfor bean = checkInforList.get(position);
                String checktime = bean.getCheckTime();
                String checkname = bean.getCheckName();
                application.setTxtName(checkname);
                application.setCheckTime(checktime);

                DialogButtonOnLongClickListener dialogButtonOnClickListener = new DialogButtonOnLongClickListener();
                
//                mDialog = new AlertDialog.Builder(mActivity)
//                        .setTitle(R.string.change_list_dialog)
//                        .setPositiveButton(R.string.change_export_button, dialogButtonOnClickListener)
//                        .setNegativeButton(R.string.change_del_button, dialogButtonOnClickListener)
//                        .show();

                mDialog = new AlertDialog.Builder(mActivity)
                        .setTitle(R.string.dialog_change)
                        .setPositiveButton(R.string.dialog_return, dialogButtonOnClickListener)
                        .show();



                return true;
            }
        });

    }

    private List<CheckInfor> getCheckListData() {
        String id = application.getID();
        checkInforList = checkInforDao.imQueryList("UserID=?", new String[]{id});
        return checkInforList;

    }


    private void setAdapterMethod() {
        int a = checkInforList.size();
        String b = a + "";
        tvNumber.setText(b);

        CommonAdapter<CheckInfor> adapter = new CommonAdapter<CheckInfor>(mActivity, checkInforList, R.layout
                .change_inventory_item) {
            @Override
            public void convert(ViewHolder helper, CheckInfor item, int position) {
                helper.setText(R.id.change_inventory_name_tv, item.getCheckName());
                helper.setText(R.id.change_inventory_time_tv, item.getCheckTime());

                //   helper.setText(R.id.goods_price_tv, item.getGoodsPrice());
                //   helper.setText(R.id.goods_number_tv, item.getGoodsCount());

            }
        };
        lvCheck.setAdapter(adapter);
    }


    @Override
    public void onClick(View v) {
        if (v == btnNew) {
            Toast.makeText(mContext, R.string.change_new_toast, Toast.LENGTH_SHORT).show();
        } else if (v == tvBluetooth) {
            Toast.makeText(mContext, R.string.change_print_toast, Toast.LENGTH_SHORT).show();
        } else if (v == btnExplore) {
            Toast.makeText(mContext, R.string.toast_explore, Toast.LENGTH_SHORT).show();
        } else if (v == btnDel) {
            Toast.makeText(mContext, R.string.toast_del, Toast.LENGTH_SHORT).show();
        }

    }




    /**
     * 长按item时的对话框的按钮点击事件
     */
    private class DialogButtonOnLongClickListener implements DialogInterface.OnClickListener {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            switch (which) {
                case DialogInterface.BUTTON_POSITIVE: // 导出表单

//                    try {
//                        FileUtils fileUtils = new FileUtils();
//                        int h = fileUtils.outputfile(getOutputList(), createFilename());
//                        if (h == 1) {
//                            Toast.makeText(mContext, R.string.change_export_success, Toast.LENGTH_SHORT).show();
//                        }
//
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }

                    mDialog.dismiss();

                    break;
                case DialogInterface.BUTTON_NEGATIVE: // 删除表单
                    // 取消显示对话框
                    checkInforDao.imDelete("CheckTime=?", new String[]{application.getCheckTime()});
                    mDialog.dismiss();
                    checkInforList = getCheckListData();
                    setAdapterMethod();
                    Toast.makeText(mContext,getString(R.string.change_del_list) + application.getTxtName(), Toast.LENGTH_SHORT).show();
                    break;

            }
        }
    }


    //得到需要导出的文件List<>
    private List<OutputTxt> getOutputList() {
        String time = application.getCheckTime();
        List<CheckDetailInfor> checkDetailInfors = checkDetailInforDao.imQueryList("CheckID=?", new String[]{time});
        CheckDetailInfor bean;
        List<OutputTxt> outputTxts = new ArrayList<>();
        for (int i = 0; i < checkDetailInfors.size(); i++) {
            bean = checkDetailInfors.get(i);
            OutputTxt obean = new OutputTxt();
            obean.setGoodsnumber(bean.getGoodsNum());
            obean.setTab("\t");
            obean.setGoodscount(bean.getGoodsCount());
            obean.setEnter("\n");
            outputTxts.add(obean);
        }

        return outputTxts;
    }

    //创建导出文件的名字
    private String createFilename() throws IOException {

        return getString(R.string.export_path_) + application.getTxtName()+getString(R.string.txt);

    }



}
