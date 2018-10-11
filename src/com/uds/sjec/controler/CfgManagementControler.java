package com.uds.sjec.controler;

import java.awt.AWTEvent;
import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Toolkit;
import java.awt.event.AWTEventListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;

import com.teamcenter.rac.aif.AIFDesktop;
import com.teamcenter.rac.aif.AbstractAIFUIApplication;
import com.teamcenter.rac.aif.kernel.AIFComponentContext;
import com.teamcenter.rac.kernel.TCComponent;
import com.teamcenter.rac.kernel.TCComponentBOMLine;
import com.teamcenter.rac.kernel.TCComponentBOMViewRevision;
import com.teamcenter.rac.kernel.TCComponentItem;
import com.teamcenter.rac.kernel.TCComponentItemRevision;
import com.teamcenter.rac.kernel.TCComponentQuery;
import com.teamcenter.rac.kernel.TCException;
import com.teamcenter.rac.kernel.TCPreferenceService;
import com.teamcenter.rac.kernel.TCSession;
import com.teamcenter.rac.pse.AbstractPSEApplication;
import com.teamcenter.rac.stylesheet.PropertyDateButton;
import com.teamcenter.rac.util.MessageBox;
import com.uds.Jr.TreeTable.PreviewBom;
import com.uds.common.utils.MathUtil;
import com.uds.sjec.bean.BomToPreviewBean;
import com.uds.sjec.bean.ParamReadedBean;
import com.uds.sjec.common.CommonFunction;
import com.uds.sjec.common.Const.PreferenceService;
import com.uds.sjec.common.ConstDefine;
import com.uds.sjec.service.ICfgManagementService;
import com.uds.sjec.service.impl.CfgManagementServiceImpl;
import com.uds.sjec.utils.BomUtil;
import com.uds.sjec.utils.QueryUtil;
import com.uds.sjec.utils.TipsUI;
import com.uds.sjec.view.PSEAPP;

@SuppressWarnings("deprecation")
public class CfgManagementControler {
	private static CfgManagementFrame m_frame;
	
	public static void SetFrameShow() {
//		m_frame.setVisible(true);
		if (m_frame != null) {
			m_frame.setAlwaysOnTop(true);
		}
	}
	
