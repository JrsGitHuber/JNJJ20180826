package com.uds.sjec.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import com.uds.sjec.common.CommonFunction;
import com.uds.sjec.common.ConstDefine;
import com.uds.sjec.controler.CommandOperationManager;

/**
 * Our sample handler extends AbstractHandler, an IHandler base class.
 * 
 * @see org.eclipse.core.commands.IHandler
 * @see org.eclipse.core.commands.AbstractHandler
 */
public class SampleHandler extends AbstractHandler {
	/**
	 * The constructor.
	 */
	public SampleHandler() {
	}

	/**
	 * the command has been executed, so extract extract the needed information
	 * from the application context.
	 */
	public Object execute(ExecutionEvent event) throws ExecutionException {
		// 用于导出的Jar包是否包含新的修改
		System.out.println("----------------------------------");
		System.out.println("Jar Time :  2018.09.25 10:18");
		
		Shell TCShell = Display.getCurrent().getShells()[0];
		Rectangle rectangle0 = TCShell.getBounds();
		java.awt.Rectangle rectangle = new java.awt.Rectangle(rectangle0.x, rectangle0.y, rectangle0.width, rectangle0.height);
		
		CommonFunction.GetTCSession();
		CommandOperationManager opr = new CommandOperationManager();
		String command = event.getCommand().getId();
		opr.m_commandId = command;
		opr.m_rectangle = rectangle;
		ConstDefine.TC_SESSION.queueOperation(opr);

		return null;
	}
}
