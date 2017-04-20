package com.speedata.xu.myapplication.fragment;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.speedata.xu.myapplication.R;
import com.speedata.xu.myapplication.adapter.CommonAdapter;
import com.speedata.xu.myapplication.adapter.ViewHolder;
import com.speedata.xu.myapplication.application.CustomerApplication;
import com.speedata.xu.myapplication.base.BaseScanFragment;
import com.speedata.xu.myapplication.db.bean.BaseInfor;
import com.speedata.xu.myapplication.db.bean.CheckDetailInfor;
import com.speedata.xu.myapplication.db.dao.BaseInforDao;
import com.speedata.xu.myapplication.db.dao.CheckDetailInforDao;
import com.speedata.xu.myapplication.utils.MyDateAndTime;
import com.speedata.xu.myapplication.utils.PlaySoundPool;
import com.speedata.xu.myapplication.utils.ToolCommon;

import java.util.List;

/**
 * Created by xu on 2016/4/7.
 */
public class ChangeFragment extends BaseScanFragment implements View.OnClickListener {
    @Override
    public int setFragmentLayout() {
        return R.layout.change_frag;
    }
    private EditText etInput;
    private RadioButton rbtnSearch;
    private RadioButton rbtnAdd;
    private Button btnConfirm;
    private Button btnSubChange;
    private TextView tvDetailed;
    private TextView tvNumberCount;
    private ListView lvchange;
    private CheckDetailInforDao checkDetailInforDao;
    private List<CheckDetailInfor> checkDetailInfors;
    private Context mContext;
    private CustomerApplication application;
    private CommonAdapter<CheckDetailInfor> adapter;
    private AlertDialog mDialog;

    private BaseInforDao baseInforDao;


    @Override
    public void findById(View view) {

        mContext = mActivity;
        checkDetailInforDao = new CheckDetailInforDao(mContext);
        baseInforDao = new BaseInforDao(mContext);
        application = (CustomerApplication) mActivity.getApplication();

        etInput = (EditText) view.findViewById(R.id.change_input_et);
        rbtnSearch = (RadioButton) view.findViewById(R.id.change_search_rbtn);
        rbtnAdd = (RadioButton) view.findViewById(R.id.change_add_rbtn);
        btnConfirm = (Button) view.findViewById(R.id.change_confirm_btn);
        TextView tvCheckChange = (TextView) view.findViewById(R.id.change_check_tv);
        tvDetailed = (TextView) view.findViewById(R.id.change_detailed_tv);
        tvNumberCount = (TextView) view.findViewById(R.id.change_number_tv);
        lvchange = (ListView) view.findViewById(R.id.change_list_lv);
        btnSubChange = (Button) view.findViewById(R.id.change_subchange_btn);


        String cname = getArguments().getString("C2checkName");
        tvCheckChange.setText(cname);


        btnSubChange.setOnClickListener(this);
        rbtnSearch.setOnClickListener(this);
        rbtnAdd.setOnClickListener(this);
        btnConfirm.setOnClickListener(this);

        String ctime = getArguments().getString("C2checkTime");
        checkDetailInfors = checkDetailInforDao.imQueryList("CheckID=?", new String[]{ctime});
        String a = checkDetailInfors.size() + "";
        tvDetailed.setText(a);
        setAdapterMethod();

    }

    @Override
    public void onClick(View v) {

        if (v == btnConfirm) {
            String input = etInput.getText().toString();
            if (rbtnSearch.isChecked()) {

                if ("".equals(input)) {
                    Toast.makeText(mContext, R.string.change_number_input, Toast.LENGTH_SHORT).show();
                    return;
                }
   //             String ctime = getArguments().getString("C2checkTime");

  //              checkDetailInfors=checkDetailInforDao.imQueryList("CheckID=?",new String[]{ctime});

                List<CheckDetailInfor> checkDetailInforlist;
                String ctime = getArguments().getString("C2checkTime");
                checkDetailInforlist = checkDetailInforDao.imQueryList("CheckID=? and GoodsNum=?", new String[]{ctime,input});

                if (checkDetailInforlist.size() == 0) {

                    Toast.makeText(mContext, R.string.change_null_good, Toast.LENGTH_SHORT).show();
                    PlaySoundPool.getPlaySoundPool(mActivity).playError();
                    return;
                }

                for (int i = 0; i < checkDetailInfors.size(); i++) {
                    CheckDetailInfor bean;
                    bean = checkDetailInfors.get(i);
                    if (bean.getGoodsNum().equals(input)) {
                        lvchange.setSelection(i);
                        Toast.makeText(mContext, R.string.change_number_jump, Toast.LENGTH_SHORT).show();
                    }

                }

            } else if (rbtnAdd.isChecked()) {

                if ("".equals(input)) {
                    Toast.makeText(mContext, R.string.change_add_number, Toast.LENGTH_SHORT).show();
                    return;
                }
                List<BaseInfor> baseInfors = baseInforDao.imQueryList("GoodsNum=?", new String[]{input});
                if (baseInfors.isEmpty()) {
                    Toast.makeText(mContext, R.string.change_new_number, Toast.LENGTH_SHORT).show();


                    String ctime = getArguments().getString("C2checkTime");

                       

                    CheckDetailInfor cbean = new CheckDetailInfor();
                    cbean.setGoodsCount(getString(R.string.number_1));
                    cbean.setGoodsNum(input);
                    cbean.setGoodsName(getString(R.string.change_out_list));
                    cbean.setGoodsPrice(getString(R.string.number_0));

                    cbean.setCheckDetailTime(MyDateAndTime.getTimeString());
                    cbean.setCheckID(ctime);

                    checkDetailInfors.add(cbean);
                    adapter.notifyDataSetChanged();

                    Toast.makeText(mContext, R.string.change_add_success, Toast.LENGTH_SHORT).show();
                    return;
                }


                String ctime = getArguments().getString("C2checkTime");

             
                BaseInfor bean;
                bean = baseInfors.get(0);
                CheckDetailInfor cbean = new CheckDetailInfor();
                cbean.setGoodsCount(bean.getGoodsCount());
                cbean.setGoodsNum(bean.getGoodsNum());
                cbean.setGoodsName(bean.getGoodsName());
                cbean.setGoodsPrice(bean.getGoodsPrice());
                cbean.setCheckDetailTime(MyDateAndTime.getTimeString());
                cbean.setCheckID(ctime);

                checkDetailInfors.add(cbean);
                adapter.notifyDataSetChanged();
                Toast.makeText(mContext, R.string.change_success_add, Toast.LENGTH_SHORT).show();



            }
        } else if (v == btnSubChange) {
            //确认修改

            String ctime = getArguments().getString("C2checkTime");
            checkDetailInforDao.imDelete("CheckID=?", new String[]{ctime});

            checkDetailInforDao.imInsertList(checkDetailInfors);

            Toast.makeText(mContext, R.string.change_change_success, Toast.LENGTH_SHORT).show();


        }
    }