	public void userTask() {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					if (m_frame == null) {
						m_frame = new CfgManagementFrame();
					}
					m_frame.setResizable(false);
					m_frame.setVisible(true);
					
					Toolkit.getDefaultToolkit().addAWTEventListener(new AWTEventListener() {
					    public void eventDispatched(AWTEvent event) {
					    	if (event instanceof MouseEvent && event.getID() == MouseEvent.MOUSE_CLICKED){
					    		if (m_frame != null) {
						    		m_frame.setAlwaysOnTop(false);
						    	}
					    	}
					    }
					}, AWTEvent.MOUSE_EVENT_MASK);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
	
	public static void SetCfgManagementFrameButton() {
		if (m_frame != null) {
			m_frame.SetButton();
		}
	}
	
	public static void SetButton1() {
		if (m_frame != null) {
			m_frame.SetButton1();
		}
	}
//	
	public static void SetButton2() {
		if (m_frame != null) {
			m_frame.SetButton2();
		}
	}
	
	class CfgManagementFrame extends JDialog {
		private static final long serialVersionUID = 1L;
		
		private JTextField textField_searchID;
		private JTable table_configaration;
		private JTable table_projectInfomation;
		private JTextField textField_configurationID;
		private JTextField textField__revision;
		private PropertyDateButton propertyDateButton_startedTime;
		private PropertyDateButton propertyDateButton_finishedTime;
		private JPopupMenu popupMenu;
		private DefaultTableModel tableModel;
		private JButton button_paramRead;
		private JButton button_paramCaculate;
		public JButton button_BOMConfiguration;
		private JButton button_configurationCaculate;
		private JComboBox<String> comboBox_searchID;
		private JComboBox<String> comboBox_taskStatus;
		private String startedTime = "";
		private String finishedTime = "";
		private JMenuItem checkStructItem;
		private String cfgItemId;
		private List<ParamReadedBean> paramReadedList;
		private String productType;
		private TCComponentBOMLine superTopBOMLine;
		private Map<String, String> paramMap;// key：参数代号 value：参数值
		private String excelpath;
		private Map<String, String> computationMap;// 计算项列表
		private DefaultTableModel configListModel;
		private DefaultTableModel projectInfomationModel;
		private String tipText;
		private JLabel label_configurationID;
		public ICfgManagementService cfgManagementService = new CfgManagementServiceImpl();
		
		public CfgManagementFrame() {
			setTitle("配置单管理");
			setBounds(100, 100, 874, 564);
			setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
			getContentPane().setLayout(null);
			setLocationRelativeTo(null);
//			setType(Type.UTILITY);
			JPanel panel_configurationTask = new JPanel();
			panel_configurationTask.setBounds(10, 10, 384, 505);
			getContentPane().add(panel_configurationTask);
			panel_configurationTask.setBorder(BorderFactory.createTitledBorder("配置单任务"));
			panel_configurationTask.setLayout(null);

			comboBox_searchID = new JComboBox<String>();
			comboBox_searchID.addItem("配置单号");
			comboBox_searchID.addItem("合同号");
			comboBox_searchID.addItem("设备号");
			comboBox_searchID.setBounds(30, 31, 81, 21);
			panel_configurationTask.add(comboBox_searchID);

			textField_searchID = new JTextField();
			textField_searchID.setBounds(139, 31, 138, 21);
			panel_configurationTask.add(textField_searchID);
			textField_searchID.setColumns(10);

			JButton button_search = new JButton("搜索");
			button_search.setBounds(307, 142, 67, 21);
			panel_configurationTask.add(button_search);
			this.getRootPane().setDefaultButton(button_search);

			JLabel label_taskStatus = new JLabel("任务状态");
			label_taskStatus.setBounds(35, 75, 59, 15);
			panel_configurationTask.add(label_taskStatus);

			JLabel label_startedTime = new JLabel("选择时段");
			label_startedTime.setBounds(35, 110, 59, 15);
			panel_configurationTask.add(label_startedTime);

			comboBox_taskStatus = new JComboBox<String>();
			comboBox_taskStatus.addItem("未开始");
			comboBox_taskStatus.addItem("流程中");
			comboBox_taskStatus.addItem("已完成");
			comboBox_taskStatus.setBounds(139, 72, 138, 21);
			panel_configurationTask.add(comboBox_taskStatus);

			propertyDateButton_startedTime = new PropertyDateButton();
			propertyDateButton_startedTime.setDisplayFormat("yyyy-M-dd");
			propertyDateButton_startedTime.setBounds(139, 107, 138, 21);
			panel_configurationTask.add(propertyDateButton_startedTime);

			propertyDateButton_finishedTime = new PropertyDateButton();
			propertyDateButton_finishedTime.setDisplayFormat("yyyy-M-dd");
			propertyDateButton_finishedTime.setBounds(139, 142, 138, 21);
			panel_configurationTask.add(propertyDateButton_finishedTime);

			JScrollPane scrollPane_configurationID = new JScrollPane();
			scrollPane_configurationID.setBounds(10, 177, 364, 318);
			panel_configurationTask.add(scrollPane_configurationID);

			// 定义table并不可编辑
			tableModel = new DefaultTableModel();
			table_configaration = new JTable(tableModel) {
				/**
				 * 
				 */
				private static final long serialVersionUID = 1L;

				public boolean isCellEditable(int row, int column) {
					return false;
				}

				public String getToolTipText(MouseEvent e) {
					int row = table_configaration.rowAtPoint(e.getPoint());
					int col = table_configaration.columnAtPoint(e.getPoint());

					if (row > -1 && col > -1) {
						Object value = table_configaration.getValueAt(row, col);
						if (value != null && !value.equals("")) {
							tipText = value.toString();
						} else {
							return null;
						}
					}
					return tipText;
				}
			};
			table_configaration.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);// 自适应长度
			table_configaration.setAutoCreateRowSorter(true);// 排序
			table_configaration.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
			table_configaration.setModel(new DefaultTableModel(new Object[][] {}, new String[] { "配置单号", "版本号", "项目名称", "创建用户", "创建时间", "发起时间",
					"发布时间", "状态" }));
			scrollPane_configurationID.setViewportView(table_configaration);
			table_configaration.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));

			JPanel panel_projectInfomation = new JPanel();
			panel_projectInfomation.setBounds(404, 10, 449, 505);
			getContentPane().add(panel_projectInfomation);
			panel_projectInfomation.setBorder(BorderFactory.createTitledBorder("项目信息"));
			panel_projectInfomation.setLayout(null);

