package com.uds.sjec.base;

import com.teamcenter.rac.kernel.TCComponentBOMLine;

public interface BaseControler {
	void userTask(TCComponentBOMLine bomLine, String m_commandId);
}
