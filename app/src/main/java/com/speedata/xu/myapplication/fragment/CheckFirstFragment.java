package com.speedata.xu.myapplication.fragment;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.printer.sdk.PrinterConstants;
import com.printer.sdk.PrinterInstance;
import com.speedata.xu.myapplication.R;
import com.speedata.xu.myapplication.adapter.CommonAdapter;
import com.speedata.xu.myapplication.adapter.ViewHolder;
import com.speedata.xu.myapplication.application.CustomerApplication;
import com.speedata.xu.myapplication.base.BaseFragment;
import com.speedata.xu.myapplication.db.bean.CheckDetailInfor;
import com.speedata.xu.myapplication.db.bean.CheckInfor;
import com.speedata.xu.myapplication.db.bean.FloorInfor;
import com.speedata.xu.myapplication.db.bean.OutputTxt;
import com.speedata.xu.myapplication.db.dao.CheckDetailInforDao;
import com.speedata.xu.myapplication.db.dao.CheckInforDao;
import com.speedata.xu.myapplication.db.dao.FloorInforDao;
import com.speedata.xu.myapplication.print.utils.BluetoothDeviceList;
import com.speedata.xu.myapplication.print.utils.GlobalContants;
import com.speedata.xu.myapplication.print.utils.PrefUtils;
import com.speedata.xu.myapplication.print.utils.PrintUtils;
import com.speedata.xu.myapplication.utils.FileUtils;
import com.speedata.xu.myapplication.utils.MyDateAndTime;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by xu on 2016/4/21.
 */
public class CheckFirstFragment extends BaseFragment implements View.OnClickListener {



    //蓝牙打印打印机连接

    public static final int CONNECT_DEVICE = 1;

    private int interfaceType = 0;
    private static String devicesAddress;
    private IntentFilter bluDisconnectFilter;
    public static PrinterInstance myPrinter;
    private static BluetoothDevice mDevice;
    private static  boolean  hasRegDisconnectReceiver = false;
    protected static final String TAG = "蓝牙打印机";
    public static boolean isConnected = false; // 蓝牙连接状态
    private ProgressDialog dialog;
    private ArrayAdapter<CharSequence> printType_adapter;
    private int printerId = 0;
    private BluetoothAdapter mBtAdapter;
    public static String devicesName = "未知设备";


    //盘点
    private ListView lvCheck;
    private TextView tvNumber;
    private TextView tvBluetooth;
    private CheckInforDao checkInforDao;
    private Context mContext;
    private List<CheckInfor> checkInforList;

    private List<CheckDetailInfor> checkDetailInfors;
    private CheckDetailInforDao checkDetailInforDao;

    private Button btnNew;
    private EditText etCheckName;
    private AlertDialog mDialog;
    private CustomerApplication application;
    //添加货层信息
    private FloorInforDao floorInforDao;
    private CheckInfor bean; //修改表名

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
        floorInforDao = new FloorInforDao(mContext);

        application = (CustomerApplication) mActivity.getApplication();
        btnNew.setOnClickListener(this);
        tvBluetooth.setOnClickListener(this);



        btnExplore = (Button) view.findViewById(R.id.inventory_explore_btn);
        btnDel = (TextView) view.findViewById(R.id.inventory_del_tv);
        btnExplore.setOnClickListener(this);
        btnDel.setOnClickListener(this);
        btnExplore.setText(R.string.onekey_explore);
        btnDel.setText(R.string.onekey_del);


