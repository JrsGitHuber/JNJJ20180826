package com.uds.sjec.bean;

import com.teamcenter.rac.kernel.TCComponentBOMLine;

/**
 * 生产明细表
 * 
 * @author DDL
 * 
 */
public class ProductionTableBean {

	public String assemblyNumber = ""; // 装配号
	public String identification = ""; // 标识
	public String code = ""; // 代号编码
	public String name = ""; // 名称
	public String material = ""; // 材料
	public String quantity = ""; // 数量
	public String note = ""; // 备注
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
