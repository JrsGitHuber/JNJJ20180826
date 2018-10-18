package com.uds.Jr.TreeTable;

import java.awt.Color;
import java.awt.Component;
import java.awt.EventQueue;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

import org.jdesktop.swingx.JXTreeTable;
import org.jdesktop.swingx.treetable.AbstractMutableTreeTableNode;
import org.jdesktop.swingx.treetable.DefaultMutableTreeTableNode;
import org.jdesktop.swingx.treetable.MutableTreeTableNode;

import com.uds.Jr.utils.TxtUtil;
import com.uds.sjec.bean.BomToPreviewBean;
import com.uds.sjec.controler.CfgManagementControler;

public class PreviewBom {
	private List<TreeNode[]> m_expandPath;
	public String m_notStandardMessage;
	private String m_item_id;
	private JDialog m_frame;
	private JPopupMenu m_popupMenu;
	
	private static Map<String, Icon> m_icon_map;
	
	public PreviewBom(JDialog parent, BomToPreviewBean bean) {
		CfgManagementControler.SetButton1();
		
		m_expandPath = new ArrayList<TreeNode[]>();
		m_notStandardMessage = "";
		m_item_id = "";
		
		// 初始化图标
		InitIconMap();
		
		DefaultMutableTreeTableNode root = new DefaultMutableTreeTableNode(new BomToPreviewBean());
		GetTreeTableData(root, bean);
		
//		m_frame = new JDialog(parent, "配置预览", true);
		m_frame = new JDialog();
		m_frame.setTitle("配置预览");
		JXTreeTable treetable = new JXTreeTable(new TreeTableModel(root));
		treetable.setTreeCellRenderer(new MyRenderer(m_icon_map));
		SetTreeTableProperty(treetable);
		SetTreeTableExpand(treetable);
		ShowNotStandardMessage();
//		TipsUI.CloseUI();
		
		m_frame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent we) {
//				System.exit(0);
//				System.out.println("Exit...");
//				CfgManagementControler.SetCfgManagementFrameButton();
				
				int n = JOptionPane.showConfirmDialog(m_frame.getContentPane(), "是否进行配置计算", "提示", JOptionPane.YES_NO_OPTION);
				if (n == 0) {
					CfgManagementControler.SetCfgManagementFrameButton();
				} else {
					CfgManagementControler.SetButton2();
				}
				
				m_frame.dispose();
				m_frame = null;
				
				CfgManagementControler.SetFrameShow();
			}
		});
		
		Rectangle rectangle = parent.getBounds();
		int height = 500;
		if (BomToPreviewBean.AllBeanCount > 20) {
			height = 800;
		}
		m_frame.setBounds(rectangle.x + rectangle.width/2 - 600, rectangle.y + rectangle.height/2 - height/2, 1200, height);
