package com.uds.Jr.TreeTable;

import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Vector;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

import com.uds.sjec.view.TreeTableExample;

public class ShowMessage {
	private static ShowMessageUI ui;
	
	public ShowMessage(JDialog parent, String notStandardMessage) {
		if(ui == null){
			ui = new ShowMessageUI(parent, notStandardMessage);
        } else {
        	Rectangle rectangle = parent.getBounds();
        	ui.setBounds(rectangle.x + rectangle.width + 10, rectangle.y, 280, 400);
        }
		ui.setVisible(true);
//		ui.setResizable(false);
		ui.setAlwaysOnTop(true);
		
		ui.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent we) {
				if (ui != null) {
					ui.dispose();
					ui = null;
				}
			}
		});
	}
}

class ShowMessageUI extends JDialog {
	
	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private JTable table;
	private String notStandardMessage;

//	/**
//	 * Launch the application.
//	 */
//	public static void main(String[] args) {
//		EventQueue.invokeLater(new Runnable() {
//			public void run() {
//				try {
//					ShowMessage frame = new ShowMessage();
//					frame.setVisible(true);
//				} catch (Exception e) {
//					e.printStackTrace();
//				}
//			}
//		});
//	}

	/**
	 * Create the frame.
	 */
	public ShowMessageUI(JDialog parent, String notStandardMessage) {
//		super(parent, "非标信息", true);
		this.notStandardMessage = notStandardMessage;
//		super(parentFrame, title, modal);
		
		setType(Type.UTILITY);
//		setTitle("非标信息");
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		Rectangle rectangle = parent.getBounds();
		setBounds(rectangle.x + rectangle.width + 10, rectangle.y, 280, 400);
//		setBounds(100, 100, 275, 367);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
//		setLocationRelativeTo(null);
		
		table = new JTable();
		table.setBounds(0, 0, 259, 328);
		
		IntiTableData();
		JScrollPane scrollPane = new JScrollPane(table);
		scrollPane.setBounds(0, 0, 264, 361);
		contentPane.add(scrollPane);
		
		scrollPane.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
            	TreeTableExample.SetAlwaysOnTop(false);
            }

            @Override
            public void mousePressed(MouseEvent e) { }

            @Override
            public void mouseReleased(MouseEvent e) { }

            @Override
            public void mouseEntered(MouseEvent e) { }

            @Override
            public void mouseExited(MouseEvent e) { }
            
        });
	}
	
	public void IntiTableData() {
		Vector<Object> columnNames = new Vector<Object>();
		columnNames.add("序号");
		columnNames.add("信息");
		
		Vector<Vector<Object>> rowDatas = new Vector<Vector<Object>>();
		String[] strs = notStandardMessage.split("\n");
		int index = 1;
		for (String str : strs) {
			Vector<Object> vector1 = new Vector<Object>();
			vector1.add(index++);
			vector1.add(str);
			rowDatas.add(vector1);
		}
		
		DefaultTableModel newTableModel = new DefaultTableModel(rowDatas, columnNames) {
			private static final long serialVersionUID = 1L;

			@Override
			public boolean isCellEditable(int row, int column) {
				return false;
			}
		};
		
		table = new JTable();
		table.setModel(newTableModel);
		table.getColumnModel().getColumn(0).setMaxWidth(35);
	}
}