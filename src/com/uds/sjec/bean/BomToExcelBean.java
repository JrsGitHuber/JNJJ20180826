package com.uds.sjec.bean;

import java.util.List;

public class BomToExcelBean implements Comparable<BomToExcelBean> {
	/**
	 * װ���
	 */
	public String assemblyNumber = ""; 
	/**
	 * ��ʶ
	 */
	public String identification = "";
	/**
	 * ���ű���
	 */
	public String code = "";
	/**
	 * ����
	 */
	public String name = "";
	/**
	 * ����
	 */
	public String material = "";
	/**
	 * ����
	 */
	public String quantity = "";
	/**
	 * ��ע
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
