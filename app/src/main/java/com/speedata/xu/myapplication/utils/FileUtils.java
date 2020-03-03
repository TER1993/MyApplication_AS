package com.speedata.xu.myapplication.utils;


import android.content.Context;
import android.util.Log;

import com.speedata.xu.myapplication.R;
import com.speedata.xu.myapplication.db.bean.BaseInfor;
import com.speedata.xu.myapplication.db.bean.OutputTxt;
import com.speedata.xu.myapplication.db.dao.BaseInforDao;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 *
 * @author xu
 * @date 2016/4/18
 */
public class FileUtils {
    public int outCount;

    //读取文本文件中的内容
    public static int importBaseInforTxt(Context context, String strFilePath) {
        BaseInforDao baseInforDao = new BaseInforDao(context);

        String path = context.getString(R.string.path_import) + strFilePath + ".txt";
        List<BaseInfor>  listResult = new ArrayList<>();
        //打开文件
        File file = new File(path);
        int x = 0;
        int y = 0;
        int z = 0;
        try {
            InputStream instream = new FileInputStream(file);
            InputStreamReader inputreader = new InputStreamReader(instream, "GBK"); //编码
            BufferedReader buffreader = new BufferedReader(inputreader);
            String line;
            //分行读取
            for (int i = 1; i <= 100; i++) {
                //预留10W条
                while ((line = buffreader.readLine()) != null) {
                    //把line截取成3段

                    String number = line.substring(0, line.indexOf("\t")); //获得前面number的部分
                    String price = line.substring(line.lastIndexOf("\t")); //获得后面price的部分
                    price = price.replaceAll("\t", "");

                    //

                    String name = (line.substring(line.indexOf("\t") + 1, line.lastIndexOf("\t")));
                    //获得中间品名的部分,从第一个tab到最后一个tab之间
                    String countnum = "1"; //自定义数量
                    //存入数据
                    BaseInfor bean = new BaseInfor();
                    bean.setGoodsNum(number);
                    bean.setGoodsName(name);
                    bean.setGoodsPrice(price);
                    bean.setGoodsCount(countnum);
                    listResult.add(bean);

                    x = x + 1;
                    y = y + 1;
                    if (x == 1000) { //x==1000
                        Log.d("TestFile", String.valueOf(++z));
                        baseInforDao.imInsertList(listResult);
                        listResult.clear();
                        x = 0;
                    }
                }
                baseInforDao.imInsertList(listResult);
                listResult.clear();
                x = 0;

            }
            instream.close();

        } catch (java.io.FileNotFoundException e) {
            Log.d("TestFile", "The File doesn't not exist.");
            return -1;
        } catch (IOException e) {
            Log.d("TestFile", e.getMessage());
        }
        return y;
    }


    /**
     *    盘点单列表--导出
     * @param list
     * @param filename
     * @return
     */
    public int outputfile(List<OutputTxt> list, String filename) {
        outCount = 0;
        BufferedWriter bw = null;
        try {
            bw = new BufferedWriter(new FileWriter(new File(filename)));
            for (int i = 0; i < list.size(); i++) {
            //拿字符串
                OutputTxt str = list.get(i);
                if (str.getGoodsnumber().length() == 2 && "0".equals(str.getGoodscount())) {

                } else {
                    outCount++;
                    String over = str.getGoodsnumber() + "  " + str.getGoodscount() + "\r\n";
                    // 写文件
                    bw.write(over, 0, over.length());
                    // 刷新流
                    bw.flush();
                }



            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                // 关闭文件流
                Objects.requireNonNull(bw).close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return  1;
    }




}








