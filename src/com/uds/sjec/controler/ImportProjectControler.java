package com.uds.sjec.controler;

import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableModel;

import com.teamcenter.rac.aif.kernel.AIFComponentContext;
import com.teamcenter.rac.aif.kernel.InterfaceAIFComponent;
import com.teamcenter.rac.aifrcp.AIFUtility;
import com.teamcenter.rac.kernel.TCComponent;
import com.teamcenter.rac.kernel.TCComponentBOMLine;
import com.teamcenter.rac.kernel.TCComponentDataset;
import com.teamcenter.rac.kernel.TCComponentFolder;
import com.teamcenter.rac.kernel.TCComponentItem;
import com.teamcenter.rac.kernel.TCComponentItemRevision;
import com.teamcenter.rac.kernel.TCComponentQuery;
import com.teamcenter.rac.kernel.TCException;
import com.teamcenter.rac.util.MessageBox;
import com.teamcenter.rac.workflow.commands.newprocess.NewProcessCommand;
import com.teamcenter.rac.workflow.commands.newprocess.NewProcessDialog;
import com.uds.Jr.utils.TCBomUtil;
import com.uds.Jr.utils.TCQueryUtil;
import com.uds.sjec.bean.ParamViewTableBean;
import com.uds.sjec.common.ConstDefine;
import com.uds.sjec.service.IImportProjectService;
import com.uds.sjec.service.impl.ImportProjectImpl;
import com.uds.sjec.utils.DatesetUtil;
import com.uds.sjec.utils.ItemUtil;

public class ImportProjectControler {

	public IImportProjectService importProjectService = new ImportProjectImpl();