    private void setAdapterMethod() {
        etInput.setText("");
       // final MyDateAndTime myDateAndTime=new MyDateAndTime();
        final String time = MyDateAndTime.getTimeString();

        adapter = new CommonAdapter<CheckDetailInfor>(mActivity, checkDetailInfors, R.layout
                .check_item) {


            @Override
            public void convert(final ViewHolder helper, final CheckDetailInfor item, final int position) {
                helper.setText(R.id.check_num_tv, item.getGoodsNum());
                helper.setText(R.id.check_name_tv, item.getGoodsName());
                helper.setText(R.id.check_price_tv, item.getGoodsPrice());
                helper.setText(R.id.check_sum_tv, item.getGoodsCount());
                helper.setText(R.id.check_time_tv, item.getCheckDetailTime());

                helper.getView(R.layout.check_item);

                TextView tvAdd = helper.getView(R.id.check_add_tv);

                tvAdd.setOnClickListener(new TextView.OnClickListener() {
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
                        b++;
                        count = b + "";
                        bean.setGoodsCount(count);
                        bean.setCheckDetailTime(time);
                        checkDetailInfors.get(position).setGoodsCount(count);
                        checkDetailInfors.get(position).setCheckDetailTime(time);
                        String c = checkDetailInfors.size() + "";
                        tvDetailed.setText(c);
                        helper.setText(R.id.check_time_tv, item.getCheckDetailTime());
                        helper.setText(R.id.check_sum_tv, item.getGoodsCount());
                        tvNumberCount.setText(count);

                    }
                });


                TextView tvSub = helper.getView(R.id.check_sub_tv);

                tvSub.setOnClickListener(new  TextView.OnClickListener() {
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
                        bean.setCheckDetailTime(time);
                        checkDetailInfors.get(position).setGoodsCount(count);
                        checkDetailInfors.get(position).setCheckDetailTime(time);
                        helper.setText(R.id.check_time_tv, item.getCheckDetailTime());
                        helper.setText(R.id.check_sum_tv, item.getGoodsCount());
                        String d = checkDetailInfors.size() + "";
                        tvDetailed.setText(d);
                        tvNumberCount.setText(count);



                    }
                });

                TextView tvDelete = helper.getView(R.id.check_delete_tv);

                tvDelete.setOnClickListener(new TextView.OnClickListener() {
                    public void onClick(View v) {

                        CheckDetailInfor bean;

                        bean = checkDetailInfors.get(position);
                        application.setCheckDetailInfo2(bean);

                        DialogButtonOnClickListener dialogButtonOnClickListener = new DialogButtonOnClickListener();

                        mDialog = new AlertDialog.Builder(mActivity)
                                .setTitle(R.string.change_title_dialog)
                                .setPositiveButton(R.string.change_sure, dialogButtonOnClickListener)
                                .setNegativeButton(R.string.change_miss, dialogButtonOnClickListener)
                                .show();
                        lvchange.setSelection(position);

                    }
                });

                TextView tvPrice = helper.getView(R.id.check_price_tv);
                TextView tvCount = helper.getView(R.id.check_sum_tv);
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
        lvchange.setAdapter(adapter);
    }


    public void onGetBarcode(String barcode) {
        barcode = barcode.replace("\n", "").replace("\u0000", "").replace("\r", "");
        etInput.setText(barcode);
        rbtnSearch.isChecked();

        btnConfirm.performClick();

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
                    checkDetailInfors.remove(application.getCheckDetailInfor2());
                    String ctime = getArguments().getString("C2checkTime");
                    checkDetailInforDao.imDelete("CheckID=?", new String[]{ctime});

                    checkDetailInforDao.imInsertList(checkDetailInfors);
                    String e = checkDetailInfors.size() + "";
                    tvDetailed.setText(e);

                    adapter.notifyDataSetChanged();

                    Toast.makeText(mContext, R.string.change_del_success, Toast.LENGTH_SHORT).show();

                    break;
                case DialogInterface.BUTTON_NEGATIVE: // 取消
                    // 取消显示对话框
                    mDialog.dismiss();

                    break;
            }
        }
    }
}
