package com.uds.sjec.bean;

public class CalculationTableBean {
	public String configID = "";// 配置单号
	public String conditionCode = "";// 公式编码
	public String calculationValue = "";// 结果

	public String getConfigID() {
		return configID;
	}

	public void setConfigID(String configID) {
		this.configID = configID;
	}

	public String getConditionCode() {
		return conditionCode;
	}

	public void setConditionCode(String conditionCode) {
		this.conditionCode = conditionCode;
	}

	public String getCalculationValue() {
		return calculationValue;
	}

	public void setCalculationValue(String calculationValue) {
		this.calculationValue = calculationValue;
	}

}
