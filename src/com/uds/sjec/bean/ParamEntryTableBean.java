package com.uds.sjec.bean;

/**
 * 存储从excel获取的配置单信息
 * 
 * @author DDL
 * 
 */
public class ParamEntryTableBean {

	public String contractNo;// 合同号
	public String deviceNo;// 设备号
	public String projectName;// 项目名

	public String getContractNo() {
		return contractNo;
	}

	public void setContractNo(String contractNo) {
		this.contractNo = contractNo;
	}

	public String getDeviceNo() {
		return deviceNo;
	}

	public void setDeviceNo(String deviceNo) {
		this.deviceNo = deviceNo;
	}

	public String getProjectName() {
		return projectName;
	}

	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}
}
