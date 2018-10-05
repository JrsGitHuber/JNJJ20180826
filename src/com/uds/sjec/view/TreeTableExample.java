package com.uds.sjec.view;

/*
 * %W% %E%
 *
 * Copyright 1997, 1998 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * Redistribution and use in source and binary forms, with or
 * without modification, are permitted provided that the following
 * conditions are met:
 * 
 * - Redistributions of source code must retain the above copyright
 *   notice, this list of conditions and the following disclaimer. 
 *   
 * - Redistribution in binary form must reproduce the above
 *   copyright notice, this list of conditions and the following
 *   disclaimer in the documentation and/or other materials
 *   provided with the distribution. 
 *   
 * Neither the name of Sun Microsystems, Inc. or the names of
 * contributors may be used to endorse or promote products derived
 * from this software without specific prior written permission.  
 * 
 * This software is provided "AS IS," without a warranty of any
 * kind. ALL EXPRESS OR IMPLIED CONDITIONS, REPRESENTATIONS AND
 * WARRANTIES, INCLUDING ANY IMPLIED WARRANTY OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE OR NON-INFRINGEMENT, ARE HEREBY
 * EXCLUDED. SUN AND ITS LICENSORS SHALL NOT BE LIABLE FOR ANY
 * DAMAGES OR LIABILITIES SUFFERED BY LICENSEE AS A RESULT OF OR
 * RELATING TO USE, MODIFICATION OR DISTRIBUTION OF THIS SOFTWARE OR
 * ITS DERIVATIVES. IN NO EVENT WILL SUN OR ITS LICENSORS BE LIABLE 
 * FOR ANY LOST REVENUE, PROFIT OR DATA, OR FOR DIRECT, INDIRECT,   
 * SPECIAL, CONSEQUENTIAL, INCIDENTAL OR PUNITIVE DAMAGES, HOWEVER  
 * CAUSED AND REGARDLESS OF THE THEORY OF LIABILITY, ARISING OUT OF 
 * THE USE OF OR INABILITY TO USE THIS SOFTWARE, EVEN IF SUN HAS 
 * BEEN ADVISED OF THE POSSIBILITY OF SUCH DAMAGES.
 * 
 * You acknowledge that this software is not designed, licensed or
 * intended for use in the design, construction, operation or
 * maintenance of any nuclear facility.
 */

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;

import com.teamcenter.rac.kernel.TCComponentBOMLine;
import com.uds.sjec.controler.CfgManagementControler;

/**
 * A TreeTable example, showing a JTreeTable, operating on the local file
 * system.
 * 
 * @version %I% %G%
 * 
 * @author Philip Milne
 */

public class TreeTableExample {

	private static JFrame frame;
	
	public TreeTableExample(final JButton button_BOMConfiguration, final JButton button_configurationCaculate, TCComponentBOMLine bomLine,
			String configListId, Map<String, String> map) {
		frame = new JFrame("配置预览-" + configListId);
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		JTreeTable treeTable = new JTreeTable(new BOMViewModel(bomLine, map));
		treeTable.getColumnModel().getColumn(0).setPreferredWidth(200);
		JScrollPane pane = new JScrollPane(treeTable);
		frame.getContentPane().add(pane);
		frame.setBounds(100, 100, 1287, 675);
		frame.setVisible(true);
		frame.setLocationRelativeTo(null);
		frame.setAlwaysOnTop(true);
		pane.addMouseListener(new MouseListener() {

            @Override
            public void mouseClicked(MouseEvent e) {
            	frame.setAlwaysOnTop(false);
            }

            @Override
            public void mousePressed(MouseEvent e) {
            }

            @Override
            public void mouseReleased(MouseEvent e) {
            }

            @Override
            public void mouseEntered(MouseEvent e) {
            	
            }

            @Override
            public void mouseExited(MouseEvent e) {
            }
            
        });
		frame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent we) {
				frame.setAlwaysOnTop(true);
				int n = JOptionPane.showConfirmDialog(frame.getContentPane(), "是否进行配置计算", "提示", JOptionPane.YES_NO_OPTION);
				if (n == 0) {
					button_BOMConfiguration.setEnabled(false);
					button_configurationCaculate.setEnabled(true);
					frame.dispose();
				} else {
					frame.setAlwaysOnTop(false);
				}
				
				CfgManagementControler.SetFrameShow();
			}
		});
	}
	
	public static void SetAlwaysOnTop(boolean ifOnTop) {
		if (frame != null) {
			frame.setAlwaysOnTop(ifOnTop);
		}
	}
}
