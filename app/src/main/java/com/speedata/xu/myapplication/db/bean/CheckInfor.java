package com.speedata.xu.myapplication.db.bean;

import com.elsw.base.db.orm.annotation.Column;
import com.elsw.base.db.orm.annotation.Table;

/**
 *
 * @author Administrator
 * @date 2016/2/26
 */
@Table(name = "CheckInfor")
public class CheckInfor { //盘点表

    @Column(name = "UserID")
    private String UserID; //用户id

    @Column(name = "CheckName")
    private String CheckName; //盘点表名字

    @Column(name = "CheckTime")
    private String CheckTime; //盘点时间


    public String getUserID() {
        return UserID;
    }

    public void setUserID(String userID) {
        UserID = userID;
    }

    public String getCheckName() {
        return CheckName;
    }

    public void setCheckName(String checkName) {
        this.CheckName = checkName;
    }

    public String getCheckTime() {
        return CheckTime;
    }

    public void setCheckTime(String checkTime) {
        CheckTime = checkTime;
    }


    @Override
    public String toString() {
        return "BaseInfor [UserID=" + UserID
                + ", CheckName=" + CheckName + '\''
                + ", CheckTime=" + CheckTime + '\''
                + "]";
    }


}
