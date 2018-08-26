package com.uds.sjec.view;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.border.EmptyBorder;

import com.teamcenter.rac.util.MessageBox;

public class UDSJProcessBar extends JFrame {

	public static final long serialVersionUID = 1L;
	public JProgressBar processBar;
	public JPanel contentPane = null;

	public UDSJProcessBar() {
		setTitle("正在生成明细表...");
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 500, 100);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
		processBar = new JProgressBar();
		processBar.setPreferredSize(new Dimension(400, 30));
		processBar.setStringPainted(true);
		processBar.setIndeterminate(true);
		processBar.setBackground(Color.gray);
		contentPane.add(processBar);

	}

	public void SetProcessBarValue(int value) {
		processBar.setValue(value);
	}

	public void SetProcessBarString(String result) {
		processBar.setString(result);
	}

	public void CloseBarSuccess(UDSJProcessBar bar) {
		bar.setVisible(false);
		MessageBox.post("false", "warn", 2);
	}

	public void CloseBarFail(UDSJProcessBar bar) {
		bar.setVisible(false);
	}
}