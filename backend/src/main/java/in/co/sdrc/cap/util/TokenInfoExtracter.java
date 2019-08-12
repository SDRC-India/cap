package in.co.sdrc.cap.util;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.authentication.OAuth2AuthenticationDetails;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.stereotype.Component;

import in.co.sdrc.cap.model.UserModel;

/**
 * @author Ratikanta Pradhan (ratikanta@sdrc.co.in)
 * Will help is getting user info from session
 */
@Component
public class TokenInfoExtracter {

	@Autowired(required = false)
	private TokenStore tokenStore;

	public Map<String, Object> tokenInfo(OAuth2Authentication auth) {

		OAuth2AuthenticationDetails details = (OAuth2AuthenticationDetails) auth.getDetails();
		OAuth2AccessToken accessToken = tokenStore.readAccessToken(details.getTokenValue());
		return accessToken.getAdditionalInformation();

	}

	/*
	 * Extracting the user info from JWT token and setting it to UserModel
	 * Object
	 */
	@SuppressWarnings("unchecked")
	public UserModel getUserModelInfo(OAuth2Authentication auth) {

		Map<String, Object> tokenInfoMap = tokenInfo(auth);
		
//		for (Map.Entry<String,Object> entry : tokenInfoMap.entrySet())  
//            System.out.println("Key = " + entry.getKey() + 
//                             ", Value = " + entry.getValue());
		
		

		UserModel user = new UserModel();
//		user.setDesignationNames((List<String>) tokenInfoMap.get("designationNames"));
//		user.setDesignationIds((List<String>) tokenInfoMap.get("designationIds"));
//		user.setAuthorities((List<String>) tokenInfoMap.get("authorities"));
		user.setId((Integer)tokenInfoMap.get("userId"));
	
		return user;
	}
}
