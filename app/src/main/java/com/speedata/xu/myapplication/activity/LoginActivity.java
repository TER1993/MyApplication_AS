package com.speedata.xu.myapplication.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.speedata.xu.myapplication.MainActivity;
import com.speedata.xu.myapplication.R;
import com.speedata.xu.myapplication.application.CustomerApplication;
import com.speedata.xu.myapplication.db.bean.BaseTest;
import com.speedata.xu.myapplication.db.dao.BaseTestDao;

import java.io.File;
import java.util.List;

/**
 * @author xu
 * @date 2016/4/21
 */
public class LoginActivity extends Activity implements View.OnClickListener {
    private EditText etUserID;
    private EditText etPassword;
    private Button btnLogin;
    private Button btnFinish;
    private BaseTestDao baseTestDao;
    private CustomerApplication application;
    private SharedPreferences.Editor ed;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);

        Context mContext = this;
        baseTestDao = new BaseTestDao(mContext);
        application = (CustomerApplication) getApplication();
        etUserID = findViewById(R.id.login_userid_et);
        etPassword = findViewById(R.id.login_password_et);
        TextView tvVersion = findViewById(R.id.login_version_tv);
        tvVersion.setText(getVersion());

        Switch sbtnAutoLogin = findViewById(R.id.login_autologin_sbtn);
        Switch sbtnRememberPassword = findViewById(R.id.login_rememberpassword_sbtn);


        btnLogin = findViewById(R.id.login_in_btn);
        btnFinish = findViewById(R.id.login_out_btn);


        btnLogin.setOnClickListener(this);
        btnFinish.setOnClickListener(this);


        if (android.os.Build.MODEL.equalsIgnoreCase(getString(R.string.kt40))
                || android.os.Build.MODEL.equalsIgnoreCase(getString(R.string.kt55))
                || android.os.Build.MODEL.equalsIgnoreCase(getString(R.string.spda6735))
                || android.os.Build.MODEL.equalsIgnoreCase(getString(R.string.kt40q))) {
            Toast.makeText(mContext, "ok," + Build.MODEL
                    + getString(R.string.login_module_right), Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(mContext, Build.MODEL
                    + getString(R.string.login_module_wrong), Toast.LENGTH_SHORT).show();
            finish();
        }

        newFile();


        SharedPreferences sp = getSharedPreferences("users", MODE_PRIVATE);
        ed = sp.edit();
        if (sp.getBoolean("ISCHECK", false)) {
            sbtnRememberPassword.setChecked(true);
            etUserID.setText(sp.getString("oa_name", ""));
            etPassword.setText(sp.getString("oa_pass", ""));
            if (sp.getBoolean("AUTO_ISCHECK", false)) {
                sbtnAutoLogin.setChecked(true);
            }
        }

        sbtnRememberPassword.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @SuppressLint("ApplySharedPref")
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                ed.putBoolean("ISCHECK", isChecked);
                ed.commit();
            }
        });
        boolean value1 = sp.getBoolean("AUTO_ISCHECK", false);
        sbtnAutoLogin.setChecked(value1);
        sbtnAutoLogin.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                ed.putBoolean("AUTO_ISCHECK", isChecked);
                ed.commit();
            }
        });


        if (sbtnAutoLogin.isChecked() && sbtnRememberPassword.isChecked()) {
            if (application.getChangeuser() == 1) {
                btnLogin.performClick();
            }
        }


    }


    private void newFile() {
        File file = new File(getString(R.string.import_path));
        if (!file.exists()) {
            file.mkdirs();

        }
        file = new File(getString(R.string.export_path));
        if (!file.exists()) {
            file.mkdirs();
        }

    }


    @Override
    public void onClick(View v) {
        String userID = etUserID.getText().toString();
        String password = etPassword.getText().toString();

        if (v == btnLogin) {

            List<BaseTest> baseTests = baseTestDao.imQueryList();
            if (baseTests.size() == 0) {
                BaseTest bean = new BaseTest();
                bean.setUserID("admin");
                bean.setPassWord("00000000");
                baseTests.add(bean);
                baseTestDao.imInsertList(baseTests);
            }
            if ("".equals(userID) || "".equals(password)) {
                Toast.makeText(this, R.string.login_null_input, Toast.LENGTH_SHORT).show();
                return;
            }
            baseTests = baseTestDao.imQueryList("UserID=?", new String[]{userID});
            if (baseTests.size() == 0) {
                Toast.makeText(this, R.string.login_null_username, Toast.LENGTH_SHORT).show();
                return;
            }
            BaseTest bean = baseTests.get(0);
            boolean result = bean.getPassWord().equals(password);
            if (!result) {
                Toast.makeText(this, R.string.login_wrong_password, Toast.LENGTH_SHORT).show();
                return;
            }
            application.setID(userID);
            application.setPswd(password);
            application.setChangeuser(1);
            ed.putString("oa_name", userID);
            ed.putString("oa_pass", password);
            ed.commit();

            Toast.makeText(this, getString(R.string.login_welcome) + userID, Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
            finish();

        } else if (v == btnFinish) {
            finish();
        }

    }


    /**
     * 获取当前应用程序的版本号
     */
    private String getVersion() {
        PackageManager pm = getPackageManager();
        String version;
        String wrongVersion;
        try {
            PackageInfo packinfo = pm.getPackageInfo(getPackageName(), 0);
            version = packinfo.versionName;
            return version;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            wrongVersion = this.getString(R.string.wrong_version);
            return wrongVersion;
        }
    }


}

