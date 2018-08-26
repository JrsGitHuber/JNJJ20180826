package com.uds.sjec.bean;

/**
 * 用于存储配置单管理模块中参数读取的数据
 * 
 * @author DDL
 * 
 */
public class ParamReadedBean {

	public String paramCode;// 参数代号
	public String paramName;// 参数名称
	public String paramValue;// 参数值
	public String rangeOfParamValue;// 参数值范围

	public String getParamCode() {
		return paramCode;
	}

	public void setParamCode(String paramCode) {
		this.paramCode = paramCode;
	}

	public String getParamName() {
		return paramName;
	}

	public void setParamName(String paramName) {
		this.paramName = paramName;
	}

	public String getParamValue() {
		return paramValue;
	}

	public void setParamValue(String paramValue) {
		this.paramValue = paramValue;
	}

	public String getRangeOfParamValue() {
		return rangeOfParamValue;
	}

	public void setRangeOfParamValue(String rangeOfParamValue) {
		this.rangeOfParamValue = rangeOfParamValue;
	}

}
