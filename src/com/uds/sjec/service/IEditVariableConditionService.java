package com.uds.sjec.service;

import java.util.List;
import java.util.Map;

import javax.swing.table.DefaultTableModel;

import com.uds.common.exceptions.CalculateException;
import com.uds.sjec.bean.VariabelConditionTableBean;

public interface IEditVariableConditionService {

	void searchParam(DefaultTableModel paramTableModel, List<VariabelConditionTableBean> paramInfoList, String text, String text2);

	Map<String, String> getParamCodeAndTypeMap(List<VariabelConditionTableBean> paramInfoList);

	Boolean checkType(String variableCondition, Map<String, String> paramCodeAndTypeMap) throws CalculateException;

}
