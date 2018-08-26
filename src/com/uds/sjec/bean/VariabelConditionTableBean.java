package com.uds.sjec.bean;

/**
 * 编辑变量条件里参数信息
 * 
 * @author DDL
 * 
 */
public class VariabelConditionTableBean {

	public String paramClassification; // 参数分类
	public String paramCode; // 参数代号
	public String paramName;// 参数名称
	public String paramType; // 参数类型
	public String rangeOfParamValue; // 参数范围

	public String getParamClassification() {
		return paramClassification;
	}

	public void setParamClassification(String paramClassification) {
		this.paramClassification = paramClassification;
	}

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

	public String getParamType() {
		return paramType;
	}

	public void setParamType(String paramType) {
		this.paramType = paramType;
	}

	public String getRangeOfParamValue() {
		return rangeOfParamValue;
	}

	public void setRangeOfParamValue(String rangeOfParamValue) {
		this.rangeOfParamValue = rangeOfParamValue;
	}
}
