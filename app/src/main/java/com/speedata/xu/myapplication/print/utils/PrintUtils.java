package com.speedata.xu.myapplication.print.utils;

import com.printer.sdk.PrinterConstants;
import com.printer.sdk.PrinterInstance;
import com.speedata.xu.myapplication.db.bean.CheckDetailInfor;

import java.util.List;

public class PrintUtils {


	public static void printTxt(String txtname, List<CheckDetailInfor> list, PrinterInstance mPrinter) {
		mPrinter.initPrinter();
		mPrinter.setPrinter(PrinterConstants.Command.ALIGN, 0); //左
		mPrinter.setFont(0, 1, 1, 0,0);
		mPrinter.printText(txtname);
		mPrinter.setFont(0, 0, 0, 0,0);
		mPrinter.setPrinter(PrinterConstants.Command.PRINT_AND_WAKE_PAPER_BY_LINE, 1);
		mPrinter.printText("==============================");
		mPrinter.setPrinter(PrinterConstants.Command.PRINT_AND_WAKE_PAPER_BY_LINE, 1);
		mPrinter.setFont(0,1, 1, 0, 0);
		mPrinter.printText("01");
		mPrinter.setFont(0,0, 0, 0, 0);
		mPrinter.setPrinter(PrinterConstants.Command.PRINT_AND_WAKE_PAPER_BY_LINE, 1);

		int sum = 0;
		int total = 0;

		double sumpri = 0;
		double totpri = 0;
		for (int i = 1; i < list.size(); i++) {
			mPrinter.printText("==============================");
			mPrinter.setPrinter(PrinterConstants.Command.PRINT_AND_WAKE_PAPER_BY_LINE, 1);
			CheckDetailInfor checkDetailInfor = list.get(i);
			String number = checkDetailInfor.getGoodsNum();
			String name = checkDetailInfor.getGoodsName();
			String count = checkDetailInfor.getGoodsCount();
			String price = checkDetailInfor.getGoodsPrice();

			int num = Integer.parseInt(count);
			total = num + total;

			double pri = Double.parseDouble(price);
			totpri = pri*num+totpri;

			if ("0".equals(price) && "0".equals(count)) {

				sum = total + sum;
				sumpri = totpri + sumpri;
				String stotal = String.valueOf(total);

				String stotpri = String.valueOf(totpri);

				mPrinter.setFont(0,1, 1, 0, 0);
				mPrinter.printText("小计: 数量:" + stotal);
				mPrinter.setPrinter(PrinterConstants.Command.PRINT_AND_WAKE_PAPER_BY_LINE, 1);
				mPrinter.printText("  金额:" + stotpri);
				mPrinter.setFont(0,0, 0, 0, 0);
				mPrinter.setPrinter(PrinterConstants.Command.PRINT_AND_WAKE_PAPER_BY_LINE, 1);
				mPrinter.printText("==============================");
				mPrinter.setPrinter(PrinterConstants.Command.PRINT_AND_WAKE_PAPER_BY_LINE, 1);
				mPrinter.setFont(0,1, 1, 0, 0);
				mPrinter.printText(name);
				mPrinter.setFont(0,0, 0, 0, 0);
				mPrinter.setPrinter(PrinterConstants.Command.PRINT_AND_WAKE_PAPER_BY_LINE, 1);
				total = 0;
				totpri = 0;

			} else {

				mPrinter.printText(number);
				mPrinter.setPrinter(PrinterConstants.Command.PRINT_AND_WAKE_PAPER_BY_LINE, 1);
				mPrinter.printText(name);
				mPrinter.setPrinter(PrinterConstants.Command.PRINT_AND_WAKE_PAPER_BY_LINE, 1);
				mPrinter.printText("数量:" + count + "            " + "价格:" + price);

				mPrinter.setPrinter(PrinterConstants.Command.ALIGN, 0);
				mPrinter.setPrinter(PrinterConstants.Command.PRINT_AND_WAKE_PAPER_BY_LINE, 1);

			}
		}

		mPrinter.printText("==============================");
		mPrinter.setPrinter(PrinterConstants.Command.PRINT_AND_WAKE_PAPER_BY_LINE, 1);
		String stotal = String.valueOf(total);
		String stotpri = String.valueOf(totpri);
		mPrinter.setFont(0,1, 1, 0, 0);
		mPrinter.printText("小计: 数量:" + stotal);
		mPrinter.setPrinter(PrinterConstants.Command.PRINT_AND_WAKE_PAPER_BY_LINE, 1);
		mPrinter.printText("  金额:" + stotpri);
		mPrinter.setFont(0,0, 0, 0, 0);
		mPrinter.setPrinter(PrinterConstants.Command.PRINT_AND_WAKE_PAPER_BY_LINE, 1);
		sum = sum + total;
		sumpri=sumpri+totpri;
		String ssum = String.valueOf(sum);
		String ssumpri = String.valueOf(sumpri);
		mPrinter.printText("==============================");
		mPrinter.setPrinter(PrinterConstants.Command.PRINT_AND_WAKE_PAPER_BY_LINE, 2);
		mPrinter.setFont(0,1, 1, 0, 0);
		mPrinter.printText("总计: 数量:" + ssum);
		mPrinter.setPrinter(PrinterConstants.Command.PRINT_AND_WAKE_PAPER_BY_LINE, 1);
		mPrinter.printText("  金额:" + ssumpri);
		mPrinter.setFont(0,0, 0, 0, 0);
		mPrinter.setPrinter(PrinterConstants.Command.PRINT_AND_WAKE_PAPER_BY_LINE, 3);
	}



}
