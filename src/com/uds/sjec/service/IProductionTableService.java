package com.uds.sjec.service;

import java.util.List;

import com.teamcenter.rac.kernel.TCComponentBOMLine;
import com.teamcenter.rac.kernel.TCSession;
import com.uds.sjec.bean.ProductionTableBean;
import com.uds.sjec.view.UDSJProcessBar;

public interface IProductionTableService {

	Boolean downloadDatasetToLocal(TCSession session, String datasetType, String datasetName, String nameReferences, String localFileName,
			String localDirName);

	Boolean writeToExcel(TCComponentBOMLine topBomLine, List<ProductionTableBean> productonTableList, String localDirectory);

	List<ProductionTableBean> getAndSortAllBOM(TCComponentBOMLine topBomLine);

	List<ProductionTableBean> getAndSortBOMOfOne(TCComponentBOMLine topBomLine);

}
