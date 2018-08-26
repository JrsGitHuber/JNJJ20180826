package com.uds.sjec.service.impl;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.JButton;
import javax.swing.table.DefaultTableModel;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import com.teamcenter.rac.aif.AIFDesktop;
import com.teamcenter.rac.aif.kernel.AIFComponentContext;
import com.teamcenter.rac.kernel.TCComponent;
import com.teamcenter.rac.kernel.TCComponentBOMLine;
import com.teamcenter.rac.kernel.TCComponentBOMWindow;
import com.teamcenter.rac.kernel.TCComponentItem;
import com.teamcenter.rac.kernel.TCComponentQuery;
import com.teamcenter.rac.kernel.TCComponentTask;
import com.teamcenter.rac.kernel.TCException;
import com.teamcenter.rac.kernel.TCPreferenceService;
import com.teamcenter.rac.kernel.TCSession;
import com.teamcenter.rac.util.MessageBox;
import com.uds.common.utils.MathUtil;
import com.uds.sjec.bean.CalculationTableBean;
import com.uds.sjec.bean.CfgListInfoTableBean;
import com.uds.sjec.bean.ParamReadedBean;
import com.uds.sjec.common.ConstDefine;
import com.uds.sjec.service.ICfgManagementService;
import com.uds.sjec.utils.ItemUtil;
import com.uds.sjec.utils.QueryUtil;

public class CfgManagementServiceImpl implements ICfgManagementService {

	private String querySql;
	private String columName;
	private Connection connection;
	private ResultSet resultSet;
	private Statement statement;
	private ResultSetMetaData resultSetMetaData;
	private List<CfgListInfoTableBean> cfgListInfoTableList;
	private List<ParamReadedBean> paramReadedTableList;
	private List<CalculationTableBean> calculationList;

