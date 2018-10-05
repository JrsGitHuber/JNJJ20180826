//package com.uds.sjec.newcontroler;
//
//import java.awt.Rectangle;
//import java.io.File;
//import java.io.InputStream;
//import java.util.ArrayList;
//import java.util.Collections;
//import javax.swing.JFileChooser;
//import javax.swing.JOptionPane;
//import javax.swing.filechooser.FileSystemView;
//
//import com.aspose.cells.Cells;
//import com.aspose.cells.FileFormatType;
//import com.aspose.cells.License;
//import com.aspose.cells.Workbook;
//import com.aspose.cells.Worksheet;
//import com.teamcenter.rac.aif.kernel.AIFComponentContext;
//import com.teamcenter.rac.aif.kernel.InterfaceAIFComponent;
//import com.teamcenter.rac.kernel.TCComponentBOMLine;
//import com.teamcenter.rac.util.MessageBox;
//import com.uds.sjec.bean.BomToExcelBean;
//import com.uds.sjec.utils.StringUtils;
//import com.uds.sjec.utils.TipsUI;
//
//import common.Jr.utils.AsposeExcelUtils;
//
//public class ExportBomToFile {
//	private TCComponentBOMLine m_topBomLine;
//	private boolean m_ifGetAll;
//	private String m_filePath;
//
//	public void DoTask(String commandID, InterfaceAIFComponent selComp, Rectangle rectangle) throws Exception {
//		if (selComp == null || !(selComp instanceof TCComponentBOMLine)) {
//			MessageBox.post("所选目标" + selComp.getObjectString() + "不是BomLine无法进行操作", "提示", MessageBox.INFORMATION);
//			return;
//		}
//		m_topBomLine = (TCComponentBOMLine)selComp;
//		String fileSuffixString = "";
//		String fileName = m_topBomLine.getProperty("bl_item_item_id") + "-" + m_topBomLine.getProperty("bl_rev_item_revision_id") + "-" + m_topBomLine.getProperty("bl_item_object_name");
//		if (commandID.equals("com.uds.sjec.commands.productionTable")) {
//			m_ifGetAll = true;
//			fileSuffixString = "-多层.xlsx";
//		} else if (commandID.equals("com.uds.sjec.commands.productionTablePdf")) {
//			m_ifGetAll = true;
//			fileSuffixString = "-多层.pdf";
//		} else if (commandID.equals("com.uds.sjec.commands.productionTableOfLevelOne")) {
//			m_ifGetAll = false;
//			fileSuffixString = "-单层.xlsx";
//		} else if (commandID.equals("com.uds.sjec.commands.productionTablePdfOfLevelOne")) {
//			m_ifGetAll = false;
//			fileSuffixString = "-单层.pdf";
//		} else {
//			MessageBox.post("菜单命令ID错误，请联系管理员检查", "提示", MessageBox.INFORMATION);
//			return;
//		}
//		// 检查特殊字符
//		fileName = StringUtils.GetNameByString(fileName) + fileSuffixString;
//		
//		// 选择文件保存的目录
//        String filePath = "";
//        while (!filePath.equals("")) {
//        	JFileChooser fileChooser  = new JFileChooser();
//            FileSystemView fsv = FileSystemView.getFileSystemView();
//            fileChooser .setCurrentDirectory(fsv.getHomeDirectory());
//            fileChooser .setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
//            int returnVal = fileChooser.showDialog(null, "选择路径");
//            if (returnVal == JFileChooser.APPROVE_OPTION) {
//                filePath= fileChooser.getSelectedFile().getAbsolutePath();
//            }
//            if (filePath.equals("")) {
//            	JOptionPane.showMessageDialog(null, "请选择保存文件的目录", "提示", JOptionPane.INFORMATION_MESSAGE);
//            } else {
//            	File file = new File(filePath + "\\" + fileName);
//            	if (file.exists()) {
//            		JOptionPane.showMessageDialog(null, "目录" + filePath + "下已存在文件" + fileName, "提示", JOptionPane.INFORMATION_MESSAGE);
//            		filePath = "";
//            	}
//            }
//		}
//        m_filePath = filePath + "\\" + fileName;
//        
//        Thread thread = new Thread()
//		{
//			@Override
//			public void run() {
//				try {
//					// 组织数据
//			        BomToExcelBean bean = GetBomDataRecursive(m_topBomLine, true, m_ifGetAll);
//					DoExport(bean, m_filePath);
//			        
//			        // 导出报表
//				} catch (Exception e) {
//					JOptionPane.showMessageDialog(TipsUI.contentPanel, "出错：" + e.getMessage() + "\n详细信息请联系管理员查看控制台", "提示", JOptionPane.INFORMATION_MESSAGE);
//					e.printStackTrace();
//				}
//			}
//		};
//		TipsUI.ShowUI("正在组织发送申请...", thread, rectangle);
//	}
//	
//	private void DoExport(BomToExcelBean bean, String filePath) throws Exception {
//		GetLicense();
//		
//		Workbook wb = new Workbook(FileFormatType.XLSX);
//		Worksheet workSheet = wb.getWorksheets().get(0);
//		workSheet.setName("第一页");
//		Cells cells = workSheet.getCells();
//		// 设置表头
//		cells.get(1, 1).setValue("装配号");
//		cells.get(1, 2).setValue("标识");
//		cells.get(1, 3).setValue("代码编号");
//		cells.get(1, 4).setValue("名称");
//		cells.get(1, 5).setValue("材料");
//		cells.get(1, 6).setValue("数量");
//		cells.get(1, 7).setValue("长度");
//		cells.get(1, 8).setValue("宽度");
//		cells.get(1, 9).setValue("备注");
//		// 设置内容
//		SetContent(cells, bean, 2);
//		
//		wb.save(filePath);
//	}
//	
//	private void GetLicense() {
//		InputStream license = AsposeExcelUtils.class.getClassLoader().getResourceAsStream("\\license.xml"); // license路径
//		License aposeLic = new License();
//		aposeLic.setLicense(license);
//	}
//	
//	private void SetContent(Cells cells, BomToExcelBean bean, int rowIndex) {
//		cells.get(rowIndex, 1).setValue(bean.assemblyNumber);
//		cells.get(rowIndex, 2).setValue(bean.identification);
//		cells.get(rowIndex, 3).setValue(bean.code);
//		cells.get(rowIndex, 4).setValue(bean.name);
//		cells.get(rowIndex, 5).setValue(bean.material);
//		cells.get(rowIndex, 6).setValue(bean.quantity);
//		cells.get(rowIndex, 9).setValue(bean.note);
//		if (bean.children == null || bean.children.size() == 0) {
//			return;
//		} else {
//			int rowStart = rowIndex + 1;
//			int rowEnd = rowStart + bean.children.size();
//			for (BomToExcelBean tempBean : bean.children) {
//				SetContent(cells, tempBean, rowStart);
//			}
//			cells.groupRows(rowStart, rowEnd);
//		}
//	}
//	
//	private BomToExcelBean GetBomDataRecursive(TCComponentBOMLine topBomLine, boolean ifFirst, boolean ifGetAll) throws Exception {
//		BomToExcelBean bean = new BomToExcelBean();
//		String[] propertiesValue = topBomLine.getProperties(bean.properties);
//		bean.assemblyNumber = propertiesValue[0]; // 装配号
//		bean.identification = propertiesValue[1]; // 标识
//		bean.code = propertiesValue[2]; // 代号编码
//		bean.name = propertiesValue[3]; // 名称
//		bean.material = propertiesValue[4]; // 材料
//		String quantity = propertiesValue[5]; // 数量
//		bean.quantity = quantity;
//		if (quantity.equals("0") || quantity.equals("")) {
//			bean.quantity = propertiesValue[6]; // 数量
//		}
//		bean.note = propertiesValue[7]; // 备注
//		if (!ifFirst) {
//			bean.prefix += "  ";
//		}
//		if (!topBomLine.hasChildren()) {
//			return bean;
//		}
//		if (!ifFirst && !ifGetAll) {
//			return bean;
//		}
//		
//		bean.children = new ArrayList<BomToExcelBean>();
//		AIFComponentContext[] children = topBomLine.getChildren();
//		for (AIFComponentContext context : children) {
//			TCComponentBOMLine bomLine = (TCComponentBOMLine) context.getComponent();
//			bean.children.add(GetBomDataRecursive(bomLine, false, ifGetAll));
//		}
//		
//		// 排序
//		Collections.sort(bean.children);
//		
//		return bean;
//	}
//	
//}
