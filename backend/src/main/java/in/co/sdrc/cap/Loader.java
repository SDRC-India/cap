package in.co.sdrc.cap;

import org.sdrc.usermgmt.core.annotations.EnableUserManagementWithJWTJPASecurityConfiguration;
import org.sdrc.usermgmt.core.util.UgmtClientCredentials;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableUserManagementWithJWTJPASecurityConfiguration
public class Loader {
	
	@Bean
	public UgmtClientCredentials ugmtClientCredentials(){
		return new UgmtClientCredentials("web","pass");
	}
	
}