			label_configurationID = new JLabel("配置单号");
			label_configurationID.setBounds(22, 24, 57, 15);
			panel_projectInfomation.add(label_configurationID);

			textField_configurationID = new JTextField();
			textField_configurationID.setEditable(false);
			textField_configurationID.setColumns(10);
			textField_configurationID.setBounds(89, 21, 132, 21);
			panel_projectInfomation.add(textField_configurationID);

			JLabel label_revision = new JLabel("版本");
			label_revision.setBounds(253, 24, 32, 15);
			panel_projectInfomation.add(label_revision);

			textField__revision = new JTextField();
			textField__revision.setEditable(false);
			textField__revision.setColumns(10);
			textField__revision.setBounds(295, 21, 144, 21);
			panel_projectInfomation.add(textField__revision);

			button_paramRead = new JButton("参数读取");
			button_paramRead.setEnabled(false);
			button_paramRead.setBounds(10, 49, 86, 21);
			panel_projectInfomation.add(button_paramRead);

			button_paramCaculate = new JButton("参数计算");
			button_paramCaculate.setEnabled(false);
			button_paramCaculate.setBounds(123, 49, 86, 21);
			panel_projectInfomation.add(button_paramCaculate);

			button_BOMConfiguration = new JButton("配置预览");
			button_BOMConfiguration.setEnabled(false);
			button_BOMConfiguration.setBounds(236, 49, 92, 21);
			panel_projectInfomation.add(button_BOMConfiguration);

			button_configurationCaculate = new JButton("配置计算");
			button_configurationCaculate.setEnabled(false);
			button_configurationCaculate.setBounds(353, 49, 86, 21);
			panel_projectInfomation.add(button_configurationCaculate);

			JScrollPane scrollPane_paramInformation = new JScrollPane();
			scrollPane_paramInformation.setBounds(10, 80, 429, 415);
			scrollPane_paramInformation.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
			scrollPane_paramInformation.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
			panel_projectInfomation.add(scrollPane_paramInformation);

