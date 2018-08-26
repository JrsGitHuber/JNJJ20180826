package com.uds.sjec.service;

import java.util.List;

import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;
import com.teamcenter.rac.kernel.TCComponentItem;
import com.teamcenter.rac.kernel.TCComponentItemRevision;
import com.teamcenter.rac.kernel.TCSession;
import com.uds.sjec.bean.ParamViewTableBean;

public interface IImportProjectService {

	List<String> queryElevatorTypeList();

	List<ParamViewTableBean> getParamDatabaseList(String selectedElevatorType);

	List<ParamViewTableBean> compareInfo(List<ParamViewTableBean> paramInfoOFromExcelList,
			List<ParamViewTableBean> paramInfoFromDatabaseList);

	TCComponentItem createItem(TCSession session, String itemType, String itemID, String itemName, String description);

	void createDataSet(TCSession session, String localFile, String dataSetType, String datasetNamedRef, String dataSetName,
			TCComponentItemRevision itemRevision, String relationType, boolean replaceAlert);

	void showData(DefaultTableModel previewModel, List<ParamViewTableBean> paramList);

	List<ParamViewTableBean> getParamExcelList(String excelPath, List<String> elevatorTypeList, JTextField textField_ElevatorType);
	
	boolean getInfoTodatabase(TCComponentItem configurationListItem, List<ParamViewTableBean> paramInfoFromExcelList,
			List<ParamViewTableBean> paramInfoFromDatabaseList, TCSession session);

	String getConfigListID(String equipmentNo);

	

}
