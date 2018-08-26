package com.uds.sjec.service.impl;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import com.teamcenter.rac.aif.kernel.AIFComponentContext;
import com.teamcenter.rac.kernel.TCComponentBOMLine;
import com.teamcenter.rac.kernel.TCComponentDataset;
import com.teamcenter.rac.kernel.TCComponentDatasetType;
import com.teamcenter.rac.kernel.TCException;
import com.teamcenter.rac.kernel.TCSession;
import com.teamcenter.rac.kernel.TCTypeService;
import com.teamcenter.rac.util.MessageBox;
import com.uds.sjec.bean.ProductionTableBean;
import com.uds.sjec.service.IProductionTableService;
import com.uds.sjec.view.UDSJProcessBar;

public class ProductionTableImpl implements IProductionTableService {

	int pageNum; // ҳ��
	List<ProductionTableBean> productonTableList = new ArrayList<ProductionTableBean>();
	List<ProductionTableBean> tempList = new ArrayList<ProductionTableBean>(); // ��ŵ�ǰ���List

	@Override
	public List<ProductionTableBean> getAndSortAllBOM(TCComponentBOMLine topBomLine, UDSJProcessBar bar) {

		List<TCComponentBOMLine> sortedBomLines = new ArrayList<TCComponentBOMLine>();
		try {
			AIFComponentContext[] children = topBomLine.getChildren();
			for (AIFComponentContext context : children) {
				TCComponentBOMLine bomLine = (TCComponentBOMLine) context.getComponent();
				ProductionTableBean bean = new ProductionTableBean();
				try {
					String[] productionInfo = bomLine.getProperties(new String[] { "S2_bl_asmno", "bl_rev_s2_Rev_Manu_Type",
							"bl_item_item_id", "bl_item_object_name", "bl_rev_s2_Rev_Matl", "Usage_Quantity", "S2_bl_ylgsl",
							"bl_item_s2_Note" });
					bean.assemblyNumber = productionInfo[0];// װ���
					bean.identification = productionInfo[1];// ��ʶ
					bean.code = productionInfo[2];// ���ű���
					bean.name = productionInfo[3];// ����
					bean.material = productionInfo[4];// ����
					String quantity = productionInfo[5];// ����
					bean.quantity = quantity;
					if (quantity.equals("0") || quantity.equals("")) {
						bean.quantity = productionInfo[6];// ����
					}
					bean.note = productionInfo[7];// ��ע
					bean.bomLine = bomLine;
				} catch (TCException e) {
					e.printStackTrace();
					bar.setVisible(false);
					MessageBox.post(e.getMessage(), "������ϸ��", MessageBox.ERROR);
					return null;
				}
				tempList.add(bean);
			}
			// ����װ��Ű���������
			Collections.sort(tempList, new Comparator<ProductionTableBean>() {
				public int compare(ProductionTableBean o1, ProductionTableBean o2) {
					if (Integer.parseInt(o1.assemblyNumber) > Integer.parseInt(o2.assemblyNumber)) {
						return 1;
					}
					return -1;
				}
			});
			for (ProductionTableBean childrenBean : tempList) {
				sortedBomLines.add(childrenBean.bomLine);
			}
			for (TCComponentBOMLine tempBomLine : sortedBomLines) {
				try {
					ProductionTableBean bean = new ProductionTableBean();
					String[] productionInfo = tempBomLine.getProperties(new String[] { "S2_bl_asmno", "bl_rev_s2_Rev_Manu_Type",
							"bl_item_item_id", "bl_item_object_name", "bl_rev_s2_Rev_Matl", "Usage_Quantity", "S2_bl_ylgsl",
							"bl_item_s2_Note" });
					bean.assemblyNumber = productionInfo[0];// װ���
					bean.identification = productionInfo[1];// ��ʶ
					bean.code = productionInfo[2];// ���ű���
					bean.name = productionInfo[3];// ����
					bean.material = productionInfo[4];// ����
					String quantity = productionInfo[5];// ����
					bean.quantity = quantity;
					if (quantity.equals("0") || quantity.equals("")) {
						bean.quantity = productionInfo[6];// ����
					}
					bean.note = productionInfo[7];// ��ע
					bean.bomLine = tempBomLine;
					productonTableList.add(bean);
					transfer(tempBomLine, bar);
				} catch (TCException e) {
					e.printStackTrace();
					bar.setVisible(false);
					MessageBox.post(e.getMessage(), "������ϸ��", MessageBox.ERROR);
					return null;
				}
			}
		} catch (TCException e) {
			e.printStackTrace();
			bar.setVisible(false);
			MessageBox.post(e.getMessage(), "������ϸ��", MessageBox.ERROR);
			return null;
		}
		return productonTableList;
	}

