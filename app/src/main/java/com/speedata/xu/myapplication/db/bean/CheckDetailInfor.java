package com.speedata.xu.myapplication.db.bean;

import com.elsw.base.db.orm.annotation.Column;
import com.elsw.base.db.orm.annotation.Table;

/**
 * Created by Administrator on 2016/2/26.
 */
@Table(name = "CheckDetailInfor")
public  class  CheckDetailInfor { //盘点明细表

    @Column(name = "GoodsCount")
    private String GoodsCount; //货品数量

    @Column(name = "GoodsName")
    private String GoodsName; //货品名称

    @Column(name = "GoodsPrice")
    private String GoodsPrice; //货品价格

    @Column(name = "GoodsNum")
    private String GoodsNum; //货品编号

    @Column(name = "CheckID")
    private String CheckID; //盘点单时间ID

    @Column(name = "CheckDetailTime")
    private String CheckDetailTime; //盘点明细时间




    public String getCheckID() {
        return CheckID;
    }

    public void setCheckID(String checkID) {
        CheckID = checkID;
    }

    public String getCheckDetailTime() {
        return CheckDetailTime;
    }

    public void setCheckDetailTime(String checkDetailTime) {
        CheckDetailTime = checkDetailTime;
    }

    public String getGoodsCount() {
        return GoodsCount;
    }

    public void setGoodsCount(String goodsCount) {
        GoodsCount = goodsCount;
    }

    public String getGoodsName() {
        return GoodsName;
    }

    public void setGoodsName(String goodsName) {
        GoodsName = goodsName;
    }

    public String getGoodsNum() {
        return GoodsNum;
    }

    public void setGoodsNum(String goodsNum) {
        this.GoodsNum = goodsNum;
    }

    public String getGoodsPrice() {
        return GoodsPrice;
    }

    public void setGoodsPrice(String goodsPrice) {
        GoodsPrice = goodsPrice;
    }


    @Override
    public String toString() {
        return "BaseInfor [CheckID=" + CheckID
                + ", CheckDetailTime=" + CheckDetailTime + '\''
                + ", GoodsNum=" + GoodsNum + '\''
                + ", GoodsName=" + GoodsName + '\''
                + ", GoodsPrice=" + GoodsPrice + '\''
                + ", GoodsCount=" + GoodsCount  + '\''
                + "]";
    }


}
