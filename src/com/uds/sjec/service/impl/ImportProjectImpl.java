package com.uds.sjec.service.impl;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import com.teamcenter.rac.kernel.TCComponentDataset;
import com.teamcenter.rac.kernel.TCComponentDatasetType;
import com.teamcenter.rac.kernel.TCComponentItem;
import com.teamcenter.rac.kernel.TCComponentItemRevision;
import com.teamcenter.rac.kernel.TCComponentItemType;
import com.teamcenter.rac.kernel.TCException;
import com.teamcenter.rac.kernel.TCSession;
import com.teamcenter.rac.kernel.TCTypeService;
import com.teamcenter.rac.util.MessageBox;
import com.uds.sjec.bean.ParamViewTableBean;
import com.uds.sjec.common.ConstDefine;
import com.uds.sjec.service.IImportProjectService;

public class ImportProjectImpl implements IImportProjectService {

	private List<ParamViewTableBean> paramInfoOFromExcelList = new ArrayList<ParamViewTableBean>();
	private Connection connection;
	private ResultSet resultSet = null;
	private Statement statement = null;
	private PreparedStatement pstmt = null;

	@Override
	public String getConfigListID(String equipmentNo) {
		String configListID = "";
		try {
			Class.forName(ConstDefine.EAPDB_CLASSNAME);
			connection = DriverManager.getConnection(ConstDefine.EAPDB_URL, ConstDefine.EAPDB_USER, ConstDefine.EAPDB_PASSWORD);
			if (connection == null) {
				MessageBox.post("���ݿ�����ʧ�ܡ�", "����", MessageBox.ERROR);
				return null;
			}
			String queryConfigListID = "SELECT Code FROM view_contract_BasicInformationEle where SONo= '" + equipmentNo + "'";
			statement = connection.createStatement();
			resultSet = statement.executeQuery(queryConfigListID);
			while (resultSet.next()) {
				configListID = resultSet.getString("CODE");
			}
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			MessageBox.post(e.getMessage(), "��EAP��ȡ���õ�", MessageBox.ERROR);
			return null;
		} catch (SQLException e) {
			e.printStackTrace();
			MessageBox.post(e.getMessage(), "��EAP��ȡ���õ�", MessageBox.ERROR);
			return null;
		} finally {
			try {
				if (resultSet != null) {
					resultSet.close();
					resultSet = null;
				}
				if (statement != null) {
					statement.close();
					statement = null;
				}
				if (connection != null) {
					connection.close();
					connection = null;
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return configListID;
	}

	/**
	 * ��ȡ���������ͺŴ���
	 */
	@Override
	public List<String> queryElevatorTypeList() {
		List<String> elevatorTypeList = new ArrayList<String>();
		String sql = "select * from t_elevator_model"; // �������ͺű�����������ͺŴ���
		try {
			Class.forName(ConstDefine.TCDB_CLASSNAME);
			connection = DriverManager.getConnection(ConstDefine.TCDB_URL, ConstDefine.TCDB_USER, ConstDefine.TCDB_PASSWORD);
			if (connection == null) {
				MessageBox.post("���ݿ�����ʧ�ܡ�", "����", MessageBox.ERROR);
				return null;
			}
			pstmt = connection.prepareStatement(sql);
			resultSet = pstmt.executeQuery();
			ResultSetMetaData mdData = resultSet.getMetaData();
			int columnCount = mdData.getColumnCount();
			while (resultSet.next()) {
				Map<String, String> rowDateMap = new HashMap<String, String>();
				for (int i = 1; i <= columnCount; i++) {
					String columnName = mdData.getColumnLabel(i);
					String value = resultSet.getString(columnName);
					rowDateMap.put(columnName, value);
				}
				elevatorTypeList.add(rowDateMap.get("ELEVATOR_MODEL_CODE"));
			}
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			MessageBox.post(e.getMessage(), "������Ŀ", MessageBox.ERROR);
			return null;
		} catch (SQLException e) {
			e.printStackTrace();
			MessageBox.post(e.getMessage(), "������Ŀ", MessageBox.ERROR);
			return null;
		} finally {
			try {
				if (resultSet != null) {
					resultSet.close();
					resultSet = null;
				}
				if (statement != null) {
					statement.close();
					statement = null;
				}
				if (connection != null) {
					connection.close();
					connection = null;
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		
		return elevatorTypeList;
	}

	/**
	 * ��ȡexcel�в�����Ϣ
	 */
	@Override
	public List<ParamViewTableBean> getParamExcelList(String excelPath, List<String> elevatorTypeList, JTextField textField_ElevatorType) {
		paramInfoOFromExcelList.clear();
		FileInputStream fileInputStream;
		List<String> paramCodeList = new ArrayList<String>();// �洢��������
		try {
			fileInputStream = new FileInputStream(excelPath);
			InputStream inputStream = new BufferedInputStream(fileInputStream);
			XSSFWorkbook wookbook = new XSSFWorkbook(inputStream);
			XSSFSheet sheet = wookbook.getSheet("UDS");
			Cell cell = sheet.getRow(9).getCell(3);
			cell.setCellType(Cell.CELL_TYPE_STRING);
			String productType = cell.toString();
			boolean isExist = false;
			for (int i = 0; i < elevatorTypeList.size(); i++) {
				if (elevatorTypeList.get(i).equals(productType)) {
					isExist = true;
				}
			}
			if (isExist) {
				// ��ʾ����
				textField_ElevatorType.setText(productType);
				// ����excel��ȡ������Ϣ ��ÿ�еĲ������ơ��������롢����ֵ�Ͳ���������Bean
				for (int i = 1; i < sheet.getLastRowNum() + 1; i++) {
					ParamViewTableBean bean = new ParamViewTableBean();
					for (int j = 1; j < 5; j++) {
						cell = sheet.getRow(i).getCell(j);
						if (j == 1 || j == 3) {
							cell.setCellType(Cell.CELL_TYPE_STRING);
						}
						String tempInfo = cell.toString();
						if (j == 1) {
							bean.paramName = tempInfo;
						} else if (j == 2) {
							bean.paramCode = tempInfo;
							paramCodeList.add(tempInfo);
						} else if (j == 3) {
							bean.paramValue = tempInfo;
						} else if (j == 4) {
							bean.paramType = tempInfo;
						}
					}
					paramInfoOFromExcelList.add(bean);
				}
				// �ж��Ƿ����ظ��Ĳ�������
				String tempParamCode = "";
				HashSet<String> paramCodeHashSet = new HashSet<String>();
				for (int i = 0; i < paramCodeList.size() - 1; i++) {
					tempParamCode = paramCodeList.get(i);
					if (!tempParamCode.equals("")) {
						for (int j = i + 1; j < paramCodeList.size(); j++) {
							if (tempParamCode.equals(paramCodeList.get(j))) {
								paramCodeHashSet.add(tempParamCode);
							}
						}
					}
				}
				if (paramCodeHashSet.size() > 0) {
					String repeatCodeString = "";
					for (String temp : paramCodeHashSet) {
						repeatCodeString = repeatCodeString + temp + " ";
					}
					MessageBox.post("Excel�к����ظ���������:" + repeatCodeString, "������Ŀ", MessageBox.ERROR);
					return null;
				} else {
					return paramInfoOFromExcelList;
				}
			} else {
				MessageBox.post("δ�ҵ��ò���¼����е����͡�", "������Ŀ", MessageBox.ERROR);
				return null;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return paramInfoOFromExcelList;
	}

	/**
	 * ��ȡ���ݿ��в�����Ϣ
	 */
	@Override
	public List<ParamViewTableBean> getParamDatabaseList(String selectedElevatorType) {
		List<ParamViewTableBean> paramDatabaseList = new ArrayList<ParamViewTableBean>();
		List<String> ePCIdList = new ArrayList<String>();
		ResultSet resultSet = null;
		Statement statement = null;
		ResultSetMetaData mdData = null;
		String columnName = null;
		String value = null;
		// �����ͱ����ҵ������ʹ����Ӧ������ID
		String queryModelIDSql = "select elevator_model_id from t_elevator_model where elevator_model_code ='" + selectedElevatorType + "'";
		try {
			Class.forName(ConstDefine.TCDB_CLASSNAME);
			connection = DriverManager.getConnection(ConstDefine.TCDB_URL, ConstDefine.TCDB_USER, ConstDefine.TCDB_PASSWORD);
			if (connection == null) {
				MessageBox.post("���ݿ�����ʧ�ܡ�", "����", MessageBox.ERROR);
				return null;
			}
			statement = connection.createStatement();
			resultSet = statement.executeQuery(queryModelIDSql);
			mdData = resultSet.getMetaData();
			while (resultSet.next()) {
				columnName = mdData.getColumnLabel(1);
				value = resultSet.getString(columnName);
			}
			// �����Ͳ�����������ҵ�������ID��Ӧ���е����Ͳ�������ID
			String queryEPCIDSql = "select epc_id from t_epc where elevator_model_id ='" + value + "'";
			resultSet = statement.executeQuery(queryEPCIDSql);
			mdData = resultSet.getMetaData();
			while (resultSet.next()) {
				columnName = mdData.getColumnLabel(1);
				value = resultSet.getString(columnName);
				ePCIdList.add(value);
			}
			if (ePCIdList.size() > 0) {
				// �����Ͳ������в��Ҷ�Ӧ�Ĳ�����Ϣ
				for (int i = 0; i < ePCIdList.size(); i++) {
					String queryParamDatabaseSql = "select * from t_elevator_param where epc_id ='" + ePCIdList.get(i) + "'";
					resultSet = statement.executeQuery(queryParamDatabaseSql);
					mdData = resultSet.getMetaData();
					while (resultSet.next()) {
						ParamViewTableBean bean = new ParamViewTableBean();
						columnName = mdData.getColumnLabel(3);
						bean.paramCode = resultSet.getString(columnName);
						columnName = mdData.getColumnLabel(4);
						bean.paramName = resultSet.getString(columnName);
						columnName = mdData.getColumnLabel(5);
						bean.paramType = resultSet.getString(columnName);
						if (bean.paramType.equals("Double")) {
							bean.paramType = "ʵ��";
						} else if (bean.paramType.equals("Int")) {
							bean.paramType = "����";
						} else if (bean.paramType.equals("Text")) {
							bean.paramType = "�ı�";
						}
						columnName = mdData.getColumnLabel(6);
						bean.paramValue = resultSet.getString(columnName);
						paramDatabaseList.add(bean);
					}
				}
			}
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if (resultSet != null) {
					resultSet.close();
					resultSet = null;
				}
				if (statement != null) {
					statement.close();
					statement = null;
				}
				if (connection != null) {
					connection.close();
					connection = null;
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return paramDatabaseList;
	}

	/**
	 * ���߱Ƚ�
	 */
	@Override
	public List<ParamViewTableBean> compareInfo(List<ParamViewTableBean> paramInfoFromExcelList,
			List<ParamViewTableBean> paramInfoFromDatabaseList) {
		for (int i = 0; i < paramInfoFromDatabaseList.size(); i++) {
			for (int j = 0; j < paramInfoFromExcelList.size(); j++) {
				if (paramInfoFromDatabaseList.get(i).paramCode.equals(paramInfoFromExcelList.get(j).paramCode)) {
					paramInfoFromDatabaseList.get(i).paramValue = paramInfoFromExcelList.get(j).paramValue;
				}
			}
		}
		return paramInfoFromDatabaseList;
	}

	/**
	 * ��ʾ����
	 */
	@Override
	public void showData(DefaultTableModel configarationModel, List<ParamViewTableBean> paramList) {
		// JTable�е����ݾ�����ʾ
		for (int i = 0; i < paramList.size(); i++) {
			configarationModel.addRow(new String[] { paramList.get(i).paramCode, paramList.get(i).paramName, paramList.get(i).paramValue,
					paramList.get(i).paramType });
		}
	}

	/**
	 * �������õ�Item
	 */
	@Override
	public TCComponentItem createItem(TCSession session, String itemType, String itemID, String itemName, String description) {
		try {
			TCComponentItemType item_type = (TCComponentItemType) session.getTypeComponent(itemType);
			String newRev = item_type.getNewRev(null);
			TCComponentItem newItem = item_type.create(itemID, newRev, itemType, itemName, description, null); // itemId�Ѵ��ھͻᱨ��
			return newItem;
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * �������ݼ����������õ���
	 */
	@Override
	public void createDataSet(TCSession session, String localFile, String dataSetType, String datasetNamedRef, String dataSetName,
			TCComponentItemRevision itemRevision, String relationType, boolean replaceAlert) {
		try {
			String filePathNames[] = { localFile };
			String namedRefs[] = { datasetNamedRef };
			TCTypeService typeService = session.getTypeService();
			TCComponentDatasetType TCDatasetType = (TCComponentDatasetType) typeService.getTypeComponent(dataSetType);
			TCComponentDataset newDataset = TCDatasetType.setFiles(dataSetName, "Created by program.", dataSetType, filePathNames,
					namedRefs);
			itemRevision.add(relationType, newDataset);
		} catch (TCException e) {
			e.printStackTrace();
		}
	}

	/**
	 * ��������Ϣд�����ݿ�
	 */
	@Override
	public boolean getInfoTodatabase(TCComponentItem configurationListItem, List<ParamViewTableBean> paramInfoFromExcelList,
			List<ParamViewTableBean> paramInfoFromDatabaseList, TCSession session) {
		String cfgListItemId = null;// ���õ����
		String cfgListItemRev = null; // ���õ��汾
		String createdDate = null; // ��������
		String createdUser = null; // ������
		ResultSet resultSet = null;
		Statement statement = null;
		PreparedStatement pstmt = null;
		String contractNo = paramInfoFromExcelList.get(0).paramValue;// ��ͬ��
		String deviceNo = paramInfoFromExcelList.get(1).paramValue;// �豸��
		String projectName = paramInfoFromExcelList.get(3).paramValue;// ��Ŀ��
		String productType = paramInfoFromExcelList.get(8).paramValue;// ��Ʒ�ͺ�
		try {
			cfgListItemId = configurationListItem.getProperty("item_id");
			cfgListItemRev = configurationListItem.getLatestItemRevision().getProperty("item_revision_id");
			createdDate = configurationListItem.getProperty("creation_date");
			createdUser = session.getUserName();
		} catch (TCException e1) {
			e1.printStackTrace();
		}
		// ������д�����õ���
		String insertCFGListInfoSql = "insert into t_configurationlist(configurationlist_id,configurationlist_rev,contract_no,device_no,"
				+ "project_name,product_type,creation_user,creation_date) values('" + cfgListItemId + "','" + cfgListItemRev + "','"
				+ contractNo + "','" + deviceNo + "','" + projectName + "','" + productType + "','" + createdUser + "','" + createdDate
				+ "')"; // �������ͺű�����������ͺŴ���
		try {
			Class.forName(ConstDefine.TCDB_CLASSNAME);
			connection = DriverManager.getConnection(ConstDefine.TCDB_URL, ConstDefine.TCDB_USER, ConstDefine.TCDB_PASSWORD);
			if (connection == null) {
				MessageBox.post("���ݿ�����ʧ�ܡ�", "����", MessageBox.ERROR);
				return false;
			}
			pstmt = connection.prepareStatement(insertCFGListInfoSql);
			pstmt.executeUpdate(insertCFGListInfoSql);
			for (int i = 0; i < paramInfoFromDatabaseList.size(); i++) {
				// ������д�����õ�������
				String insertCFGListParamInfoSql = "insert into t_configurationlist_parameter(param_code,configurationlist_id,param_name,"
						+ "param_value,param_classification,creation_user,creation_date) values('"
						+ paramInfoFromDatabaseList.get(i).paramCode + "','" + cfgListItemId + "','"
						+ paramInfoFromDatabaseList.get(i).paramName + "','" + paramInfoFromDatabaseList.get(i).paramValue + "','"
						+ paramInfoFromDatabaseList.get(i).paramType + "','" + createdUser + "','" + createdDate + "')";
				pstmt = connection.prepareStatement(insertCFGListParamInfoSql);
				pstmt.executeUpdate(insertCFGListParamInfoSql);
			}
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			return false;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		} finally {
			try {
				if (resultSet != null) {
					resultSet.close();
					resultSet = null;
				}
				if (statement != null) {
					statement.close();
					statement = null;
				}
				if (connection != null) {
					connection.close();
					connection = null;
				}
			} catch (SQLException e) {
				e.printStackTrace();
				return false;
			}
		}
		return true;
	}
}
