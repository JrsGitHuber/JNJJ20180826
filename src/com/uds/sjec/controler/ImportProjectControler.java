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
		private List<ParamViewTableBean> paramInfoFromExcelList; // �洢��excel�л�ȡ�Ĳ�����Ϣ
		private List<ParamViewTableBean> paramInfoFromDatabaseList; // �洢��t_elevator_param�ӻ�ȡ�Ĳ�����Ϣ
		private List<String> elevatorTypeList; // �洢���������������ֵ
		private TCComponentItem configurationListItem;
		private DefaultTableModel previewModel;
		private String equipmentNo;// �豸��
		private String tipText;
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		public ImportProjectFrame(final TCComponentDataset dataset) {
			getContentPane().setFont(new Font("����", Font.BOLD, 12));
			setTitle("������Ŀ");
			setBounds(100, 100, 456, 586);
			setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
			getContentPane().setLayout(null);
			setLocationRelativeTo(null);

			JPanel panel = new JPanel();
			panel.setBounds(10, 10, 420, 530);
			panel.setBorder(BorderFactory.createTitledBorder(""));
			getContentPane().add(panel);
			panel.setLayout(null);

			JLabel label_ConfigID = new JLabel("���õ����");
			label_ConfigID.setBounds(10, 10, 72, 21);
			panel.add(label_ConfigID);

			JLabel label_ConfigRev = new JLabel("���õ��汾");
			label_ConfigRev.setBounds(220, 10, 72, 21);
			panel.add(label_ConfigRev);

			JLabel label_elevatorType = new JLabel("�����ͺ�");
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

			button_paramPreview = new JButton("Ԥ��");
			button_paramPreview.setBounds(201, 494, 62, 23);
			panel.add(button_paramPreview);

			button_createItem = new JButton("����");
			button_createItem.setBounds(273, 494, 62, 23);
			panel.add(button_createItem);

			button_dispatch = new JButton("����");
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
				
				// �������ʱ��ʾֵ
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
			// ����table������
			table_preview.setAutoCreateRowSorter(true);
			table_preview.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
			table_preview.setModel(new DefaultTableModel(new Object[][] {}, new String[] { "��������", "��������", "����ֵ", "��������" }));
			scrollPane.setViewportView(table_preview);
			table_preview.setBorder(new LineBorder(new Color(0, 0, 0)));
			table_preview.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));

			previewModel = (DefaultTableModel) table_preview.getModel();

			// Ԥ��
			button_paramPreview.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					// ��ȡ��������
					elevatorTypeList = new ArrayList<String>();
					elevatorTypeList = importProjectService.queryElevatorTypeList();
					paramInfoFromExcelList = new ArrayList<ParamViewTableBean>();
					paramInfoFromDatabaseList = new ArrayList<ParamViewTableBean>();
					// �����ݼ����ص�����
					String tempDir = System.getProperty("java.io.tmpdir");
					boolean ret = DatesetUtil.datasetFileToLocalDir(ConstDefine.TC_SESSION, dataset, "excel", "����¼���.xlsx", tempDir);
					if (ret) {
						// ��ȡexcel�������ݣ�ȷ�������������ظ���жϵ������������ݿ����Ƿ����
						excelPath = tempDir + "\\" + "����¼���.xlsx";
						// TODO Jr textField_ElevatorType�Ƿ����ɾ������ǰ������
						paramInfoFromExcelList = importProjectService
								.getParamExcelList(excelPath, elevatorTypeList, textField_ElevatorType);
						if (paramInfoFromExcelList != null && paramInfoFromExcelList.size() > 0) {
							// ���table
							DefaultTableModel model = new DefaultTableModel();
							model = (DefaultTableModel) table_preview.getModel();
							model.setRowCount(0);
							// �����ݿ��ȡ������Ϣ
							paramInfoFromDatabaseList = importProjectService.getParamDatabaseList(textField_ElevatorType.getText());
							// �Ա�����List���õ�Ԥ����Ϣ
							paramInfoFromDatabaseList = importProjectService.compareInfo(paramInfoFromExcelList, paramInfoFromDatabaseList);
							// ��excel��������ʾ��Table��
							importProjectService.showData(previewModel, paramInfoFromDatabaseList);
						}
					}
				}

			});

			// ����
			button_createItem.addActionListener(new ActionListener() {
				@SuppressWarnings({ "static-access", "deprecation" })
				public void actionPerformed(ActionEvent e) {
					if (previewModel.getRowCount() > 0) {
						TCComponentFolder componentFolder = new TCComponentFolder();
						TCComponentFolder newstuffFolder = null;
						try {
							for (int i = 0; i < paramInfoFromDatabaseList.size(); i++) {
								if (paramInfoFromDatabaseList.get(i).paramName.equals("�豸��")) {
									String[] tempString = paramInfoFromDatabaseList.get(i).paramValue.split("%");
									equipmentNo = tempString[0];
								}
							}
							if (equipmentNo == null || equipmentNo.equals("")) {
								MessageBox.post("û���ҵ����� �豸��", "��ʾ", MessageBox.INFORMATION);
								return;
							}
							
							// ��SQLServer�л�ȡ���õ���
							String configListID = importProjectService.getConfigListID(equipmentNo);
							if (configListID == null || configListID.equals("")) {
								MessageBox.post("û�л�ȡ�����õ��ţ�������ˮ�봴�����õ�", "��ʾ", MessageBox.INFORMATION);
								configListID = "";
							}
							
							// ��֯���õ�
							TCComponentQuery itemQuery = TCQueryUtil.GetTCComponentQuery(ConstDefine.TC_SESSION, "����� ID");
							TCComponent[] items0 = TCQueryUtil.GetItemByID(itemQuery, configListID);
							if (items0 == null || items0.length == 0) {
								configurationListItem = importProjectService.createItem(ConstDefine.TC_SESSION, "S2_CFG", configListID, "���õ�", "");
								if (configurationListItem == null) {
									MessageBox.post("�������õ�" + configListID + "ʧ�ܣ���������ϵ����Ա������̨", "��ʾ", MessageBox.INFORMATION);
									return;
								}
							} else {
//								configurationListItem = (TCComponentItem)items0[0];
								MessageBox.post("�������õ�" + configListID + "�Ѿ�����", "��ʾ", MessageBox.INFORMATION);
								return;
							}
							// �������õ�
							TCComponentItem electricalItem = null;
							String electricalItemID = configurationListItem.getProperty("item_id") + ConstDefine.TC_GROUP_ELECTRICAL;
							items0 = TCQueryUtil.GetItemByID(itemQuery, electricalItemID);
							if (items0 == null || items0.length == 0) {
								electricalItem = ItemUtil.createtItem("S2_CFG", electricalItemID, "�������õ�VI", "");
								if (electricalItem == null) {
									MessageBox.post("�������õ�" + electricalItemID + "ʧ�ܣ���������ϵ����Ա������̨", "��ʾ", MessageBox.INFORMATION);
									return;
								}
							} else {
								electricalItem = (TCComponentItem)items0[0];
							}
							// ��е���õ�
							TCComponentItem mechanicalItem = null;
							String mechanicalItemID = configurationListItem.getProperty("item_id") + ConstDefine.TC_GROUP_MECHANICAL;
							items0 = TCQueryUtil.GetItemByID(itemQuery, mechanicalItemID);
							if (items0 == null || items0.length == 0) {
								mechanicalItem = ItemUtil.createtItem("S2_CFG", mechanicalItemID, "��е���õ�VI", "");
								if (mechanicalItem == null) {
									MessageBox.post("�������õ�" + mechanicalItemID + "ʧ�ܣ���������ϵ����Ա������̨", "��ʾ", MessageBox.INFORMATION);
									return;
								}
							} else {
								mechanicalItem = (TCComponentItem)items0[0];
							}
							
							// ����õ�BOM
							TCComponentBOMLine configListTopBomLine = TCBomUtil.GetTopBomLine(ConstDefine.TC_SESSION, configurationListItem.getLatestItemRevision(), "��ͼ");
							if (configListTopBomLine == null) {
								// û����ͼ��ȥ����
								configListTopBomLine = TCBomUtil.SetBOMViewForItemRev(ConstDefine.TC_SESSION, configurationListItem.getLatestItemRevision());
							}else{
								if(configListTopBomLine.hasChildren()){
									AIFComponentContext[] children = configListTopBomLine.getChildren();
									for (AIFComponentContext context : children) {
										TCComponentBOMLine bomLine = (TCComponentBOMLine) context.getComponent();
										bomLine.cut();
									}
									// �����ȹر���ͼ�������´������BomLine�ᰴ��֮ǰ������(���ұ������)
									TCBomUtil.CloseWindow(configListTopBomLine);
									configListTopBomLine = TCBomUtil.GetTopBomLine(ConstDefine.TC_SESSION, configurationListItem.getLatestItemRevision(), "��ͼ");
								}
							}
							configListTopBomLine.add(electricalItem, "");
							configListTopBomLine.add(mechanicalItem, "");
							TCBomUtil.CloseWindow(configListTopBomLine);

							// ���õ�����newStuff��
							try {
								newstuffFolder = componentFolder.getNewStuffFolder(ConstDefine.TC_SESSION);
								newstuffFolder.add("contents", configurationListItem);
							} catch (Exception e1) {
								e1.printStackTrace();
							}

							// �����õ���Ϣ��ʾ�ڴ���
							textField_ID.setText(configurationListItem.getLatestItemRevision().getProperty("item_id"));
							textField_Rev.setText(configurationListItem.getLatestItemRevision().getProperty("item_revision_id"));

							// �����õ���Ϣ��excel��Ϣ�洢�����ݿ�
							boolean isStoraged = importProjectService.getInfoTodatabase(configurationListItem, paramInfoFromExcelList, paramInfoFromDatabaseList, ConstDefine.TC_SESSION);
							if (isStoraged) {
								// ��excel�ϴ���TC
								TCComponentItemRevision itemRevision = configurationListItem.getLatestItemRevision();
								importProjectService.createDataSet(ConstDefine.TC_SESSION, excelPath, "MSExcelX", "excel", 
										configurationListItem.getProperty("item_id") + "����¼���", itemRevision, "IMAN_specification", true);
								MessageBox.post("���õ������ɹ���", "������Ŀ", MessageBox.INFORMATION);
							} else {
								MessageBox.post("���õ��������뵽���ݿ�ʧ�ܡ�", "������Ŀ", MessageBox.INFORMATION);
							}
						} catch (Exception e2) {
							e2.printStackTrace();
							MessageBox.post(e2.getMessage(), "������Ŀ", MessageBox.ERROR);
						}
					} else {
						MessageBox.post("����ִ�в���Ԥ����", "������Ŀ", MessageBox.ERROR);
					}
				}
			});

			// ����
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
							MessageBox.post(e1.getMessage(), "������Ŀ", MessageBox.ERROR);
							return;
						}
						dispose();
					} else {
						MessageBox.post("���ȴ������õ���", "������Ŀ", MessageBox.ERROR);
					}
				}
			});
		}
	}
}