	public void userTask(final TCComponentDataset dataset) {

		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					ImportProjectFrame frame = new ImportProjectFrame(dataset);
					frame.setResizable(false);
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	class ImportProjectFrame extends JFrame {
		private JTextField textField_ID;
		private JTextField textField_Rev;
		private JTextField textField_ElevatorType;
		private JTable table_preview;
		private JButton button_paramPreview;
		private JButton button_createItem;
		private JButton button_dispatch;
		private String excelPath;
		private List<ParamViewTableBean> paramInfoFromExcelList; // 存储从excel中获取的参数信息
		private List<ParamViewTableBean> paramInfoFromDatabaseList; // 存储从t_elevator_param从获取的参数信息
		private List<String> elevatorTypeList; // 存储电梯类型下拉框的值
		private TCComponentItem configurationListItem;
		private DefaultTableModel previewModel;
		private String equipmentNo;// 设备号
		private String tipText;
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		public ImportProjectFrame(final TCComponentDataset dataset) {
			getContentPane().setFont(new Font("宋体", Font.BOLD, 12));
			setTitle("导入项目");
			setBounds(100, 100, 456, 586);
			setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
			getContentPane().setLayout(null);
			setLocationRelativeTo(null);

			JPanel panel = new JPanel();
			panel.setBounds(10, 10, 420, 530);
			panel.setBorder(BorderFactory.createTitledBorder(""));
			getContentPane().add(panel);
			panel.setLayout(null);

			JLabel label_ConfigID = new JLabel("配置单编号");
			label_ConfigID.setBounds(10, 10, 72, 21);
			panel.add(label_ConfigID);

			JLabel label_ConfigRev = new JLabel("配置单版本");
			label_ConfigRev.setBounds(220, 10, 72, 21);
			panel.add(label_ConfigRev);

			JLabel label_elevatorType = new JLabel("电梯型号");
			label_elevatorType.setBounds(10, 495, 56, 21);
			panel.add(label_elevatorType);

			textField_ID = new JTextField();
			textField_ID.setEditable(false);
			textField_ID.setBounds(87, 10, 110, 21);
			panel.add(textField_ID);
			textField_ID.setColumns(10);

			textField_Rev = new JTextField();
			textField_Rev.setEditable(false);
			textField_Rev.setBounds(300, 10, 110, 21);
			textField_Rev.setColumns(10);
			panel.add(textField_Rev);

			textField_ElevatorType = new JTextField();
			textField_ElevatorType.setBounds(76, 495, 88, 21);
			textField_ElevatorType.setEditable(false);
			textField_ElevatorType.setColumns(10);
			panel.add(textField_ElevatorType);

			button_paramPreview = new JButton("预览");
			button_paramPreview.setBounds(201, 494, 62, 23);
			panel.add(button_paramPreview);

			button_createItem = new JButton("创建");
			button_createItem.setBounds(273, 494, 62, 23);
			panel.add(button_createItem);

			button_dispatch = new JButton("分派");
			button_dispatch.setBounds(348, 494, 62, 23);
			panel.add(button_dispatch);

			JScrollPane scrollPane = new JScrollPane();
			scrollPane.setBounds(10, 41, 400, 444);
			panel.add(scrollPane);

			previewModel = new DefaultTableModel();
			table_preview = new JTable(previewModel) {
				private static final long serialVersionUID = 1L;

				public boolean isCellEditable(int row, int column) {
					return false;
				}
				
				// 鼠标悬浮时显示值
				public String getToolTipText(MouseEvent e) {
					int row = table_preview.rowAtPoint(e.getPoint());
					int col = table_preview.columnAtPoint(e.getPoint());

					if (row > -1 && col > -1) {
						Object value = table_preview.getValueAt(row, col);
						if (value != null && !value.equals("")) {
							tipText = value.toString();
						} else {
							return null;
						}
					}
					return tipText;
				}

			};
			// 设置table可排序
			table_preview.setAutoCreateRowSorter(true);
			table_preview.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
			table_preview.setModel(new DefaultTableModel(new Object[][] {}, new String[] { "参数代号", "参数名称", "参数值", "参数类型" }));
			scrollPane.setViewportView(table_preview);
			table_preview.setBorder(new LineBorder(new Color(0, 0, 0)));
			table_preview.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));

			previewModel = (DefaultTableModel) table_preview.getModel();

			// 预览
			button_paramPreview.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					// 获取所有梯型
					elevatorTypeList = new ArrayList<String>();
					elevatorTypeList = importProjectService.queryElevatorTypeList();
					paramInfoFromExcelList = new ArrayList<ParamViewTableBean>();
					paramInfoFromDatabaseList = new ArrayList<ParamViewTableBean>();
					// 将数据集下载到本地
					String tempDir = System.getProperty("java.io.tmpdir");
					boolean ret = DatesetUtil.datasetFileToLocalDir(ConstDefine.TC_SESSION, dataset, "excel", "参数录入表.xlsx", tempDir);
					if (ret) {
						// 读取excel表中数据，确定参数代号无重复项，判断电梯类型在数据库中是否存在
						excelPath = tempDir + "\\" + "参数录入表.xlsx";
						// TODO Jr textField_ElevatorType是否可以删掉（以前下拉框）
						paramInfoFromExcelList = importProjectService
								.getParamExcelList(excelPath, elevatorTypeList, textField_ElevatorType);
						if (paramInfoFromExcelList != null && paramInfoFromExcelList.size() > 0) {
							// 清空table
							DefaultTableModel model = new DefaultTableModel();
							model = (DefaultTableModel) table_preview.getModel();
							model.setRowCount(0);
							// 从数据库获取参数信息
							paramInfoFromDatabaseList = importProjectService.getParamDatabaseList(textField_ElevatorType.getText());
							// 对比两个List，得到预览信息
							paramInfoFromDatabaseList = importProjectService.compareInfo(paramInfoFromExcelList, paramInfoFromDatabaseList);
							// 将excel的内容显示到Table里
							importProjectService.showData(previewModel, paramInfoFromDatabaseList);
						}
					}
				}

			});

			// 创建
			button_createItem.addActionListener(new ActionListener() {
				@SuppressWarnings({ "static-access", "deprecation" })
				public void actionPerformed(ActionEvent e) {
					if (previewModel.getRowCount() > 0) {
						TCComponentFolder componentFolder = new TCComponentFolder();
						TCComponentFolder newstuffFolder = null;
						try {
							for (int i = 0; i < paramInfoFromDatabaseList.size(); i++) {
								if (paramInfoFromDatabaseList.get(i).paramName.equals("设备号")) {
									String[] tempString = paramInfoFromDatabaseList.get(i).paramValue.split("%");
									equipmentNo = tempString[0];
								}
							}
							if (equipmentNo == null || equipmentNo.equals("")) {
								MessageBox.post("没有找到参数 设备号", "提示", MessageBox.INFORMATION);
								return;
							}
							
							// 从SQLServer中获取配置单号
							String configListID = importProjectService.getConfigListID(equipmentNo);
							if (configListID == null || configListID.equals("")) {
								MessageBox.post("没有获取到配置单号，将以流水码创建配置单", "提示", MessageBox.INFORMATION);
								configListID = "";
							}
							
							// 组织配置单
							TCComponentQuery itemQuery = TCQueryUtil.GetTCComponentQuery(ConstDefine.TC_SESSION, "零组件 ID");
							TCComponent[] items0 = TCQueryUtil.GetItemByID(itemQuery, configListID);
							if (items0 == null || items0.length == 0) {
								configurationListItem = importProjectService.createItem(ConstDefine.TC_SESSION, "S2_CFG", configListID, "配置单", "");
								if (configurationListItem == null) {
									MessageBox.post("创建配置单" + configListID + "失败，详情请联系管理员检查控制台", "提示", MessageBox.INFORMATION);
									return;
								}
							} else {
//								configurationListItem = (TCComponentItem)items0[0];
								MessageBox.post("创建配置单" + configListID + "已经存在", "提示", MessageBox.INFORMATION);
								return;
							}
							// 电器配置单
							TCComponentItem electricalItem = null;
							String electricalItemID = configurationListItem.getProperty("item_id") + ConstDefine.TC_GROUP_ELECTRICAL;
							items0 = TCQueryUtil.GetItemByID(itemQuery, electricalItemID);
							if (items0 == null || items0.length == 0) {
								electricalItem = ItemUtil.createtItem("S2_CFG", electricalItemID, "电气配置单VI", "");
								if (electricalItem == null) {
									MessageBox.post("创建配置单" + electricalItemID + "失败，详情请联系管理员检查控制台", "提示", MessageBox.INFORMATION);
									return;
								}
							} else {
								electricalItem = (TCComponentItem)items0[0];
							}
							// 机械配置单
							TCComponentItem mechanicalItem = null;
							String mechanicalItemID = configurationListItem.getProperty("item_id") + ConstDefine.TC_GROUP_MECHANICAL;
							items0 = TCQueryUtil.GetItemByID(itemQuery, mechanicalItemID);
							if (items0 == null || items0.length == 0) {
								mechanicalItem = ItemUtil.createtItem("S2_CFG", mechanicalItemID, "机械配置单VI", "");
								if (mechanicalItem == null) {
									MessageBox.post("创建配置单" + mechanicalItemID + "失败，详情请联系管理员检查控制台", "提示", MessageBox.INFORMATION);
									return;
								}
							} else {
								mechanicalItem = (TCComponentItem)items0[0];
							}
							
							// 搭建配置单BOM
							TCComponentBOMLine configListTopBomLine = TCBomUtil.GetTopBomLine(ConstDefine.TC_SESSION, configurationListItem.getLatestItemRevision(), "视图");
							if (configListTopBomLine == null) {
								// 没有视图就去创建
								configListTopBomLine = TCBomUtil.SetBOMViewForItemRev(ConstDefine.TC_SESSION, configurationListItem.getLatestItemRevision());
							}else{
								if(configListTopBomLine.hasChildren()){
									AIFComponentContext[] children = configListTopBomLine.getChildren();
									for (AIFComponentContext context : children) {
										TCComponentBOMLine bomLine = (TCComponentBOMLine) context.getComponent();
										bomLine.cut();
									}
									// 必须先关闭视图后再重新搭建，否则BomLine会按照之前的排序(查找编号属性)
									TCBomUtil.CloseWindow(configListTopBomLine);
									configListTopBomLine = TCBomUtil.GetTopBomLine(ConstDefine.TC_SESSION, configurationListItem.getLatestItemRevision(), "视图");
								}
							}
							configListTopBomLine.add(electricalItem, "");
							configListTopBomLine.add(mechanicalItem, "");
							TCBomUtil.CloseWindow(configListTopBomLine);

							// 配置单挂在newStuff下
							try {
								newstuffFolder = componentFolder.getNewStuffFolder(ConstDefine.TC_SESSION);
								newstuffFolder.add("contents", configurationListItem);
							} catch (Exception e1) {
								e1.printStackTrace();
							}

							// 将配置单信息显示在窗口
							textField_ID.setText(configurationListItem.getLatestItemRevision().getProperty("item_id"));
							textField_Rev.setText(configurationListItem.getLatestItemRevision().getProperty("item_revision_id"));

							// 将配置单信息和excel信息存储到数据库
							boolean isStoraged = importProjectService.getInfoTodatabase(configurationListItem, paramInfoFromExcelList, paramInfoFromDatabaseList, ConstDefine.TC_SESSION);
							if (isStoraged) {
								// 将excel上传到TC
								TCComponentItemRevision itemRevision = configurationListItem.getLatestItemRevision();
								importProjectService.createDataSet(ConstDefine.TC_SESSION, excelPath, "MSExcelX", "excel", 
										configurationListItem.getProperty("item_id") + "参数录入表", itemRevision, "IMAN_specification", true);
								MessageBox.post("配置单创建成功。", "导入项目", MessageBox.INFORMATION);
							} else {
								MessageBox.post("配置单参数导入到数据库失败。", "导入项目", MessageBox.INFORMATION);
							}
						} catch (Exception e2) {
							e2.printStackTrace();
							MessageBox.post(e2.getMessage(), "导入项目", MessageBox.ERROR);
						}
					} else {
						MessageBox.post("请先执行参数预览。", "导入项目", MessageBox.ERROR);
					}
				}
			});

			// 分派
			button_dispatch.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					if (configurationListItem != null) {
						try {
							TCComponentItemRevision revision = configurationListItem.getLatestItemRevision();
							InterfaceAIFComponent[] component = new InterfaceAIFComponent[] { revision };
							NewProcessCommand command = new NewProcessCommand(AIFUtility.getCurrentApplication().getDesktop(), AIFUtility
									.getCurrentApplication(), component);
							new NewProcessDialog(command);
						} catch (TCException e1) {
							e1.printStackTrace();
							MessageBox.post(e1.getMessage(), "导入项目", MessageBox.ERROR);
							return;
						}
						dispose();
					} else {
						MessageBox.post("请先创建配置单。", "导入项目", MessageBox.ERROR);
					}
				}
			});
		}
	}
}
