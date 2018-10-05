package com.uds.sjec.bean;

import java.util.List;

public class BomToExcelBean implements Comparable<BomToExcelBean> {
	public int orderNum = 0;
	public List<String> propertyValueList = null;
	public List<BomToExcelBean> children = null;
	
	public String prefix = "";

	@Override
	public int compareTo(BomToExcelBean arg0) {
//		return this.assemblyNumber.compareTo(arg0.assemblyNumber);
//		return 0;
		if (this.orderNum > arg0.orderNum) {
			return 1;
		} else {
			return -1;
		}
	}
}
