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
//			MessageBox.post("��ѡĿ��" + selComp.getObjectString() + "����BomLine�޷����в���", "��ʾ", MessageBox.INFORMATION);
//			return;
//		}
//		m_topBomLine = (TCComponentBOMLine)selComp;
//		String fileSuffixString = "";
//		String fileName = m_topBomLine.getProperty("bl_item_item_id") + "-" + m_topBomLine.getProperty("bl_rev_item_revision_id") + "-" + m_topBomLine.getProperty("bl_item_object_name");
//		if (commandID.equals("com.uds.sjec.commands.productionTable")) {
//			m_ifGetAll = true;
//			fileSuffixString = "-���.xlsx";
//		} else if (commandID.equals("com.uds.sjec.commands.productionTablePdf")) {
//			m_ifGetAll = true;
//			fileSuffixString = "-���.pdf";
//		} else if (commandID.equals("com.uds.sjec.commands.productionTableOfLevelOne")) {
//			m_ifGetAll = false;
//			fileSuffixString = "-����.xlsx";
//		} else if (commandID.equals("com.uds.sjec.commands.productionTablePdfOfLevelOne")) {
//			m_ifGetAll = false;
//			fileSuffixString = "-����.pdf";
//		} else {
//			MessageBox.post("�˵�����ID��������ϵ����Ա���", "��ʾ", MessageBox.INFORMATION);
//			return;
//		}
//		// ��������ַ�
//		fileName = StringUtils.GetNameByString(fileName) + fileSuffixString;
//		
//		// ѡ���ļ������Ŀ¼
//        String filePath = "";
//        while (!filePath.equals("")) {
//        	JFileChooser fileChooser  = new JFileChooser();
//            FileSystemView fsv = FileSystemView.getFileSystemView();
//            fileChooser .setCurrentDirectory(fsv.getHomeDirectory());
//            fileChooser .setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
//            int returnVal = fileChooser.showDialog(null, "ѡ��·��");
//            if (returnVal == JFileChooser.APPROVE_OPTION) {
//                filePath= fileChooser.getSelectedFile().getAbsolutePath();
//            }
//            if (filePath.equals("")) {
//            	JOptionPane.showMessageDialog(null, "��ѡ�񱣴��ļ���Ŀ¼", "��ʾ", JOptionPane.INFORMATION_MESSAGE);
//            } else {
//            	File file = new File(filePath + "\\" + fileName);
//            	if (file.exists()) {
//            		JOptionPane.showMessageDialog(null, "Ŀ¼" + filePath + "���Ѵ����ļ�" + fileName, "��ʾ", JOptionPane.INFORMATION_MESSAGE);
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
//					// ��֯����
//			        BomToExcelBean bean = GetBomDataRecursive(m_topBomLine, true, m_ifGetAll);
//					DoExport(bean, m_filePath);
//			        
//			        // ��������
//				} catch (Exception e) {
//					JOptionPane.showMessageDialog(TipsUI.contentPanel, "����" + e.getMessage() + "\n��ϸ��Ϣ����ϵ����Ա�鿴����̨", "��ʾ", JOptionPane.INFORMATION_MESSAGE);
//					e.printStackTrace();
//				}
//			}
//		};
//		TipsUI.ShowUI("������֯��������...", thread, rectangle);
//	}
//	
//	private void DoExport(BomToExcelBean bean, String filePath) throws Exception {
//		GetLicense();
//		
//		Workbook wb = new Workbook(FileFormatType.XLSX);
//		Worksheet workSheet = wb.getWorksheets().get(0);
//		workSheet.setName("��һҳ");
//		Cells cells = workSheet.getCells();
//		// ���ñ�ͷ
//		cells.get(1, 1).setValue("װ���");
//		cells.get(1, 2).setValue("��ʶ");
//		cells.get(1, 3).setValue("������");
//		cells.get(1, 4).setValue("����");
//		cells.get(1, 5).setValue("����");
//		cells.get(1, 6).setValue("����");
//		cells.get(1, 7).setValue("����");
//		cells.get(1, 8).setValue("���");
//		cells.get(1, 9).setValue("��ע");
//		// ��������
//		SetContent(cells, bean, 2);
//		
//		wb.save(filePath);
//	}
//	
//	private void GetLicense() {
//		InputStream license = AsposeExcelUtils.class.getClassLoader().getResourceAsStream("\\license.xml"); // license·��
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
//		bean.assemblyNumber = propertiesValue[0]; // װ���
//		bean.identification = propertiesValue[1]; // ��ʶ
//		bean.code = propertiesValue[2]; // ���ű���
//		bean.name = propertiesValue[3]; // ����
//		bean.material = propertiesValue[4]; // ����
//		String quantity = propertiesValue[5]; // ����
//		bean.quantity = quantity;
//		if (quantity.equals("0") || quantity.equals("")) {
//			bean.quantity = propertiesValue[6]; // ����
//		}
//		bean.note = propertiesValue[7]; // ��ע
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
//		// ����
//		Collections.sort(bean.children);
//		
//		return bean;
//	}
//	
//}