	private void transfer(TCComponentBOMLine tempBomLine, UDSJProcessBar bar) {
		List<TCComponentBOMLine> sortedBomLines = new ArrayList<TCComponentBOMLine>();
		tempList.clear();
		try {
			AIFComponentContext[] children = tempBomLine.getChildren();
			for (AIFComponentContext context : children) {
				TCComponentBOMLine bomLine = (TCComponentBOMLine) context.getComponent();
				ProductionTableBean bean = new ProductionTableBean();
				try {
					String[] productionInfo = bomLine.getProperties(new String[] { "S2_bl_asmno", "bl_rev_s2_Rev_Manu_Type",
							"bl_item_item_id", "bl_item_object_name", "bl_rev_s2_Rev_Matl", "Usage_Quantity", "S2_bl_ylgsl",
							"bl_item_s2_Note" });
					bean.assemblyNumber = productionInfo[0];// װ���
					bean.identification = productionInfo[1];// ��ʶ
					bean.code = productionInfo[2];// ���ű���
					bean.name = productionInfo[3];// ����
					bean.material = productionInfo[4];// ����
					String quantity = productionInfo[5];// ����
					bean.quantity = quantity;
					if (quantity.equals("0") || quantity.equals("")) {
						bean.quantity = productionInfo[6];// ����
					}
					bean.note = productionInfo[7];// ��ע
					bean.bomLine = bomLine;
				} catch (TCException e) {
					e.printStackTrace();
					bar.setVisible(false);
					MessageBox.post(e.getMessage(), "������ϸ��", MessageBox.ERROR);
					return;
				}
				tempList.add(bean);
			}
			// ����װ��Ű���������
			Collections.sort(tempList, new Comparator<ProductionTableBean>() {
				public int compare(ProductionTableBean o1, ProductionTableBean o2) {
					if (Integer.parseInt(o1.assemblyNumber) > Integer.parseInt(o2.assemblyNumber)) {
						return 1;
					}
					return -1;
				}
			});
			for (ProductionTableBean childrenBean : tempList) {
				sortedBomLines.add(childrenBean.bomLine);
			}
			for (TCComponentBOMLine childrenBomLine : sortedBomLines) {
				try {
					ProductionTableBean bean = new ProductionTableBean();

					String[] productionInfo = childrenBomLine.getProperties(new String[] { "S2_bl_asmno", "bl_rev_s2_Rev_Manu_Type",
							"bl_item_item_id", "bl_item_object_name", "bl_rev_s2_Rev_Matl", "Usage_Quantity", "S2_bl_ylgsl",
							"bl_item_s2_Note" });
					bean.assemblyNumber = productionInfo[0];// װ���
					bean.identification = productionInfo[1];// ��ʶ
					bean.code = productionInfo[2];// ���ű���
					bean.name = productionInfo[3];// ����
					bean.material = productionInfo[4];// ����
					String quantity = productionInfo[5];// ����
					bean.quantity = quantity;
					if (quantity.equals("0") || quantity.equals("")) {
						bean.quantity = productionInfo[6];// ����
					}
					bean.note = productionInfo[7];// ��ע
					bean.bomLine = childrenBomLine;
					productonTableList.add(bean);
					transfer(childrenBomLine, bar);
				} catch (TCException e) {
					e.printStackTrace();
					bar.setVisible(false);
					MessageBox.post(e.getMessage(), "������ϸ��", MessageBox.ERROR);
					return;
				}
			}
		} catch (TCException e) {
			e.printStackTrace();
			bar.setVisible(false);
			MessageBox.post(e.getMessage(), "������ϸ��", MessageBox.ERROR);
			return;
		}
	}

