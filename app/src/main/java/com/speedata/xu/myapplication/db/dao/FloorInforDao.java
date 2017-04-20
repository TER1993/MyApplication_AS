package com.speedata.xu.myapplication.db.dao;

import android.content.Context;

import com.elsw.base.db.orm.dao.ABaseDao;
import com.speedata.xu.myapplication.db.bean.FloorInfor;

/**
 * Created by xu on 2016/11/3.
 */

public class FloorInforDao extends ABaseDao<FloorInfor> {

    public FloorInforDao(Context context) {
        super(new DBInsideHelper(context), FloorInfor.class);
    }

}
