package com.uds.sjec.controler;

import java.awt.EventQueue;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileSystemView;

import com.teamcenter.rac.kernel.TCComponentBOMLine;
import com.teamcenter.rac.util.MessageBox;
import com.uds.sjec.base.BaseControler;
import com.uds.sjec.bean.ProductionTableBean;
import com.uds.sjec.common.CommonFunction;
import com.uds.sjec.common.ConstDefine;
import com.uds.sjec.service.IProductionTableService;
import com.uds.sjec.service.impl.ProductionTableImpl;
import com.uds.sjec.utils.TipsUI;

import common.Jr.utils.AsposeExcelUtils;

public class ProductionTableControler implements BaseControler {
	private static ExportBomUI m_frame;
	private Rectangle m_rectangle;
	private TCComponentBOMLine m_topBomLine;
	private String m_commandId;

	public IProductionTableService productionTableService = new ProductionTableImpl();

	public void userTask(Rectangle rectangle, TCComponentBOMLine topBomLine, String commandId) {
		m_rectangle = rectangle;
		m_topBomLine = topBomLine;
		m_commandId = commandId;
		
		EventQueue.invokeLater(new Runnable() {
			@Override
			public void run() {
				try {
					if (m_frame == null) {
						m_frame = new ExportBomUI();
					}
					m_frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});

	}
	
	class ExportBomUI extends JFrame {
		private static final long serialVersionUID = 1L;
		
		private JPanel contentPane;
		private JTextField textField;

		/**
		 * Create the frame.
		 */
		public ExportBomUI() {
//			setIconImage(Toolkit.getDefaultToolkit().getImage(ExportBomUI.class.getResource("/logo2.png")));
			setResizable(false);
			setTitle("��������");
			setAlwaysOnTop(true);
			setType(Type.UTILITY);
			setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
			// ���ݴ�����Rectangle���ô���λ��
	        int centerX = m_rectangle.x + m_rectangle.width / 2;
	        int centerY = m_rectangle.y + m_rectangle.height / 2;
	        setBounds(centerX - 200, centerY - 80, 400, 160);
//			setBounds(100, 100, 400, 160);
			contentPane = new JPanel();
			contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
			setContentPane(contentPane);
			contentPane.setLayout(null);
			
			JLabel label = new JLabel("��ѡ��·����");
			label.setBounds(10, 10, 179, 15);
			contentPane.add(label);
			
			textField = new JTextField();
			textField.setBounds(10, 36, 281, 23);
			textField.setEditable(false);
			contentPane.add(textField);
			
			JButton btnNewButton = new JButton("ѡ��");
			btnNewButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					// ѡ���ļ������Ŀ¼
	                String filePath = "";
	                JFileChooser fileChooser  = new JFileChooser();
	                FileSystemView fsv = FileSystemView.getFileSystemView();
	                fileChooser .setCurrentDirectory(fsv.getHomeDirectory());
	                fileChooser .setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
	                int returnVal = fileChooser.showDialog(contentPane, "ѡ��·��");
	                if (returnVal == JFileChooser.APPROVE_OPTION) {
	                    filePath= fileChooser.getSelectedFile().getAbsolutePath();
	                }
	                if (!filePath.equals("")) {
	                	textField.setText(filePath);
	                }
				}
			});
			btnNewButton.setBounds(300, 36, 74, 23);
			contentPane.add(btnNewButton);
			
			JButton btnNewButton_1 = new JButton("ȷ��");
			btnNewButton_1.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					final String filePath = textField.getText();
					if (filePath == null || filePath.equals("")) {
						JOptionPane.showMessageDialog(contentPane, "����ѡ��·��", "��ʾ", JOptionPane.INFORMATION_MESSAGE);
	                    return;
					}
					
					Thread thread = new Thread()
					{
						@Override
						public void run() {
							try {
								String itemID = m_topBomLine.getProperty("bl_item_item_id");
								String revision = m_topBomLine.getProperty("bl_rev_item_revision_id");
								String objectName = m_topBomLine.getProperty("bl_item_object_name");
								String fileName = "";
								String fileNamePdf = "";
								List<ProductionTableBean> productonTableList = new ArrayList<ProductionTableBean>();
								
								if (("com.uds.sjec.commands.productionTable").equals(m_commandId)
										|| ("com.uds.sjec.commands.productionTablePdf").equals(m_commandId)) {
									productonTableList = productionTableService.getAndSortAllBOM(m_topBomLine);
									fileName = itemID + "-" + revision + "-" + objectName + "-���" + ".xlsx";
									fileNamePdf = itemID + "-" + revision + "-" + objectName + "-���" + ".pdf";
								} else {
									productonTableList = productionTableService.getAndSortBOMOfOne(m_topBomLine);
									fileName = itemID + "-" + revision + "-" + objectName + "-����" + ".xlsx";
									fileNamePdf = itemID + "-" + revision + "-" + objectName + "-����" + ".pdf";
								}
								
								if (("com.uds.sjec.commands.productionTablePdf").equals(m_commandId)
										|| ("com.uds.sjec.commands.productionTablePdfOfLevelOne").equals(m_commandId)) { // PDF
									CommonFunction.GetTCSession();
									String tempDir = System.getProperty("java.io.tmpdir");
									Boolean reg = productionTableService.downloadDatasetToLocal(ConstDefine.TC_SESSION, "MSExcelX", "������ϸ��", "excel", fileName, tempDir);
									if (reg && productonTableList.size() > 0) {
										Boolean isFinished = productionTableService.writeToExcel(m_topBomLine, productonTableList, tempDir + "\\"
												+ fileName);
										try { // ����Ϊexcel
											AsposeExcelUtils.ExcelToPDF(tempDir + "\\" + fileName, filePath + "\\" + fileNamePdf);
											// ɾ��TEMP�µ�excel
											File file = new File(tempDir + "\\" + fileName);
											if (file.exists() && file.isFile()) {
												file.delete();
											}
											if (isFinished) {
												MessageBox.post("��ϸ�����ɳɹ���", "�����ϸ��", MessageBox.INFORMATION);
											}
										} catch (Exception e1) {
											e1.printStackTrace();
										}
									}
								} else { // excel
									Boolean reg = productionTableService.downloadDatasetToLocal(ConstDefine.TC_SESSION, "MSExcelX", "������ϸ��", "excel", fileName,
											filePath);
									// ������سɹ�����excel��д������
									if (reg) {
										Boolean isFinished = productionTableService.writeToExcel(m_topBomLine, productonTableList, filePath + "\\"
												+ fileName);
										if (isFinished) {
											MessageBox.post("��ϸ�����ɳɹ���", "�����ϸ��", MessageBox.INFORMATION);
										}
									}
								}
							} catch (Exception e) {
								JOptionPane.showMessageDialog(TipsUI.contentPanel, "����" + e.getMessage() + "\n��ϸ��Ϣ����ϵ����Ա�鿴����̨", "��ʾ", JOptionPane.INFORMATION_MESSAGE);
								e.printStackTrace();
							} finally {
								m_frame.dispose();
							}
						}
					};
					TipsUI.ShowUI("���ڵ�������...", thread, m_frame);
				}
			});
			btnNewButton_1.setBounds(66, 79, 93, 23);
			contentPane.add(btnNewButton_1);
			
			JButton btnNewButton_2 = new JButton("ȡ��");
			btnNewButton_2.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					dispose();
				}
			});
			btnNewButton_2.setBounds(225, 79, 93, 23);
			contentPane.add(btnNewButton_2);
		}
		
		public void dispose() {
			super.dispose();
			m_frame = null;
		}
	}

	@Override
	public void userTask(TCComponentBOMLine bomLine, String m_commandId) {
	}
}
