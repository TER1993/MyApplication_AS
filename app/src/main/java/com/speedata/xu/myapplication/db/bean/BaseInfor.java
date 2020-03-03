package com.speedata.xu.myapplication.db.bean;

import com.elsw.base.db.orm.annotation.Column;
import com.elsw.base.db.orm.annotation.Table;

/**
 *
 * @author Administrator
 * @date 2016/2/26
 */
@Table(name = "BaseInfor")
public class BaseInfor { //商品表

    @Column(name = "GoodsCount")
    private String GoodsCount; //货品数量

    @Column(name = "GoodsName")
    private String GoodsName; //货品名称

    @Column(name = "GoodsPrice")
    private String GoodsPrice; //货品价格

    @Column(name = "GoodsNum")
    private String GoodsNum; //货品编号

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
        GoodsNum = goodsNum;
    }

    public String getGoodsPrice() {
        return GoodsPrice;
    }

    public void setGoodsPrice(String goodsPrice) {
        GoodsPrice = goodsPrice;
    }


    @Override
    public String toString() {
        return "BaseInfor [GoodsNum=" + GoodsNum
                + ", GoodsName=" + GoodsName + '\''
                + ", GoodsPrice=" + GoodsPrice + '\''
                + ", GoodsCount=" + GoodsCount + '\''
                + "]";
    }


}
