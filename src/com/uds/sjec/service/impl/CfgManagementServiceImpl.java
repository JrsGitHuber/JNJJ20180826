package com.uds.sjec.service.impl;

import java.io.File;
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
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JButton;
import javax.swing.table.DefaultTableModel;

import com.teamcenter.rac.aif.AIFDesktop;
import com.teamcenter.rac.aif.kernel.AIFComponentContext;
import com.teamcenter.rac.kernel.TCComponent;
import com.teamcenter.rac.kernel.TCComponentBOMLine;
import com.teamcenter.rac.kernel.TCComponentBOMWindow;
import com.teamcenter.rac.kernel.TCComponentItem;
import com.teamcenter.rac.kernel.TCComponentItemType;
import com.teamcenter.rac.kernel.TCComponentQuery;
import com.teamcenter.rac.kernel.TCComponentTask;
import com.teamcenter.rac.kernel.TCException;
import com.teamcenter.rac.kernel.TCPreferenceService;
import com.teamcenter.rac.kernel.TCSession;
import com.teamcenter.rac.util.MessageBox;
import com.uds.Jr.utils.TCBomUtil;
import com.uds.common.utils.MathUtil;
import com.uds.sjec.bean.CalculationTableBean;
import com.uds.sjec.bean.CfgListInfoTableBean;
import com.uds.sjec.bean.ParamReadedBean;
import com.uds.sjec.common.CommonFunction;
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
	
	private TCComponentQuery m_query_item;

	/**
	 * �������õ�
	 * 
	 * @param query_class
	 * @throws TCException 
	 */
	@Override
	public void searchCfgList(DefaultTableModel configarationModel, String searchId, String searchIdType, String taskStatu,
			String startedTime, String finishedTime, String workFlowName) throws Exception {
		
		// ��ȡ���õ���Ϣ
		cfgListInfoTableList = new ArrayList<CfgListInfoTableBean>();
		searchId = searchId.replace("*", "%");
		try {
			Class.forName(ConstDefine.TCDB_CLASSNAME);
			connection = DriverManager.getConnection(ConstDefine.TCDB_URL, ConstDefine.TCDB_USER, ConstDefine.TCDB_PASSWORD);
			if (connection == null) {
				MessageBox.post("���ݿ�����ʧ�ܡ�", "����", MessageBox.ERROR);
				return;
			}
			statement = connection.createStatement();
			if (taskStatu.equals("δ��ʼ")) {
				if (searchIdType.equals("���õ���")) {
					querySql = "select * from t_configurationlist where configurationlist_id like'" + searchId + "'";
				} else if (searchIdType.equals("��ͬ��")) {
					querySql = "select * from t_configurationlist where contract_no like'" + searchId + "'";
				} else if (searchIdType.equals("�豸��")) {
					querySql = "select * from t_configurationlist where device_no like'" + searchId + "'";
				}
				resultSet = statement.executeQuery(querySql);
				while (resultSet.next()) {
					CfgListInfoTableBean bean = new CfgListInfoTableBean();
					bean.cfgID = resultSet.getString("configurationlist_id");
					bean.cfgRevision = resultSet.getString("configurationlist_rev");
					bean.projectName = resultSet.getString("project_name");
					bean.createUser = resultSet.getString("creation_user");
					// ͨ��itemID��ѯ���õ�
					TCComponentQuery query = QueryUtil.getTCComponentQuery("Item...");
					TCComponent[] searchResult = QueryUtil.getSearchResult(query, new String[] { "����� ID" }, new String[] { bean.cfgID });
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
//							bean.createTime = resultSet.getString("creation_date");
							bean.createTime = CommonFunction.GetCommonDateStr(resultSet.getString("creation_date"));
							if (!CommonFunction.m_errorMessage.equals("")) {
								MessageBox.post(CommonFunction.m_errorMessage, "��ʾ", MessageBox.INFORMATION);
							}
							if (bean.createTime.compareTo(startedTime) >= 0 && bean.createTime.compareTo(finishedTime) <= 0) {
								bean.status = "δ��ʼ";
								cfgListInfoTableList.add(bean);
							}
						}
					}
				}
			} else if (taskStatu.equals("������")) {
				if (searchIdType.equals("���õ���")) {
					querySql = "select * from t_configurationlist where configurationlist_id like'" + searchId + "'";
				} else if (searchIdType.equals("��ͬ��")) {
					querySql = "select * from t_configurationlist where contract_no like'" + searchId + "'";
				} else if (searchIdType.equals("�豸��")) {
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
					// ͨ��itemID��ѯ���õ�
					TCComponentQuery query = QueryUtil.getTCComponentQuery("Item...");
					TCComponent[] searchResult = QueryUtil.getSearchResult(query, new String[] { "����� ID" }, new String[] { bean.cfgID });
					if (searchResult.length > 0) {
						TCComponentItem cfgListItem = (TCComponentItem) searchResult[0];
						try {
							AIFComponentContext[] whereReference = cfgListItem.getLatestItemRevision().whereReferenced();
							for (AIFComponentContext context : whereReference) {
								if (context.getComponent() instanceof TCComponentTask) {
									// TODO Jr ������Ҫ�Ķ�����Ҫ��ʵʩ��ͨ
									TCComponentTask task = (TCComponentTask) context.getComponent();
									if (task.getProperty("current_name").equals(workFlowName)) { // �ҵ�������̣�ȡ�������̵�һЩ���Խ�����ʾ
										AIFComponentContext[] childTaskContexts = task.getRelated("child_tasks");
										for (AIFComponentContext tempContext : childTaskContexts) {
											TCComponentTask tempTask = (TCComponentTask) tempContext.getComponent();
											if (tempTask.getTaskType().equals("EPMDoTask")
													&& tempTask.getProperty("current_name").equals("����")) {// ����
//												bean.sponsorTime = tempTask.getProperty("creation_date"); // ���̷���ʱ��
//												bean.releaseTime = tempTask.getProperty("fnd0EndDate"); // ���̷���ʱ��
												bean.sponsorTime = CommonFunction.GetCommonDateStr(tempTask.getProperty("creation_date"));
												if (!CommonFunction.m_errorMessage.equals("")) {
													MessageBox.post(CommonFunction.m_errorMessage, "��ʾ", MessageBox.INFORMATION);
													return;
												}
												bean.releaseTime = CommonFunction.GetCommonDateStr(tempTask.getProperty("fnd0EndDate"));
												if (!CommonFunction.m_errorMessage.equals("")) {
													MessageBox.post(CommonFunction.m_errorMessage, "��ʾ", MessageBox.INFORMATION);
													return;
												}
												if ((bean.sponsorTime.compareTo(startedTime) >= 0)
														&& (bean.sponsorTime.compareTo(finishedTime) <= 0) && bean.releaseTime.equals("")) {
													bean.status = "������";
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
			} else if (taskStatu.equals("�����")) {
				if (startedTime.compareTo(finishedTime) < 0 && !startedTime.equals(finishedTime)) {
					if (searchIdType.equals("���õ���")) {
						querySql = "select * from t_configurationlist where configurationlist_id like'" + searchId + "'";
					} else if (searchIdType.equals("��ͬ��")) {
						querySql = "select * from t_configurationlist where contract_no like'" + searchId + "'";
					} else if (searchIdType.equals("�豸��")) {
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
						// ͨ��itemID��ѯ���õ�
						TCComponentQuery query = QueryUtil.getTCComponentQuery("Item...");
						TCComponent[] searchResult = QueryUtil.getSearchResult(query, new String[] { "����� ID" },
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
														&& tempTask.getProperty("current_name").equals("����")) {// ����
//													bean.sponsorTime = tempTask.getProperty("creation_date"); // ���̷���ʱ��
//													bean.releaseTime = tempTask.getProperty("fnd0EndDate"); // ���̷���ʱ��
													bean.sponsorTime = CommonFunction.GetCommonDateStr(tempTask.getProperty("creation_date"));
													if (!CommonFunction.m_errorMessage.equals("")) {
														MessageBox.post(CommonFunction.m_errorMessage, "��ʾ", MessageBox.INFORMATION);
														return;
													}
													bean.releaseTime = CommonFunction.GetCommonDateStr(tempTask.getProperty("fnd0EndDate"));
													if (!CommonFunction.m_errorMessage.equals("")) {
														MessageBox.post(CommonFunction.m_errorMessage, "��ʾ", MessageBox.INFORMATION);
														return;
													}
													if ((bean.releaseTime.compareTo(startedTime) >= 0)
															&& (bean.releaseTime.compareTo(finishedTime) <= 0)
															&& !bean.releaseTime.equals("")) {
														bean.status = "�����";
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
		// ��ʾ����
		if (cfgListInfoTableList.size() > 0) {
			// TODO Jr ����Ҫ�ĳɰ�����������
			// ����ˮ������
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
			MessageBox.post("δ�ҵ�������õ���Ϣ��", "���õ�����", MessageBox.WARNING);
		}
	}

	/**
	 * ������ȡ
	 */
	@Override
	public List<ParamReadedBean> getParamReadedList(String cfgListId) {
		// ��ȡ���õ���Ϣ
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
				// ��������
				columName = resultSetMetaData.getColumnLabel(2);
				bean.paramCode = resultSet.getString(columName);
				// ��������
				columName = resultSetMetaData.getColumnLabel(3);
				bean.paramName = resultSet.getString(columName);
				// ����ֵ
				columName = resultSetMetaData.getColumnLabel(4);
				bean.paramValue = resultSet.getString(columName);
				// ������Χ
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
	 * ��ʾ����
	 */
	@Override
	public void showParamReadedTable(DefaultTableModel configarationModel, List<ParamReadedBean> paramReadedList) {
		// JTable�е����ݾ�����ʾ
		for (int i = 0; i < paramReadedTableList.size(); i++) {
			configarationModel.addRow(new String[] { paramReadedTableList.get(i).paramCode, paramReadedTableList.get(i).paramName,
					paramReadedTableList.get(i).paramValue, paramReadedTableList.get(i).rangeOfParamValue, });
		}
	}

	/**
	 * ����excel������
	 */
	@SuppressWarnings({ "deprecation", "static-access" })
	@Override
	public String downLoadExcelToDir(String preferenceName, String configListId, String configListRev) {
		String dirPath = System.getProperty("java.io.tmpdir");
		// ��ȡ��ѡ��·��
		TCSession session = (TCSession) AIFDesktop.getActiveDesktop().getCurrentApplication().getSession();
		TCPreferenceService preferenceService = session.getPreferenceService();
		String UDSCodeConfigPath = preferenceService.getString(preferenceService.TC_preference_user, preferenceName);
		if ((UDSCodeConfigPath == null) || (UDSCodeConfigPath.equals(""))) {
			UDSCodeConfigPath = preferenceService.getString(TCPreferenceService.TC_preference_site, preferenceName);
		}
		if ((UDSCodeConfigPath == null) || (UDSCodeConfigPath.equals(""))) {
			MessageBox.post("��������ѡ��:<���������>�ļ�·��", "��������", MessageBox.ERROR);
			return null;
		}
		if (!UDSCodeConfigPath.endsWith("\\")) {
			UDSCodeConfigPath = UDSCodeConfigPath + "\\";
		}
		String targetFilePath = dirPath + "SJEC���ݼ������б�_V1.0.xlsx"; // Ŀ���ļ�·��
		File oldFile = new File(dirPath + configListId + configListRev + ".xlsx");
		if (oldFile.exists()) {
			oldFile.delete();
		}
		try {
			Files.copy(new File(UDSCodeConfigPath).toPath(), new File(targetFilePath).toPath(), StandardCopyOption.REPLACE_EXISTING);
			// ���ļ���
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
	 * ��������
	 */
	@Override
	public void paramCalculated(List<ParamReadedBean> paramReadedList, String excelpath, String configListId, JButton button_paramCaculate,
			JButton button_BOMConfiguration) {
		calculationList = new ArrayList<CalculationTableBean>();
		try {
			CommonFunction.GetExcelLicense();
			
			com.aspose.cells.Workbook wb = new com.aspose.cells.Workbook(excelpath);
			com.aspose.cells.WorksheetCollection worksheets =  wb.getWorksheets();
			com.aspose.cells.Worksheet worksheet = worksheets.get("������");
	        if (worksheet == null) {
	        	MessageBox.post("�ڱ��ģ����û���ҵ� ������ ҳ", "����", MessageBox.ERROR);
				return;
	        }
	        
	        // ��������ҳд������
	        com.aspose.cells.Cells cells = worksheet.getCells();
	        Map<String, Integer> cellMap = new HashMap<String, Integer>();
	        for (int rowNum = 2; rowNum < 65536; rowNum++) {
	        	com.aspose.cells.Cell cell = cells.get(rowNum, 2);
	        	String cellValue = cell == null ? "" : cell.getStringValue();
	        	if (cellValue.equals("")) {
	        		break;
	        	}
	        	
	        	if (!cellMap.containsKey(cellValue)) {
	        		cellMap.put(cellValue, rowNum);
	        	}
	        }
	        for (int i = 0; i < paramReadedList.size(); i++) {
	        	ParamReadedBean bean = paramReadedList.get(i);
	        	if (cellMap.containsKey(bean.paramCode)) {
	        		int rowNum = cellMap.get(bean.paramCode);
	        		com.aspose.cells.Cell cell = cells.get(rowNum, 3);
	        		cell.setValue(bean.paramValue);
	        	}
	        }
	        
			// ��ȡ�������б�ҳ������
	        com.aspose.cells.CalculationOptions obj = new com.aspose.cells.CalculationOptions();
			obj.setIgnoreError(true);
			worksheet = worksheets.get("�������б�");
			if (worksheet == null) {
	        	MessageBox.post("�ڱ��ģ����û���ҵ� �������б� ҳ", "����", MessageBox.ERROR);
				return;
	        }
			worksheet.calculateFormula(obj, true);
			cells = worksheet.getCells();
			for (int rowNum = 4; rowNum < 65536; rowNum++) {
				com.aspose.cells.Cell cell = cells.get(rowNum, 1);
				com.aspose.cells.Cell cell1 = cells.get(rowNum, 4);
				String cellValue = cell == null ? "" : cell.getStringValue();
				String cell1Value = cell1 == null ? "" : cell1.getStringValue();
				if (cellValue.equals("") || cell1Value.equals("")) {
					break;
				}
				
				CalculationTableBean bean = new CalculationTableBean();
				bean.configID = configListId;
				bean.conditionCode = cellValue;
				bean.calculationValue = cell1Value;
				
				calculationList.add(bean);
			}
			wb.save(excelpath);
			
			Class.forName(ConstDefine.TCDB_CLASSNAME);
			connection = DriverManager.getConnection(ConstDefine.TCDB_URL, ConstDefine.TCDB_USER, ConstDefine.TCDB_PASSWORD);
			if (connection == null) {
				MessageBox.post("���ݿ�����ʧ�ܡ�", "����", MessageBox.ERROR);
				return;
			}
			if (calculationList.size() > 0) {
				// ��ɾ���������õ���Ӧ�Ĳ�����Ϣ
				String deleteSql = "delete from t_condition where configlist_id ='" + configListId + "'";
				statement = connection.createStatement();
				statement.executeUpdate(deleteSql);
				for (int i = 0; i < calculationList.size(); i++) {
					// ������д�빫ʽ��
					String insertSql = "insert into t_condition(configlist_id,condition_code,condition_value) values('"
							+ calculationList.get(i).configID + "','" + calculationList.get(i).conditionCode + "','"
							+ calculationList.get(i).calculationValue + "')";
					statement = connection.createStatement();
					statement.executeUpdate(insertSql);
				}
				button_paramCaculate.setEnabled(false);
				MessageBox.post("������ɡ�", "��������", MessageBox.INFORMATION);
				button_BOMConfiguration.setEnabled(true);
			} else {
				MessageBox.post("�������б�û�����ݡ�", "��������", MessageBox.ERROR);
			}
		} catch (Exception e) {
			e.printStackTrace();
			MessageBox.post(e.getMessage(), "��������", MessageBox.ERROR);
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
	 * ͨ�����õ��Ų��Ҷ�Ӧ���ͺ�
	 */
	@Override
	public String queryProductType(String configListId) {
		try {
			Class.forName(ConstDefine.TCDB_CLASSNAME);
			connection = DriverManager.getConnection(ConstDefine.TCDB_URL, ConstDefine.TCDB_USER, ConstDefine.TCDB_PASSWORD);
			if (connection == null) {
				MessageBox.post("���ݿ�����ʧ�ܡ�", "����", MessageBox.ERROR);
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
	 * ���Ӳ�����ȡ�л�ȡ�Ĳ�����Ϣ
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
	 * ��ȡ�������б�
	 */
	public Map<String, String> getComputationMap(String cfgItemId) {
		Map<String, String> computationMap = new HashMap<String, String>();
		String sql = "select condition_code,condition_value from t_condition where configlist_id ='" + cfgItemId + "'";
		try {
			Connection connection = DriverManager.getConnection(ConstDefine.TCDB_URL, ConstDefine.TCDB_USER, ConstDefine.TCDB_PASSWORD);
			if (connection == null) {
				MessageBox.post("���ݿ�����ʧ�ܡ�", "����", MessageBox.ERROR);
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

	@Override
	public void configurantionCalculated(TCComponentBOMLine superTopBOMLine, TCComponentBOMLine configListTopBomLine,
			Map<String, String> paramMap, Map<String, String> computationMap, String configListId, JButton button_BOMInstantiation) {
		try {
			m_query_item = QueryUtil.getTCComponentQuery("Item...");
			ClearBomLine(configListTopBomLine);
			RecursiveToGetBom(superTopBOMLine, configListTopBomLine, paramMap, computationMap, configListId);
		} catch (Exception e) {
			MessageBox.post("���ü������" + e.getMessage(), "���ü���", MessageBox.INFORMATION);
			e.printStackTrace();
			return;
		} finally {
			TCBomUtil.CloseWindow(superTopBOMLine);
			TCBomUtil.CloseWindow(configListTopBomLine);
		}
		
		MessageBox.post("���ü������", "���ü���", MessageBox.INFORMATION);
		button_BOMInstantiation.setEnabled(false);
	}
	
	private void RecursiveToGetBom(TCComponentBOMLine superTopBomLine, TCComponentBOMLine topBomLine, Map<String, String> paramMap,
			Map<String, String> computationMap, String configListId) throws Exception {
		if (!superTopBomLine.hasChildren()) {
			return;
		}
		
		AIFComponentContext[] children = superTopBomLine.getChildren();		
		for (AIFComponentContext context : children) {
			TCComponentBOMLine superBomLine = (TCComponentBOMLine) context.getComponent();
			if (JudgeIfAdd(superBomLine, paramMap)) {
				// ��ȡ����
				String tempValue = superBomLine.getProperty("bl_item_s2_C1");
//				if (tempValue.equals("")) {
//					continue;
//				}
				List<String> ruleList = tempValue.equals("") ? new ArrayList<String>() : Arrays.asList(tempValue.split("%"));
				Map<String, String> propertyMap = new HashMap<String, String>();
				String item_id = superBomLine.getProperty("bl_item_item_id");
				for (String rule : ruleList) {
					if (rule.startsWith("U")) {
						item_id = item_id + "-" + configListId;
					} else if (rule.startsWith("C")) {
						if (computationMap.containsKey(rule)) {
							item_id = item_id + "-" + configListId;
							propertyMap.put("S2_bl_Note", computationMap.get(rule));
						} else {
							throw new Exception("û���ҵ�����ֵ" + rule);
						}
					} else if (rule.startsWith("F")) {
						if (computationMap.containsKey(rule)) {
							propertyMap.put("Usage_Quantity", computationMap.get(rule));
						} else {
							throw new Exception("û���ҵ�����ֵ" + rule);
						}
					} else if (rule.startsWith("G")) {
						if (computationMap.containsKey(rule)) {
							item_id = item_id + "-" + computationMap.get(rule);
						} else {
							throw new Exception("û���ҵ�����ֵ" + rule);
						}
					} else {
						System.out.println("�ȴ����" + rule + "�Ĵ������");
					}
				}
				
				TCComponentItem item = GetItem(superBomLine, item_id);
				TCComponentBOMLine newBomLine = topBomLine.add(item, item.getLatestItemRevision(), null, false);
				if (!item_id.equals(superBomLine.getProperty("bl_item_item_id"))) {
					// ���topBomLine������
					ClearBomLine(newBomLine);
					RecursiveToGetBom(superBomLine, newBomLine, paramMap, computationMap, configListId);
				}
				
				if (propertyMap.size() != 0) {
					newBomLine.setProperties(propertyMap);
				}
			}
		}
	}

	private TCComponentItem GetItem(TCComponentBOMLine bomLine, String item_id) throws Exception {
		TCComponent[] searchResult = QueryUtil.getSearchResult(m_query_item, new String[] { "����� ID" }, new String[] { item_id });
		if (searchResult.length == 0) {
			// ��������Ҫ����item_Loss
			String itemTypeStr = bomLine.getItem().getType();
			TCComponentItemType itemType = (TCComponentItemType) ConstDefine.TC_SESSION.getTypeComponent(itemTypeStr);
			String newRev = itemType.getNewRev(null);
			String type = itemTypeStr;
			String name = bomLine.getProperty("bl_item_object_name");
			String desc = "";
			return itemType.create(item_id, newRev, type, name, desc, null);
		} else if (searchResult.length != 1) {
			throw new Exception("ͨ��ID " + item_id + " �ҵ����Item");
		} else {
			return (TCComponentItem)searchResult[0];
		}
		
	}

	private void ClearBomLine(TCComponentBOMLine topBomLine) throws Exception {
		if(topBomLine.hasChildren()){
			AIFComponentContext[] children = topBomLine.getChildren();
			for (AIFComponentContext context : children) {
				TCComponentBOMLine bomLine = (TCComponentBOMLine) context.getComponent();
				bomLine.cut();
			}
		}
	}

	private boolean JudgeIfAdd(TCComponentBOMLine bomLine, Map<String, String> paramMap) throws Exception {
		String[] proertyValues = bomLine.getProperties(new String[] { "S2_bl_vc", "S2_bl_vc1", "S2_bl_vc2", "S2_bl_vc3", "S2_bl_vc4" });
		String variableCondition = "";
		for (String value : proertyValues) {
			variableCondition += value;
		}
		if (variableCondition.equals("")) {
			return true;
		}
		
		for (String key : paramMap.keySet()) {
			Pattern pattern = Pattern.compile("\\W{0,1}" + key + "\\W");
			Matcher matcher = pattern.matcher(variableCondition);
			while (matcher.find()) {
				String findStr = matcher.group();
				String tempStr = "";
				String tempStr1 = "";
				if (variableCondition.startsWith(findStr)) {
					tempStr = key + findStr.charAt(findStr.length()-1);
					tempStr1 = paramMap.get(key) + findStr.charAt(findStr.length()-1);
				} else {
					tempStr = findStr.charAt(0) + key + findStr.charAt(findStr.length()-1);
					tempStr1 = findStr.charAt(0) + paramMap.get(key) + findStr.charAt(findStr.length()-1);
				}
				variableCondition = variableCondition.replace(tempStr, tempStr1);
			}
			
//			String keyStr = key + "==";
//			if (variableCondition.contains(keyStr)) {
//				variableCondition = variableCondition.replaceAll(keyStr, paramMap.get(key)+"==");
//			}
		}
		variableCondition = variableCondition.replaceAll("!=", "!==");
		return MathUtil.PassCondition(variableCondition);
	}
	
	/**
	 * ���ü���
	 */
	public void configurantionCalculated_pre(TCComponentBOMLine superTopBOMLine, TCComponentBOMLine configListTopBomLine,
			Map<String, String> paramMap, Map<String, String> computationMap, String configListId, JButton button_BOMInstantiation) {
		try {
			AIFComponentContext[] superBOMContexts = superTopBOMLine.getChildren();
			if (superBOMContexts != null && superBOMContexts.length > 0) {
				for (AIFComponentContext superBOMContext : superBOMContexts) {
					TCComponentBOMLine superBomLine = (TCComponentBOMLine) superBOMContext.getComponent();
					// ��ȡ����BOMLine�ļ��������
					String computationCode = superBomLine.getProperty("bl_item_s2_C1");
					if (!computationCode.equals("")) {
						String[] computationCodes = computationCode.split("%");
						if (computationCodes.length > 0) {
							// ���Ƿ�U��ͷҪ�����õ���,�ٴ������� 
							for (String childComputationCode : computationCodes) {
								if (childComputationCode.startsWith("U")) {
									String note1 = superBomLine.getProperty("S2_bl_vc");
									String note2 = superBomLine.getProperty("S2_bl_vc1");
									String note3 = superBomLine.getProperty("S2_bl_vc2");
									String note4 = superBomLine.getProperty("S2_bl_vc3");
									String note5 = superBomLine.getProperty("S2_bl_vc4");
									String variableCondition = note1 + note2 + note3 + note4 + note5;
									boolean isCorrected = false;
									// ��������������,�ж��Ƿ���ϣ���ɸѡ
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
										// ����Ҫ�滻��item�Ƿ��Ѿ�����
										TCComponentQuery query = QueryUtil.getTCComponentQuery("Item...");
										TCComponent[] searchResult = QueryUtil.getSearchResult(query, new String[] { "����� ID" },
												new String[] { superBomLine.getProperty("bl_item_item_id") + "-" + configListId });
										if (searchResult == null || searchResult.length == 0) {
											// ������item
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
													MessageBox.post(e.getMessage(), "���ü���", MessageBox.INFORMATION);
													return;
												}
											}
										}
									}
								}
							}
						}
					} else {// ���������Ϊ��
						String note1 = superBomLine.getProperty("S2_bl_vc");
						String note2 = superBomLine.getProperty("S2_bl_vc1");
						String note3 = superBomLine.getProperty("S2_bl_vc2");
						String note4 = superBomLine.getProperty("S2_bl_vc3");
						String note5 = superBomLine.getProperty("S2_bl_vc4");
						String variableCondition = note1 + note2 + note3 + note4 + note5;
						boolean isCorrected = false;
						// ��������������,�ж��Ƿ���ϣ���ɸѡ
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
			MessageBox.post("���ü�����ɡ�", "���ü���", MessageBox.INFORMATION);
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
				// ��ȡ����BOMLine�ļ��������
				String computationCode = tempBomLine.getProperty("bl_item_s2_C1");
				if (!computationCode.equals("")) {
					String[] computationCodes = computationCode.split("%");
					if (computationCodes.length > 0) {
						// Jr ���ŵĹ����ڴ˴��������
						// ���Ƿ�U��ͷҪ�����õ���,�ٴ�������
						for (String childComputationCode : computationCodes) {
							if (childComputationCode.startsWith("U")) {
								String note1 = tempBomLine.getProperty("S2_bl_vc");
								String note2 = tempBomLine.getProperty("S2_bl_vc1");
								String note3 = tempBomLine.getProperty("S2_bl_vc2");
								String note4 = tempBomLine.getProperty("S2_bl_vc3");
								String note5 = tempBomLine.getProperty("S2_bl_vc4");
								String variableCondition = note1 + note2 + note3 + note4 + note5;
								boolean isCorrected = false;
								// ��������������,�ж��Ƿ���ϣ���ɸѡ
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
									// ����Ҫ�滻��item�Ƿ��Ѿ�����
									TCComponentQuery query = QueryUtil.getTCComponentQuery("Item...");
									TCComponent[] searchResult = QueryUtil.getSearchResult(query, new String[] { "����� ID" },
											new String[] { tempBomLine.getProperty("bl_item_item_id") + "-" + configListId });
									if (searchResult == null || searchResult.length == 0) {
										// ������item
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
						// ������ͷ��������
						for (String childComputationCode : computationCodes) {
							// Jr ���˻��ŵģ������Ĺ����ڴ˴��������
							if (childComputationCode.startsWith("A")) {
								String note1 = tempBomLine.getProperty("S2_bl_vc");
								String note2 = tempBomLine.getProperty("S2_bl_vc1");
								String note3 = tempBomLine.getProperty("S2_bl_vc2");
								String note4 = tempBomLine.getProperty("S2_bl_vc3");
								String note5 = tempBomLine.getProperty("S2_bl_vc4");
								String variableCondition = note1 + note2 + note3 + note4 + note5;
								boolean isCorrected = false;
								// ��������������,�ж��Ƿ���ϣ���ɸѡ
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
							} else if (childComputationCode.startsWith("F")) {
								if (computationMap.containsKey(childComputationCode)) {
									TCComponentBOMLine newBomLine = parentBOMLine.add((TCComponent) tempBomLine.getItem(), "");
									newBomLine.setProperty("Usage_Quantity", computationMap.get(childComputationCode));
								}
							}
						}
					}
				} else {// ���������Ϊ��
					String note1 = tempBomLine.getProperty("S2_bl_vc");
					String note2 = tempBomLine.getProperty("S2_bl_vc1");
					String note3 = tempBomLine.getProperty("S2_bl_vc2");
					String note4 = tempBomLine.getProperty("S2_bl_vc3");
					String note5 = tempBomLine.getProperty("S2_bl_vc4");
					String variableCondition = note1 + note2 + note3 + note4 + note5;
					boolean isCorrected = false;
					// ��������������,�ж��Ƿ���ϣ���ɸѡ
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
