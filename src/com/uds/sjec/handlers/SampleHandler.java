package com.uds.sjec.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;

import com.teamcenter.rac.aif.AbstractAIFUIApplication;
import com.teamcenter.rac.aifrcp.AIFUtility;
import com.teamcenter.rac.kernel.TCSession;
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
		System.out.println("Jar Time :  2018.08.19 10:17");
		
		AbstractAIFUIApplication app = AIFUtility.getCurrentApplication();
		TCSession session = (TCSession) app.getSession();
		CommandOperationManager opr = new CommandOperationManager();
		String command = event.getCommand().getId();
		opr.m_commandId = command;
		session.queueOperation(opr);

		return null;
	}
}
