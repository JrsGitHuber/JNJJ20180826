package common.Jr.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import com.aspose.cells.License;
import com.aspose.cells.SaveFormat;
import com.aspose.cells.Workbook;

public class AsposeExcelUtils {
	private static InputStream license;

	public static void ExcelToPDF(String excelPath, String PDFPath) throws Exception {
		// ��֤License
		if (!GetLicense()) {
			System.out.println("��ȡAsppseCells Licenseʧ��");
			return;
		}
		File pdfFile = new File(PDFPath);// ���·��
		Workbook wb = new Workbook(excelPath);// ԭʼexcel·��
		FileOutputStream fileOS = new FileOutputStream(pdfFile);
		wb.save(fileOS, SaveFormat.PDF);
		fileOS.close();
	}

	public static boolean GetLicense() {
		boolean result = false;
		try {
			license = AsposeExcelUtils.class.getClassLoader().getResourceAsStream("\\license.xml"); // license·��
			License aposeLic = new License();
			aposeLic.setLicense(license);
			result = true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
}
