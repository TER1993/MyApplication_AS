package com.speedata.xu.myapplication.fragment;

import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.speedata.xu.myapplication.R;
import com.speedata.xu.myapplication.application.CustomerApplication;
import com.speedata.xu.myapplication.base.BaseFragment;
import com.speedata.xu.myapplication.db.bean.BaseTest;
import com.speedata.xu.myapplication.db.dao.BaseTestDao;

/**
 * @author xu
 * @date 2016/4/8
 */
public class ReviseFragment extends BaseFragment implements View.OnClickListener {
    @Override
    public int setFragmentLayout() {
        return R.layout.setting_revise;
    }

    private EditText etOldPassword;
    private EditText etNewPassword;
    private EditText etConfirmPassword;
    private Button btnSubmit;
    private Button btnBack;
    private Context mContext;
    private BaseTestDao baseTestDao;
    private CustomerApplication application;

    @Override
    public void findById(View view) {

        mContext = mActivity;
        application = (CustomerApplication) mActivity.getApplication();
        baseTestDao = new BaseTestDao(mContext);
        TextView tvUsername = view.findViewById(R.id.revise_username_tv);
        etOldPassword = view.findViewById(R.id.revise_oldpassword_et);
        etNewPassword = view.findViewById(R.id.revise_newpassword_et);
        etConfirmPassword = view.findViewById(R.id.revise_confirmpassword_et);
        btnSubmit = view.findViewById(R.id.revise_submit_btn);
        btnSubmit.setOnClickListener(this);
        btnBack = view.findViewById(R.id.revise_back_btn);
        btnBack.setOnClickListener(this);

        String username = application.getID();

        tvUsername.setText(username);

    }

    @Override
    public void onClick(View v) {
        String oldPassword = etOldPassword.getText().toString();
        String newPassword = etNewPassword.getText().toString();
        String confirmPassword = etConfirmPassword.getText().toString();

        if (v == btnSubmit) {

            if ("".equals(oldPassword) || "".equals(newPassword) || "".equals(confirmPassword)) {
                Toast.makeText(mContext, R.string.re_null, Toast.LENGTH_SHORT).show();
                return;
            } else if (!newPassword.equals(confirmPassword)) {
                Toast.makeText(mContext, R.string.re_defferent, Toast.LENGTH_SHORT).show();
                etConfirmPassword.setText("");
                return;
            }
            String password = application.getPswd();
            String username = application.getID();
            if (!password.equals(oldPassword)) {
                Toast.makeText(mContext, R.string.re_wrong, Toast.LENGTH_SHORT).show();
                return;
            }
            Toast.makeText(mContext, R.string.re_success, Toast.LENGTH_SHORT).show();
            application.setPswd(newPassword);
            etOldPassword.setText("");
            etNewPassword.setText("");
            etConfirmPassword.setText("");

            baseTestDao.imRawQuery("update BaseTest set PassWord=? where Password=? and UserID=?",
                    new String[]{newPassword, oldPassword, username}, BaseTest.class);

        } else if (v == btnBack) {
            SettingFragment fragment = new SettingFragment();
            openFragment(fragment);
        }

    }

}
