package com.uds.Jr.utils;

import com.teamcenter.rac.kernel.TCComponentDataset;
import com.teamcenter.rac.kernel.TCComponentDatasetType;
import com.teamcenter.rac.kernel.TCTypeService;
import com.uds.sjec.common.ConstDefine;

public class TCDatasetUtil {
	
	public static TCComponentDataset CreateDatasetAndSetFile(String datasetName, String datasetType, String datasetNamedRef, String localFile) throws Exception {
		String filePathNames[] = { localFile };
		String namedRefs[] = { datasetNamedRef };
		TCTypeService typeService = ConstDefine.TC_SESSION.getTypeService();
		TCComponentDatasetType TCDatasetType = (TCComponentDatasetType) typeService.getTypeComponent(datasetType);
		TCComponentDataset datasetComponent = TCDatasetType.setFiles(datasetName, "程序自动创建", datasetType, filePathNames, namedRefs);
		return datasetComponent;
	}
}
