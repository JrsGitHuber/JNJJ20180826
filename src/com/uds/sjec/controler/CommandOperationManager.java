package com.uds.sjec.controler;

import com.teamcenter.rac.aif.kernel.InterfaceAIFComponent;
import com.teamcenter.rac.aifrcp.AIFUtility;
import com.teamcenter.rac.kernel.TCComponentBOMLine;
import com.teamcenter.rac.kernel.TCComponentItemRevision;
import com.teamcenter.rac.kernel.TCSession;
import com.teamcenter.rac.util.MessageBox;
import com.uds.sjec.base.BaseModuleOperation;
import com.uds.sjec.common.CommonFunction;
import com.uds.sjec.newcontroler.ExportBomsToFiles;

public class CommandOperationManager extends BaseModuleOperation {
	public String m_commandId = "";
	public java.awt.Rectangle m_rectangle;

	@Override
	protected void DoUserTask() {
		String dlgTitle = "";
		String failure = "出错";
		try {
//			if ("com.uds.sjec.commands.productionTable".equals(m_commandId)) {// 整体BOM生成excel
//				InterfaceAIFComponent selComp = this.GetSelectedComponent();
//				boolean isSelectedOk = false;
//				if (selComp != null && selComp instanceof TCComponentBOMLine) {
//					isSelectedOk = true;
//					TCComponentBOMLine bomLine = (TCComponentBOMLine) selComp;
//					ProductionTableControler productionTableControler = new ProductionTableControler();
//					productionTableControler.userTask(bomLine, m_commandId);
//				}
//				if (!isSelectedOk) {
//					com.teamcenter.rac.util.MessageBox.post("所选对象类型错误", "生成明细表", com.teamcenter.rac.util.MessageBox.ERROR);
//				}
//			}
//			if ("com.uds.sjec.commands.productionTablePdf".equals(m_commandId)) {// 整体BOM生成PDF
//				InterfaceAIFComponent selComp = this.GetSelectedComponent();
//				boolean isSelectedOk = false;
//				if (selComp != null && selComp instanceof TCComponentBOMLine) {
//					isSelectedOk = true;
//					TCComponentBOMLine bomLine = (TCComponentBOMLine) selComp;
//					ProductionTableControler productionTableControler = new ProductionTableControler();
//					productionTableControler.userTask(bomLine, m_commandId);
//				}
//				if (!isSelectedOk) {
//					com.teamcenter.rac.util.MessageBox.post("请在结构管理器中操作。", "生成明细表", com.teamcenter.rac.util.MessageBox.ERROR);
//				}
//			}
			if ("com.uds.sjec.commands.productionTable".equals(m_commandId) || "com.uds.sjec.commands.productionTablePdf".equals(m_commandId)) {
				InterfaceAIFComponent[] selComps = this.GetSelectedComponents();
				ExportBomsToFiles process = new ExportBomsToFiles(m_rectangle);
				process.DoTask(m_commandId, selComps, false);
			}
			if ("com.uds.sjec.commands.productionTableOfLevelOne".equals(m_commandId)) {// 一级BOM生成Excel
				InterfaceAIFComponent selComp = this.GetSelectedComponent();
				boolean isSelectedOk = false;
				if (selComp != null && selComp instanceof TCComponentBOMLine) {
					isSelectedOk = true;
					TCComponentBOMLine bomLine = (TCComponentBOMLine) selComp;
					ProductionTableControler productionTableControler = new ProductionTableControler();
					productionTableControler.userTask(m_rectangle, bomLine, m_commandId);
				}
				if (!isSelectedOk) {
					com.teamcenter.rac.util.MessageBox.post("请在结构管理器中操作。", "生成明细表", com.teamcenter.rac.util.MessageBox.ERROR);
				}
			}
			if ("com.uds.sjec.commands.productionTablePdfOfLevelOne".equals(m_commandId)) {// 一级BOM生成PDF
				InterfaceAIFComponent selComp = this.GetSelectedComponent();
				boolean isSelectedOk = false;
				if (selComp != null && selComp instanceof TCComponentBOMLine) {
					isSelectedOk = true;
					TCComponentBOMLine bomLine = (TCComponentBOMLine) selComp;
					ProductionTableControler productionTableControler = new ProductionTableControler();
					productionTableControler.userTask(m_rectangle, bomLine, m_commandId);
				}
				if (!isSelectedOk) {
					com.teamcenter.rac.util.MessageBox.post("请在结构管理器中操作。", "生成明细表", com.teamcenter.rac.util.MessageBox.ERROR);
				}
			}
			if ("com.uds.sjec.commands.batchExportExcel".equals(m_commandId)) {// 生成明细表
				TCSession session = (TCSession) AIFUtility.getActiveDesktop().getCurrentApplication().getSession();
				InterfaceAIFComponent[] selComp = this.GetSelectedComponents();
				boolean isSelectedOk = true;
				if (selComp != null && selComp[0] instanceof TCComponentBOMLine) {// 选择BOM
					for (int i = 1; i < selComp.length; i++) {
						if (selComp != null && !(selComp[i] instanceof TCComponentBOMLine)) {
							isSelectedOk = false;
							break;
						}
					}
					if (isSelectedOk) {
						BatchExportExcelControler batchExportExcelControler = new BatchExportExcelControler();
						batchExportExcelControler.userTask(selComp, session);
					} else {
						com.teamcenter.rac.util.MessageBox.post("请选择零组件版本或在结构管理器中操作。", "生成明细表", com.teamcenter.rac.util.MessageBox.ERROR);
					}
				} else if (selComp != null && selComp[0] instanceof TCComponentItemRevision) {// 选择版本
					for (int i = 1; i < selComp.length; i++) {
						if (selComp != null && !(selComp[i] instanceof TCComponentItemRevision)) {
							isSelectedOk = false;
							break;
						}
					}
					if (isSelectedOk) {
						BatchExportExcelControler batchExportExcelControler = new BatchExportExcelControler();
						batchExportExcelControler.userTask(selComp, session);
					} else {
						com.teamcenter.rac.util.MessageBox.post("请选择零组件版本或在结构管理器中操作。", "生成明细表", com.teamcenter.rac.util.MessageBox.ERROR);
					}
				} else {
					com.teamcenter.rac.util.MessageBox.post("请选择零组件版本或在结构管理器中操作。", "生成明细表", com.teamcenter.rac.util.MessageBox.ERROR);
				}
			}
			if ("com.uds.sjec.commands.EditVariableCondition".equals(m_commandId)) {// 编辑变量条件
				InterfaceAIFComponent selComp = this.GetSelectedComponent();
				boolean isSelectedOk = false;
				if (selComp != null && selComp instanceof TCComponentBOMLine) {
					CommonFunction.GetDBMessage();
					isSelectedOk = true;
					TCComponentBOMLine bomLine = (TCComponentBOMLine) selComp;
					TCComponentBOMLine topBomLine = bomLine.getCachedWindow().getTopBOMLine();
					EditVariableConditionControler editVariableConditionControler = new EditVariableConditionControler();
					editVariableConditionControler.userTask(topBomLine, bomLine);
				}
				if (!isSelectedOk) {
					com.teamcenter.rac.util.MessageBox.post("请在超级BOM中操作。", "编辑变量条件", com.teamcenter.rac.util.MessageBox.ERROR);
				}
			}
		} catch (Exception ex) {
			String msg = failure;
			ex.printStackTrace();
			com.teamcenter.rac.util.MessageBox.post(msg, dlgTitle, MessageBox.ERROR);
		}
	}
}
