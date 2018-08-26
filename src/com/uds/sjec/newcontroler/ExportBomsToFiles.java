package com.uds.sjec.newcontroler;

import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map.Entry;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileSystemView;

import com.aspose.cells.Cells;
import com.aspose.cells.FileFormatType;
import com.aspose.cells.License;
import com.aspose.cells.Workbook;
import com.aspose.cells.Worksheet;
import com.teamcenter.rac.aif.kernel.AIFComponentContext;
import com.teamcenter.rac.aif.kernel.InterfaceAIFComponent;
import com.teamcenter.rac.kernel.TCComponentBOMLine;
import com.teamcenter.rac.kernel.TCComponentItemRevision;
import com.teamcenter.rac.util.MessageBox;
import com.uds.sjec.bean.BomToExcelBean;
import com.uds.sjec.utils.BomUtil;
import com.uds.sjec.utils.StringUtils;
import com.uds.sjec.utils.TipsUI;
import common.Jr.utils.AsposeExcelUtils;

public class ExportBomsToFiles {
	private boolean m_ifGetAll;
	private ArrayList<TCComponentBOMLine> m_bomLineList;
	private String m_fileSuffixString;
	private Rectangle m_rectangle;
	private static ExportBomUI m_frame;
	
	public ExportBomsToFiles(Rectangle rectangle) {
		m_ifGetAll = false;
		m_bomLineList = new ArrayList<TCComponentBOMLine>();
		m_fileSuffixString = "";
		m_rectangle = rectangle;
	}

