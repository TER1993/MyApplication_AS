package com.speedata.xu.myapplication.fragment;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.speedata.xu.myapplication.R;
import com.speedata.xu.myapplication.adapter.CommonAdapter;
import com.speedata.xu.myapplication.adapter.ViewHolder;
import com.speedata.xu.myapplication.application.CustomerApplication;
import com.speedata.xu.myapplication.base.BaseScanFragment;
import com.speedata.xu.myapplication.db.bean.BaseInfor;
import com.speedata.xu.myapplication.db.bean.CheckDetailInfor;
import com.speedata.xu.myapplication.db.bean.FloorInfor;
import com.speedata.xu.myapplication.db.dao.BaseInforDao;
import com.speedata.xu.myapplication.db.dao.CheckDetailInforDao;
import com.speedata.xu.myapplication.db.dao.FloorInforDao;
import com.speedata.xu.myapplication.utils.MyDateAndTime;
import com.speedata.xu.myapplication.utils.PlaySoundPool;
import com.speedata.xu.myapplication.utils.ToolCommon;

import java.util.List;

/**
 * Created by xu on 2016/4/7.
 */
public class CheckFragment extends BaseScanFragment implements View.OnClickListener {
    public CheckFragment() {
    }

    @Override
    public int setFragmentLayout() {
        return R.layout.check_frag;
    }

    private EditText etNum;
    private Button btnSearch;
    private Button btnSave;
    private Button btnFloor;
    private ListView lvcheck;
    private BaseInforDao baseInforDao;
    private Context mContext;
    private CommonAdapter<CheckDetailInfor> adapter;
    //明细表
    private CheckDetailInforDao checkDetailInforDao;
    private CustomerApplication application;
    private List<CheckDetailInfor> checkDetailInfors;
    private AlertDialog mDialog;
    private EditText etCount;
    private String floorNumber;
    private FloorInforDao floorInforDao;




    @Override
    public void findById(View view) {

        mContext = mActivity;
        baseInforDao = new BaseInforDao(mContext);
        checkDetailInforDao = new CheckDetailInforDao(mContext);
        floorInforDao = new FloorInforDao(mContext);
        application = (CustomerApplication) mActivity.getApplication();
        etNum = (EditText) view.findViewById(R.id.check_num_et);
        btnSearch = (Button) view.findViewById(R.id.check_search_btn);
        btnSave = (Button) view.findViewById(R.id.check_save_btn);
        btnFloor = (Button) view.findViewById(R.id.check_floor_btn);
        lvcheck = (ListView) view.findViewById(R.id.check_list_lv);
        //
        btnSearch.setOnClickListener(this);
        btnSave.setOnClickListener(this);
        btnFloor.setOnClickListener(this);

        String checkId = getArguments().getString("CcheckTime");
        checkDetailInfors = checkDetailInforDao.imQueryList("CheckID=?",new String[]{checkId});
        List<FloorInfor> floorInforList = floorInforDao.imQueryList("CheckID=?", new String[]{checkId});



        floorNumber = floorInforList.get(0).getFloor();
        if (checkDetailInfors.size() == 0) {
            btnFloor.performClick();
        }
        setAdapterMethod();
    }


    @Override
    public void onPause() {
        saveData();
        super.onPause();
    }

    @Override
    public void onClick(View v) {
        if (v == btnSearch) {
            searchInfor();
        } else if (v == btnSave) {
            saveData();
            closeFragment();
        } else if (v == btnFloor) {
        //处理货层
            int floorInt = Integer.parseInt(floorNumber);
            floorInt++;
            String floor;
            if (floorInt < 10) {
                floor=getString(R.string.number_0) + floorInt;
            } else {
                floor = floorInt + "";
            }
            floorNumber = floorInt + "";
            String checkid = getArguments().getString("CcheckTime");
            CheckDetailInfor bean = new CheckDetailInfor();
            floor = floor.replace("\n", "");
            floor = floor.replace("\u0000", "");
            bean.setGoodsName(floor);
            bean.setCheckID(checkid);
            bean.setGoodsNum(floor);
            bean.setGoodsCount(getString(R.string.number_0));
            bean.setGoodsPrice(getString(R.string.number_0));

            checkDetailInfors.add(bean);
            application.setCheckDetailInfo(bean);
            setAdapterMethod();
            lvcheck.setSelection(checkDetailInfors.size());

        }


    }

