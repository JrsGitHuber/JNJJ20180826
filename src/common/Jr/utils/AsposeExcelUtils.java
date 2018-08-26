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
		// 验证License
		if (!GetLicense()) {
			System.out.println("获取AsppseCells License失败");
			return;
		}
		File pdfFile = new File(PDFPath);// 输出路径
		Workbook wb = new Workbook(excelPath);// 原始excel路径
		FileOutputStream fileOS = new FileOutputStream(pdfFile);
		wb.save(fileOS, SaveFormat.PDF);
		fileOS.close();
	}

	public static boolean GetLicense() {
		boolean result = false;
		try {
			license = AsposeExcelUtils.class.getClassLoader().getResourceAsStream("\\license.xml"); // license路径
			License aposeLic = new License();
			aposeLic.setLicense(license);
			result = true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
}