	public void DoTask(String commandID, InterfaceAIFComponent[] selComps, boolean ifAllowRevision) throws Exception {
		if (selComps == null || selComps.length == 0) {
			MessageBox.post("未选中目标，无法进行操作", "提示", MessageBox.INFORMATION);
			return;
		}
		
		for (InterfaceAIFComponent selComp : selComps) {
			TCComponentBOMLine topBomLine = null;
			if (selComp instanceof TCComponentBOMLine) {
				topBomLine = (TCComponentBOMLine)selComp;
			} else {
				if (selComp instanceof TCComponentItemRevision) {
					if (ifAllowRevision) {
						TCComponentItemRevision itemRevision = (TCComponentItemRevision)selComp;
						topBomLine = BomUtil.getTopBomLine(itemRevision, "View");
						if (topBomLine == null) {
							topBomLine = BomUtil.getTopBomLine(itemRevision, "视图");
						}
					} else {
						MessageBox.post("所选目标" + selComp.getProperty("object_name") + "不是BomLine，无法进行操作", "提示", MessageBox.INFORMATION);
						return;
					}
				} else {
					MessageBox.post("所选目标" + selComp.getProperty("object_name") + "不是BomLine，无法进行操作", "提示", MessageBox.INFORMATION);
					return;
				}
			}
			m_bomLineList.add(topBomLine);
		}
		
		if (commandID.equals("com.uds.sjec.commands.productionTable")) {
			m_ifGetAll = true;
			m_fileSuffixString = "-多层.xlsx";
		} else if (commandID.equals("com.uds.sjec.commands.productionTablePdf")) {
			m_ifGetAll = true;
			m_fileSuffixString = "-多层.pdf";
		} else if (commandID.equals("com.uds.sjec.commands.productionTableOfLevelOne")) {
			m_ifGetAll = false;
			m_fileSuffixString = "-单层.xlsx";
		} else if (commandID.equals("com.uds.sjec.commands.productionTablePdfOfLevelOne")) {
			m_ifGetAll = false;
			m_fileSuffixString = "-单层.pdf";
		} else if (commandID.equals("com.uds.sjec.commands.batchExportExcel")) {
			m_ifGetAll = false;
			m_fileSuffixString = "-单层.xlsx";
		} else {
			MessageBox.post("菜单命令ID错误，请联系管理员检查", "提示", MessageBox.INFORMATION);
			return;
		}
		
		try {
			if (m_frame == null) {
				m_frame = new ExportBomUI();
			}
			m_frame.setVisible(true);
		} catch (Exception e) {
			m_frame = null;
			e.printStackTrace();
			MessageBox.post("出错：" + e.getMessage() + "\n详细信息请联系管理员查看控制台", "提示", MessageBox.INFORMATION);
		}
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
			setTitle("导出报表");
			setAlwaysOnTop(true);
			setType(Type.POPUP);
			setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
			// 根据传来的Rectangle设置窗口位置
	        int centerX = m_rectangle.x + m_rectangle.width / 2;
	        int centerY = m_rectangle.y + m_rectangle.height / 2;
	        setBounds(centerX - 200, centerY - 80, 400, 160);
//			setBounds(100, 100, 400, 160);
			contentPane = new JPanel();
			contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
			setContentPane(contentPane);
			contentPane.setLayout(null);
			
			JLabel label = new JLabel("请选择路径：");
			label.setBounds(10, 10, 179, 15);
			contentPane.add(label);
			
			textField = new JTextField();
			textField.setBounds(10, 36, 281, 23);
			textField.setEditable(false);
			contentPane.add(textField);
			
			JButton btnNewButton = new JButton("选择");
			btnNewButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					// 选择文件保存的目录
	                String filePath = "";
	                JFileChooser fileChooser  = new JFileChooser();
	                FileSystemView fsv = FileSystemView.getFileSystemView();
	                fileChooser .setCurrentDirectory(fsv.getHomeDirectory());
	                fileChooser .setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
	                int returnVal = fileChooser.showDialog(contentPane, "选择路径");
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
			
			JButton btnNewButton_1 = new JButton("确定");
			btnNewButton_1.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					final String filePath = textField.getText();
					if (filePath == null || filePath.equals("")) {
						JOptionPane.showMessageDialog(contentPane, "请先选择路径", "提示", JOptionPane.INFORMATION_MESSAGE);
	                    return;
					}
					
					Thread thread = new Thread()
					{
						@Override
						public void run() {
							try {
								// 判断文件是否已经存在
								HashMap<String, TCComponentBOMLine> pathAndBomMap = new HashMap<String, TCComponentBOMLine>();
								for (TCComponentBOMLine bomLine : m_bomLineList) {
				            		String fileName = bomLine.getProperty("bl_item_item_id") + "-" + bomLine.getProperty("bl_rev_item_revision_id") + "-" + bomLine.getProperty("bl_item_object_name");
				        			// 检查特殊字符
				            		fileName = StringUtils.GetNameByString(fileName) + m_fileSuffixString;
				        			File file = new File(filePath + "\\" + fileName);
				                	if (file.exists()) {
				                		JOptionPane.showMessageDialog(contentPane, "目录" + filePath + "下已存在文件" + fileName, "提示", JOptionPane.INFORMATION_MESSAGE);
				                		return;
				                	} else {
				                		pathAndBomMap.put(filePath + "\\" + fileName, bomLine);
				                	}
				            	}
								
								for (Entry<String, TCComponentBOMLine> entry : pathAndBomMap.entrySet()) {
									// 组织数据
							        BomToExcelBean bean = GetBomDataRecursive(entry.getValue(), true, m_ifGetAll, "");
							        // 导出报表
							        DoExport(bean, entry.getKey());
								}
								
								// 提示完成
						        JOptionPane.showMessageDialog(TipsUI.contentPanel, "导出报表成功", "提示", JOptionPane.INFORMATION_MESSAGE);
							} catch (Exception e) {
								JOptionPane.showMessageDialog(TipsUI.contentPanel, "出错：" + e.getMessage() + "\n详细信息请联系管理员查看控制台", "提示", JOptionPane.INFORMATION_MESSAGE);
								e.printStackTrace();
							} finally {
								m_frame.dispose();
							}
						}
					};
					TipsUI.ShowUI("正在导出报表...", thread, m_frame);
				}
			});
			btnNewButton_1.setBounds(66, 79, 93, 23);
			contentPane.add(btnNewButton_1);
			
			JButton btnNewButton_2 = new JButton("取消");
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
	
	private void DoExport(BomToExcelBean bean, String filePath) throws Exception {
		GetLicense();
		
		Workbook wb = new Workbook(FileFormatType.XLSX);
		Worksheet workSheet = wb.getWorksheets().get(0);
		workSheet.setName("第一页");
		Cells cells = workSheet.getCells();
		// 设置表头
		cells.get(1, 1).setValue("装配号");
		cells.get(1, 2).setValue("标识");
		cells.get(1, 3).setValue("代码编号");
		cells.get(1, 4).setValue("名称");
		cells.get(1, 5).setValue("材料");
		cells.get(1, 6).setValue("数量");
		cells.get(1, 7).setValue("长度");
		cells.get(1, 8).setValue("宽度");
		cells.get(1, 9).setValue("备注");
		
		// 设置内容
		SetContent(cells, bean, 2);
		
		// 设置列宽自动适应
        int columnCount = cells.getMaxColumn(); //获取表页的最大列数
        int rowCount = cells.getMaxRow(); //获取表页的最大行数
        for (int col = 0; col < columnCount; col++) {
        	workSheet.autoFitColumn(col, 0, rowCount);
        }
        for (int col = 0; col < columnCount; col++) {
            cells.setColumnWidthPixel(col, cells.getColumnWidthPixel(col) + 30);
        }
        
		wb.save(filePath);
	}
	
	private void GetLicense() {
		InputStream license = AsposeExcelUtils.class.getClassLoader().getResourceAsStream("\\license.xml"); // license路径
		License aposeLic = new License();
		aposeLic.setLicense(license);
	}
	
	private int SetContent(Cells cells, BomToExcelBean bean, int rowIndex) {
		cells.get(rowIndex, 1).setValue(bean.assemblyNumber);
		cells.get(rowIndex, 2).setValue(bean.identification);
		cells.get(rowIndex, 3).setValue(bean.prefix + bean.code);
		cells.get(rowIndex, 4).setValue(bean.name);
		cells.get(rowIndex, 5).setValue(bean.material);
		cells.get(rowIndex, 6).setValue(bean.quantity);
		cells.get(rowIndex, 9).setValue(bean.note);
		if (bean.children == null || bean.children.size() == 0) {
			return rowIndex;
		} else {
			int rowStart = rowIndex + 1;
			int rowEnd = rowIndex + bean.children.size();
			for (BomToExcelBean tempBean : bean.children) {
				rowEnd = SetContent(cells, tempBean, rowStart);
				rowStart = rowEnd + 1;
			}
			cells.groupRows(rowIndex + 1, rowEnd);
			return rowEnd;
		}
	}
	
	private BomToExcelBean GetBomDataRecursive(TCComponentBOMLine topBomLine, boolean ifFirst, boolean ifGetAll, String prefix) throws Exception {
		BomToExcelBean bean = new BomToExcelBean();
		String[] propertiesValue = topBomLine.getProperties(bean.properties);
		bean.assemblyNumber = propertiesValue[0]; // 装配号
		bean.identification = propertiesValue[1]; // 标识
		bean.code = propertiesValue[2]; // 代号编码
		bean.name = propertiesValue[3]; // 名称
		bean.material = propertiesValue[4]; // 材料
		String quantity = propertiesValue[5]; // 数量
		bean.quantity = quantity;
		if (quantity.equals("0") || quantity.equals("")) {
			bean.quantity = propertiesValue[6]; // 数量
		}
		bean.note = propertiesValue[7]; // 备注
		if (!ifFirst) {
			bean.prefix += prefix + "   ";
		}
		if (!topBomLine.hasChildren()) {
			return bean;
		}
		// 如果只获取一层，则只获取第一次进入本函数的这层
		if (!ifFirst && !ifGetAll) {
			return bean;
		}
		
		bean.children = new ArrayList<BomToExcelBean>();
		AIFComponentContext[] children = topBomLine.getChildren();
		for (AIFComponentContext context : children) {
			TCComponentBOMLine bomLine = (TCComponentBOMLine) context.getComponent();
			bean.children.add(GetBomDataRecursive(bomLine, false, ifGetAll, bean.prefix));
		}
		
		// 排序
		Collections.sort(bean.children);
		
		return bean;
	}
	
}