	@Override
	public List<ProductionTableBean> getAndSortBOMOfOne(TCComponentBOMLine topBomLine, UDSJProcessBar bar) {
		List<ProductionTableBean> productonTableList = new ArrayList<ProductionTableBean>();
		List<TCComponentBOMLine> sortedBomLines = new ArrayList<TCComponentBOMLine>();
		try {
			AIFComponentContext[] children = topBomLine.getChildren();
			for (AIFComponentContext context : children) {
				TCComponentBOMLine bomLine = (TCComponentBOMLine) context.getComponent();
				ProductionTableBean bean = new ProductionTableBean();
				try {
					String[] productionInfo = bomLine.getProperties(new String[] { "S2_bl_asmno", "bl_rev_s2_Rev_Manu_Type",
							"bl_item_item_id", "bl_item_object_name", "bl_rev_s2_Rev_Matl", "Usage_Quantity", "S2_bl_ylgsl",
							"bl_item_s2_Note" });
					bean.assemblyNumber = productionInfo[0];// װ���
					bean.identification = productionInfo[1];// ��ʶ
					bean.code = productionInfo[2];// ���ű���
					bean.name = productionInfo[3];// ����
					bean.material = productionInfo[4];// ����
					String quantity = productionInfo[5];// ����
					bean.quantity = quantity;
					if (quantity.equals("0") || quantity.equals("")) {
						bean.quantity = productionInfo[6];// ����
					}
					bean.note = productionInfo[7];// ��ע
					bean.bomLine = bomLine;
				} catch (TCException e) {
					e.printStackTrace();
					bar.setVisible(false);
					MessageBox.post(e.getMessage(), "������ϸ��", MessageBox.ERROR);
					return null;
				}
				tempList.add(bean);
			}
			// ����װ��Ű���������
			Collections.sort(tempList, new Comparator<ProductionTableBean>() {
				public int compare(ProductionTableBean o1, ProductionTableBean o2) {
					if (Integer.parseInt(o1.assemblyNumber) > Integer.parseInt(o2.assemblyNumber)) {
						return 1;
					}
					return -1;
				}
			});
			for (ProductionTableBean childrenBean : tempList) {
				sortedBomLines.add(childrenBean.bomLine);
			}
			for (TCComponentBOMLine tempBomLine : sortedBomLines) {
				try {
					ProductionTableBean bean = new ProductionTableBean();
					String[] productionInfo = tempBomLine.getProperties(new String[] { "S2_bl_asmno", "bl_rev_s2_Rev_Manu_Type",
							"bl_item_item_id", "bl_item_object_name", "bl_rev_s2_Rev_Matl", "Usage_Quantity", "S2_bl_ylgsl",
							"bl_item_s2_Note" });
					bean.assemblyNumber = productionInfo[0];// װ���
					bean.identification = productionInfo[1];// ��ʶ
					bean.code = productionInfo[2];// ���ű���
					bean.name = productionInfo[3];// ����
					bean.material = productionInfo[4];// ����
					String quantity = productionInfo[5];// ����
					bean.quantity = quantity;
					if (quantity.equals("0") || quantity.equals("")) {
						bean.quantity = productionInfo[6];// ����
					}
					bean.note = productionInfo[7];// ��ע
					bean.bomLine = tempBomLine;
					productonTableList.add(bean);
				} catch (TCException e) {
					e.printStackTrace();
					bar.setVisible(false);
					MessageBox.post(e.getMessage(), "������ϸ��", MessageBox.ERROR);
					return null;
				}
			}
		} catch (TCException e) {
			e.printStackTrace();
			bar.setVisible(false);
			MessageBox.post(e.getMessage(), "������ϸ��", MessageBox.ERROR);
			return null;
		}
		return productonTableList;
	}

	@Override
	public Boolean downloadDatasetToLocal(TCSession session, String datasetType, String datasetName, String nameReferences,
			String localFileName, String localDirName, UDSJProcessBar bar) {
		File fileObject = null;
		File tempFileObject;
		File dirObject = new File(localDirName);
		if (!dirObject.exists())
			dirObject.mkdirs();
		TCComponentDataset componentDataset = findDatasetByName(session, datasetType, datasetName);
		if (componentDataset == null) {
			MessageBox.post("���ݼ�<" + datasetName + ">û���ҵ���", "ϵͳ���ô���", MessageBox.ERROR);
			return false;
		}
		try {
			componentDataset = componentDataset.latest();
			String namedRefFileName[] = componentDataset.getFileNames(nameReferences);
			if ((namedRefFileName == null) || (namedRefFileName.length == 0)) {
				MessageBox.post("���ݼ�<" + datasetName + ">û�ж�Ӧ����������!", "ϵͳ���ô���", MessageBox.ERROR);
				return false;
			}
			// Delete old file
			tempFileObject = new File(localDirName, namedRefFileName[0]);
			if (tempFileObject.exists())
				tempFileObject.delete();
			fileObject = componentDataset.getFile(nameReferences, namedRefFileName[0], localDirName);
		} catch (TCException e) {
			e.printStackTrace();
			bar.setVisible(false);
			MessageBox.post(e.getMessage(), "������ϸ��", MessageBox.ERROR);
			return false;
		} catch (Exception e) {
			e.printStackTrace();
			bar.setVisible(false);
			MessageBox.post(e.getMessage(), "������ϸ��", MessageBox.ERROR);
			return false;
		}
		// Delete old file
		tempFileObject = new File(localDirName, localFileName);
		if (!fileObject.getAbsolutePath().toUpperCase().equals(tempFileObject.getAbsolutePath().toUpperCase())) {
			if (tempFileObject.exists())
				tempFileObject.delete();
			fileObject.renameTo(tempFileObject);
		}
		return true;
	}

