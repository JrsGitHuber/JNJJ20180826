package com.uds.sjec.bean;

/**
 * ���õ����������õ���Ϣ
 * 
 * @author DDL
 * 
 */
public class CfgListInfoTableBean {

	public String cfgID = ""; // ���õ���
	public String cfgRevision = ""; // �汾��
	public String projectName = ""; // ��Ŀ����
	public String createUser = ""; // �����û�
	public String createTime = ""; // ����ʱ��
	public String sponsorTime = "";// ����ʱ��
	public String releaseTime = ""; // ����ʱ��
	public String status = ""; // ״̬

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
