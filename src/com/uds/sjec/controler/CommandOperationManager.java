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
		String failure = "����";
		try {
//			if ("com.uds.sjec.commands.productionTable".equals(m_commandId)) {// ����BOM����excel
//				InterfaceAIFComponent selComp = this.GetSelectedComponent();
//				boolean isSelectedOk = false;
//				if (selComp != null && selComp instanceof TCComponentBOMLine) {
//					isSelectedOk = true;
//					TCComponentBOMLine bomLine = (TCComponentBOMLine) selComp;
//					ProductionTableControler productionTableControler = new ProductionTableControler();
//					productionTableControler.userTask(bomLine, m_commandId);
//				}
//				if (!isSelectedOk) {
//					com.teamcenter.rac.util.MessageBox.post("��ѡ�������ʹ���", "������ϸ��", com.teamcenter.rac.util.MessageBox.ERROR);
//				}
//			}
//			if ("com.uds.sjec.commands.productionTablePdf".equals(m_commandId)) {// ����BOM����PDF
//				InterfaceAIFComponent selComp = this.GetSelectedComponent();
//				boolean isSelectedOk = false;
//				if (selComp != null && selComp instanceof TCComponentBOMLine) {
//					isSelectedOk = true;
//					TCComponentBOMLine bomLine = (TCComponentBOMLine) selComp;
//					ProductionTableControler productionTableControler = new ProductionTableControler();
//					productionTableControler.userTask(bomLine, m_commandId);
//				}
//				if (!isSelectedOk) {
//					com.teamcenter.rac.util.MessageBox.post("���ڽṹ�������в�����", "������ϸ��", com.teamcenter.rac.util.MessageBox.ERROR);
//				}
//			}
			if ("com.uds.sjec.commands.productionTable".equals(m_commandId) || "com.uds.sjec.commands.productionTablePdf".equals(m_commandId)) {
				InterfaceAIFComponent[] selComps = this.GetSelectedComponents();
				ExportBomsToFiles process = new ExportBomsToFiles(m_rectangle);
				process.DoTask(m_commandId, selComps, false);
			}
			if ("com.uds.sjec.commands.productionTableOfLevelOne".equals(m_commandId)) {// һ��BOM����Excel
				InterfaceAIFComponent selComp = this.GetSelectedComponent();
				boolean isSelectedOk = false;
				if (selComp != null && selComp instanceof TCComponentBOMLine) {
					isSelectedOk = true;
					TCComponentBOMLine bomLine = (TCComponentBOMLine) selComp;
					ProductionTableControler productionTableControler = new ProductionTableControler();
					productionTableControler.userTask(m_rectangle, bomLine, m_commandId);
				}
				if (!isSelectedOk) {
					com.teamcenter.rac.util.MessageBox.post("���ڽṹ�������в�����", "������ϸ��", com.teamcenter.rac.util.MessageBox.ERROR);
				}
			}
			if ("com.uds.sjec.commands.productionTablePdfOfLevelOne".equals(m_commandId)) {// һ��BOM����PDF
				InterfaceAIFComponent selComp = this.GetSelectedComponent();
				boolean isSelectedOk = false;
				if (selComp != null && selComp instanceof TCComponentBOMLine) {
					isSelectedOk = true;
					TCComponentBOMLine bomLine = (TCComponentBOMLine) selComp;
					ProductionTableControler productionTableControler = new ProductionTableControler();
					productionTableControler.userTask(m_rectangle, bomLine, m_commandId);
				}
				if (!isSelectedOk) {
					com.teamcenter.rac.util.MessageBox.post("���ڽṹ�������в�����", "������ϸ��", com.teamcenter.rac.util.MessageBox.ERROR);
				}
			}
			if ("com.uds.sjec.commands.batchExportExcel".equals(m_commandId)) {// ������ϸ��
				TCSession session = (TCSession) AIFUtility.getActiveDesktop().getCurrentApplication().getSession();
				InterfaceAIFComponent[] selComp = this.GetSelectedComponents();
				boolean isSelectedOk = true;
				if (selComp != null && selComp[0] instanceof TCComponentBOMLine) {// ѡ��BOM
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
						com.teamcenter.rac.util.MessageBox.post("��ѡ��������汾���ڽṹ�������в�����", "������ϸ��", com.teamcenter.rac.util.MessageBox.ERROR);
					}
				} else if (selComp != null && selComp[0] instanceof TCComponentItemRevision) {// ѡ��汾
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
						com.teamcenter.rac.util.MessageBox.post("��ѡ��������汾���ڽṹ�������в�����", "������ϸ��", com.teamcenter.rac.util.MessageBox.ERROR);
					}
				} else {
					com.teamcenter.rac.util.MessageBox.post("��ѡ��������汾���ڽṹ�������в�����", "������ϸ��", com.teamcenter.rac.util.MessageBox.ERROR);
				}
			}
			if ("com.uds.sjec.commands.EditVariableCondition".equals(m_commandId)) {// �༭��������
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
					com.teamcenter.rac.util.MessageBox.post("���ڳ���BOM�в�����", "�༭��������", com.teamcenter.rac.util.MessageBox.ERROR);
				}
			}
		} catch (Exception ex) {
			String msg = failure;
			ex.printStackTrace();
			com.teamcenter.rac.util.MessageBox.post(msg, dlgTitle, MessageBox.ERROR);
		}
	}
}
