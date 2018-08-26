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
				MessageBox.post("数据库连接失败。", "出错", MessageBox.ERROR);
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
			MessageBox.post(e.getMessage(), "从EAP获取配置单", MessageBox.ERROR);
			return null;
		} catch (SQLException e) {
			e.printStackTrace();
			MessageBox.post(e.getMessage(), "从EAP获取配置单", MessageBox.ERROR);
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
	 * 获取所有梯种型号代码
	 */
	@Override
	public List<String> queryElevatorTypeList() {
		List<String> elevatorTypeList = new ArrayList<String>();
		String sql = "select * from t_elevator_model"; // 从梯种型号表里查找梯种型号代码
		try {
			Class.forName(ConstDefine.TCDB_CLASSNAME);
			connection = DriverManager.getConnection(ConstDefine.TCDB_URL, ConstDefine.TCDB_USER, ConstDefine.TCDB_PASSWORD);
			if (connection == null) {
				MessageBox.post("数据库连接失败。", "出错", MessageBox.ERROR);
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
			MessageBox.post(e.getMessage(), "导入项目", MessageBox.ERROR);
			return null;
		} catch (SQLException e) {
			e.printStackTrace();
			MessageBox.post(e.getMessage(), "导入项目", MessageBox.ERROR);
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
	 * 获取excel中参数信息
	 */
	@Override
	public List<ParamViewTableBean> getParamExcelList(String excelPath, List<String> elevatorTypeList, JTextField textField_ElevatorType) {
		paramInfoOFromExcelList.clear();
		FileInputStream fileInputStream;
		List<String> paramCodeList = new ArrayList<String>();// 存储参数代号
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
				// 显示梯型
				textField_ElevatorType.setText(productType);
				// 遍历excel获取参数信息 将每行的参数名称、参数代码、参数值和参数类别存入Bean
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
				// 判断是否有重复的参数代号
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
					MessageBox.post("Excel中含有重复参数代号:" + repeatCodeString, "导入项目", MessageBox.ERROR);
					return null;
				} else {
					return paramInfoOFromExcelList;
				}
			} else {
				MessageBox.post("未找到该参数录入表中的梯型。", "导入项目", MessageBox.ERROR);
				return null;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return paramInfoOFromExcelList;
	}

	/**
	 * 获取数据库中参数信息
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
		// 在梯型表中找到该梯型代码对应的梯型ID
		String queryModelIDSql = "select elevator_model_id from t_elevator_model where elevator_model_code ='" + selectedElevatorType + "'";
		try {
			Class.forName(ConstDefine.TCDB_CLASSNAME);
			connection = DriverManager.getConnection(ConstDefine.TCDB_URL, ConstDefine.TCDB_USER, ConstDefine.TCDB_PASSWORD);
			if (connection == null) {
				MessageBox.post("数据库连接失败。", "出错", MessageBox.ERROR);
				return null;
			}
			statement = connection.createStatement();
			resultSet = statement.executeQuery(queryModelIDSql);
			mdData = resultSet.getMetaData();
			while (resultSet.next()) {
				columnName = mdData.getColumnLabel(1);
				value = resultSet.getString(columnName);
			}
			// 在梯型参数分类表中找到该梯型ID对应所有的梯型参数分类ID
			String queryEPCIDSql = "select epc_id from t_epc where elevator_model_id ='" + value + "'";
			resultSet = statement.executeQuery(queryEPCIDSql);
			mdData = resultSet.getMetaData();
			while (resultSet.next()) {
				columnName = mdData.getColumnLabel(1);
				value = resultSet.getString(columnName);
				ePCIdList.add(value);
			}
			if (ePCIdList.size() > 0) {
				// 在梯型参数表中查找对应的参数信息
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
							bean.paramType = "实数";
						} else if (bean.paramType.equals("Int")) {
							bean.paramType = "整数";
						} else if (bean.paramType.equals("Text")) {
							bean.paramType = "文本";
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
	 * 两者比较
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
	 * 显示数据
	 */
	@Override
	public void showData(DefaultTableModel configarationModel, List<ParamViewTableBean> paramList) {
		// JTable中的数据居中显示
		for (int i = 0; i < paramList.size(); i++) {
			configarationModel.addRow(new String[] { paramList.get(i).paramCode, paramList.get(i).paramName, paramList.get(i).paramValue,
					paramList.get(i).paramType });
		}
	}

	/**
	 * 创建配置单Item
	 */
	@Override
	public TCComponentItem createItem(TCSession session, String itemType, String itemID, String itemName, String description) {
		try {
			TCComponentItemType item_type = (TCComponentItemType) session.getTypeComponent(itemType);
			String newRev = item_type.getNewRev(null);
			TCComponentItem newItem = item_type.create(itemID, newRev, itemType, itemName, description, null); // itemId已存在就会报错
			return newItem;
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * 创建数据集并挂在配置单下
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
	 * 将参数信息写入数据库
	 */
	@Override
	public boolean getInfoTodatabase(TCComponentItem configurationListItem, List<ParamViewTableBean> paramInfoFromExcelList,
			List<ParamViewTableBean> paramInfoFromDatabaseList, TCSession session) {
		String cfgListItemId = null;// 配置单编号
		String cfgListItemRev = null; // 配置单版本
		String createdDate = null; // 创建日期
		String createdUser = null; // 所有者
		ResultSet resultSet = null;
		Statement statement = null;
		PreparedStatement pstmt = null;
		String contractNo = paramInfoFromExcelList.get(0).paramValue;// 合同号
		String deviceNo = paramInfoFromExcelList.get(1).paramValue;// 设备号
		String projectName = paramInfoFromExcelList.get(3).paramValue;// 项目名
		String productType = paramInfoFromExcelList.get(8).paramValue;// 产品型号
		try {
			cfgListItemId = configurationListItem.getProperty("item_id");
			cfgListItemRev = configurationListItem.getLatestItemRevision().getProperty("item_revision_id");
			createdDate = configurationListItem.getProperty("creation_date");
			createdUser = session.getUserName();
		} catch (TCException e1) {
			e1.printStackTrace();
		}
		// 将数据写入配置单表
		String insertCFGListInfoSql = "insert into t_configurationlist(configurationlist_id,configurationlist_rev,contract_no,device_no,"
				+ "project_name,product_type,creation_user,creation_date) values('" + cfgListItemId + "','" + cfgListItemRev + "','"
				+ contractNo + "','" + deviceNo + "','" + projectName + "','" + productType + "','" + createdUser + "','" + createdDate
				+ "')"; // 从梯种型号表里查找梯种型号代码
		try {
			Class.forName(ConstDefine.TCDB_CLASSNAME);
			connection = DriverManager.getConnection(ConstDefine.TCDB_URL, ConstDefine.TCDB_USER, ConstDefine.TCDB_PASSWORD);
			if (connection == null) {
				MessageBox.post("数据库连接失败。", "出错", MessageBox.ERROR);
				return false;
			}
			pstmt = connection.prepareStatement(insertCFGListInfoSql);
			pstmt.executeUpdate(insertCFGListInfoSql);
			for (int i = 0; i < paramInfoFromDatabaseList.size(); i++) {
				// 将数据写入配置单参数表
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
