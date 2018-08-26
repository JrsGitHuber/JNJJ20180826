package com.uds.sjec.bean;

import java.util.List;

public class BomToExcelBean implements Comparable<BomToExcelBean> {
	/**
	 * 装配号
	 */
	public String assemblyNumber = ""; 
	/**
	 * 标识
	 */
	public String identification = "";
	/**
	 * 代号编码
	 */
	public String code = "";
	/**
	 * 名称
	 */
	public String name = "";
	/**
	 * 材料
	 */
	public String material = "";
	/**
	 * 数量
	 */
	public String quantity = "";
	/**
	 * 备注
	 */
	public String note = "";
	
	public List<BomToExcelBean> children = null;
	
	public String prefix = "";
	public String[] properties = new String[] { "S2_bl_asmno", "bl_rev_s2_Rev_Manu_Type",
			"bl_item_item_id", "bl_item_object_name", "bl_rev_s2_Rev_Matl",
			"Usage_Quantity", "S2_bl_ylgsl", "bl_item_s2_Note" };

	@Override
	public int compareTo(BomToExcelBean arg0) {
		return this.assemblyNumber.compareTo(arg0.assemblyNumber);
//		return 0;
	}
}
