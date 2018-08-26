package com.uds.sjec.export;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.aspose.cells.Cells;
import com.aspose.cells.FileFormatType;
import com.aspose.cells.License;
import com.aspose.cells.Workbook;
import com.aspose.cells.Worksheet;
import com.teamcenter.rac.aif.kernel.AIFComponentContext;
import com.teamcenter.rac.kernel.TCComponentBOMLine;
import com.uds.sjec.bean.BomToExcelBean;
import common.Jr.utils.AsposeExcelUtils;

public class ExportBomToExcel {
	public String m_errorMessage;
	public List<BomToExcelBean> beanList;
	
	public ExportBomToExcel() {
		m_errorMessage = "";
		beanList = new ArrayList<BomToExcelBean>();
	}
	
	public void DoTask(TCComponentBOMLine topBomLine, boolean ifGetAll, String filePath) {
		try {
			BomToExcelBean bean = GetBomDataRecursive(topBomLine, true, ifGetAll);
			DoExport(bean, filePath);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void DoExport(BomToExcelBean bean, String filePath) throws Exception {
		GetLicense();
		if (!m_errorMessage.equals("")) {
			return;
		}
		
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
		
		wb.save(filePath);
		
	}
	
	private void SetContent(Cells cells, BomToExcelBean bean, int rowIndex) {
		cells.get(rowIndex, 1).setValue(bean.assemblyNumber);
		cells.get(rowIndex, 2).setValue(bean.identification);
		cells.get(rowIndex, 3).setValue(bean.code);
		cells.get(rowIndex, 4).setValue(bean.name);
		cells.get(rowIndex, 5).setValue(bean.material);
		cells.get(rowIndex, 6).setValue(bean.quantity);
		cells.get(rowIndex, 9).setValue(bean.note);
		if (bean.children == null || bean.children.size() == 0) {
			return;
		} else {
			int rowStart = rowIndex + 1;
			int rowEnd = rowStart + bean.children.size();
			for (BomToExcelBean tempBean : bean.children) {
				SetContent(cells, tempBean, rowStart);
			}
			cells.groupRows(rowStart, rowEnd);
		}
	}

	private void GetLicense() {
		m_errorMessage = "";
		
		try {
			InputStream license = AsposeExcelUtils.class.getClassLoader().getResourceAsStream("\\license.xml"); // license路径
			License aposeLic = new License();
			aposeLic.setLicense(license);
		} catch (Exception e) {
			e.printStackTrace();
			m_errorMessage = "GetLicense Error: " + e.getMessage();
		}
	}

	public BomToExcelBean GetBomDataRecursive(TCComponentBOMLine topBomLine, boolean ifFirst, boolean ifGetAll) throws Exception {
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
			bean.prefix += "  ";
		}
		if (!topBomLine.hasChildren()) {
			return bean;
		}
		if (!ifFirst && !ifGetAll) {
			return bean;
		}
		
		bean.children = new ArrayList<BomToExcelBean>();
		AIFComponentContext[] children = topBomLine.getChildren();
		for (AIFComponentContext context : children) {
			TCComponentBOMLine bomLine = (TCComponentBOMLine) context.getComponent();
			bean.children.add(GetBomDataRecursive(bomLine, false, ifGetAll));
		}
		
		// 排序
		Collections.sort(bean.children);
		
		return bean;
	}
}
