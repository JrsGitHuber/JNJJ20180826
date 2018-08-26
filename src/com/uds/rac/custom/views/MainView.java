package com.uds.rac.custom.views;

import java.io.IOException;
import java.text.SimpleDateFormat;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.part.ViewPart;

import com.teamcenter.rac.aif.AIFDesktop;
import com.teamcenter.rac.aif.AbstractAIFUIApplication;
import com.teamcenter.rac.aif.kernel.InterfaceAIFComponent;
import com.teamcenter.rac.kernel.TCComponentDataset;
import com.teamcenter.rac.kernel.TCPreferenceService;
import com.teamcenter.rac.util.MessageBox;
import com.uds.common.utils.EncryptionUtil;
import com.uds.sjec.common.CommonFunction;
import com.uds.sjec.common.Const.PreferenceService;
import com.uds.sjec.common.ConstDefine;
import com.uds.sjec.controler.CfgManagementControler;
import com.uds.sjec.controler.ImportProjectControler;
import com.uds.sjec.frame.CfgManagementFrame;

public class MainView extends ViewPart {

	private AIFDesktop desk = null;
	private CfgManagementFrame cfgManagementFrame;// 配置单管理

	@Override
	public void createPartControl(Composite arg0) {

		org.eclipse.swt.graphics.Color _color = new org.eclipse.swt.graphics.Color(null, new RGB(255, 255, 255));
		arg0.setBackground(_color);
		this.InitialTreeData(arg0);
	}

	private void InitialTreeData(Composite _parent) {
		
		final Tree tree = new Tree(_parent, SWT.SINGLE);
		desk = AIFDesktop.getActiveDesktop();
		desk.getCurrentApplication();
		try {
			TreeItem importProjectItem = new TreeItem(tree, SWT.NONE);
			//org.eclipse.swt.graphics.Image image1 = Toolkit.getDefaultToolkit().getImage(MainView.class.getResource("/teamcenter_app_256.png"));
			ClassLoader classLoader = MainView.class.getClassLoader();
			org.eclipse.swt.graphics.Image image = new org.eclipse.swt.graphics.Image(null, classLoader.getResourceAsStream("/导入项目.png"));
			importProjectItem.setImage(image);
			importProjectItem.setText("导入项目");
			TreeItem configurationManagementItem = new TreeItem(tree, SWT.NONE);
			org.eclipse.swt.graphics.Image image1 = new org.eclipse.swt.graphics.Image(null, classLoader.getResourceAsStream("/配置单管理.png"));
			configurationManagementItem.setImage(image1);
			configurationManagementItem.setText("配置单管理");
			TreeItem paramManagementItem = new TreeItem(tree, SWT.NONE);
			org.eclipse.swt.graphics.Image image2 = new org.eclipse.swt.graphics.Image(null, classLoader.getResourceAsStream("/参数管理.png"));
			paramManagementItem.setImage(image2);
			paramManagementItem.setText("参数管理");
		} catch (Exception e) {
			e.printStackTrace();
			MessageBox.post(e.getLocalizedMessage(), "", 0);
		}
		tree.addMouseListener(new MouseListener() {
			@SuppressWarnings({ "deprecation" })
			@Override
			public void mouseDoubleClick(MouseEvent arg0) {
				CommonFunction.Init();
				if (!ConstDefine.IFCANDO || !ConstDefine.IFDEFINE) {
					return;
				}
				
				TreeItem treeitem = tree.getItem(new Point(arg0.x, arg0.y));
				String Item = treeitem.getText();
				if (Item.equals("导入项目")) {
					AbstractAIFUIApplication application = AIFDesktop.getActiveDesktop().getCurrentApplication();
					InterfaceAIFComponent selComp = application.getTargetComponent();
					Boolean isSelected = false;
					if (selComp != null && selComp instanceof TCComponentDataset) {
						TCComponentDataset dataset = (TCComponentDataset) selComp;
						if (dataset.getType().equals("MSExcelX") && dataset.toString().contains("参数录入表")) {
							isSelected = true;
							ImportProjectControler importProjectControler = new ImportProjectControler();
							importProjectControler.userTask(dataset);
						}
					}
					if (!isSelected) {
						MessageBox.post("请选择参数录入表。", "项目导入", MessageBox.ERROR);
					}
				} else if (Item.equals("配置单管理")) {
					if (cfgManagementFrame == null) {
						cfgManagementFrame = new CfgManagementFrame();
					}
					CfgManagementControler cfgManagementControler = new CfgManagementControler();
					cfgManagementControler.userTask(cfgManagementFrame);
				} else if (Item.equals("参数管理")) {
					String currentUserName = ConstDefine.TC_SESSION.getUserName();
					String[] paramConfigUser = ConstDefine.TC_PREFERENCESERVICE.getStringArray(TCPreferenceService.TC_preference_site,
							PreferenceService.PARAM_CONFIG_USER);
					if (paramConfigUser.length == 0) {
						MessageBox.post("没有找到首选项配置：" + PreferenceService.PARAM_CONFIG_USER, "参数管理", MessageBox.ERROR);
						return;
					}
					Boolean hasPermissions = false;
					for (String user : paramConfigUser) {
						if (user.equals(currentUserName)) {
							hasPermissions = true;
							break;
						}
					}
					if (hasPermissions) {
						// 启动exe路径
						String exePath = ConstDefine.TC_PREFERENCESERVICE.getString(TCPreferenceService.TC_preference_site,
								PreferenceService.PARAM_CONFIG_PATH);
						if (exePath.equals("")) {
							MessageBox.post("没有找到首选项配置：" + PreferenceService.PARAM_CONFIG_PATH, "参数管理", MessageBox.ERROR);
							return;
						}
						java.util.Date date = new java.util.Date();
						SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");// 设置日期格式
						String systemDate = df.format(date);// 当前时间
						try {
							systemDate = EncryptionUtil.MD5(systemDate);
						} catch (Exception e1) {
							e1.printStackTrace();
							MessageBox.post("日期转码出错", "提示", MessageBox.INFORMATION);
							return;
						}
						String connection = "Data Source=(DESCRIPTION=(ADDRESS=(PROTOCOL=TCP)(HOST=" + ConstDefine.TCDB_IP + ")(PORT=" + ConstDefine.TCDB_PORT + "))(CONNECT_DATA=(SID=" + ConstDefine.TCDB_UID + ")));User Id=" + ConstDefine.TCDB_USER + ";Password=" + ConstDefine.TCDB_PASSWORD + ";";
						Runtime runtime = Runtime.getRuntime();
						try {
							String[] param = { exePath, systemDate, currentUserName, connection };
							runtime.exec(param);
						} catch (IOException e) {
							e.printStackTrace();
							MessageBox.post(e.getMessage(), "参数管理", MessageBox.ERROR);
						}
					} else {
						MessageBox.post("该功能不可用，无操作权限。", "参数管理", MessageBox.WARNING);
					}
				}
			}

			@Override
			public void mouseDown(MouseEvent arg0) {}

			@Override
			public void mouseUp(MouseEvent arg0) {}
		});
	}

	@Override
	public void setFocus() {}
}
