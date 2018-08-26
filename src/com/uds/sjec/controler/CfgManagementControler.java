package com.uds.sjec.controler;

import java.awt.EventQueue;

import com.uds.sjec.frame.CfgManagementFrame;

public class CfgManagementControler {

	public void userTask(final CfgManagementFrame frame) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					frame.setResizable(false);
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
}