    private void saveData() {
        String checkid = getArguments().getString("CcheckTime");

        CheckDetailInfor cbean = application.getCheckDetailInfor();

        //保存货层信息
        floorInforDao.imDelete("CheckID=?", new String[]{checkid});
        FloorInfor fbean = new FloorInfor();
        fbean.setCheckID(checkid);
        fbean.setFloor(floorNumber);
        floorInforDao.imInsert(fbean);

        if (cbean == null) {
            Toast.makeText(mContext, R.string.check_change_null, Toast.LENGTH_SHORT);
            return;
        }

        checkDetailInforDao.imDelete("CheckID=?", new String[]{checkid});

        checkDetailInforDao.imInsertList(checkDetailInfors);

        Toast.makeText(mContext, R.string.check_success, Toast.LENGTH_SHORT).show();


    }

    private void searchInfor() {
        String number = etNum.getText().toString().trim();
        number = number.replace("\n", "");
        number = number.replace("\u0000", "");
        number = number.replace("\r", "");

        if ("".equals(number)) {
            Toast.makeText(mContext, R.string.check_floor_input, Toast.LENGTH_SHORT).show();
            return;
        }


        if (number.length() == 2) {
            for (int i = 0; i < checkDetailInfors.size(); i++) {
                CheckDetailInfor a = checkDetailInfors.get(i);
                String b = a.getGoodsName();
                if (b.equals(number)) {
                    lvcheck.setSelection(i);
                    Toast.makeText(mContext, R.string.check_floor_found, Toast.LENGTH_SHORT).show();
                    etNum.setText("");
                    return;
                }


            }
        }



        String checkid = getArguments().getString("CcheckTime");

        List<BaseInfor> baseInfors = baseInforDao.imQueryList("GoodsNum=?", new String[]{number});
        if (baseInfors.isEmpty()) {
            Toast.makeText(mContext, R.string.check_number_right, Toast.LENGTH_SHORT).show();
            PlaySoundPool.getPlaySoundPool(mActivity).playError();
            return;
        }
        BaseInfor bean;
        bean = baseInfors.get(0);

        CheckDetailInfor cbean = new CheckDetailInfor();
        cbean.setGoodsCount(bean.getGoodsCount());
        cbean.setCheckDetailTime(MyDateAndTime.getTimeString());
        cbean.setCheckID(checkid);
        cbean.setGoodsName(bean.getGoodsName());
        cbean.setGoodsNum(bean.getGoodsNum());
        cbean.setGoodsPrice(bean.getGoodsPrice());

        checkDetailInfors.add(cbean);

        application.setCheckDetailInfo(cbean);

        setAdapterMethod();
        lvcheck.setSelection(checkDetailInfors.size());
    }

    private void setAdapterMethod() {
        etNum.setText("");


        adapter = new CommonAdapter<CheckDetailInfor>(mActivity, checkDetailInfors, R.layout
                .check_item) {


            @Override
            public void convert(ViewHolder helper, CheckDetailInfor item, final int position) {
                helper.setText(R.id.check_num_tv, item.getGoodsNum());
                helper.setText(R.id.check_name_tv, item.getGoodsName());
                helper.setText(R.id.check_price_tv, item.getGoodsPrice());
                helper.setText(R.id.check_sum_tv, item.getGoodsCount());
                helper.setText(R.id.check_time_tv, item.getCheckDetailTime());


                helper.getView(R.layout.check_item);




                TextView tvCount = helper.getView(R.id.check_sum_tv);

                tvCount.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        application.setBposition(position);
                        DialogButtonOnClickListener2 dialogButtonOnClickListener = new DialogButtonOnClickListener2();
                        etCount = new EditText(mActivity);
                        mDialog = new AlertDialog.Builder(mActivity)
                                .setTitle(R.string.check_title_number)
                                .setView(etCount)
                                .setPositiveButton(R.string.check_sure_button, dialogButtonOnClickListener)
                                .setNegativeButton(R.string.check_miss_button, dialogButtonOnClickListener)
                                .show();

                        lvcheck.setSelection(position);
                    }
                });


                TextView tvAdd = helper.getView(R.id.check_add_tv);

