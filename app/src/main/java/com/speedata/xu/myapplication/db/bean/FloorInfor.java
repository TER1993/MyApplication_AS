package com.speedata.xu.myapplication.db.bean;

import com.elsw.base.db.orm.annotation.Column;
import com.elsw.base.db.orm.annotation.Table;

/**
 *
 * @author xu
 * @date 2016/11/3
 */

@Table(name = "FloorInfor")
public class FloorInfor { //货层表

    @Column(name = "Floor")
    private String Floor; //货层

    @Column(name = "CheckID")
    private String CheckID; //盘点单时间


    public String getFloor() {
        return Floor;
    }

    public void setFloor(String floor) {
        Floor = floor;
    }

    public String getCheckID() {
        return CheckID;
    }

    public void setCheckID(String checkID) {
        CheckID = checkID;
    }

    @Override
    public String toString() {
        return "FloorInfor{"
                + "Floor='" + Floor + '\''
                + ", CheckID='" + CheckID + '\''
                + '}';
    }


}

