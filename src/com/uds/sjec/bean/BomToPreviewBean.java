package com.uds.sjec.bean;

import java.util.ArrayList;
import java.util.List;

import com.teamcenter.rac.kernel.TCComponentBOMLine;

public class BomToPreviewBean {
	public static int AllBeanCount = 0;
	
	public String item_type;
	public boolean ifChangeColor;
	private static String[] propertyNames = new String[] { 
		"bl_indented_title", // propertyList 0
		"S2_bl_asmno",  // propertyList 1
		"bl_item_item_id", // propertyList 2
		"bl_item_object_name", // propertyList 3
		"S2_bl_vc", "S2_bl_vc1", "S2_bl_vc2", "S2_bl_vc3", "S2_bl_vc4", // propertyList 4
		"Usage_Quantity", // propertyList 5
		"S2_bl_ylgsl", // propertyList 6
		"bl_item_s2_Spec", // propertyList 7
		"bl_item_s2_Rev_Matl", // propertyList 8
		"bl_item_s2_Rev_Manu_Type", // propertyList 9
		"bl_item_s2_Rev_Weight", // propertyList 10
		"S2_bl_zz", // propertyList 11
		"bl_item_s2_Note", // propertyList 12
		"bl_rev_s2_Rev_Name_BJ", // propertyList 13
		"bl_rev_item_revision_id" }; // propertyList 14
	public List<String> propertyList;
	public List<BomToPreviewBean> children;
	
	public BomToPreviewBean() {
		propertyList = new ArrayList<String>();
		children = new ArrayList<BomToPreviewBean>();
		item_type = "";
		ifChangeColor = false;
		
		propertyList.add(""); // propertyList 0
		propertyList.add(""); // propertyList 1
		propertyList.add(""); // propertyList 2
		propertyList.add(""); // propertyList 3
		propertyList.add(""); // propertyList 4
		propertyList.add(""); // propertyList 5
		propertyList.add(""); // propertyList 6
		propertyList.add(""); // propertyList 7
		propertyList.add(""); // propertyList 8
		propertyList.add(""); // propertyList 9
		propertyList.add(""); // propertyList 10
		propertyList.add(""); // propertyList 11
		propertyList.add(""); // propertyList 12
		propertyList.add(""); // propertyList 13
		propertyList.add(""); // propertyList 14
	}
	
	public BomToPreviewBean(TCComponentBOMLine bomLine) throws Exception {
		propertyList = new ArrayList<String>();
		children = new ArrayList<BomToPreviewBean>();
		item_type = bomLine.getItem().getType();
		ifChangeColor = false;
		
		String[] values = bomLine.getProperties(propertyNames);
		propertyList.add(values[0]); // propertyList 0
		propertyList.add(values[1]); // propertyList 1
		propertyList.add(values[2]); // propertyList 2
		propertyList.add(values[3]); // propertyList 3
		String S2_bl_vcs = values[4] + values[5] + values[6] + values[7] + values[8];
		propertyList.add(S2_bl_vcs); // propertyList 4
		propertyList.add(values[9]); // propertyList 5
		propertyList.add(values[10]); // propertyList 6
		propertyList.add(values[11]); // propertyList 7
		propertyList.add(values[12]); // propertyList 8
		propertyList.add(values[13]); // propertyList 9
		propertyList.add(values[14]); // propertyList 10
		propertyList.add(values[15]); // propertyList 11
		propertyList.add(values[16]); // propertyList 12
		propertyList.add(values[17]); // propertyList 13
		propertyList.add(values[18]); // propertyList 14
	}
	
	@Override
    public String toString() {
        return propertyList.get(0);
    }
}