                tvAdd.setOnClickListener(new TextView.OnClickListener() {
                    public void onClick(View v) {

                        CheckDetailInfor bean;
                        bean = checkDetailInfors.get(position);
                        String count = bean.getGoodsCount();
                        //判断String字符串是否为数字，否则不支持加减操作
                        if (!ToolCommon.isNumeric(count)) {
                            Toast.makeText(mContext, "此数量非纯数字，不支持加减操作", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        int b = Integer.parseInt(count);
                        b++;
                        count = b + "";
                        bean.setGoodsCount(count);
                        checkDetailInfors.get(position).setGoodsCount(count);

                        setAdapterMethod();
                        lvcheck.setSelection(position);
                        application.setCheckDetailInfo(bean);


                    }
                });


                TextView tvSub = helper.getView(R.id.check_sub_tv);

                tvSub.setOnClickListener(new TextView.OnClickListener() {
                    public void onClick(View v) {


                        CheckDetailInfor bean;
                        bean = checkDetailInfors.get(position);
                        String count = bean.getGoodsCount();
                        //判断String字符串是否为数字，否则不支持加减操作
                        if (!ToolCommon.isNumeric(count)) {
                            Toast.makeText(mContext, "此数量的格式，不支持加减操作", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        int b = Integer.parseInt(count);
                        if (b == 0) {
                            Toast.makeText(mContext, "货品数量不能小于0", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        b--;
                        count = b + "";
                        bean.setGoodsCount(count);

                        checkDetailInfors.get(position).setGoodsCount(count);
                        setAdapterMethod();
                        lvcheck.setSelection(position);
                        application.setCheckDetailInfo(bean);

                    }
                });

                TextView tvDelete = helper.getView(R.id.check_delete_tv);

                tvDelete.setOnClickListener(new TextView.OnClickListener() {
                    public void onClick(View v) {
                        CheckDetailInfor bean;
                        bean = checkDetailInfors.get(position);
                        application.setCheckDetailInfo(bean);

                        DialogButtonOnClickListener dialogButtonOnClickListener = new DialogButtonOnClickListener();

                        mDialog = new AlertDialog.Builder(mActivity)
                                .setTitle(getString(R.string.check_del_ask) + bean.getGoodsNum() + getString(R.string.check_ask_end))
                                .setPositiveButton(R.string.sure, dialogButtonOnClickListener)
                                .setNegativeButton(R.string.miss, dialogButtonOnClickListener)
                                .show();


                        lvcheck.setSelection(position);

                    }
                });

                TextView tvPrice = helper.getView(R.id.check_price_tv);
                if (item.getGoodsPrice().equals("0") && item.getGoodsCount().equals("0")) {
                    tvCount.setVisibility(View.INVISIBLE);
                    tvAdd.setVisibility(View.INVISIBLE);
                    tvSub.setVisibility(View.INVISIBLE);
                    tvPrice.setVisibility(View.INVISIBLE);
                } else {
                    tvCount.setVisibility(View.VISIBLE);
                    tvAdd.setVisibility(View.VISIBLE);
                    tvSub.setVisibility(View.VISIBLE);
                    tvPrice.setVisibility(View.VISIBLE);
                }



            }

        };

        lvcheck.setAdapter(adapter);
    }

    @Override
    public void onGetBarcode(String barcode) {
        barcode = barcode.replace("\n", "").replace("\u0000", "").replace("\r", "");
        etNum.setText(barcode);
//        btnSearch.performClick();
        searchInfor();
        PlaySoundPool.getPlaySoundPool(mActivity).playLaser();
    }


    /**
     * 退出时的对话框的按钮点击事件
     */
    private class DialogButtonOnClickListener implements DialogInterface.OnClickListener {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            switch (which) {
                case DialogInterface.BUTTON_POSITIVE: // 确定

                    String floor = application.getCheckDetailInfor().getGoodsNum();
                    int floorInt = Integer.parseInt(floorNumber);
                    if (floor.length() == 2) {
                        floorInt--;
                    }

                    floorNumber = floorInt + "";

                    checkDetailInfors.remove(application.getCheckDetailInfor());

                    adapter.notifyDataSetChanged();

                    Toast.makeText(mContext, R.string.check_del_success, Toast.LENGTH_SHORT).show();

                    break;
                case DialogInterface.BUTTON_NEGATIVE: // 取消
                    // 取消显示对话框
                    mDialog.dismiss();

                    break;
            }
        }
    }



    /**
     * 退出时的对话框的按钮点击事件
     */
    private class DialogButtonOnClickListener2 implements DialogInterface.OnClickListener {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            switch (which) {
                case DialogInterface.BUTTON_POSITIVE: // 确定

                    String countInput = etCount.getText().toString();
                    if ("".equals(countInput)) {
                        Toast.makeText(mContext, R.string.check_input_number, Toast.LENGTH_SHORT).show();
                        return;
                    }

                    int cposition = application.getBposition();

                    checkDetailInfors.get(cposition).setGoodsCount(countInput);
                    mDialog.dismiss();
                    adapter.notifyDataSetChanged();

                    Toast.makeText(mContext, R.string.check_changed, Toast.LENGTH_SHORT).show();

                    break;
                case DialogInterface.BUTTON_NEGATIVE: // 取消
                    // 取消显示对话框
                    mDialog.dismiss();

                    break;
            }
        }
    }

}
