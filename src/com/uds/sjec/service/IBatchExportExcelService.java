package com.uds.sjec.service;

import java.util.List;

import com.teamcenter.rac.kernel.TCComponentBOMLine;
import com.teamcenter.rac.kernel.TCComponentItemRevision;
import com.teamcenter.rac.kernel.TCSession;
import com.uds.sjec.bean.ProductionTableBean;
import com.uds.sjec.view.UDSJProcessBar;

public interface IBatchExportExcelService {

	Boolean downloadDatasetToLocal(TCSession session, String datasetType, String datasetName, String nameReferences, String localFileName,
			String localDirName, UDSJProcessBar bar);

	Boolean writeToExcel(TCComponentBOMLine topBomLine, List<ProductionTableBean> productonTableList, String localDirectory, UDSJProcessBar bar);

	List<ProductionTableBean> getAndSortBOMOfOne(TCComponentBOMLine topBomLine, UDSJProcessBar bar);

	Boolean createDataSet(TCSession session, String localFile, String dataSetType, String datasetNamedRef, String dataSetName,
			TCComponentItemRevision itemRevision, String relationType, boolean replaceAlert, UDSJProcessBar bar);
}
