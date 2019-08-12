package in.co.sdrc.cap.service;

import java.io.IOException;

public interface MobileService {

	/**
	 * This method will create the data file in JSON and zip it
	 * @param fcmToken 
	 * @return
	 * @throws IOException
	 */
	boolean createJsonAndZip() throws IOException;
}
