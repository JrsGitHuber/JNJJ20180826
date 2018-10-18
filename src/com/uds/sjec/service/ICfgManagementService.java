package com.uds.sjec.service;

import java.util.List;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.table.DefaultTableModel;

import com.teamcenter.rac.kernel.TCComponentBOMLine;
import com.uds.sjec.bean.ParamReadedBean;

public interface ICfgManagementService {

	void searchCfgList(DefaultTableModel configarationModel, String searchId, String searchIdType, String taskStatu, String startedTime,
			String finishedTime, String workFlowName) throws Exception;

	List<ParamReadedBean> getParamReadedList(String cfgListId);

	void showParamReadedTable(DefaultTableModel projectInfoModel, List<ParamReadedBean> paramReadedList);

	String downLoadExcelToDir(String preferenceName, String configListId, String configListRev);

	void paramCalculated(List<ParamReadedBean> paramReadedList, String excelpath, String configListId, JButton button_paramCaculate,
			JButton button_BOMConfiguration);

	String queryProductType(String configListId);

	Map<String, String> getBOMConfigParamInfo(List<ParamReadedBean> paramReadedList);

	Map<String, String> getComputationMap(String cfgItemId);

	void configurantionCalculated(TCComponentBOMLine superTopBOMLine, TCComponentBOMLine configListTopBomLine,
			Map<String, String> paramMap, Map<String, String> computationMap, String configListId, JButton button_BOMInstantiation);

}
