package com.uds.sjec.controler;

import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;
import com.teamcenter.rac.aif.kernel.AIFComponentContext;
import com.teamcenter.rac.aif.kernel.InterfaceAIFComponent;
import com.teamcenter.rac.kernel.TCComponentBOMLine;
import com.teamcenter.rac.kernel.TCComponentDataset;
import com.teamcenter.rac.kernel.TCComponentItemRevision;
import com.teamcenter.rac.kernel.TCException;
import com.teamcenter.rac.kernel.TCSession;
import com.teamcenter.rac.util.MessageBox;
import com.uds.sjec.bean.ProductionTableBean;
import com.uds.sjec.service.IBatchExportExcelService;
import com.uds.sjec.service.impl.BatchExportExcelImpl;
import com.uds.sjec.utils.BomUtil;
import com.uds.sjec.view.UDSJProcessBar;

public class BatchExportExcelControler {

	public IBatchExportExcelService batchExportExcelService = new BatchExportExcelImpl();
	String fileName;
	String path = System.getProperty("java.io.tmpdir");
	UDSJProcessBar bar;

	public void userTask(final InterfaceAIFComponent[] selComp, TCSession session) {

		bar = new UDSJProcessBar();
		bar.setLocationRelativeTo(null);
		bar.setVisible(true);
		Boolean isFinished = false;
		int finishedNum = 0;
		TCComponentBOMLine bomLine;
		for (int i = 0; i < selComp.length; i++) {
			List<ProductionTableBean> productonTableList = new ArrayList<ProductionTableBean>();
			if (selComp[i] instanceof TCComponentItemRevision) {
				TCComponentItemRevision itemRevision = (TCComponentItemRevision) selComp[i];
				bomLine = BomUtil.getTopBomLine(itemRevision, "View");
				if (bomLine == null) {
					bomLine = BomUtil.getTopBomLine(itemRevision, "视图");
				}
			} else {
				bomLine = (TCComponentBOMLine) selComp[i];
			}
			try {
				String itemID = bomLine.getProperty("bl_item_item_id");
				String revision = bomLine.getProperty("bl_rev_item_revision_id");
				String objectName = bomLine.getProperty("bl_item_object_name");
				fileName = itemID + "-" + revision + "-" + objectName + ".xlsx";
				// 检查revision下是否存在数据集，存在则提示是否覆盖，否则跳过
				Boolean isExisted = false;
				TCComponentDataset oldDataset = null;
				AIFComponentContext componentContext[] = bomLine.getItemRevision().getRelated("IMAN_specification");
				for (AIFComponentContext context : componentContext) {
					if (context.getComponent() instanceof TCComponentDataset) {
						if (fileName.contains(context.toString())) {
							oldDataset = (TCComponentDataset) context.getComponent();
							isExisted = true;
							break;
						}
					}

				}
				if (!isExisted) {
					productonTableList = batchExportExcelService.getAndSortBOMOfOne(bomLine, bar);
					Boolean reg = batchExportExcelService
							.downloadDatasetToLocal(session, "MSExcelX", "生产明细表", "excel", fileName, path, bar);
					// 如果下载成功，向excel里写入数据
					if (reg && productonTableList.size() > 0) {
						isFinished = batchExportExcelService.writeToExcel(bomLine, productonTableList, path + "\\" + fileName, bar);
						if (isFinished) {
							isFinished = batchExportExcelService.createDataSet(session, path + "\\" + fileName, "MSExcelX", "excel", itemID
									+ "-" + revision + "-" + objectName, bomLine.getItemRevision(), "IMAN_specification", true, bar);
							if (isFinished == true) {
								finishedNum++;
							}
						}
					} else {
						finishedNum++;
					}
				} else {
					// 数据集存在，提示覆盖还是更新
					Object[] possibilities = { "覆盖", "取消" };
					int n = JOptionPane.showOptionDialog(null, fileName + "已经存在，是否覆盖？", "批量导出", JOptionPane.YES_NO_OPTION,
							JOptionPane.QUESTION_MESSAGE, null, possibilities, possibilities[0]);
					if (n == 0) {
						bomLine.getItemRevision().remove("IMAN_specification", oldDataset);
						productonTableList = batchExportExcelService.getAndSortBOMOfOne(bomLine, bar);
						Boolean reg = batchExportExcelService.downloadDatasetToLocal(session, "MSExcelX", "生产明细表", "excel", fileName, path,
								bar);
						// 如果下载成功，向excel里写入数据
						if (reg && productonTableList.size() > 0) {
							isFinished = batchExportExcelService.writeToExcel(bomLine, productonTableList, path + "\\" + fileName, bar);
							if (isFinished) {
								isFinished = batchExportExcelService.createDataSet(session, path + "\\" + fileName, "MSExcelX", "excel",
										itemID + "-" + revision + "-" + objectName, bomLine.getItemRevision(), "IMAN_specification", true,
										bar);
								if (isFinished == true) {
									finishedNum++;
								}
							}
						} else {
							finishedNum++;
						}
					} else {
						finishedNum++;
					}
				}
			} catch (TCException e1) {
				e1.printStackTrace();
				MessageBox.post(e1.getMessage(), "批量导出", MessageBox.ERROR);
				return;
			}
			bar.processBar.setValue((int) ((double) (i + 1) / selComp.length * 100));
		}
		if (finishedNum == selComp.length) {
			bar.setVisible(false);
			MessageBox.post("批量导出完成！", "批量导出", MessageBox.INFORMATION);
		}
	}
}
