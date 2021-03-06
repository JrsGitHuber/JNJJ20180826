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
import com.teamcenter.rac.kernel.TCComponentItemRevision;
import com.teamcenter.rac.kernel.TCException;
import com.teamcenter.rac.kernel.TCSession;
import com.teamcenter.rac.kernel.TCTypeService;
import com.teamcenter.rac.util.MessageBox;
import com.uds.sjec.bean.ProductionTableBean;
import com.uds.sjec.common.CommonFunction;
import com.uds.sjec.service.IBatchExportExcelService;
import com.uds.sjec.view.UDSJProcessBar;

public class BatchExportExcelImpl implements IBatchExportExcelService {

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
			bar.setVisible(false);
			MessageBox.post("数据集<" + datasetName + ">没有找到！", "系统配置错误", MessageBox.ERROR);
			return false;
		}
		try {
			componentDataset = componentDataset.latest();
			String namedRefFileName[] = componentDataset.getFileNames(nameReferences);
			if ((namedRefFileName == null) || (namedRefFileName.length == 0)) {
				bar.setVisible(false);
				MessageBox.post("数据集<" + datasetName + ">没有对应的命名引用!", "系统配置错误", MessageBox.ERROR);
				return false;
			}
			// Delete old file
			tempFileObject = new File(localDirName, namedRefFileName[0]);
			if (tempFileObject.exists())
				tempFileObject.delete();
			fileObject = componentDataset.getFile(nameReferences, namedRefFileName[0], localDirName);
		} catch (TCException e) {
			bar.setVisible(false);
			MessageBox.post(e.getMessage(), "错误", MessageBox.ERROR);
			return false;
		} catch (Exception e) {
			bar.setVisible(false);
			MessageBox.post(e.getMessage(), "错误", MessageBox.ERROR);
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
		int pageNum;
		// 判断页数
		if (productonTableList.size() % 22 == 0 && productonTableList.size() != 0) {
			pageNum = productonTableList.size() / 22 - 1;
		} else {
			pageNum = productonTableList.size() / 22;
		}
		// 复制excel
		try {
			copyReportSheet(localDirectory, "1:27", "28:54", 1, pageNum);
		} catch (Exception e) {
			bar.setVisible(false);
			e.printStackTrace();
			MessageBox.post(e.getMessage(), "批量导出", MessageBox.ERROR);
			return false;
		}
		try {
			FileInputStream fileInputStream = new FileInputStream(localDirectory);
			InputStream inputStream = new BufferedInputStream(fileInputStream);
			XSSFWorkbook wookbook = new XSSFWorkbook(inputStream);
			XSSFSheet sheet = wookbook.getSheetAt(0);
			// 写固定信息
			String productionRevision = topBomLine.getProperty("bl_rev_item_revision_id");// 版本
			String productionName = topBomLine.getProperty("bl_item_object_name"); // 产品名称
			String productionId = topBomLine.getProperty("bl_item_item_id"); // 产品编码
			String productionWeight = topBomLine.getProperty("bl_item_s2_Asm_Weight"); // 重量
			String productionNote = topBomLine.getProperty("bl_item_s2_Note"); // 备注
			String productionReplace = topBomLine.getProperty("bl_item_s2_Original_Code"); // 替代
			String productionInfomation = topBomLine.getProperty("bl_item_s2_ECN_No"); // 通知单
			for (int j = 0; j < pageNum + 1; j++) {
				FillCell(sheet, 23 + j * 27, 6, productionInfomation);
				FillCell(sheet, 23 + j * 27, 10, productionNote);
				FillCell(sheet, 24 + j * 27, 6, productionRevision);
				FillCell(sheet, 24 + j * 27, 7, productionName);
				FillCell(sheet, 24 + j * 27, 9, productionId);
				FillCell(sheet, 25 + j * 27, 6, productionWeight);
				FillCell(sheet, 26 + j * 27, 6, productionReplace);
				FillCell(sheet, 26 + j * 27, 7, " 第 " + (j + 1 + "") + " 页，" + "共 " + (pageNum + 1) + " 页");
			}
			// 写BOMLine上的信息
			for (int i = 0; i < productonTableList.size(); i++) {
				FillCell(sheet, 1 + i + (i / 22) * 5, 1, productonTableList.get(i).assemblyNumber); // 装配号
				FillCell(sheet, 1 + i + (i / 22) * 5, 2, productonTableList.get(i).identification); // 标识
				FillCell(sheet, 1 + i + (i / 22) * 5, 3, productonTableList.get(i).code); // 代号编码
				FillCell(sheet, 1 + i + (i / 22) * 5, 4, productonTableList.get(i).name); // 名称
				FillCell(sheet, 1 + i + (i / 22) * 5, 6, productonTableList.get(i).material); // 材料
				FillCell(sheet, 1 + i + (i / 22) * 5, 7, productonTableList.get(i).quantity); // 数量
				String lengthStr = productonTableList.get(i).length;
				lengthStr = CommonFunction.RemoveEndZero(lengthStr);
				FillCell(sheet, 1 + i + (i / 22) * 5, 8, lengthStr); // 长度
				String widthStr = productonTableList.get(i).width;
				widthStr = CommonFunction.RemoveEndZero(widthStr);
				FillCell(sheet, 1 + i + (i / 22) * 5, 9, widthStr); // 宽度
				FillCell(sheet, 1 + i + (i / 22) * 5, 10, productonTableList.get(i).note); // 备注
			}
			// 关闭流
			FileOutputStream fileOutputStream = new FileOutputStream(localDirectory);
			wookbook.write(fileOutputStream);
			fileInputStream.close();
			fileOutputStream.close();
		} catch (Exception e) {
			bar.setVisible(false);
			e.printStackTrace();
			MessageBox.post(e.getMessage(), "批量导出", MessageBox.ERROR);
			return false;
		}
		return true;
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

	/**
	 * 填写单元格值
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
	 * 返回Excel指定单元格
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

	@Override
	public List<ProductionTableBean> getAndSortBOMOfOne(TCComponentBOMLine topBomLine, UDSJProcessBar bar) {
		List<ProductionTableBean> tempList = new ArrayList<ProductionTableBean>(); // 存放当前层的List
		List<ProductionTableBean> productonTableList = new ArrayList<ProductionTableBean>();
		List<TCComponentBOMLine> sortedBomLines = new ArrayList<TCComponentBOMLine>();
		try {
			AIFComponentContext[] children = topBomLine.getChildren();
			for (AIFComponentContext context : children) {
				TCComponentBOMLine bomLine = (TCComponentBOMLine) context.getComponent();
				ProductionTableBean bean = new ProductionTableBean();
				try {
					String[] propertyNames = new String[] {
							"S2_bl_asmno",
							"bl_rev_s2_Rev_Manu_Type",
							"bl_item_item_id",
							"bl_item_object_name",
							"bl_rev_s2_Rev_Matl", "bl_item_s2_Spec",
							"Usage_Quantity",
							"S2_bl_ylgsl",
							"bl_item_s2_Note",
							"bl_rev_s2_Rev_Weight",
							"bl_item_s2_Asm_Weight" };
					String[] productionInfo = bomLine.getProperties(propertyNames);
					bean.assemblyNumber = productionInfo[0];// 装配号
					bean.identification = productionInfo[1];// 标识
					bean.code = productionInfo[2];// 代号编码
					bean.name = productionInfo[3];// 名称
					String material = "";
					if (productionInfo[4].equals(productionInfo[5])) {
						material = productionInfo[4];
					} else {
						material = productionInfo[4] + " " + productionInfo[5];
					}
					bean.material = material;// 材料
					String quantity = productionInfo[6];// 数量
					bean.quantity = quantity;
					if (quantity.equals("0") || quantity.equals("")) {
						bean.quantity = productionInfo[7];// 数量
					}
					bean.note = productionInfo[8];// 备注
					bean.length = productionInfo[9];// 长度
					bean.width = productionInfo[10];// 宽度
					bean.bomLine = bomLine;
				} catch (TCException e) {
					bar.setVisible(false);
					e.printStackTrace();
					MessageBox.post(e.getMessage(), "批量导出", MessageBox.ERROR);
					return null;
				}
				tempList.add(bean);
			}
			// 根据装配号按升序排序
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
					String[] propertyNames = new String[] {
							"S2_bl_asmno",
							"bl_rev_s2_Rev_Manu_Type",
							"bl_item_item_id",
							"bl_item_object_name",
							"bl_rev_s2_Rev_Matl", "bl_item_s2_Spec",
							"Usage_Quantity",
							"S2_bl_ylgsl",
							"bl_item_s2_Note",
							"bl_rev_s2_Rev_Weight",
							"bl_item_s2_Asm_Weight" };
					String[] productionInfo = tempBomLine.getProperties(propertyNames);
					bean.assemblyNumber = productionInfo[0];// 装配号
					bean.identification = productionInfo[1];// 标识
					bean.code = productionInfo[2];// 代号编码
					bean.name = productionInfo[3];// 名称
					String material = "";
					if (productionInfo[4].equals(productionInfo[5])) {
						material = productionInfo[4];
					} else {
						material = productionInfo[4] + " " + productionInfo[5];
					}
					bean.material = material;// 材料
					String quantity = productionInfo[6];// 数量
					bean.quantity = quantity;
					if (quantity.equals("0") || quantity.equals("")) {
						bean.quantity = productionInfo[7];// 数量
					}
					bean.note = productionInfo[8];// 备注
					bean.length = productionInfo[9];// 长度
					bean.width = productionInfo[10];// 宽度
					bean.bomLine = tempBomLine;
					productonTableList.add(bean);
				} catch (TCException e) {
					bar.setVisible(false);
					e.printStackTrace();
					MessageBox.post(e.getMessage(), "批量导出", MessageBox.ERROR);
					return null;
				}
			}
		} catch (TCException e) {
			bar.setVisible(false);
			e.printStackTrace();
			MessageBox.post(e.getMessage(), "批量导出", MessageBox.ERROR);
			return null;
		}
		return productonTableList;
	}

	private TCComponentDataset findDatasetByName(TCSession session, String datasetType, String datasetName) {
		try {
			if (datasetName != null) {
				TCTypeService typeService = session.getTypeService();
				TCComponentDatasetType imanDatasetType = (TCComponentDatasetType) typeService.getTypeComponent(datasetType);
				TCComponentDataset imanComponentAllDataset[] = imanDatasetType.findAll(datasetName);
				for (int i = 0; i < imanComponentAllDataset.length; i++) {
					if (imanComponentAllDataset[i].getType().equals(datasetType)) {
						if (imanComponentAllDataset[i].getProperty("owning_user").contains("infodba")
								&& (imanComponentAllDataset[i].getProperty("object_string").equals(datasetName))) {
							return imanComponentAllDataset[i];
						}
					}
				}
			}
			return null;
		} catch (TCException e) {
			System.out.print("数据集类型<" + datasetType + ">不存在!");
			return null;
		}
	}

	/**
	 * 创建数据集并挂在配置单下
	 */
	@Override
	public Boolean createDataSet(TCSession session, String localFile, String dataSetType, String datasetNamedRef, String dataSetName,
			TCComponentItemRevision itemRevision, String relationType, boolean replaceAlert, UDSJProcessBar bar) {
		try {
			String filePathNames[] = { localFile };
			String namedRefs[] = { datasetNamedRef };
			TCTypeService typeService = session.getTypeService();
			TCComponentDatasetType TCDatasetType = (TCComponentDatasetType) typeService.getTypeComponent(dataSetType);
			TCComponentDataset newDataset = TCDatasetType.setFiles(dataSetName, "Created by program.", dataSetType, filePathNames,
					namedRefs);
			itemRevision.add(relationType, newDataset);
		} catch (TCException e) {
			bar.setVisible(false);
			e.printStackTrace();
			MessageBox.post(e.getMessage(), "批量导出", MessageBox.INFORMATION);
			return false;
		}
		return true;
	}
}
