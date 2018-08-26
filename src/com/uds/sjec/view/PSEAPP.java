package com.uds.sjec.view;

import com.teamcenter.rac.aif.AIFDesktop;
import com.teamcenter.rac.aif.AbstractAIFUIApplication;
import com.teamcenter.rac.kernel.TCComponentBOMWindow;
import com.teamcenter.rac.pse.AbstractPSEApplication;
import com.teamcenter.rac.pse.AbstractPSEApplicationPanel;
import com.teamcenter.rac.pse.common.BOMPanel;
import java.util.List;

public class PSEAPP {
	private AbstractPSEApplication pseapp = null;

	public AbstractPSEApplication GetPSEAPP() {
		AbstractPSEApplication _pseapp = null;
		List<AbstractAIFUIApplication> applist = AIFDesktop.getActiveDesktop().getApplications();
		for (int i = 0; i < applist.size(); i++) {
			if (((AbstractAIFUIApplication) applist.get(i)).getApplicationId() == "com.teamcenter.rac.pse.PSEApplication") {
				_pseapp = (AbstractPSEApplication) applist.get(i);
				break;
			}

		}
		return _pseapp;
	}

	public void CloseBomWindows() {
		AbstractPSEApplication _pseapp = null;
		List<AbstractAIFUIApplication> applist = AIFDesktop.getActiveDesktop().getApplications();
		for (int i = 0; i < applist.size(); i++) {
			if (((AbstractAIFUIApplication) applist.get(i)).getApplicationId() == "com.teamcenter.rac.pse.PSEApplication") {
				_pseapp = (AbstractPSEApplication) applist.get(i);
				try {
					_pseapp.closeBOMPanel();
					TCComponentBOMWindow bomwindow = _pseapp.getBOMWindow();
					bomwindow.getTopBOMLine();
					bomwindow.save();
					bomwindow.clearCache();
					bomwindow.close();
					AbstractPSEApplicationPanel psepanel = _pseapp.getAppPanel();
					BOMPanel bp = _pseapp.getBOMPanel();
					bp.saveBOM();
					psepanel.closeBOMPanel(bp);
				} catch (Exception localException) {
					System.out.println("close bw error");
				}
			}
		}
	}

	public AbstractPSEApplication getPseapp() {
		return this.pseapp;
	}

	public void setPseapp(AbstractPSEApplication pseapp) {
		this.pseapp = pseapp;
	}
}
