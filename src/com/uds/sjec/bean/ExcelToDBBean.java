package com.uds.sjec.bean;

public class ExcelToDBBean implements Comparable<ExcelToDBBean> {
	public String paramName = "";
	public String paramCode = "";
	public String paramValue = "";
	public String paramType = "";
	
	@Override
	public int compareTo(ExcelToDBBean arg0) {
		return this.paramCode.compareTo(arg0.paramCode);
	}
}
