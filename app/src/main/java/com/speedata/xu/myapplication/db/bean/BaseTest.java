package com.speedata.xu.myapplication.db.bean;

import com.elsw.base.db.orm.annotation.Column;
import com.elsw.base.db.orm.annotation.Table;

/**
 * Created by Administrator on 2016/2/26.
 */
@Table(name = "BaseTest")
public  class BaseTest { //user表

    @Column(name = "UserID")
    private String UserID; //用户名

    @Column(name = "PassWord")
    private String PassWord; //密码



    public String getUserID() {
        return UserID;
    }

    public void setUserID(String userID) {
        UserID = userID;
    }

    public String getPassWord() {
        return PassWord;
    }

    public void setPassWord(String passWord) {
        PassWord = passWord;
    }


    @Override
    public String toString() {
        return "BaseTest [UserID=" + UserID + '\''
                + ", PassWord=" + PassWord + '\''
                + "]";
    }


}
