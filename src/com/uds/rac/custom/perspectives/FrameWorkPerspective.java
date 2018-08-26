package com.uds.rac.custom.perspectives;

import org.eclipse.ui.IFolderLayout;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;

public class FrameWorkPerspective implements IPerspectiveFactory {

	@Override
	public void createInitialLayout(IPageLayout layout) {

		String editorArea = layout.getEditorArea();
		layout.setEditorAreaVisible(false);
		InitialPSEView(layout, editorArea);
		InitialMainView(layout, editorArea);
		InitialHomeInbox(layout, editorArea);
	}

	// �Զ�����ͼ�����ϣ�
	private void InitialMainView(IPageLayout layout, String editorArea) {
		IFolderLayout left = layout.createFolder("left", 3, 0.4f, editorArea);
		left.addView("com.uds.rac.custom.views.MainView");
	}

	// �Զ�����ͼ�����£�
	private void InitialHomeInbox(IPageLayout layout, String editorArea) {
		IFolderLayout bottom = layout.createFolder("Bottom", 4, 0.6f, editorArea);
		bottom.addView("com.teamcenter.rac.ui.views.TCComponentView:*");
	}

	// �Ҳ���ͼ
	private void InitialPSEView(IPageLayout layout, String editorArea) {
		IFolderLayout right = layout.createFolder("DataPaneFolder", 2, 0.3f, editorArea);
		right.addView("com.teamcenter.rac.pse.PSEView");

	}
}
