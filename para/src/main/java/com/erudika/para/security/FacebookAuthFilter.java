/*
 * Copyright 2013-2014 Erudika. http://erudika.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * For issues and patches go to: https://github.com/erudika
 */
package com.erudika.para.security;

import com.eaio.uuid.UUID;
import com.erudika.para.core.User;
import com.erudika.para.utils.Config;
import com.erudika.para.utils.Utils;
import java.io.IOException;
import java.util.Arrays;
import java.util.Map;
import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;

/**
 * A filter that handles authentication requests to Facebook.
 * @author Alex Bogdanovski [alex@erudika.com]
 */
public class FacebookAuthFilter extends AbstractAuthenticationProcessingFilter {

	private static final Logger log = LoggerFactory.getLogger(FacebookAuthFilter.class);

	/**
	 * The default filter mapping
	 */
	public static final String FACEBOOK_ACTION = "facebook_auth";

	/**
	 * Default constructor.
	 * @param defaultFilterProcessesUrl the url of the filter
	 */
	public FacebookAuthFilter(String defaultFilterProcessesUrl) {
		super(defaultFilterProcessesUrl);
	}

	/**
	 * Handles an authentication request.
	 * @param request HTTP request
	 * @param response HTTP response
	 * @return an authentication object that contains the principal object if successful.
	 * @throws IOException ex
	 * @throws ServletException ex
	 */
	@Override
	public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {
		String requestURI = request.getRequestURI();
		Authentication userAuth = null;
		User user = new User();

		if (requestURI.endsWith(FACEBOOK_ACTION)) {
			//Facebook Connect Authentication
			String fbSig = request.getParameter("fbsig");
			String fbEmail = request.getParameter("fbemail");
			String fbName = request.getParameter("fbname");
			String fbID = verifiedFacebookID(fbSig);

			if (fbID != null) {
				//success!
				user.setIdentifier(Config.FB_PREFIX.concat(fbID));
				user = User.readUserForIdentifier(user);
				if (user == null) {
					//user is new
					user = new User();
					user.setEmail(StringUtils.isBlank(fbEmail) ? "email@domain.com" : fbEmail);
					user.setName(StringUtils.isBlank(fbName) ? "No Name" : fbName);
					user.setPassword(new UUID().toString());
					user.setIdentifier(Config.FB_PREFIX.concat(fbID));
					String id = user.create();
					if (id == null) {
						throw new AuthenticationServiceException("Authentication failed: cannot create new user.");
					}
				}
				userAuth = new UserAuthentication(user);
			}
		}

		if (userAuth == null || user == null || user.getIdentifier() == null) {
			throw new BadCredentialsException("Bad credentials.");
		} else if (!user.isEnabled()) {
			throw new LockedException("Account is locked.");
//		} else {
//			SecurityUtils.setAuthCookie(user, request, response);
		}
		return userAuth;
	}

	private String verifiedFacebookID(String fbSig) {
		if (!StringUtils.contains(fbSig, ".")) {
			return null;
		}
		String fbid = null;

		try {
			String[] parts = fbSig.split("\\.");
			byte[] sig = Utils.base64dec(parts[0]).getBytes();
			byte[] encodedJSON = parts[1].getBytes();	// careful, we compute against the base64 encoded version
			String decodedJSON = Utils.base64dec(parts[1]);
			Map<String, String> root = Utils.getJsonReader(Map.class).readValue(decodedJSON);

			if (StringUtils.contains(decodedJSON, "HMAC-SHA256")) {
				SecretKey secret = new SecretKeySpec(Config.FB_SECRET.getBytes(), "HMACSHA256");
				Mac mac = Mac.getInstance("HMACSHA256");
				mac.init(secret);
				byte[] digested = mac.doFinal(encodedJSON);
				if (Arrays.equals(sig, digested)) {
					fbid = root.get("user_id");
				}
			}
		} catch (Exception ex) {
			log.warn("Failed to decode FB signature: {0}", ex);
		}

		return fbid;
	}

}