	/**
	 * 搜素配置单
	 * 
	 * @param query_class
	 */
	@Override
	public void searchCfgList(DefaultTableModel configarationModel, String searchId, String searchIdType, String taskStatu,
			String startedTime, String finishedTime, String workFlowName) {
		// 获取配置单信息
		cfgListInfoTableList = new ArrayList<CfgListInfoTableBean>();
		searchId = searchId.replace("*", "%");
		try {
			Class.forName(ConstDefine.TCDB_CLASSNAME);
			connection = DriverManager.getConnection(ConstDefine.TCDB_URL, ConstDefine.TCDB_USER, ConstDefine.TCDB_PASSWORD);
			if (connection == null) {
				MessageBox.post("数据库连接失败。", "出错", MessageBox.ERROR);
				return;
			}
			statement = connection.createStatement();
			if (taskStatu.equals("未开始")) {
				if (searchIdType.equals("配置单号")) {
					querySql = "select * from t_configurationlist where configurationlist_id like'" + searchId + "'";
				} else if (searchIdType.equals("合同号")) {
					querySql = "select * from t_configurationlist where contract_no like'" + searchId + "'";
				} else if (searchIdType.equals("设备号")) {
					querySql = "select * from t_configurationlist where device_no like'" + searchId + "'";
				}
				resultSet = statement.executeQuery(querySql);
				while (resultSet.next()) {
					CfgListInfoTableBean bean = new CfgListInfoTableBean();
					bean.cfgID = resultSet.getString("configurationlist_id");
					bean.cfgRevision = resultSet.getString("configurationlist_rev");
					bean.projectName = resultSet.getString("project_name");
					bean.createUser = resultSet.getString("creation_user");
					// 通过itemID查询配置单
					TCComponentQuery query = QueryUtil.getTCComponentQuery("Item...");
					TCComponent[] searchResult = QueryUtil.getSearchResult(query, new String[] { "零组件 ID" }, new String[] { bean.cfgID });
					if (searchResult.length > 0) {
						TCComponentItem cfgListItem = (TCComponentItem) searchResult[0];
						boolean unStarted = true;
						try {
							AIFComponentContext[] whereReference = cfgListItem.getLatestItemRevision().whereReferenced();
							for (AIFComponentContext context : whereReference) {
								if (context.getComponent() instanceof TCComponentTask) {
									TCComponentTask task = (TCComponentTask) context.getComponent();
									if (task.getProperty("current_name").equals(workFlowName)) {
										unStarted = false;
										break;
									}
								}
							}
						} catch (TCException e) {
							e.printStackTrace();
						}
						if (unStarted) {
							bean.createTime = resultSet.getString("creation_date");
							if (bean.createTime.compareTo(startedTime) >= 0 && bean.createTime.compareTo(finishedTime) <= 0) {
								bean.status = "未开始";
								cfgListInfoTableList.add(bean);
							}
						}
					}
				}
			} else if (taskStatu.equals("流程中")) {
				if (searchIdType.equals("配置单号")) {
					querySql = "select * from t_configurationlist where configurationlist_id like'" + searchId + "'";
				} else if (searchIdType.equals("合同号")) {
					querySql = "select * from t_configurationlist where contract_no like'" + searchId + "'";
				} else if (searchIdType.equals("设备号")) {
					querySql = "select * from t_configurationlist where device_no like'" + searchId + "'";
				}
				resultSet = statement.executeQuery(querySql);
				while (resultSet.next()) {
					CfgListInfoTableBean bean = new CfgListInfoTableBean();
					bean.cfgID = resultSet.getString("configurationlist_id");
					bean.cfgRevision = resultSet.getString("configurationlist_rev");
					bean.projectName = resultSet.getString("project_name");
					bean.createUser = resultSet.getString("creation_user");
					bean.createTime = resultSet.getString("creation_date");
					// 通过itemID查询配置单
					TCComponentQuery query = QueryUtil.getTCComponentQuery("Item...");
					TCComponent[] searchResult = QueryUtil.getSearchResult(query, new String[] { "零组件 ID" }, new String[] { bean.cfgID });
					if (searchResult.length > 0) {
						TCComponentItem cfgListItem = (TCComponentItem) searchResult[0];
						try {
							AIFComponentContext[] whereReference = cfgListItem.getLatestItemRevision().whereReferenced();
							for (AIFComponentContext context : whereReference) {
								if (context.getComponent() instanceof TCComponentTask) {
									// TODO Jr 可能需要改动，需要和实施沟通
									TCComponentTask task = (TCComponentTask) context.getComponent();
									if (task.getProperty("current_name").equals(workFlowName)) { // 找到这个流程，取出来流程的一些属性界面显示
										AIFComponentContext[] childTaskContexts = task.getRelated("child_tasks");
										for (AIFComponentContext tempContext : childTaskContexts) {
											TCComponentTask tempTask = (TCComponentTask) tempContext.getComponent();
											if (tempTask.getTaskType().equals("EPMDoTask")
													&& tempTask.getProperty("current_name").equals("编制")) {// 编制
												bean.sponsorTime = tempTask.getProperty("creation_date"); // 流程发起时间
												bean.releaseTime = tempTask.getProperty("fnd0EndDate"); // 流程发布时间
												if ((bean.sponsorTime.compareTo(startedTime) >= 0)
														&& (bean.sponsorTime.compareTo(finishedTime) <= 0) && bean.releaseTime.equals("")) {
													bean.status = "流程中";
													cfgListInfoTableList.add(bean);
												}
											}
										}
										break;
									}
								}
							}
						} catch (TCException e) {
							e.printStackTrace();
						}
					}
				}
			} else if (taskStatu.equals("已完成")) {
				if (startedTime.compareTo(finishedTime) < 0 && !startedTime.equals(finishedTime)) {
					if (searchIdType.equals("配置单号")) {
						querySql = "select * from t_configurationlist where configurationlist_id like'" + searchId + "'";
					} else if (searchIdType.equals("合同号")) {
						querySql = "select * from t_configurationlist where contract_no like'" + searchId + "'";
					} else if (searchIdType.equals("设备号")) {
						querySql = "select * from t_configurationlist where device_no like'" + searchId + "'";
					}
					resultSet = statement.executeQuery(querySql);
					while (resultSet.next()) {
						CfgListInfoTableBean bean = new CfgListInfoTableBean();
						bean.cfgID = resultSet.getString("configurationlist_id");
						bean.cfgRevision = resultSet.getString("configurationlist_rev");
						bean.projectName = resultSet.getString("project_name");
						bean.createUser = resultSet.getString("creation_user");
						bean.createTime = resultSet.getString("creation_date");
						// 通过itemID查询配置单
						TCComponentQuery query = QueryUtil.getTCComponentQuery("Item...");
						TCComponent[] searchResult = QueryUtil.getSearchResult(query, new String[] { "零组件 ID" },
								new String[] { bean.cfgID });
						if (searchResult.length > 0) {
							TCComponentItem cfgListItem = (TCComponentItem) searchResult[0];
							try {
								AIFComponentContext[] whereReference = cfgListItem.getLatestItemRevision().whereReferenced();
								for (AIFComponentContext context : whereReference) {
									if (context.getComponent() instanceof TCComponentTask) {
										TCComponentTask task = (TCComponentTask) context.getComponent();
										if (task.getProperty("current_name").equals(workFlowName)) {
											AIFComponentContext[] childTaskContexts = task.getRelated("child_tasks");
											for (AIFComponentContext tempContext : childTaskContexts) {
												TCComponentTask tempTask = (TCComponentTask) tempContext.getComponent();
												if (tempTask.getTaskType().equals("EPMDoTask")
														&& tempTask.getProperty("current_name").equals("编制")) {// 编制
													bean.sponsorTime = tempTask.getProperty("creation_date"); // 流程发起时间
													bean.releaseTime = tempTask.getProperty("fnd0EndDate"); // 流程发布时间
													if ((bean.releaseTime.compareTo(startedTime) >= 0)
															&& (bean.releaseTime.compareTo(finishedTime) <= 0)
															&& !bean.releaseTime.equals("")) {
														bean.status = "已完成";
														cfgListInfoTableList.add(bean);
													}
												}
											}
											break;
										}
									}
								}
							} catch (TCException e) {
								e.printStackTrace();
							}
						}
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
		// 显示数据
		if (cfgListInfoTableList.size() > 0) {
			// TODO Jr 或需要改成按照其他排序
			// 按流水号排序
			Collections.sort(cfgListInfoTableList, new Comparator<CfgListInfoTableBean>() {
				public int compare(CfgListInfoTableBean bean1, CfgListInfoTableBean bean2) {
					if (bean1.getCfgID().compareTo(bean2.getCfgID()) > 0) {
						return -1;
					}
					return 1;
				}
			});
			for (int i = 0; i < cfgListInfoTableList.size(); i++) {
				configarationModel.addRow(new String[] { cfgListInfoTableList.get(i).cfgID, cfgListInfoTableList.get(i).cfgRevision,
						cfgListInfoTableList.get(i).projectName, cfgListInfoTableList.get(i).createUser,
						cfgListInfoTableList.get(i).createTime, cfgListInfoTableList.get(i).sponsorTime,
						cfgListInfoTableList.get(i).releaseTime, cfgListInfoTableList.get(i).status });
			}
		} else {
			MessageBox.post("未找到相关配置单信息。", "配置单管理", MessageBox.WARNING);
		}
	}

	/**
	 * 参数读取
	 */
	@Override
	public List<ParamReadedBean> getParamReadedList(String cfgListId) {
		// 获取配置单信息
		paramReadedTableList = new ArrayList<ParamReadedBean>();
		try {
			Class.forName(ConstDefine.TCDB_CLASSNAME);
			connection = DriverManager.getConnection(ConstDefine.TCDB_URL, ConstDefine.TCDB_USER, ConstDefine.TCDB_PASSWORD);
			String paramReadedSql = "select * from t_configurationlist_parameter where configurationlist_id ='" + cfgListId + "'";
			statement = connection.createStatement();
			resultSet = statement.executeQuery(paramReadedSql);
			resultSetMetaData = resultSet.getMetaData();
			while (resultSet.next()) {
				ParamReadedBean bean = new ParamReadedBean();
				// 参数代码
				columName = resultSetMetaData.getColumnLabel(2);
				bean.paramCode = resultSet.getString(columName);
				// 参数名称
				columName = resultSetMetaData.getColumnLabel(3);
				bean.paramName = resultSet.getString(columName);
				// 参数值
				columName = resultSetMetaData.getColumnLabel(4);
				bean.paramValue = resultSet.getString(columName);
				// 参数范围
				String queryParamID = "select elevator_param_id from t_elevator_param where param_code='" + bean.paramCode + "'";
				Statement statement2 = connection.createStatement();
				ResultSet resultSet2 = statement2.executeQuery(queryParamID);
				ResultSetMetaData resultSetMetaData2 = resultSet2.getMetaData();
				while (resultSet2.next()) {
					columName = resultSetMetaData2.getColumnLabel(1);
					String paramID = resultSet2.getString(columName);
					String queryElevatorParamSql = "select range_value from t_elevator_param_range where elevator_param_id='" + paramID
							+ "'";
					connection = DriverManager.getConnection(ConstDefine.TCDB_URL, ConstDefine.TCDB_USER, ConstDefine.TCDB_PASSWORD);
					Statement statement3 = connection.createStatement();
					ResultSet resultSet3 = statement3.executeQuery(queryElevatorParamSql);
					ResultSetMetaData resultSetMetaData3 = resultSet3.getMetaData();
					String paramRange = "";
					while (resultSet3.next()) {
						columName = resultSetMetaData3.getColumnLabel(1);
						paramRange = paramRange + resultSet3.getString(columName) + ";";
						bean.rangeOfParamValue = paramRange;
					}
					if(bean.rangeOfParamValue == null || bean.rangeOfParamValue.equals("")){
						continue;
					}
					bean.rangeOfParamValue = bean.rangeOfParamValue.substring(0, bean.rangeOfParamValue.length() - 1);
				}
				paramReadedTableList.add(bean);
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
		return paramReadedTableList;
	}

	/**
	 * 显示参数
	 */
	@Override
	public void showParamReadedTable(DefaultTableModel configarationModel, List<ParamReadedBean> paramReadedList) {
		// JTable中的数据居中显示
		for (int i = 0; i < paramReadedTableList.size(); i++) {
			configarationModel.addRow(new String[] { paramReadedTableList.get(i).paramCode, paramReadedTableList.get(i).paramName,
					paramReadedTableList.get(i).paramValue, paramReadedTableList.get(i).rangeOfParamValue, });
		}
	}

	/**
	 * 下载excel到本地
	 */
	@SuppressWarnings({ "deprecation", "static-access" })
	@Override
	public String downLoadExcelToDir(String preferenceName, String configListId, String configListRev) {
		String dirPath = System.getProperty("java.io.tmpdir");
		// 获取首选项路径
		TCSession session = (TCSession) AIFDesktop.getActiveDesktop().getCurrentApplication().getSession();
		TCPreferenceService preferenceService = session.getPreferenceService();
		String UDSCodeConfigPath = preferenceService.getString(preferenceService.TC_preference_user, preferenceName);
		if ((UDSCodeConfigPath == null) || (UDSCodeConfigPath.equals(""))) {
			UDSCodeConfigPath = preferenceService.getString(TCPreferenceService.TC_preference_site, preferenceName);
		}
		if ((UDSCodeConfigPath == null) || (UDSCodeConfigPath.equals(""))) {
			MessageBox.post("请配置首选项:<计算项代号>文件路径", "参数计算", MessageBox.ERROR);
			return null;
		}
		if (!UDSCodeConfigPath.endsWith("\\")) {
			UDSCodeConfigPath = UDSCodeConfigPath + "\\";
		}
		String targetFilePath = dirPath + "SJEC电梯计算项列表_V1.0.xlsx"; // 目标文件路径
		File oldFile = new File(dirPath + configListId + configListRev + ".xlsx");
		if (oldFile.exists()) {
			oldFile.delete();
		}
		try {
			Files.copy(new File(UDSCodeConfigPath).toPath(), new File(targetFilePath).toPath(), StandardCopyOption.REPLACE_EXISTING);
			// 改文件名
			File file = new File(targetFilePath);
			if (file.exists()) {
				file.renameTo(new File(dirPath + configListId + configListRev + ".xlsx"));
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return dirPath + configListId + configListRev + ".xlsx";
	}

	/**
	 * 参数计算
	 */
	@Override
	public void paramCalculated(List<ParamReadedBean> paramReadedList, String excelpath, String configListId, JButton button_paramCaculate,
			JButton button_BOMConfiguration) {
		calculationList = new ArrayList<CalculationTableBean>();
		try {
			// 写入
			BufferedInputStream inputStream = new BufferedInputStream(new FileInputStream(excelpath));
			XSSFWorkbook workbook = new XSSFWorkbook(inputStream);
			XSSFSheet paramSheet = workbook.getSheet("参数表");
			for (int i = 0; i < paramReadedList.size(); i++) {
				for (int j = 2; j < paramSheet.getLastRowNum() + 1; j++) {
					Cell paramCodeCell = paramSheet.getRow(j).getCell(2);
					paramCodeCell.setCellType(Cell.CELL_TYPE_STRING);
					if (paramCodeCell.toString().equals(paramReadedList.get(i).paramCode)) {
						Cell paramVauleCell = paramSheet.getRow(j).getCell(3);
						paramVauleCell.setCellValue(paramReadedList.get(i).paramValue);
						break;
					}
				}
			}
			FileOutputStream fileOut = new FileOutputStream(excelpath);
			workbook.write(fileOut);
			fileOut.close();
			// 读取计算项列表，写入数据库
			inputStream = new BufferedInputStream(new FileInputStream(excelpath));
			workbook = new XSSFWorkbook(inputStream);
			XSSFSheet calculationSheet = workbook.getSheet("计算项列表");
			for (int i = 4; i < calculationSheet.getLastRowNum() + 1; i++) {
				CalculationTableBean bean = new CalculationTableBean();
				bean.configID = configListId;
				Cell cell = calculationSheet.getRow(i).getCell(1);
				cell.setCellType(Cell.CELL_TYPE_STRING);
				bean.conditionCode = cell.toString();
				cell = calculationSheet.getRow(i).getCell(4);
				cell.setCellType(Cell.CELL_TYPE_STRING);
				bean.calculationValue = cell.toString();

				calculationList.add(bean);
			}
			fileOut = new FileOutputStream(excelpath);
			workbook.write(fileOut);
			fileOut.close();
			Class.forName(ConstDefine.TCDB_CLASSNAME);
			connection = DriverManager.getConnection(ConstDefine.TCDB_URL, ConstDefine.TCDB_USER, ConstDefine.TCDB_PASSWORD);
			if (connection == null) {
				MessageBox.post("数据库连接失败。", "出错", MessageBox.ERROR);
				return;
			}
			if (calculationList.size() > 0) {
				// 先删除掉该配置单对应的参数信息
				String deleteSql = "delete from t_condition where configlist_id ='" + configListId + "'";
				statement = connection.createStatement();
				statement.executeUpdate(deleteSql);
				for (int i = 0; i < calculationList.size(); i++) {
					// 将数据写入公式表
					String insertSql = "insert into t_condition(configlist_id,condition_code,condition_value) values('"
							+ calculationList.get(i).configID + "','" + calculationList.get(i).conditionCode + "','"
							+ calculationList.get(i).calculationValue + "')";
					statement = connection.createStatement();
					statement.executeUpdate(insertSql);
				}
				button_paramCaculate.setEnabled(false);
				MessageBox.post("计算完成。", "参数计算", MessageBox.INFORMATION);
				button_BOMConfiguration.setEnabled(true);
			} else {
				MessageBox.post("计算项列表没有数据。", "参数计算", MessageBox.ERROR);
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			MessageBox.post(e.getMessage(), "参数计算", MessageBox.ERROR);
			return;
		} catch (IOException e) {
			e.printStackTrace();
			MessageBox.post(e.getMessage(), "参数计算", MessageBox.ERROR);
			return;
		} catch (SQLException e) {
			e.printStackTrace();
			MessageBox.post(e.getMessage(), "参数计算", MessageBox.ERROR);
			return;
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			MessageBox.post(e.getMessage(), "参数计算", MessageBox.ERROR);
			return;
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
	}

	/**
	 * 通过配置单号查找对应的型号
	 */
	@Override
	public String queryProductType(String configListId) {
		try {
			Class.forName(ConstDefine.TCDB_CLASSNAME);
			connection = DriverManager.getConnection(ConstDefine.TCDB_URL, ConstDefine.TCDB_USER, ConstDefine.TCDB_PASSWORD);
			if (connection == null) {
				MessageBox.post("数据库连接失败。", "出错", MessageBox.ERROR);
				return null;
			}
			statement = connection.createStatement();
			String querySql = "select product_type from t_configurationlist where configurationlist_id=" + "'" + configListId + "'";
			ResultSet rs = statement.executeQuery(querySql);
			ResultSetMetaData resultSetMetaData = rs.getMetaData();
			while (rs.next()) {
				String colName = resultSetMetaData.getColumnLabel(1);
				String productType = rs.getString(colName);
				return productType;
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
		return null;
	}

	/**
	 * 将从参数读取中获取的参数信息
	 */
	@Override
	public Map<String, String> getBOMConfigParamInfo(List<ParamReadedBean> paramReadedList) {
		Map<String, String> paramMap = new HashMap<String, String>();
		for (int i = 0; i < paramReadedList.size(); i++) {
			paramMap.put(paramReadedList.get(i).paramCode, paramReadedList.get(i).paramValue);
		}
		return paramMap;
	}

	/**
	 * 读取计算项列表
	 */
	public Map<String, String> getComputationMap(String cfgItemId) {
		Map<String, String> computationMap = new HashMap<String, String>();
		String sql = "select condition_code,condition_value from t_condition where configlist_id ='" + cfgItemId + "'";
		try {
			Connection connection = DriverManager.getConnection(ConstDefine.TCDB_URL, ConstDefine.TCDB_USER, ConstDefine.TCDB_PASSWORD);
			if (connection == null) {
				MessageBox.post("数据库连接失败。", "出错", MessageBox.ERROR);
				return null;
			}
			PreparedStatement pstm = connection.prepareStatement(sql);
			resultSet = pstm.executeQuery(sql);
			System.out.println(resultSet.getRow());
			resultSetMetaData = resultSet.getMetaData();
			while (resultSet.next()) {
				String colName = resultSetMetaData.getColumnLabel(1);
				String conditionCode = resultSet.getString(colName);
				colName = resultSetMetaData.getColumnLabel(2);
				String conditionValue = resultSet.getString(colName);
				computationMap.put(conditionCode, conditionValue);
			}
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
		return computationMap;
	}

	/**
	 * 配置计算
	 */
	@Override
	public void configurantionCalculated(TCComponentBOMLine superTopBOMLine, TCComponentBOMLine configListTopBomLine,
			Map<String, String> paramMap, Map<String, String> computationMap, String configListId, JButton button_BOMInstantiation) {
		try {
			AIFComponentContext[] superBOMContexts = superTopBOMLine.getChildren();
			if (superBOMContexts != null && superBOMContexts.length > 0) {
				for (AIFComponentContext superBOMContext : superBOMContexts) {
					TCComponentBOMLine superBomLine = (TCComponentBOMLine) superBOMContext.getComponent();
					// 获取超级BOMLine的计算项代号
					String computationCode = superBomLine.getProperty("bl_item_s2_C1");
					if (!computationCode.equals("")) {
						String[] computationCodes = computationCode.split("%");
						if (computationCodes.length > 0) {
							// TODO ?
							// 看是否U开头要换配置单号,再处理其他 
							for (String childComputationCode : computationCodes) {
								if (childComputationCode.startsWith("U")) {
									String note1 = superBomLine.getProperty("S2_bl_vc");
									String note2 = superBomLine.getProperty("S2_bl_vc1");
									String note3 = superBomLine.getProperty("S2_bl_vc2");
									String note4 = superBomLine.getProperty("S2_bl_vc3");
									String note5 = superBomLine.getProperty("S2_bl_vc4");
									String variableCondition = note1 + note2 + note3 + note4 + note5;
									boolean isCorrected = false;
									// 将变量条件换算,判断是否符合，做筛选
									if (!variableCondition.equals("")) {
										for (String key : paramMap.keySet()) {
											String keyStr = key + "==";
											if (variableCondition.contains(keyStr)) {
												variableCondition = variableCondition.replaceAll(keyStr, paramMap.get(key)+"==");
											}
										}
										if (MathUtil.PassCondition(variableCondition)) {
											isCorrected = true;
										}
									}
									if (variableCondition.equals("") || isCorrected) {
										// 查找要替换的item是否已经创建
										TCComponentQuery query = QueryUtil.getTCComponentQuery("Item...");
										TCComponent[] searchResult = QueryUtil.getSearchResult(query, new String[] { "零组件 ID" },
												new String[] { superBomLine.getProperty("bl_item_item_id") + "-" + configListId });
										if (searchResult == null || searchResult.length == 0) {
											// 创建新item
											String type = superBomLine.getItem().getType();
											String itemid = superBomLine.getProperty("bl_item_item_id") + "-" + configListId;
											String name = superBomLine.getProperty("bl_item_object_name");
											TCComponentItem newItem = ItemUtil.createtItem(type, itemid, name, "");
											if (newItem != null) {
												TCComponentBOMLine newBomLine = configListTopBomLine.add((TCComponent) newItem, "");
												try {
													transfer(superBomLine, newBomLine, paramMap, computationMap, configListId);
												} catch (Exception e) {
													e.printStackTrace();
													MessageBox.post(e.getMessage(), "配置计算", MessageBox.INFORMATION);
													return;
												}
											}
										}
									}
								}
							}
						}
					} else {// 计算项代号为空
						String note1 = superBomLine.getProperty("S2_bl_vc");
						String note2 = superBomLine.getProperty("S2_bl_vc1");
						String note3 = superBomLine.getProperty("S2_bl_vc2");
						String note4 = superBomLine.getProperty("S2_bl_vc3");
						String note5 = superBomLine.getProperty("S2_bl_vc4");
						String variableCondition = note1 + note2 + note3 + note4 + note5;
						boolean isCorrected = false;
						// 将变量条件换算,判断是否符合，做筛选
						if (!variableCondition.equals("")) {
							for (String key : paramMap.keySet()) {
								String keyStr = key + "==";
								if (variableCondition.contains(keyStr)) {
									variableCondition = variableCondition.replaceAll(keyStr, paramMap.get(key)+"==");
								}
							}
							if (MathUtil.PassCondition(variableCondition)) {
								isCorrected = true;
							}
						}
						if (variableCondition.equals("") || isCorrected) {
							configListTopBomLine.add((TCComponent) superBomLine.getItem(), "");
						}
					}
				}
			}
			TCComponentBOMWindow bomWindow = configListTopBomLine.getCachedWindow();
			bomWindow.save();
			MessageBox.post("配置计算完成。", "配置计算", MessageBox.INFORMATION);
			button_BOMInstantiation.setEnabled(false);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void transfer(TCComponentBOMLine superBomLine, TCComponentBOMLine parentBOMLine, Map<String, String> paramMap,
			Map<String, String> computationMap, String configListId) throws Exception {
		AIFComponentContext[] superBOMContexts = superBomLine.getChildren();
		if (superBOMContexts != null && superBOMContexts.length > 0) {
			for (AIFComponentContext superBOMContext : superBOMContexts) {
				TCComponentBOMLine tempBomLine = (TCComponentBOMLine) superBOMContext.getComponent();
				// 获取超级BOMLine的计算项代号
				String computationCode = tempBomLine.getProperty("bl_item_s2_C1");
				if (!computationCode.equals("")) {
					String[] computationCodes = computationCode.split("%");
					if (computationCodes.length > 0) {
						// TODO Jr 换号的规则在此处进行添加
						// 看是否U开头要换配置单号,再处理其他
						for (String childComputationCode : computationCodes) {
							if (childComputationCode.startsWith("U")) {
								String note1 = tempBomLine.getProperty("S2_bl_vc");
								String note2 = tempBomLine.getProperty("S2_bl_vc1");
								String note3 = tempBomLine.getProperty("S2_bl_vc2");
								String note4 = tempBomLine.getProperty("S2_bl_vc3");
								String note5 = tempBomLine.getProperty("S2_bl_vc4");
								String variableCondition = note1 + note2 + note3 + note4 + note5;
								boolean isCorrected = false;
								// 将变量条件换算,判断是否符合，做筛选
								if (!variableCondition.equals("")) {
									for (String key : paramMap.keySet()) {
										String keyStr = key + "==";
										if (variableCondition.contains(keyStr)) {
											variableCondition = variableCondition.replaceAll(keyStr, paramMap.get(key)+"==");
										}
									}
									if (MathUtil.PassCondition(variableCondition)) {
										isCorrected = true;
									}
								}
								if (variableCondition.equals("") || isCorrected) {
									// 查找要替换的item是否已经创建
									TCComponentQuery query = QueryUtil.getTCComponentQuery("Item...");
									TCComponent[] searchResult = QueryUtil.getSearchResult(query, new String[] { "零组件 ID" },
											new String[] { tempBomLine.getProperty("bl_item_item_id") + "-" + configListId });
									if (searchResult == null || searchResult.length == 0) {
										// 创建新item
										String type = tempBomLine.getItem().getType();
										String itemid = tempBomLine.getProperty("bl_item_item_id") + "-" + configListId;
										String name = tempBomLine.getProperty("bl_item_object_name");
										TCComponentItem newItem = ItemUtil.createtItem(type, itemid, name, "");
										TCComponentBOMLine newBomLine = parentBOMLine.add((TCComponent) newItem, "");
										transfer(tempBomLine, newBomLine, paramMap, computationMap, configListId);
									}
								}
							}
							
						}
						// 其他开头，不换号
						for (String childComputationCode : computationCodes) {
							// TODO Jr 除了换号的，其他的规则在此处进行添加
							if (childComputationCode.startsWith("A")) {
								String note1 = tempBomLine.getProperty("S2_bl_vc");
								String note2 = tempBomLine.getProperty("S2_bl_vc1");
								String note3 = tempBomLine.getProperty("S2_bl_vc2");
								String note4 = tempBomLine.getProperty("S2_bl_vc3");
								String note5 = tempBomLine.getProperty("S2_bl_vc4");
								String variableCondition = note1 + note2 + note3 + note4 + note5;
								boolean isCorrected = false;
								// 将变量条件换算,判断是否符合，做筛选
								if (!variableCondition.equals("")) {
									for (String key : paramMap.keySet()) {
										String keyStr = key + "==";
										if (variableCondition.contains(keyStr)) {
											variableCondition = variableCondition.replaceAll(keyStr, paramMap.get(key)+"==");
										}
									}
									if (MathUtil.PassCondition(variableCondition)) {
										isCorrected = true;
									}
								}
								if (variableCondition.equals("") || isCorrected) {
									parentBOMLine.add((TCComponent) tempBomLine.getItem(), "");
									// System.out.println(computationMap.get(childComputationCode));
									// parentBOMLine.setProperty("bl_item_s2_Note",
									// computationMap.get(childComputationCode));
								}
							}
						}
					}
				} else {// 计算项代号为空
					String note1 = tempBomLine.getProperty("S2_bl_vc");
					String note2 = tempBomLine.getProperty("S2_bl_vc1");
					String note3 = tempBomLine.getProperty("S2_bl_vc2");
					String note4 = tempBomLine.getProperty("S2_bl_vc3");
					String note5 = tempBomLine.getProperty("S2_bl_vc4");
					String variableCondition = note1 + note2 + note3 + note4 + note5;
					boolean isCorrected = false;
					// 将变量条件换算,判断是否符合，做筛选
					if (!variableCondition.equals("")) {
						for (String key : paramMap.keySet()) {
							String keyStr = key + "==";
							if (variableCondition.contains(keyStr)) {
								variableCondition = variableCondition.replaceAll(keyStr, paramMap.get(key)+"==");
							}
						}
						if (MathUtil.PassCondition(variableCondition)) {
							isCorrected = true;
						}
					}
					if (variableCondition.equals("") || isCorrected) {
						parentBOMLine.add((TCComponent) tempBomLine.getItem(), "");
					}
				}
			}
		}
	}
}
