package com.uds.sjec.utils;

import java.io.File;
import com.teamcenter.rac.kernel.TCComponentDataset;
import com.teamcenter.rac.kernel.TCException;
import com.teamcenter.rac.kernel.TCSession;
import com.teamcenter.rac.util.MessageBox;

public class DatesetUtil {
	public static boolean datasetFileToLocalDir(TCSession imanSession, TCComponentDataset dataset, String namedRefName,
			String localFileName, String localDirName) {
		File fileObject = null;
		File tempFileObject;
		File dirObject = new File(localDirName);
		if (!dirObject.exists())
			dirObject.mkdirs();
		try {
			TCComponentDataset componentDataset = dataset.latest();
			String namedRefFileName[] = componentDataset.getFileNames(namedRefName);
			if ((namedRefFileName == null) || (namedRefFileName.length == 0)) {
				MessageBox.post("���ݼ�<" + dataset.toString() + ">û�ж�Ӧ����������!", "ϵͳ���ô���", MessageBox.ERROR);
				return false;
			}
			tempFileObject = new File(localDirName, namedRefFileName[0]);
			if (tempFileObject.exists())
				tempFileObject.delete();
			fileObject = componentDataset.getFile(namedRefName, namedRefFileName[0], localDirName);
		} catch (TCException e) {
			System.out.print("���ݼ�<" + dataset.toString() + ">���ô���!\n");
			return false;
		} catch (Exception e) {
			System.out.print("���ݼ�<" + dataset.toString() + ">���ô���!\n");
			return false;
		}
		tempFileObject = new File(localDirName, localFileName);
		if (!fileObject.getAbsolutePath().equals(tempFileObject.getAbsolutePath())) {
			if (tempFileObject.exists())
				tempFileObject.delete();
			fileObject.renameTo(tempFileObject);
		}
		return true;
	}
}
