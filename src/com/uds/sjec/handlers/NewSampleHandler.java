package com.uds.sjec.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import com.teamcenter.rac.aif.kernel.InterfaceAIFComponent;
import com.teamcenter.rac.util.MessageBox;
import com.uds.sjec.common.CommonFunction;
import com.uds.sjec.newcontroler.ExportBomsToFiles;

public class NewSampleHandler extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent arg0) throws ExecutionException {
		// 用于导出的Jar包是否包含新的修改
		System.out.println("----------------------------------");
		System.out.println("Jar Time :  2018.08.19 10:17");
		
		CommonFunction.InitSomeConst();
		if (!CommonFunction.m_errorMessage.equals("")) {
			MessageBox.post(CommonFunction.m_errorMessage, "提示", MessageBox.INFORMATION);
			return null;
		}
		InterfaceAIFComponent[] selComps = CommonFunction.GetTargetComponents();
		
		Shell TCShell = Display.getCurrent().getShells()[0];
		Rectangle rectangle0 = TCShell.getBounds();
		java.awt.Rectangle rectangle = new java.awt.Rectangle(rectangle0.x, rectangle0.y, rectangle0.width, rectangle0.height);
		
		String commandID = arg0.getCommand().getId();
		try {
			if (commandID.startsWith("com.uds.sjec.commands.productionTable")) {
				ExportBomsToFiles process = new ExportBomsToFiles(rectangle);
				process.DoTask(commandID, selComps, false);
			} else if (commandID.equals("com.uds.sjec.commands.batchExportExcel")) {
				ExportBomsToFiles process = new ExportBomsToFiles(rectangle);
				process.DoTask(commandID, selComps, true);
			} else if (commandID.equals("com.uds.sjec.commands.EditVariableCondition")) {
				CommonFunction.GetDBMessage();
				if (!CommonFunction.m_errorMessage.equals("")) {
					MessageBox.post(CommonFunction.m_errorMessage, "提示", MessageBox.INFORMATION);
					return null;
				}
				
			}
		} catch (Exception e) {
			e.printStackTrace();
			MessageBox.post("出错：" + e.getMessage() + "\n详细信息请联系管理员查看控制台", "提示", MessageBox.INFORMATION);
		}

		return null;
	}

}