			// 定义table并不可编辑
			table_projectInfomation = new JTable(tableModel) {
				/**
				 * 
				 */
				private static final long serialVersionUID = 1L;

				public boolean isCellEditable(int row, int column) {
					return false;
				}

				public String getToolTipText(MouseEvent e) {
					int row = table_projectInfomation.rowAtPoint(e.getPoint());
					int col = table_projectInfomation.columnAtPoint(e.getPoint());

					if (row > -1 && col > -1) {
						Object value = table_projectInfomation.getValueAt(row, col);
						if (value != null && !value.equals("")) {
							tipText = value.toString();
						} else {
							return null;
						}
					}
					return tipText;
				}
			};
			table_projectInfomation.setAutoCreateRowSorter(true);
			table_projectInfomation.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
			table_projectInfomation.setModel(new DefaultTableModel(new Object[][] {}, new String[] { "参数代号", "参数名称", "参数值", "参数范围" }));
			scrollPane_paramInformation.setViewportView(table_projectInfomation);
			table_projectInfomation.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));

			// 右键菜单
			popupMenu = new JPopupMenu();
			checkStructItem = new JMenuItem("查看配置单结构");
			popupMenu.add(checkStructItem);

			configListModel = (DefaultTableModel) table_configaration.getModel();
			projectInfomationModel = (DefaultTableModel) table_projectInfomation.getModel();

			// 搜索
			button_search.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					// 查找首选项
					AbstractAIFUIApplication application = AIFDesktop.getActiveDesktop().getCurrentApplication();
					TCSession session = (TCSession) application.getSession();
					TCPreferenceService preferenceService = session.getPreferenceService();
					String workFlowName = preferenceService
							.getString(TCPreferenceService.TC_preference_site, PreferenceService.CONFIG_WORkFLOW);
					if (workFlowName == null || workFlowName.equals("")) {
						MessageBox.post("首选项SJEC_CFG_WF没有值，请配置。", "参数管理", MessageBox.ERROR);
						return;
					}
					configListModel.setRowCount(0);
					String searchId = textField_searchID.getText();
					String searchIdType = comboBox_searchID.getSelectedItem().toString();
					if (searchId == null || searchId.equals("")) {
						MessageBox.post("请输入" + searchIdType + "进行搜索", "参数管理", MessageBox.ERROR);
						return;
					}
					if (!searchId.equals("")) {
						searchId = "*" + searchId + "*";
						String taskStatu = comboBox_taskStatus.getSelectedItem().toString();
						SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
						Date startedDate = propertyDateButton_startedTime.getDate();
						if (startedDate != null) {
							startedTime = simpleDateFormat.format(startedDate);
						}
						Date finishedDate = propertyDateButton_finishedTime.getDate();
						if (finishedDate != null) {
							finishedTime = simpleDateFormat.format(finishedDate);
						} else {
							Date date = new Date();
							finishedTime = simpleDateFormat.format(date);// 当前时间
						}
						cfgManagementService.searchCfgList(configListModel, searchId, searchIdType, taskStatu, startedTime, finishedTime,
								workFlowName);
					} else {
						MessageBox.post("未找到相关配置单信息。", "配置单管理", MessageBox.WARNING);
					}
				}
			});

			// 单击table_configaration，右侧更新
			table_configaration.addMouseListener(new MouseAdapter() {
				public void mouseClicked(MouseEvent event) {
					if (event.getButton() == java.awt.event.MouseEvent.BUTTON1) {
						if (table_configaration.rowAtPoint(event.getPoint()) < 0) {
							return;
						}
						String configutationID = (String) table_configaration.getValueAt(table_configaration.getSelectedRow(), 0); // 配置单号
						String configurationRev = (String) table_configaration.getValueAt(table_configaration.getSelectedRow(), 1); // 版本
						textField_configurationID.setText(configutationID);
						textField__revision.setText(configurationRev);
						button_paramRead.setEnabled(true);
					}
				}
			});

			// 为JTble对象添加点击事件
			table_configaration.addMouseListener(new MouseAdapter() {
				public void mouseClicked(MouseEvent event) {
					if (event.getButton() == java.awt.event.MouseEvent.BUTTON3) {
						// 通过点击位置找到点击为表格中的行
						int focusedRowIndex = table_configaration.rowAtPoint(event.getPoint());
						if (focusedRowIndex == -1) {
							return;
						}
						// 将表格所选项设为当前右键点击的行
						table_configaration.setRowSelectionInterval(focusedRowIndex, focusedRowIndex);
						// 弹出菜单
						popupMenu.show(table_configaration, event.getX(), event.getY());
						cfgItemId = (String) table_configaration.getValueAt(table_configaration.getSelectedRow(), 0); // 配置单号
					}
				}
			});

			// 查看配置单结构
			checkStructItem.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					// 查找配置单
					TCComponentQuery query = QueryUtil.getTCComponentQuery("Item...");
					TCComponent[] searchResult = QueryUtil.getSearchResult(query, new String[] { "零组件 ID" }, new String[] { cfgItemId });
					if (searchResult.length > 0) {
						TCComponentItem item = (TCComponentItem) searchResult[0];
						// 通过配置单获取视图版本
						TCComponentBOMViewRevision bomViewRevision = null;
						try {
							TCComponentItemRevision itemRev = item.getLatestItemRevision();
							TCComponent[] component = itemRev.getRelatedComponents();
							for (int j = 0; j < component.length; j++) {
								if (component[j].getType().equals("BOMView Revision")) {
									bomViewRevision = (TCComponentBOMViewRevision) component[j];
									break;
								}
							}
						} catch (TCException e1) {
							e1.printStackTrace();
						}
						AbstractPSEApplication pse = new PSEAPP().GetPSEAPP();
						pse.openBaseline(bomViewRevision, true);
					}
				}
			});

			// 参数读取
			button_paramRead.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					paramReadedList = new ArrayList<ParamReadedBean>();
					// 清空table信息
					projectInfomationModel.setRowCount(0);
					// 获取当前配置单参数并显示
					String cfgListId = textField_configurationID.getText();
					paramReadedList = cfgManagementService.getParamReadedList(cfgListId);
					if (paramReadedList != null && paramReadedList.size() > 0) {
						cfgManagementService.showParamReadedTable(projectInfomationModel, paramReadedList);
						button_paramRead.setEnabled(false);
						button_paramCaculate.setEnabled(true);
						button_BOMConfiguration.setEnabled(false);
						button_configurationCaculate.setEnabled(false);
					} else {
						MessageBox.post("没有相关配置单参数。", "参数读取", MessageBox.WARNING);
					}
				}
			});

			// 参数计算
			button_paramCaculate.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					String configListId = textField_configurationID.getText();
					String configListRev = textField__revision.getText();
					excelpath = cfgManagementService.downLoadExcelToDir(PreferenceService.ELEVATOR_COMPUTATION_LIST_PATH, configListId,
							configListRev);
					if (excelpath != null) {
						// 将信息写入参数表sheet,计算项列表存入t_condition
						cfgManagementService.paramCalculated(paramReadedList, excelpath, configListId, button_paramCaculate,
								button_BOMConfiguration);
					}
				}
			});

			// 配置预览
			button_BOMConfiguration.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					String configListId = textField_configurationID.getText();
					// 根据配置单号，找到电梯型号（参数）找到对应的超级BOM  （电梯型号即为超级BOM的ID）
					productType = cfgManagementService.queryProductType(configListId);
					// 通过查询器找到对应的BOMLine
					if (productType != null) {
						CommonFunction.GetGroupSuffix();
						String superBomID = productType + ConstDefine.TC_GROUP_SUFFIX;
						if (ConstDefine.IFTEST) {
							superBomID = productType;
						}
						TCComponentQuery query = QueryUtil.getTCComponentQuery("Item...");
						TCComponent[] searchResult = QueryUtil.getSearchResult(query, new String[] { "零组件 ID" }, new String[] { superBomID });
						if (searchResult == null || searchResult.length == 0) {
							JOptionPane.showMessageDialog(m_frame, "没有找到超级BOM " + superBomID, "提示", JOptionPane.INFORMATION_MESSAGE);
						} else if (searchResult.length != 1) {
							JOptionPane.showMessageDialog(m_frame, "通过ID " + superBomID + " 找到多个Item", "提示", JOptionPane.INFORMATION_MESSAGE);
						}
						
						TCComponentItemRevision itemRevision = null;
						try {
							itemRevision = ((TCComponentItem) searchResult[0]).getLatestItemRevision();
							// 通过配置单获取视图版本
							TCComponentBOMViewRevision bomViewRevision = null;
							TCComponent[] component = itemRevision.getRelatedComponents();
							for (int j = 0; j < component.length; j++) {
								if (component[j].getType().equals("BOMView Revision")) {
									bomViewRevision = (TCComponentBOMViewRevision) component[j];
									break;
								}
							}
							// 将超级BOM发送到结构管理器
							AbstractPSEApplication pse = new PSEAPP().GetPSEAPP();
							pse.openBaseline(bomViewRevision, true);
						} catch (TCException e1) {
							e1.printStackTrace();
						}
						//过滤超级BOM并显示
						superTopBOMLine = BomUtil.getTopBomLine(itemRevision, "视图");
						if (superTopBOMLine == null) {
							superTopBOMLine = BomUtil.getTopBomLine(itemRevision, "View");
						}
						if (superTopBOMLine == null) {
							MessageBox.post("没有找到超级BOM" + superBomID + "的视图", "提示", MessageBox.INFORMATION);
							return;
						}
						
						Thread thread = new Thread()
						{
							@Override
							public void run() {
								try {
									// 将参数读取中获取的参数信息存放到map里
									paramMap = new HashMap<String, String>();
									paramMap = cfgManagementService.getBOMConfigParamInfo(paramReadedList);
									
									BomToPreviewBean bean = new BomToPreviewBean(superTopBOMLine);
									BomToPreviewBean.AllBeanCount = 0;
									GetBomToPreviewBean(superTopBOMLine, bean);
									
									m_frame.setAlwaysOnTop(false);
//									TipsUI.CloseUI();
									new PreviewBom(m_frame, bean);
									// 打开预览
//									new TreeTableExample(button_BOMConfiguration, button_configurationCaculate, superTopBOMLine, configListId, paramMap);
									
								} catch (Exception e) {
									JOptionPane.showMessageDialog(TipsUI.contentPanel, "组织配置预览数据出错：" + e.getMessage() + "\n详细信息请联系管理员查看控制台", "提示", JOptionPane.INFORMATION_MESSAGE);
									e.printStackTrace();
								}
//								finally {
//									m_frame.dispose();
//								}
							}
						};
						TipsUI.ShowUI("正在组织配置预览...", thread, m_frame);
					} else {
						MessageBox.post("未找到该配置单号对应的产品型号。", "BOM配置", MessageBox.ERROR);
					}
				}
			});

			// 配置计算
			button_configurationCaculate.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					String configListId = textField_configurationID.getText();
					// 先把配置单发送到结构管理器
					TCComponent[] searchResult = null;
					TCComponentQuery query = QueryUtil.getTCComponentQuery("Item...");
					searchResult = QueryUtil.getSearchResult(query, new String[] { "零组件 ID" }, new String[] { configListId });
					if (searchResult.length > 0) {
						try {
							TCComponentItemRevision configListItemRev = ((TCComponentItem) searchResult[0]).getLatestItemRevision();
							// 通过配置单获取视图版本
							TCComponentBOMViewRevision bomViewRevision = null;
							TCComponent[] component = configListItemRev.getRelatedComponents();
							for (int j = 0; j < component.length; j++) {
								if (component[j].getType().equals("BOMView Revision")) {
									bomViewRevision = (TCComponentBOMViewRevision) component[j];
									break;
								}
							}
							// 将配置单发送到结构管理器
							AbstractPSEApplication pse = new PSEAPP().GetPSEAPP();
							pse.openBaseline(bomViewRevision, true);
						} catch (TCException e1) {
							e1.printStackTrace();
						}
					}
					
					// 从数据库中获取计算项列表信息
					computationMap = cfgManagementService.getComputationMap(configListId);
					CommonFunction.GetGroupSuffix();
					searchResult = QueryUtil.getSearchResult(query, new String[] { "零组件 ID" }, new String[] { configListId + ConstDefine.TC_GROUP_SUFFIX });
					if (searchResult.length > 0) {
						TCComponentBOMLine configListTopBomLine = null;
						try {
							TCComponentItemRevision configListItemRev = ((TCComponentItem) searchResult[0]).getLatestItemRevision();
							configListTopBomLine = BomUtil.setBOMViewForItemRev(configListItemRev);
						} catch (TCException e1) {
							e1.printStackTrace();
						}
						if (superTopBOMLine != null && configListTopBomLine != null) {
							cfgManagementService.configurantionCalculated(superTopBOMLine, configListTopBomLine, paramMap, computationMap,
									configListId, button_configurationCaculate);
						} else {
							MessageBox.post("配置计算失败。", "配置计算", MessageBox.ERROR);
						}
					} else {
						MessageBox.post("未查询到指定Item.", "配置计算", MessageBox.ERROR);
					}
				}
			});
		}
		
		private void GetBomToPreviewBean(TCComponentBOMLine topBomLine, BomToPreviewBean rootBean) throws Exception {
			rootBean.children = new ArrayList<BomToPreviewBean>();
			AIFComponentContext[] children = topBomLine.getChildren();		
			for (AIFComponentContext context : children) {
				TCComponentBOMLine bomLine = (TCComponentBOMLine) context.getComponent();
				
				// 判断bomLine是否符合条件
				if(!JudgeIfAdd(bomLine)) {
					continue;
				}
				
				BomToPreviewBean bean = new BomToPreviewBean(bomLine);
				if (bomLine.hasChildren()) {
					GetBomToPreviewBean(bomLine, bean);
				}
				rootBean.children.add(bean);
				BomToPreviewBean.AllBeanCount++;
			}
		}
		
		private boolean JudgeIfAdd(TCComponentBOMLine bomLine) throws Exception {
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
				
//				String keyStr = key + "==";
//				if (variableCondition.contains(keyStr)) {
//					variableCondition = variableCondition.replaceAll(keyStr, paramMap.get(key)+"==");
//				}
			}
			variableCondition = variableCondition.replaceAll("!=", "!==");
			return MathUtil.PassCondition(variableCondition);
		}
		
		
		public void dispose() {
			super.dispose();
			m_frame = null;
		}
		
		public void SetButton() {
			button_configurationCaculate.setEnabled(true);
			button_BOMConfiguration.setEnabled(false);
		}
		
		public void SetButton1() {
			button_BOMConfiguration.setEnabled(false);
		}
		
		public void SetButton2() {
			button_BOMConfiguration.setEnabled(true);
		}
	}
}
