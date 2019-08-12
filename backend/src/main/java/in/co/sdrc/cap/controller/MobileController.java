package in.co.sdrc.cap.controller;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import in.co.sdrc.cap.service.MobileService;
import in.co.sdrc.cap.util.Constant;

@RestController
public class MobileController {

	@Autowired
	private MobileService mobileService;
	
	private final Path rootLocation = Paths.get("/cap-data");
	
	@GetMapping("zip")
	public boolean generateZipFile() throws IOException{
		return mobileService.createJsonAndZip();
	}
	
	@CrossOrigin
	@GetMapping("sync")
	public void syncData(HttpServletResponse response) throws IOException {
		InputStream inputStream;
		try {

			inputStream = new FileInputStream(this.rootLocation.resolve(Constant.ZIP_FILE_NAME).toFile());
			String headerKey = "Content-Disposition";

			response.setHeader(headerKey, "attachment; filename=" + Constant.ZIP_FILE_NAME);
			response.setContentType("application/zip"); // for all file type
			ServletOutputStream outputStream = response.getOutputStream();
			FileCopyUtils.copy(inputStream, outputStream);
			outputStream.close();

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
}