        printType_adapter = ArrayAdapter.createFromResource(mActivity,
                R.array.interface_type, android.R.layout.simple_spinner_item);
        printType_adapter
                .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);


        // 初始化对话框
        dialog = new ProgressDialog(mContext);
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.setTitle(getString(R.string.connecting));
        dialog.setMessage(getString(R.string.please_wait));
        dialog.setIndeterminate(true);
        dialog.setCancelable(false);

        getSaveState();

        updateButtonState(isConnected);

        PrefUtils.getInt(mContext, GlobalContants.PAPERWIDTH, 58);

        checkInforList = getCheckListData();
        setAdapterMethod();

        mBtAdapter = BluetoothAdapter.getDefaultAdapter();

        lvCheck.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                CheckInfor bean = checkInforList.get(position);
                String checkTime = bean.getCheckTime();

                CheckFragment checkFragment = new CheckFragment();
                Bundle bundle = new Bundle();

                bundle.putString("CcheckTime", checkTime);

                checkFragment.setArguments(bundle);
                openFragment(checkFragment);


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

                mDialog = new AlertDialog.Builder(mActivity)
                        .setTitle(R.string.check_title_dialog)
                        .setNeutralButton(R.string.check_print, dialogButtonOnClickListener)
                        .setPositiveButton(R.string.check_export, dialogButtonOnClickListener)
                        .setNegativeButton(R.string.check_del, dialogButtonOnClickListener)
                        .show();

                return true;
            }
        });


    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        if (myPrinter != null) {
            myPrinter.closeConnection();
            myPrinter = null;
            Log.i(TAG, getString(R.string.check_cut));
            if (interfaceType == 0 && hasRegDisconnectReceiver) {
                try {
                    mContext.unregisterReceiver(myReceiver);
                    hasRegDisconnectReceiver = false;
//						Log.i(TAG, "关闭了广播！");
                } catch (IllegalArgumentException e) {
                    e.printStackTrace();
                }

            }
        }

        isConnected = false;
        PrefUtils.setBoolean(mActivity, GlobalContants.CONNECTSTATE, false);

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
                .inventory_item) {
            @Override
            public void convert(ViewHolder helper, CheckInfor item, final int position) {
                helper.setText(R.id.inventory_name_tv, item.getCheckName());
                helper.setText(R.id.inventory_time_tv, item.getCheckTime());

                //   helper.setText(R.id.goods_price_tv, item.getGoodsPrice());
                //   helper.setText(R.id.goods_number_tv, item.getGoodsCount());

                helper.getView(R.layout.inventory_item);


                //修改表单名称
                TextView tvChange = helper.getView(R.id.inventory_change_tv);

                tvChange.setOnClickListener(new TextView.OnClickListener() {
                    public void onClick(View v) {
                        bean = new CheckInfor();
                        bean = checkInforList.get(position);

                        CheckFirstFragment.DialogChangeButtonOnClickListener dialogButtonOnClickListener
                                = new CheckFirstFragment.DialogChangeButtonOnClickListener();
                        etCheckName = new EditText(mActivity);
                        mDialog = new AlertDialog.Builder(mActivity)
                                .setTitle(R.string.dialog_title_name)
                                .setView(etCheckName)
                                .setPositiveButton(R.string.dialog_sure, dialogButtonOnClickListener)
                                .setNegativeButton(R.string.dialog_miss, dialogButtonOnClickListener)
                                .show();
                        etCheckName.append(bean.getCheckName());

                    }
                });

            }
        };
        lvCheck.setAdapter(adapter);
    }


    @Override
    public void onClick(View v) {
        if (v == btnNew) {
            DialogButtonOnClickListener dialogButtonOnClickListener = new DialogButtonOnClickListener();
            etCheckName = new EditText(mActivity);
            mDialog = new AlertDialog.Builder(mActivity)
                    .setTitle(R.string.check_new_list)
                    .setView(etCheckName)
                    .setPositiveButton(R.string.check_sure, dialogButtonOnClickListener)
                    .setNegativeButton(R.string.check_miss, dialogButtonOnClickListener)
                    .show();
        } else if (v == tvBluetooth) {
            openBluetooth();

        } else if (v == btnExplore) {
            OneKeyExploreBeforeSure dialogButtonOnClickListener = new OneKeyExploreBeforeSure();
            mDialog = new AlertDialog.Builder(mActivity)
                    .setTitle(R.string.dialog_title_explore)
                    .setPositiveButton(R.string.check_sure, dialogButtonOnClickListener)
                    .setNegativeButton(R.string.check_miss, dialogButtonOnClickListener)
                    .show();


        } else if (v == btnDel) {
            OneKeyDelBeforeSure dialogButtonOnClickListener = new OneKeyDelBeforeSure();
            mDialog = new AlertDialog.Builder(mActivity)
                    .setTitle(R.string.dialog_title_del)
                    .setPositiveButton(R.string.check_sure, dialogButtonOnClickListener)
                    .setNegativeButton(R.string.check_miss, dialogButtonOnClickListener)
                    .show();


        }

    }

    private void openBluetooth() {

        Log.i(TAG, getString(R.string.check_connect) + isConnected);
        if (!isConnected) {
            switch (interfaceType) {
                case 0:// kuetooth
                    new AlertDialog.Builder(mActivity)
                            .setTitle(R.string.str_message)
                            .setMessage(R.string.str_connlast)
                            .setPositiveButton(R.string.yesconn,
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(
                                                DialogInterface arg0, int arg1) {
                                            // 重新连接
                                            if (!(mBtAdapter == null)) {
                                                // 判断设备蓝牙功能是否打开
                                                if (!mBtAdapter.isEnabled()) {
                                                    // 打开蓝牙功能
                                                    Intent enableIntent = new Intent(
                                                            BluetoothAdapter.ACTION_REQUEST_ENABLE);
                                                    startActivity(enableIntent);
                                                } else {
                                                    // mDevice
                                                    devicesAddress = PrefUtils
                                                            .getString(
                                                                    mContext,
                                                                    GlobalContants.DEVICEADDRESS,
                                                                    "");

                                                    if (devicesAddress == null
                                                            || devicesAddress
                                                            .length() <= 0) {
                                                        Toast.makeText(
                                                                mActivity,
                                                                R.string.check_first_blue,
                                                                Toast.LENGTH_SHORT)
                                                                .show();
                                                    } else {
                                                        connect2BlueToothdevice();
                                                    }
                                                }
                                            }

                                        }
                                    })
                            .setNegativeButton(R.string.str_resel,
                                    new DialogInterface.OnClickListener() {

                                        @Override
                                        public void onClick(
                                                DialogInterface dialog,
                                                int which) {
                                            if (!(mBtAdapter == null)) {
                                                // 判断设备蓝牙功能是否打开
                                                if (!mBtAdapter.isEnabled()) {
                                                    // 打开蓝牙功能
                                                    Intent enableIntent = new Intent(
                                                            BluetoothAdapter.ACTION_REQUEST_ENABLE);
                                                    startActivity(enableIntent);
                                                    Intent intent = new Intent(
                                                            mContext,
                                                            BluetoothDeviceList.class);
                                                    startActivityForResult(
                                                            intent,
                                                            CONNECT_DEVICE);
                                                } else {
                                                    Intent intent = new Intent(
                                                            mContext,
                                                            BluetoothDeviceList.class);
                                                    startActivityForResult(
                                                            intent,
                                                            CONNECT_DEVICE);

                                                }

                                            }

                                        }

                                    }).show();
                    break;

            }
        } else {

            if (myPrinter != null) {
                myPrinter.closeConnection();
                myPrinter = null;
                Log.i(TAG, getString(R.string.check_cut));
                if (interfaceType == 0 && hasRegDisconnectReceiver) {
                    try {
                        mContext.unregisterReceiver(myReceiver);
                        hasRegDisconnectReceiver = false;
//						 Log.i(TAG, "关闭了广播！");
                    } catch (IllegalArgumentException e) {
                        e.printStackTrace();
                    }

                }
            }

            isConnected = false;
            PrefUtils.setBoolean(mActivity,
                    GlobalContants.CONNECTSTATE, false);
        }



    }

    private void connect2BlueToothdevice() {
        dialog.show();
        mDevice = BluetoothAdapter.getDefaultAdapter().getRemoteDevice(
                devicesAddress);

        myPrinter = PrinterInstance.getPrinterInstance(mDevice, mHandler);
        if (mDevice.getBondState() == BluetoothDevice.BOND_NONE) { // 未绑定
            // Intent intent = new Intent(Settings.ACTION_BLUETOOTH_SETTINGS);
            // mContext.startActivity(intent);
            IntentFilter boundFilter = new IntentFilter();
            boundFilter.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
            mContext.registerReceiver(boundDeviceReceiver, boundFilter);
            pairOrConnect(true);
        } else {
            pairOrConnect(false);
        }

    }


    private void pairOrConnect(boolean pair) {
        if (pair) {
            IntentFilter boundFilter = new IntentFilter(
                    BluetoothDevice.ACTION_BOND_STATE_CHANGED);
            mContext.registerReceiver(boundDeviceReceiver, boundFilter);
            boolean success = false;
            try {
                Method createBondMethod = BluetoothDevice.class
                        .getMethod("createBond");
                success = (Boolean) createBondMethod.invoke(mDevice);

            } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException e) {
                e.printStackTrace();
            }
            Log.i(TAG, getString(R.string.check_success_blue) + success);

        } else {
            new ConnectThread().start();

        }
    }

    private BroadcastReceiver boundDeviceReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            if (BluetoothDevice.ACTION_BOND_STATE_CHANGED.equals(action)) {
                BluetoothDevice device = intent
                        .getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                if (!mDevice.equals(device)) {
                    return;
                }
                switch (device.getBondState()) {
                    case BluetoothDevice.BOND_BONDING:
                        Log.i(TAG, getString(R.string.check_bound));
                        break;
                    case BluetoothDevice.BOND_BONDED:
                        Log.i(TAG, getString(R.string.check_bound_success));
                        // if bound success, auto init BluetoothPrinter. open
                        // connect.
                        mContext.unregisterReceiver(boundDeviceReceiver);
                        dialog.show();
                        // 配对完成开始连接
                        if (myPrinter != null) {
                            new ConnectThread().start();
                        }

                        break;
                    case BluetoothDevice.BOND_NONE:
                        mContext.unregisterReceiver(boundDeviceReceiver);
                        Log.i(TAG, getString(R.string.check_bound_cancel));
                        break;
                    default:
                        break;
                }

            }
        }
    };


    private class ConnectThread extends Thread {
        @Override
        public void run() {

            if (myPrinter != null) {

                isConnected = myPrinter.openConnection();
            }
        }
    }

    /**
     * 新建盘点单的对话框的按钮点击事件
     */
    private class DialogButtonOnClickListener implements DialogInterface.OnClickListener {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            switch (which) {
                case DialogInterface.BUTTON_POSITIVE: // 确定
                    String txtName = etCheckName.getText().toString();

                    newCheckList(txtName); //新建盘点单

                    break;
                case DialogInterface.BUTTON_NEGATIVE: // 取消
                    // 取消显示对话框
                    mDialog.dismiss();
                    closeDiaolg();
                    break;

            }
        }
    }


    /**
     * 修改盘点单表名的的对话框的按钮点击事件
     */
    private class DialogChangeButtonOnClickListener implements DialogInterface.OnClickListener {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            switch (which) {
                case DialogInterface.BUTTON_POSITIVE: // 确定
                    String txtName = etCheckName.getText().toString();

                    changeCheckList(txtName); //修改盘点单名

                    break;
                case DialogInterface.BUTTON_NEGATIVE: // 取消
                    // 取消显示对话框
                    mDialog.dismiss();
                    closeDiaolg();
                    break;
            }
        }


        private void changeCheckList(String txtName) {
            //修改单名及数据库信息
            openDiaolg();

            checkInforList = getCheckListData();
            //判断表单名不能为空和有空格
            if (txtName.length() == 0) {
                Toast.makeText(mActivity, R.string.not_null, Toast.LENGTH_SHORT).show();
                return;
            }

            if (txtName.contains(" ")) {
                Toast.makeText(mActivity, R.string.not_kongge, Toast.LENGTH_SHORT).show();
                return;
            }

            for (int i = 0; i < checkInforList.size(); i++) {
                CheckInfor checkInfor = new CheckInfor();
                checkInfor = checkInforList.get(i);
                String name = checkInfor.getCheckName();
                if (name.equals(txtName)) {
                    Toast.makeText(mActivity, R.string.toast_name, Toast.LENGTH_SHORT).show();
                    return;
                }
            }

            mDialog.dismiss();
            closeDiaolg();

            //只修改那个点击的名字
            checkInforDao.imRawQuery("update CheckInfor set CheckName=? where CheckName=? and CheckTime=?",
                    new String[]{txtName, bean.getCheckName(), bean.getCheckTime()}, CheckInfor.class);

            checkInforList = getCheckListData();
            Toast.makeText(mContext, R.string.toast_success, Toast.LENGTH_SHORT).show();
            setAdapterMethod();

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

                    try {
                        FileUtils fileUtils = new FileUtils();
                        int h = fileUtils.outputfile(getOutputList(), createFilename());
                        if (h == 1) {

                            String outC = fileUtils.outCount + "";
                            ExploreSure dialogButtonOnClickListener = new ExploreSure();

                            mDialog = new AlertDialog.Builder(mActivity)
                                    .setTitle(getString(R.string.success_title) + outC)
                                    .setPositiveButton(R.string.sure2, dialogButtonOnClickListener)
                                    .show();
                        }

                    } catch (IOException e) {
                        e.printStackTrace();
                    }


                    break;
                case DialogInterface.BUTTON_NEGATIVE: // 删除表单
                    // 取消显示对话框
                    mDialog.dismiss();


                    DelBeforeSure dialogButtonOnClickListener = new DelBeforeSure();

                    mDialog = new AlertDialog.Builder(mActivity)
                            .setTitle(R.string.sure_del)
                            .setPositiveButton(R.string.check_sure, dialogButtonOnClickListener)
                            .setNegativeButton(R.string.check_miss, dialogButtonOnClickListener)
                            .show();

                    break;

                case DialogInterface.BUTTON_NEUTRAL://打印表单
                    if (!isConnected) {
                        Toast.makeText(mActivity, R.string.check_connect_fail, Toast.LENGTH_SHORT).show();
                        mDialog.dismiss();
                        return;
                    }
                    String time = application.getCheckTime();
                    checkDetailInfors = checkDetailInforDao.imQueryList("CheckID=?", new String[]{time});
                    String name = application.getTxtName();
                    PrintUtils.printTxt(name, checkDetailInfors, myPrinter);

                    Toast.makeText(mContext, R.string.check_print_success, Toast.LENGTH_SHORT).show();
                    break;
            }

        }

    }



    /**
     * 导出后确认数量的对话框的按钮点击事件
     */
    private class ExploreSure implements DialogInterface.OnClickListener {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            switch (which) {
                case DialogInterface.BUTTON_POSITIVE: // 确定
                    // 取消显示对话框
                    mDialog.dismiss();

                    break;
                case DialogInterface.BUTTON_NEGATIVE: // 取消
                    // 取消显示对话框
                    mDialog.dismiss();

                    break;

            }
        }
    }



        /**
         * 删除盘点单前确认的对话框的按钮点击事件
         */
        private class DelBeforeSure implements DialogInterface.OnClickListener {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case DialogInterface.BUTTON_POSITIVE: // 确定

                        checkInforDao.imDelete("CheckTime=?", new String[]{application.getCheckTime()});
                        mDialog.dismiss();
                        checkInforList = getCheckListData();
                        setAdapterMethod();
                        Toast.makeText(mContext, getString(R.string.check_del_list) + application.getTxtName(), Toast.LENGTH_SHORT).show();

                        break;
                    case DialogInterface.BUTTON_NEGATIVE: // 取消
                        // 取消显示对话框
                        mDialog.dismiss();

                        break;

                }
            }
        }


    /**
     * 一键删除盘点单前确认的对话框的按钮点击事件
     */
    private class OneKeyDelBeforeSure implements DialogInterface.OnClickListener {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            switch (which) {
                case DialogInterface.BUTTON_POSITIVE: // 确定

                    mDialog.dismiss();
                    checkDetailInforDao.imDeleteAll();
                    checkInforDao.imDeleteAll();
                    floorInforDao.imDeleteAll();
                    application.setBaseInfo2(null);
                    application.setList(null);
                    checkInforList = getCheckListData();
                    setAdapterMethod();
                    Toast.makeText(mContext, R.string.del_all, Toast.LENGTH_SHORT).show();

                    break;
                case DialogInterface.BUTTON_NEGATIVE: // 取消
                    // 取消显示对话框
                    mDialog.dismiss();

                    break;

            }
        }
    }



    /**
     * 一键导出盘点单前确认的对话框的按钮点击事件
     */
    private class OneKeyExploreBeforeSure implements DialogInterface.OnClickListener {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            switch (which) {
                case DialogInterface.BUTTON_POSITIVE: // 确定
                    boolean b = false;
                    int c = checkInforList.size();
                    for (int i = 0; i < c; i++) {
                        CheckInfor bean = checkInforList.get(i);
                        String checktime = bean.getCheckTime();
                        String checkname = bean.getCheckName();
                        application.setTxtName(checkname);
                        application.setCheckTime(checktime);

                        try {
                            FileUtils fileUtils = new FileUtils();
                            int h = fileUtils.outputfile(getOutputList(), createFilename());
                            if (h == 1) {
                               if (i == c - 1) {
                                   b = true;
                               }
                            }

                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                    }

                    if (!b) {
                        if (c == 0) {
                            Toast.makeText(mContext, R.string.no_list, Toast.LENGTH_SHORT).show();
                            return;
                        }
                        Toast.makeText(mContext, R.string.failed, Toast.LENGTH_SHORT).show();
                        return;
                    }

                    mDialog.dismiss();
                    //显示导出信息对话框
                    String outC = c + "";
                    ExploreSure dialogButtonOnClickListener = new ExploreSure();

                    mDialog = new AlertDialog.Builder(mActivity)
                            .setTitle(getString(R.string.explore_success) + outC + getString(R.string.end))
                            .setPositiveButton(R.string.sure3, dialogButtonOnClickListener)
                            .show();


                    break;
                case DialogInterface.BUTTON_NEGATIVE: // 取消
                    // 取消显示对话框
                    mDialog.dismiss();

                    break;

            }
        }
    }



    //创建在数据库中的新盘点表
    private void newCheckList(String txtName) {
        openDiaolg();

        String checktime = MyDateAndTime.getTimeString();

        String userID = application.getID();

        checkInforList = getCheckListData();

        //判断表单名不能为空和有空格
        if (txtName.length() == 0) {
            Toast.makeText(mActivity, R.string.not_null2, Toast.LENGTH_SHORT).show();
            return;
        }

        if (txtName.contains(" ")) {
            Toast.makeText(mActivity, R.string.not_kongge2, Toast.LENGTH_SHORT).show();
            return;
        }


        for (int i = 0; i < checkInforList.size(); i++) {
            CheckInfor checkInfor;
            checkInfor = checkInforList.get(i);
            String name = checkInfor.getCheckName();
            if (name.equals(txtName)) {
                Toast.makeText(mActivity, R.string.check_had_name, Toast.LENGTH_SHORT).show();

                return;
            }
        }

        mDialog.dismiss();
        closeDiaolg();

        //添加货层信息
        FloorInfor fBean = new FloorInfor();
        fBean.setCheckID(checktime);
        fBean.setFloor(getString(R.string.number_0));
        floorInforDao.imInsert(fBean);


        CheckInfor bean = new CheckInfor();
        bean.setUserID(userID);
        bean.setCheckName(txtName);
        bean.setCheckTime(checktime);
        checkInforDao.imInsert(bean);

        checkInforList = getCheckListData();
        Toast.makeText(mContext, R.string.check_new_success, Toast.LENGTH_SHORT).show();
        setAdapterMethod();


    }


    //显示与关闭dialog
    public void openDiaolg() {
        try {
            Field field = mDialog.getClass().getSuperclass().getDeclaredField("mShowing");
            field.setAccessible(true);
            //   将mShowing变量设为false，表示对话框已关闭
            field.set(mDialog, false); //不关闭对话框
        } catch (SecurityException | IllegalArgumentException | NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }
    //显示与关闭dialog
    public void closeDiaolg() {
        try {
            Field field = mDialog.getClass().getSuperclass().getDeclaredField("mShowing");
            field.setAccessible(true);
            //   将mShowing变量设为false，表示对话框已关闭
            field.set(mDialog, true); //关闭对话框
        } catch (SecurityException | IllegalArgumentException | IllegalAccessException | NoSuchFieldException e) {
            e.printStackTrace();
        }
    }



    //创建导出文件的名字
    public String createFilename() throws IOException {
        return  getString(R.string.export_path_) + application.getTxtName() + getString(R.string.txt);


    }

    //得到需要导出的文件List
    public List<OutputTxt> getOutputList() {
        String time = application.getCheckTime();
        checkDetailInfors = checkDetailInforDao.imQueryList("CheckID=?", new String[]{time});
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
            if (bean.getGoodsNum().length() == 3) {
                outputTxts.remove(obean);
            }
        }

        return outputTxts;
    }




    // 用于接受连接状态消息的 Handler
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case PrinterConstants.Connect.SUCCESS:
                    isConnected = true;
                    GlobalContants.ISCONNECTED = isConnected;

                    if (interfaceType == 0) {
                        PrefUtils.setString(mContext, GlobalContants.DEVICEADDRESS,
                                devicesAddress);
                        bluDisconnectFilter = new IntentFilter();
                        bluDisconnectFilter.addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED);
                        mContext.registerReceiver(myReceiver, bluDisconnectFilter);
                        hasRegDisconnectReceiver = true;
                    }

                    break;
                case PrinterConstants.Connect.FAILED:
                    isConnected = false;
                    Toast.makeText(mContext, R.string.conn_failed,
                            Toast.LENGTH_SHORT).show();
                    Log.i(TAG, getString(R.string.check_connect_failed));
                    break;
                case PrinterConstants.Connect.CLOSED:
                    isConnected = false;
                    GlobalContants.ISCONNECTED = isConnected;

                    Toast.makeText(mContext, R.string.conn_closed,
                            Toast.LENGTH_SHORT).show();
                    Log.i(TAG, getString(R.string.check_connect_close));
                    break;
                case PrinterConstants.Connect.NODEVICE:
                    isConnected = false;
                    Toast.makeText(mContext, R.string.conn_no, Toast.LENGTH_SHORT)
                            .show();
                    break;
                case 0:
                    Toast.makeText(mContext, R.string.check_print_good, Toast.LENGTH_SHORT).show();
                    break;
                case -1:
                    Toast.makeText(mContext, R.string.check_print_notgood, Toast.LENGTH_SHORT).show();
                    vibrator();
                    break;
                case -2:
                    Toast.makeText(mContext, R.string.check_paper_null, Toast.LENGTH_SHORT).show();
                    vibrator();
                    break;
                case -3:
                    Toast.makeText(mContext, R.string.check_print_open, Toast.LENGTH_SHORT).show();
                    vibrator();
                    break;
                default:
                    break;
            }

            updateButtonState(isConnected);

            if (dialog != null && dialog.isShowing()) {
                dialog.dismiss();
            }
        }

    };





    public BroadcastReceiver myReceiver = new BroadcastReceiver()
    {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            BluetoothDevice device = intent
                    .getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

            if (action.equals(BluetoothDevice.ACTION_ACL_DISCONNECTED)) {

                if (device != null && myPrinter != null
                        && isConnected && device.equals(mDevice)) {
                    myPrinter.closeConnection();
                    mHandler.obtainMessage(PrinterConstants.Connect.CLOSED).sendToTarget();
                }


            }

        }
    };
    int count = 0;

    public void vibrator() {
        count++;
        PrefUtils.setInt(mContext, "count3", count);
        Log.e(TAG, "" + count);

        MediaPlayer player = new MediaPlayer().create(mContext, R.raw.test);
        MediaPlayer player2 = new MediaPlayer().create(mContext, R.raw.beep);

        player.start();
        player2.start();
    }


    private void updateButtonState(boolean isConnected) {
        if (isConnected) {
            tvBluetooth.setText(R.string.disconnect);
        } else {
            tvBluetooth.setText(R.string.connect);
        }
        PrefUtils.setBoolean(mActivity, GlobalContants.CONNECTSTATE,
                isConnected);

    }




    private void getSaveState() {


        Log.d(TAG, getString(R.string.check_get_savestate));
        
        isConnected = PrefUtils.getBoolean(mActivity,
                GlobalContants.CONNECTSTATE, false);
        printerId = PrefUtils.getInt(mContext, GlobalContants.PRINTERID, 0);
        interfaceType = PrefUtils.getInt(mContext,
                GlobalContants.INTERFACETYPE, 0);

        Log.i(TAG, getString(R.string.check_is_connect) + isConnected);
    }


    // 安卓3.1以后才有权限操作USB
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR1)
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode != Activity.RESULT_OK)
            return;
        if (requestCode == CONNECT_DEVICE) { // 连接设备

            if (interfaceType == 0) {

                devicesAddress = data.getExtras().getString(
                        BluetoothDeviceList.EXTRA_DEVICE_ADDRESS);
                devicesName = data.getExtras().getString(
                        BluetoothDeviceList.EXTRA_DEVICE_NAME);
                Log.i(getString(R.string.check_tag_name), getString(R.string.check_module_name) + devicesName + getString(R.string.check_module_address) + devicesAddress);
                connect2BlueToothdevice();
            }

        }

    }


}