//		m_frame.setBounds(0, 0, 1000, 800);
		m_frame.getContentPane().add(new JScrollPane(treetable));
		treetable.addMouseListener(new MouseListener() {
			@Override
			public void mouseClicked(MouseEvent e) {
//				m_notStandardMessage = "";
				// （鼠标右键是BUTTON3）
				if (e.getButton() == java.awt.event.MouseEvent.BUTTON3) {
					System.out.println("Popup Menus Are Not Displayed Now");
//					if (m_notStandardMessage.equals("")) {
//						return;
//					}
//					
//					// 创建右键菜单先
//					CreatePopupMenu();
//					// 弹出菜单
//					m_popupMenu.show(m_frame.getContentPane(), e.getX(), e.getY());
				}
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
//		m_frame.pack();
//		m_frame.show();
		m_frame.setVisible(true);
		m_frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		m_frame.setAlwaysOnTop(true);
		
		if (!m_notStandardMessage.equals("")) {
			EventQueue.invokeLater(new Runnable() {
				public void run() {
					try {
						new ShowMessage(m_frame, m_notStandardMessage);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});
		}
	}
	
	private void InitIconMap() {
		m_icon_map = new HashMap<String, Icon>();
		
		m_icon_map.put("S2_CFG", new ImageIcon(PreviewBom.class.getResource("/image/S2_CFG.png")));
		m_icon_map.put("S2_CFGRevision", new ImageIcon(PreviewBom.class.getResource("/image/S2_CFGRevision.png")));
		m_icon_map.put("S2_CP_V", new ImageIcon(PreviewBom.class.getResource("/image/S2_CP_V.png")));
		m_icon_map.put("S2_CP_VRevision", new ImageIcon(PreviewBom.class.getResource("/image/S2_CP_VRevision.png")));
		m_icon_map.put("S2_DQ", new ImageIcon(PreviewBom.class.getResource("/image/S2_DQItem.png")));
		m_icon_map.put("S2_DQRevision", new ImageIcon(PreviewBom.class.getResource("/image/S2_DQItemRevision.png")));
		m_icon_map.put("S2_STD", new ImageIcon(PreviewBom.class.getResource("/image/S2_STDItem.png")));
		m_icon_map.put("S2_STDRevision", new ImageIcon(PreviewBom.class.getResource("/image/S2_STDItemRevision.png")));
		m_icon_map.put("S2_WL", new ImageIcon(PreviewBom.class.getResource("/image/S2_WLItem.png")));
		m_icon_map.put("S2_WLRevision", new ImageIcon(PreviewBom.class.getResource("/image/S2_WLItemRevision.png")));
	}

	private void ShowNotStandardMessage() {
		// 保存、展示非标信息
		if (!m_notStandardMessage.equals("")) {
			try {
				String fileName = m_item_id + "_" + new SimpleDateFormat("yyyyMMdd_HHmmssSSS").format(new Date()) + ".txt";
				String content = m_notStandardMessage.replaceAll("\n", "\r\n");
				TxtUtil.GetNewFile(fileName, content);
			} catch (Exception e1) {
				JOptionPane.showMessageDialog(m_frame.getContentPane(), "保存非标信息出错：" + e1.getMessage(), "提示", JOptionPane.INFORMATION_MESSAGE);
				e1.printStackTrace();
			}
		}
	}
	
	private void SetTreeTableExpand(JXTreeTable treetable) {
		DefaultMutableTreeTableNode root = (DefaultMutableTreeTableNode)treetable.getTreeTableModel().getRoot();
		Enumeration<? extends MutableTreeTableNode> children = root.children();
		DefaultMutableTreeTableNode node = (DefaultMutableTreeTableNode)children.nextElement();
		GetExpandPathList(node, true);
		
		m_item_id = ((BomToPreviewBean)node.getUserObject()).propertyList.get(2);
		for (TreeNode[] path : m_expandPath) {
			treetable.expandPath(new TreePath(path));
		}
	}
	
	private void GetExpandPathList(DefaultMutableTreeTableNode root, boolean ifFirst) {
		Enumeration<? extends MutableTreeTableNode> children = root.children();
		while(children.hasMoreElements()) {
			DefaultMutableTreeTableNode node = (DefaultMutableTreeTableNode) children.nextElement();
			BomToPreviewBean myObject = (BomToPreviewBean)node.getUserObject();
			
			if (node.isLeaf()) {
				if (ifFirst) {
					TreeNode[] path = getPathToRoot(root, 0);
					m_expandPath.add(path);
					myObject.ifChangeColor = true;
					m_notStandardMessage += myObject.propertyList.get(3) + " " + myObject.propertyList.get(2) + " 无子集，需非标\n";
				} else {
					if (myObject.item_type.equals("S2_CP_V")) {
						TreeNode[] path = getPathToRoot(root, 0);
						m_expandPath.add(path);
						myObject.ifChangeColor = true;
						m_notStandardMessage += myObject.propertyList.get(3) + " " + myObject.propertyList.get(2) + " 无子集，需非标\n";
					}
				}
			} else {
				GetExpandPathList(node, false);
			}
		}
	}
	
	private TreeNode[] getPathToRoot(TreeNode aNode, int depth) {
        TreeNode[]              retNodes;

        /* Check for null, in case someone passed in a null node, or
           they passed in an element that isn't rooted at root. */
        if(aNode == null) {
            if(depth == 0)
                return null;
            else
                retNodes = new TreeNode[depth];
        }
        else {
            depth++;
            retNodes = getPathToRoot(aNode.getParent(), depth);
            retNodes[retNodes.length - depth] = aNode;
        }
        return retNodes;
    }


	private void GetTreeTableData(DefaultMutableTreeTableNode root, BomToPreviewBean rootBean) {
		DefaultMutableTreeTableNode node = new DefaultMutableTreeTableNode(rootBean);
    	root.add(node);
    	
		if (rootBean.children == null || rootBean.children.size() == 0) {
        	return;
        }
        for (BomToPreviewBean bean : rootBean.children) {
        	GetTreeTableData(node, bean);
        }
	}

	private void SetTreeTableProperty(JXTreeTable treetable) {
//		treetable.setRootVisible(true); //显示根结点
//		Image image = Toolkit.getDefaultToolkit().createImage(PreviewBom.class.getResource("/tree_elbow.png"));
		
		treetable.getColumn(0).setMinWidth(150);
		
		// 句柄展开时的图标
//		Icon ExpandedIcon = new ImageIcon("image/tree_elbow.png"); // 插件项目不同于单独的项目
		Icon ExpandedIcon = new ImageIcon(PreviewBom.class.getResource("/image/tree_elbow.png"));
		// 句柄折叠时的图标
		Icon CollapsedIcon = new ImageIcon(PreviewBom.class.getResource("/image/tree_elbow_add.png"));
		// 叶节点的图标，也就是下面没有子结点的节点图标
		Icon LeafIcon = new ImageIcon(PreviewBom.class.getResource("/image/tree_file.gif"));
		// 非叶节点关闭时的图标，也就是下面有子结点的节点图标
		Icon ClosedIcon = new ImageIcon(PreviewBom.class.getResource("/image/tree_folder.gif"));
		// 非叶节点打开时的图标，也就是下面有子结点的节点图标
		Icon OpenedIcon = new ImageIcon(PreviewBom.class.getResource("/image/tree_folder_open.gif"));
		
		treetable.setExpandedIcon(ExpandedIcon);
		treetable.setCollapsedIcon(CollapsedIcon);
		treetable.setLeafIcon(LeafIcon);
		treetable.setClosedIcon(ClosedIcon);
		treetable.setOpenIcon(OpenedIcon);
	}
	
	@SuppressWarnings("unused")
	private void CreatePopupMenu() {  
		m_popupMenu = new JPopupMenu();

		JMenuItem delMenItem = new JMenuItem();
		delMenItem.setText("  显示非标信息  ");
		delMenItem.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				if (m_notStandardMessage.equals("")) {
					JOptionPane.showMessageDialog(m_frame.getContentPane(), "没有非标信息", "提示", JOptionPane.INFORMATION_MESSAGE);
				} else {
					new ShowMessage(m_frame, m_notStandardMessage);
				}
			}
		});
		m_popupMenu.add(delMenItem);
    }
}