	@Override
	public Boolean writeToExcel(TCComponentBOMLine topBomLine, List<ProductionTableBean> productonTableList, String localDirectory,
			UDSJProcessBar bar) {
		// �ж�ҳ��
		if (productonTableList.size() % 22 == 0 && productonTableList.size() != 0) {
			pageNum = productonTableList.size() / 22 - 1;
		} else {
			pageNum = productonTableList.size() / 22;
		}
		// ����excel
		try {
			copyReportSheet(localDirectory, "1:27", "28:54", 1, pageNum);
		} catch (Exception e) {
			e.printStackTrace();
			bar.setVisible(false);
			MessageBox.post(e.getMessage(), "������ϸ��", MessageBox.ERROR);
			return false;
		}
		try {
			FileInputStream fileInputStream = new FileInputStream(localDirectory);
			InputStream inputStream = new BufferedInputStream(fileInputStream);
			XSSFWorkbook wookbook = new XSSFWorkbook(inputStream);
			XSSFSheet sheet = wookbook.getSheetAt(0);
			// д�̶���Ϣ
			String productionRevision = topBomLine.getProperty("bl_rev_item_revision_id");// �汾
			String productionName = topBomLine.getProperty("bl_item_object_name"); // ��Ʒ����
			String productionId = topBomLine.getProperty("bl_item_item_id"); // ��Ʒ����
			String productionWeight = topBomLine.getProperty("bl_item_s2_Asm_Weight"); // ����
			String productionNote = topBomLine.getProperty("bl_item_s2_Note"); // ��ע
			String productionReplace = topBomLine.getProperty("bl_item_s2_Original_Code"); // ���
			String productionInfomation = topBomLine.getProperty("bl_item_s2_ECN_No"); // ֪ͨ��
			for (int j = 0; j < pageNum + 1; j++) {
				FillCell(sheet, 23 + j * 27, 6, productionInfomation);
				FillCell(sheet, 23 + j * 27, 10, productionNote);
				FillCell(sheet, 24 + j * 27, 6, productionRevision);
				FillCell(sheet, 24 + j * 27, 7, productionName);
				FillCell(sheet, 24 + j * 27, 9, productionId);
				FillCell(sheet, 25 + j * 27, 6, productionWeight);
				FillCell(sheet, 26 + j * 27, 6, productionReplace);
				FillCell(sheet, 26 + j * 27, 7, " �� " + (j + 1 + "") + " ҳ��" + "�� " + (pageNum + 1) + " ҳ");
			}
			// дBOMLine�ϵ���Ϣ
			for (int i = 0; i < productonTableList.size(); i++) {
				FillCell(sheet, 1 + i + (i / 22) * 5, 1, productonTableList.get(i).assemblyNumber); // װ���
				FillCell(sheet, 1 + i + (i / 22) * 5, 2, productonTableList.get(i).identification); // ��ʶ
				FillCell(sheet, 1 + i + (i / 22) * 5, 3, productonTableList.get(i).code); // ���ű���
				FillCell(sheet, 1 + i + (i / 22) * 5, 4, productonTableList.get(i).name); // ����
				FillCell(sheet, 1 + i + (i / 22) * 5, 6, productonTableList.get(i).material); // ����
				FillCell(sheet, 1 + i + (i / 22) * 5, 7, productonTableList.get(i).quantity); // ����
				FillCell(sheet, 1 + i + (i / 22) * 5, 10, productonTableList.get(i).note); // ��ע
			}
			// �ر���
			FileOutputStream fileOutputStream = new FileOutputStream(localDirectory);
			wookbook.write(fileOutputStream);
			fileInputStream.close();
			fileOutputStream.close();
		} catch (Exception e) {
			e.printStackTrace();
			bar.setVisible(false);
			MessageBox.post(e.getMessage(), "������ϸ��", MessageBox.ERROR);
			return false;
		}
		return true;
	}

