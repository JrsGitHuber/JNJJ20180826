package com.uds.sjec.bean;

/**
 * 项目导入中参数预览
 * 
 * @author DDL
 */
public class ParamViewTableBean {

	public String paramName;// 参数名称
	public String paramCode;// 参数代号
	public String paramValue;// 参数值
	public String paramType;// 参数类别
	public String note;// 备注

	public String getParamName() {
		return paramName;
	}

	public void setParamName(String paramName) {
		this.paramName = paramName;
	}

	public String getParamCode() {
		return paramCode;
	}

	public void setParamCode(String paramCode) {
		this.paramCode = paramCode;
	}

	public String getParamValue() {
		return paramValue;
	}

	public void setParamValue(String paramValue) {
		this.paramValue = paramValue;
	}

	public String getParamType() {
		return paramType;
	}

	public void setParamType(String paramType) {
		this.paramType = paramType;
	}

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}
}