class MyRenderer extends DefaultTreeCellRenderer {
	private static final long serialVersionUID = 1L;
	
	Map<String, Icon> icon_map;
	
    public MyRenderer(Map<String, Icon> icon_map) {
    	this.icon_map = icon_map;
    }
 
    public Component getTreeCellRendererComponent(
                        JTree tree,
                        Object value,
                        boolean sel,
                        boolean expanded,
                        boolean leaf,
                        int row,
                        boolean hasFocus) {
 
        super.getTreeCellRendererComponent(
                        tree, value, sel,
                        expanded, leaf, row,
                        hasFocus);
//        if (IfNonStandard(value)) {
////            setIcon(tutorialIcon);
////            setToolTipText("This book is in the Tutorial series.");
//////            setBackground(Color.RED);
//////            setOpaque(true);
//            setForeground(Color.RED);
////            setBackgroundSelectionColor(Color.RED);
//        } else {
//            setToolTipText(null); //no tool tip
//        } 
        
        SetColorAndIcon(value);
        
        return this;
    }
    
    private void SetColorAndIcon(Object value) {
    	BomToPreviewBean myObject = (BomToPreviewBean)((AbstractMutableTreeTableNode) value).getUserObject();
    	if (!myObject.item_type.equals("")) {
    		System.out.println(myObject.item_type);
    		if (icon_map.containsKey(myObject.item_type)) {
    			setIcon(icon_map.get(myObject.item_type));
    		}
    	}
    	if (myObject.ifChangeColor) {
    		setForeground(Color.RED);
    	}
    }

	protected boolean IfNonStandard(Object value) {
    	BomToPreviewBean myObject = (BomToPreviewBean)((AbstractMutableTreeTableNode) value).getUserObject();
    	if (myObject.ifChangeColor) {
    		setForeground(Color.RED);
    		return true;
    	} else {
    		return false;
    	}
    }
}

