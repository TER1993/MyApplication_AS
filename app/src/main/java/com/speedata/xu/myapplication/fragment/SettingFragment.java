package com.speedata.xu.myapplication.fragment;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.speedata.xu.myapplication.R;
import com.speedata.xu.myapplication.activity.ConActivity;
import com.speedata.xu.myapplication.activity.LoginActivity;
import com.speedata.xu.myapplication.application.CustomerApplication;
import com.speedata.xu.myapplication.base.BaseFragment;
import com.speedata.xu.myapplication.db.dao.BaseInforDao;
import com.speedata.xu.myapplication.db.dao.CheckDetailInforDao;
import com.speedata.xu.myapplication.db.dao.CheckInforDao;
import com.speedata.xu.myapplication.db.dao.FloorInforDao;

/**
 * Created by xu on 2016/4/7.
 */
public class SettingFragment extends BaseFragment implements View.OnClickListener {
    @Override
    public int setFragmentLayout() {
        return R.layout.setting_frag;
    }
    private Button btnAddUser;
    private Button btnRevise;
    private Button btnResetData;
    private Button btnChangeUser;
    private Button btnPrinter;
    private CustomerApplication application;
    private BaseInforDao baseInforDao;
    private CheckInforDao checkInforDao;
    private CheckDetailInforDao checkDetailInforDao;
    private FloorInforDao floorInforDao;
    private AlertDialog mDialog;

    ConActivity activity = (ConActivity) getActivity(); //这句代码不可删除或修改!

    @Override
    public void findById(View view) {

        application = (CustomerApplication) mActivity.getApplication();
        Context mContext = mActivity;
        baseInforDao = new BaseInforDao(mContext);
        checkDetailInforDao = new CheckDetailInforDao(mContext);
        checkInforDao = new CheckInforDao(mContext);
        floorInforDao = new FloorInforDao(mContext);

        btnAddUser = (Button) view.findViewById(R.id.setting_adduser_btn);
        btnRevise = (Button) view.findViewById(R.id.setting_revise_btn);
        btnResetData = (Button) view.findViewById(R.id.setting_resetdata_btn);
        btnChangeUser = (Button) view.findViewById(R.id.setting_changeuser_btn);
        btnPrinter = (Button) view.findViewById(R.id.setting_printer_btn);

        btnAddUser.setOnClickListener(this);
        btnRevise.setOnClickListener(this);
        btnResetData.setOnClickListener(this);
        btnChangeUser.setOnClickListener(this);
        btnPrinter.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

        if (v == btnAddUser) {
            AddUserFragment fragment = new AddUserFragment();
            openFragment(fragment);
        } else if (v == btnRevise) {
            ReviseFragment fragment = new ReviseFragment();
            openFragment(fragment);
        } else if (v == btnResetData) {
            DialogButtonOnClickListener dialogButtonOnClickListener = new DialogButtonOnClickListener();

            mDialog = new AlertDialog.Builder(mActivity)
                    .setTitle(R.string.set_clear)
                    .setMessage(R.string.set_sure_ask)
                    .setPositiveButton(R.string.sure, dialogButtonOnClickListener)
                    .setNegativeButton(R.string.miss, dialogButtonOnClickListener)
                    .show();



        } else if (v == btnChangeUser) {
            application.setChangeuser(2);
            Intent intent = new Intent(mActivity, LoginActivity.class);
            startActivity(intent);
            Toast.makeText(mActivity, R.string.set_change_user, Toast.LENGTH_SHORT).show();
            mActivity.finish();
        } else if (v == btnPrinter) {
            Toast.makeText(mActivity, R.string.set_print, Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(mActivity, R.string.set_clearing, Toast.LENGTH_LONG).show();
                    mDialog.dismiss();

                    baseInforDao.imDeleteAll();
                    checkDetailInforDao.imDeleteAll();
                    checkInforDao.imDeleteAll();
                    floorInforDao.imDeleteAll();
                    application.setBaseInfo2(null);
                    application.setList(null);

                    Toast.makeText(mActivity, R.string.set_clear_yes, Toast.LENGTH_SHORT).show();

                    break;
                case DialogInterface.BUTTON_NEGATIVE: // 取消
                    // 取消显示对话框
                    mDialog.dismiss();

                    break;
            }
        }
    }


}
