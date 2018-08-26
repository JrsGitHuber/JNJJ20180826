package com.uds.sjec.bean;

import com.teamcenter.rac.kernel.TCComponentBOMLine;

/**
 * ������ϸ��
 * 
 * @author DDL
 * 
 */
public class ProductionTableBean {

	public String assemblyNumber = ""; // װ���
	public String identification = ""; // ��ʶ
	public String code = ""; // ���ű���
	public String name = ""; // ����
	public String material = ""; // ����
	public String quantity = ""; // ����
	public String note = ""; // ��ע
	public TCComponentBOMLine bomLine;

	public String getAssemblyNumber() {
		return assemblyNumber;
	}

	public void setAssemblyNumber(String assemblyNumber) {
		this.assemblyNumber = assemblyNumber;
	}

	public String getIdentification() {
		return identification;
	}

	public void setIdentification(String identification) {
		this.identification = identification;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getMaterial() {
		return material;
	}

	public void setMaterial(String material) {
		this.material = material;
	}

	public String getQuantity() {
		return quantity;
	}

	public void setQuantity(String quantity) {
		this.quantity = quantity;
	}

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}

	public TCComponentBOMLine getBomLine() {
		return bomLine;
	}

	public void setBomLine(TCComponentBOMLine bomLine) {
		this.bomLine = bomLine;
	}
}
