//package com.uds.sjec.controler;
//
//import java.awt.EventQueue;
//import java.awt.event.ActionEvent;
//import java.awt.event.ActionListener;
//import java.io.File;
//import java.util.ArrayList;
//import java.util.List;
//import javax.swing.JButton;
//import javax.swing.JFileChooser;
//import javax.swing.JFrame;
//import javax.swing.JTextField;
//import javax.swing.WindowConstants;
//import com.teamcenter.rac.aif.AIFDesktop;
//import com.teamcenter.rac.aif.AbstractAIFUIApplication;
//import com.teamcenter.rac.kernel.TCComponentBOMLine;
//import com.teamcenter.rac.kernel.TCException;
//import com.teamcenter.rac.kernel.TCSession;
//import com.teamcenter.rac.util.MessageBox;
//import com.uds.sjec.base.BaseControler;
//import com.uds.sjec.bean.ProductionTableBean;
//import com.uds.sjec.service.IProductionTableService;
//import com.uds.sjec.service.impl.ProductionTableImpl;
//import com.uds.sjec.view.UDSJProcessBar;
//
//import common.Jr.utils.AsposeExcelUtils;
//
//public class ProductionTableControler_pre implements BaseControler {
//
//	public IProductionTableService productionTableService = new ProductionTableImpl();
//
//	public void userTask(final TCComponentBOMLine topBomLine, final String m_commandId) {
//		EventQueue.invokeLater(new Runnable() {
//			@Override
//			public void run() {
//				try {
//					FileChooser frame = new FileChooser(topBomLine, m_commandId);
//					frame.setVisible(true);
//					frame.setTitle("输出BOM明细表");
//					frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
//					frame.setResizable(false);
//				} catch (Exception e) {
//					e.printStackTrace();
//				}
//			}
//		});
//	}
//
//	class FileChooser extends JFrame {
//
//		/**
//		 * 
//		 */
//		private static final long serialVersionUID = 1L;
//		List<TCComponentBOMLine> productionBOMLineList = new ArrayList<TCComponentBOMLine>();
//		List<ProductionTableBean> productonTableList = new ArrayList<ProductionTableBean>();
//		private JTextField textField;
//		private JButton comfirm_Button;
//		private JButton cancle_Button;
//		private String fileName;
//		private String fileNamePdf;
//		private String tempDir;
//		private UDSJProcessBar bar;
//		private String itemID;
//		private String revision;
//		private String objectName;
//
//		private FileChooser(final TCComponentBOMLine topBomLine, final String m_commandId) {
//			setBounds(100, 100, 602, 152);
//			setLocationRelativeTo(null);
//			setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
//			getContentPane().setLayout(null);
//			textField = new JTextField();
//			textField.setBounds(32, 25, 328, 30);
//			getContentPane().add(textField);
//			textField.setColumns(10);
//			JButton pathChoosed_Button = new JButton("选择存储路径");
//			pathChoosed_Button.setBounds(392, 25, 156, 30);
//			getContentPane().add(pathChoosed_Button);
//			comfirm_Button = new JButton("确认");
//			comfirm_Button.setBounds(392, 73, 72, 30);
//			getContentPane().add(comfirm_Button);
//			cancle_Button = new JButton("取消");
//			cancle_Button.setBounds(476, 73, 72, 30);
//			getContentPane().add(cancle_Button);
//
//			// 选择路径
//			pathChoosed_Button.addActionListener(new ActionListener() {
//				public void actionPerformed(ActionEvent arg0) {
//					JFileChooser jFileChooser = new JFileChooser();
//					jFileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
//					int intRetVal = jFileChooser.showOpenDialog(null);
//					if (intRetVal == JFileChooser.APPROVE_OPTION) {
//						textField.setText(jFileChooser.getSelectedFile().getPath());
//					}
//				}
//			});
//
//			// 确认
//			comfirm_Button.addActionListener(new ActionListener() {
//				public void actionPerformed(ActionEvent e) {
//					AbstractAIFUIApplication application = AIFDesktop.getActiveDesktop().getCurrentApplication();
//					TCSession session = (TCSession) application.getSession();
//					dispose();
//					// 在路径下创建该文件夹
//					String path = textField.getText();
//					tempDir = System.getProperty("java.io.tmpdir");
//					try {
//						itemID = topBomLine.getProperty("bl_item_item_id");
//						revision = topBomLine.getProperty("bl_rev_item_revision_id");
//						objectName = topBomLine.getProperty("bl_item_object_name");
//					} catch (TCException e1) {
//						e1.printStackTrace();
//					}
//					if (!path.equals("")) {
//						bar = new UDSJProcessBar();
//						bar.setLocationRelativeTo(null);
//						bar.setVisible(true);
//						application = AIFDesktop.getActiveDesktop().getCurrentApplication();
//						session = (TCSession) application.getSession();
//						if (("com.uds.sjec.commands.productionTable").equals(m_commandId)
//								|| ("com.uds.sjec.commands.productionTablePdf").equals(m_commandId)) {
//							productonTableList = productionTableService.getAndSortAllBOM(topBomLine, bar);
//							fileName = itemID + "-" + revision + "-" + objectName + "-多层" + ".xlsx";
//							fileNamePdf = itemID + "-" + revision + "-" + objectName + "-多层" + ".pdf";
//						} else {
//							productonTableList = productionTableService.getAndSortBOMOfOne(topBomLine, bar);
//							fileName = itemID + "-" + revision + "-" + objectName + "-单层" + ".xlsx";
//							fileNamePdf = itemID + "-" + revision + "-" + objectName + "-单层" + ".pdf";
//						}
//						if (("com.uds.sjec.commands.productionTablePdf").equals(m_commandId)
//								|| ("com.uds.sjec.commands.productionTablePdfOfLevelOne").equals(m_commandId)) { // PDF
//							Boolean reg = productionTableService.downloadDatasetToLocal(session, "MSExcelX", "生产明细表", "excel", fileName,
//									tempDir, bar);
//							if (reg && productonTableList.size() > 0) {
//								Boolean isFinished = productionTableService.writeToExcel(topBomLine, productonTableList, tempDir + "\\"
//										+ fileName, bar);
//								try { // 导出为excel
//									AsposeExcelUtils.ExcelToPDF(tempDir + "\\" + fileName, path + "\\" + fileNamePdf);
//									// 删除TEMP下的excel
//									File file = new File(tempDir + "\\" + fileName);
//									if (file.exists() && file.isFile()) {
//										file.delete();
//									}
//									if (isFinished) {
//										bar.setVisible(false);
//										MessageBox.post("明细表生成成功！", "输出明细表", MessageBox.INFORMATION);
//									}
//								} catch (Exception e1) {
//									e1.printStackTrace();
//								}
//							}
//						} else { // excel
//							Boolean reg = productionTableService.downloadDatasetToLocal(session, "MSExcelX", "生产明细表", "excel", fileName,
//									path, bar);
//							// 如果下载成功，向excel里写入数据
//							if (reg) {
//								Boolean isFinished = productionTableService.writeToExcel(topBomLine, productonTableList, path + "\\"
//										+ fileName, bar);
//								if (isFinished) {
//									bar.setVisible(false);
//									MessageBox.post("明细表生成成功！", "输出明细表", MessageBox.INFORMATION);
//								}
//							}
//						}
//					} else {
//						MessageBox.post("请选择导出路径！", "输出明细表", MessageBox.WARNING);
//						return;
//					}
//				}
//
//			});
//			// 取消
//			cancle_Button.addActionListener(new ActionListener() {
//				public void actionPerformed(ActionEvent e) {
//					dispose();
//				}
//			});
//		}
//
//	}
//}
