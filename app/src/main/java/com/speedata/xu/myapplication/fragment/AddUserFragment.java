package com.speedata.xu.myapplication.fragment;

import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.speedata.xu.myapplication.R;
import com.speedata.xu.myapplication.base.BaseFragment;
import com.speedata.xu.myapplication.db.bean.BaseTest;
import com.speedata.xu.myapplication.db.dao.BaseTestDao;

import java.util.List;

/**
 * Created by xu on 2016/4/8.
 */
public class AddUserFragment extends BaseFragment implements View.OnClickListener {

    @Override
    public int setFragmentLayout() {
        return R.layout.setting_adduser;
    }

    private EditText etConfirmPassword;
    private EditText etUsername;
    private EditText etPassword;
    private Button btnSubmit;
    private Button btnBack;
    private BaseTestDao baseTestDao;
    private Context mContext;

    @Override
    public void findById(View view) {
        mContext = mActivity;
        baseTestDao = new BaseTestDao(mContext);

        etUsername = (EditText) view.findViewById(R.id.adduser_username_et);
        etPassword = (EditText) view.findViewById(R.id.adduser_password_et);
        etConfirmPassword = (EditText) view.findViewById(R.id.adduser_confirmpassword_et);
        btnSubmit = (Button) view.findViewById(R.id.adduser_submit_btn);
        btnSubmit.setOnClickListener(this);
        btnBack = (Button) view.findViewById(R.id.adduser_back_btn);
        btnBack.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        String username = etUsername.getText().toString();
        String password = etPassword.getText().toString();
        String confirmPassword = etConfirmPassword.getText().toString();

        if (v == btnSubmit) {
            if ("".equals(username) || "".equals(password) || "".equals(confirmPassword)) {
                Toast.makeText(mContext, R.string.add_null_input, Toast.LENGTH_SHORT).show();
                return;
            } else if (!password.equals(confirmPassword)) {
                Toast.makeText(mContext, R.string.add_defferent_input, Toast.LENGTH_SHORT).show();
                etConfirmPassword.setText("");
                return;
            }
            List<BaseTest> baseTests = baseTestDao.imQueryList("UserID=?", new String[]{username});
            if (baseTests.size() != 0) {
                Toast.makeText(mContext, R.string.add_had_username, Toast.LENGTH_SHORT).show();
                return;
            }
            BaseTest bean = new BaseTest();
            bean.setUserID(username);
            bean.setPassWord(password);
            baseTestDao.imInsert(bean);
            Toast.makeText(mContext, R.string.add_newuser_success, Toast.LENGTH_SHORT).show();
            etUsername.setText("");
            etPassword.setText("");
            etConfirmPassword.setText("");

        } else if (v == btnBack) {
            SettingFragment fragment = new SettingFragment();
            openFragment(fragment);
        }
    }
}