	/**
	 * ��д��Ԫ��ֵ
	 * 
	 * @param sheet
	 * @param indexRow
	 * @param indexCell
	 * @param str
	 */
	public void FillCell(Sheet sheet, int indexRow, int indexCell, String str) {
		try {
			Cell cell = getCell(sheet, indexRow, indexCell);
			if (str.compareTo("") == 0) {
				return;
			}
			if (cell != null) {
				cell.setCellValue(str);
			}
		} catch (Exception e) {
			return;
		}
	}

	/**
	 * ����Excelָ����Ԫ��
	 * 
	 * @param sheet
	 * @param indexRow
	 * @param indexCell
	 * @return
	 */
	public Cell getCell(Sheet sheet, int indexRow, int indexCell) {
		Row row = getRow(sheet, indexRow);
		Cell cell = row.getCell(indexCell);
		if (cell == null) {
			cell = row.createCell(indexCell);
		}
		return cell;
	}

	public Row getRow(Sheet sheet, int indexRow) {
		Row row = sheet.getRow(indexRow);
		if (row == null) {
			row = sheet.createRow(indexRow);
		}
		return row;
	}

	public static void copyReportSheet(String excelDir, String copyRange, String insertRow, int sheetNum, int num) throws Exception {
		String tempDir = System.getProperty("java.io.tmpdir");
		StringBuffer vbsBuff = new StringBuffer();
		vbsBuff.append("Dim objXL, Nowpage,mySheet\n");
		vbsBuff.append("Set objXL = WScript.CreateObject(\"Excel.Application\")\n");
		vbsBuff.append("objXL.DisplayAlerts = False \n");
		vbsBuff.append("objXL.Visible = FALSE\n");
		vbsBuff.append("Set eb = objXL.Workbooks.Open(\"" + excelDir + "\")\n");
		vbsBuff.append("Set mySheet=eb.Sheets.Item(" + sheetNum + ") \n");

		vbsBuff.append("For Nowpage=1 to " + num + "  \n");
		vbsBuff.append("mySheet.Rows(\"" + copyRange + "\").Copy \n");
		vbsBuff.append(" mySheet.Rows(\"" + insertRow + "\").Insert \n");
		vbsBuff.append(" Next \n");

		vbsBuff.append("eb.Save\n");
		vbsBuff.append("eb.Close\n");
		vbsBuff.append("objXL.ScreenUpdating = True").append("\n");
		vbsBuff.append("objXL.DisplayAlerts = False\n");
		vbsBuff.append("objXL.Quit\n");
		vbsBuff.append("set objXL = nothing\n");

		File file = new File(tempDir, "copy_sheet.vbs");
		if (file.exists())
			file.delete();
		file.createNewFile();
		FileWriter fw = new FileWriter(file);
		fw.write(vbsBuff.toString());
		fw.flush();
		fw.close();
		String[] cmds = new String[] { "Wscript.exe", file.getCanonicalPath() };
		Runtime runt = Runtime.getRuntime();
		Process process = runt.exec(cmds);
		process.waitFor();
	}

	private TCComponentDataset findDatasetByName(TCSession session, String datasetType, String datasetName) {
		try {
			if (datasetName != null) {
				TCTypeService typeService = session.getTypeService();
				TCComponentDatasetType imanDatasetType = (TCComponentDatasetType) typeService.getTypeComponent(datasetType);
				TCComponentDataset imanComponentAllDataset[] = imanDatasetType.findAll(datasetName);
				for (int i = 0; i < imanComponentAllDataset.length; i++) {
					if (imanComponentAllDataset[i].getType().equals(datasetType)) {
						System.out.println("���ݼ������ߣ�" + imanComponentAllDataset[i].getProperty("owning_user"));
						if (imanComponentAllDataset[i].getProperty("owning_user").contains("infodba")
								&& (imanComponentAllDataset[i].getProperty("object_string").equals(datasetName))) {
							return imanComponentAllDataset[i];
						}
					}
				}
			}
			return null;
		} catch (TCException e) {
			e.printStackTrace();
			MessageBox.post(e.getMessage(), "������ϸ��", MessageBox.ERROR);
			return null;
		}
	}
}
