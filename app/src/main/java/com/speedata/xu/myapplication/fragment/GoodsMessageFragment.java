package com.speedata.xu.myapplication.fragment;

import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.speedata.xu.myapplication.R;
import com.speedata.xu.myapplication.base.BaseFragment;

/**
 * Created by xu on 2016/4/14.
 */
public class GoodsMessageFragment extends BaseFragment implements View.OnClickListener {
    @Override
    public int setFragmentLayout() {
        return R.layout.goods_message;
    }

    private TextView tvNum;
    private TextView tvName;
    private TextView tvPrice;
    private Button btnGoodsFragment;

    @Override
    public void findById(View view) {
        tvNum = (TextView) view.findViewById(R.id.goods_num_tv);
        tvName = (TextView) view.findViewById(R.id.goods_name_tv);
        tvPrice = (TextView) view.findViewById(R.id.goods_price_tv);
        btnGoodsFragment = (Button) view.findViewById(R.id.goods_message_btn);

        btnGoodsFragment.setOnClickListener(this);
        tvNum.setText("");
        tvName.setText("");
        tvPrice.setText("");
        setAdapterMethod();
    }


    private void setAdapterMethod() {
        String gnumber = getArguments().getString("Gnumber");
        String gname = getArguments().getString("Gname");
        String gprice = getArguments().getString("Gprice");
    //    String gcount = getArguments().getString("Gcount");

        tvNum.setText(gnumber);
        tvName.setText(gname);
        tvPrice.setText(gprice);


    }

    @Override
    public void onClick(View v) {
        if (v == btnGoodsFragment) {
            GoodsFragment goodsFragment = new GoodsFragment();
            openFragment(goodsFragment);

        }
    }
}