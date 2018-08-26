package com.uds.sjec.bean;

/**
 * 配置单管理中配置单信息
 * 
 * @author DDL
 * 
 */
public class CfgListInfoTableBean {

	public String cfgID = ""; // 配置单号
	public String cfgRevision = ""; // 版本号
	public String projectName = ""; // 项目名称
	public String createUser = ""; // 创建用户
	public String createTime = ""; // 创建时间
	public String sponsorTime = "";// 发起时间
	public String releaseTime = ""; // 发布时间
	public String status = ""; // 状态

	public String getCfgID() {
		return cfgID;
	}

	public void setCfgID(String cfgID) {
		this.cfgID = cfgID;
	}

	public String getCfgRevision() {
		return cfgRevision;
	}

	public void setCfgRevision(String cfgRevision) {
		this.cfgRevision = cfgRevision;
	}

	public String getProjectName() {
		return projectName;
	}

	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}

	public String getCreateUser() {
		return createUser;
	}

	public void setCreateUser(String createUser) {
		this.createUser = createUser;
	}

	public String getReleaseTime() {
		return releaseTime;
	}

	public void setReleaseTime(String releaseTime) {
		this.releaseTime = releaseTime;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getSponsorTime() {
		return sponsorTime;
	}

	public void setSponsorTime(String sponsorTime) {
		this.sponsorTime = sponsorTime;
	}

	public String getCreateTime() {
		return createTime;
	}

	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}

}
