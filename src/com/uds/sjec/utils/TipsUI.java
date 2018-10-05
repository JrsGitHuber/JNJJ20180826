package com.uds.sjec.utils;

import java.awt.BorderLayout;
import java.awt.Rectangle;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.border.EmptyBorder;

import com.teamcenter.rac.util.MessageBox;

public class TipsUI {
	public static UI UI_FOR_CLOSE;
	
	UI ui;
	Thread thread;
	
	public static JPanel contentPanel;
	
	public static void ShowUI(String title, Thread thread, Object parent){
		new TipsUI(title, thread, parent);
	}
	
	public static void CloseUI() {
		if (UI_FOR_CLOSE != null) {
			UI_FOR_CLOSE.dispose();
			UI_FOR_CLOSE = null;
		}
	}
	
//	public static void CloseUI(String title, Thread thread, Object parent){
//		if (ui == null) {
//			return;
//		} else {
//			ui.dispose();
//			ui = null;
//		}
//	}
	
	
	private TipsUI(String title, Thread thread, Object parent){
		this.thread = thread;
		StartThread();
		contentPanel = new JPanel();
		
		if(parent == null){
			ui = new UI(title);
		}else if(parent instanceof JDialog){
			ui = new UI(title, (JDialog)parent);
		}else if(parent instanceof JFrame){
			ui = new UI(title, (JFrame)parent);
		}else if(parent instanceof Rectangle){
			ui = new UI(title, (Rectangle)parent);
		}else{
			ui = new UI(title);
		}
		
		ui.setVisible(true);
	}
	
	private void StartThread() {
		new Thread() {
			public void run() {
				try {
					UI_FOR_CLOSE = ui;
					thread.start(); // 处理耗时任务
					thread.join(); // 等待事务处理线程结束
				} catch (Exception e) {
					MessageBox.post("出错：\n\n" + e.getMessage() + "\n请查看控制台输出的错误信息", "提示", MessageBox.INFORMATION);
					e.printStackTrace();					
				} finally {
					if (ui == null) {
						return;
					} else {
						ui.dispose();
						ui = null;
					}
					contentPanel = null;
				}
			}
		}.start();
	}
}

class UI extends JDialog {
	
	private static final long serialVersionUID = 1L;
//	private final JPanel contentPanel = new JPanel();
	
	private int width = 450;
	private int height = 100;
	
	public UI(String title){
		setTitle(title);
		OtherInit();
		setLocationRelativeTo(null);
	}
	
	public UI(String title, JDialog parent) {
		super(parent, title, true);
		OtherInit();
		SetBounds(parent.getBounds());
	}
	
	public UI(String title, JFrame parent) {
		super(parent, title, true);
		OtherInit();
		SetBounds(parent.getBounds());
	}
	
	public UI(String title, Rectangle parent) {
		setTitle(title);
		OtherInit();
		SetBounds(parent.getBounds());
	}
	
	private void OtherInit(){
		setAlwaysOnTop(true);
		setType(Type.UTILITY);
		setBounds(100, 100, width, height);
		
		TipsUI.contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		TipsUI.contentPanel.setLayout(null);
		getContentPane().add(TipsUI.contentPanel, BorderLayout.CENTER);
		
		JProgressBar progressBar = new JProgressBar();
		progressBar.setBounds(80, 30, 280, 15);
		progressBar.setIndeterminate(true);
		TipsUI.contentPanel.add(progressBar);
	}
	
	private void SetBounds(Rectangle rectangle){
		int centerX = rectangle.x + rectangle.width / 2;
		int centerY = rectangle.y + rectangle.height / 2;
		setBounds(centerX - width/2, centerY - height/2, width, height);
	}
	
	public void dispose(){
		super.dispose();
	}
}