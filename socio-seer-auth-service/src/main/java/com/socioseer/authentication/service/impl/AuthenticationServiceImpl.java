package com.socioseer.authentication.service.impl;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import lombok.NonNull;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import com.socioseer.authentication.exception.BadCredentialsException;
import com.socioseer.authentication.exception.UserAccountInactiveExcpetion;
import com.socioseer.authentication.producer.SocioSeerKafkaProducer;
import com.socioseer.authentication.repository.AuthenticationRepository;
import com.socioseer.authentication.service.api.AuthenticationService;
import com.socioseer.authentication.service.api.ForgotPasswordService;
import com.socioseer.authentication.service.api.UserService;
import com.socioseer.authentication.util.EncryptionUtil;
import com.socioseer.authentication.util.TokenUtils;
import com.socioseer.common.constants.SocioSeerConstant;
import com.socioseer.common.constants.StatusConstants;
import com.socioseer.common.domain.AuthenticationToken;
import com.socioseer.common.domain.EmailNotification;
import com.socioseer.common.domain.User;
import com.socioseer.common.domain.model.ForgotPassword;
import com.socioseer.common.exception.SocioSeerException;

/**
 * <b>Authentication Service Implementation</b>
 * 
 * @author OrangeMantra
 * @since JDK 1.8
 * @version 1.0
 *
 */
@Slf4j
@Service
public class AuthenticationServiceImpl implements AuthenticationService {

	@Autowired
	private AuthenticationRepository authenticationRepository;

	@Autowired
	private UserService userService;

	@Autowired
	private TokenUtils tokenUtils;

	@Autowired
	private EncryptionUtil encryptionUtil;

	@Autowired
	private ForgotPasswordService forgotPasswordService;

	@Autowired
	private SocioSeerKafkaProducer<EmailNotification> producer;

	@Value("${forgot.password.email.subject}")
	private String emailSubject;

	@Value("${forgot.password.email.from}")
	private String emailFrom;

	@Value("${forgot.password.email.message}")
	private String emailMessage;

	@Value("${forgot.password.url}")
	private String forgotPasswordUrl;

	@Value("${kafka.topic.emailNotification}")
	private String emailNotificationTopic;

	/**
	 * <b>Generate Token</b>
	 * 
	 * @param email
	 * @param password
	 * @param client
	 * @return returns AuthenticationToken
	 * 
	 */
	@Override
	@SneakyThrows(SocioSeerException.class)
	public AuthenticationToken generateToken(@NonNull String email, @NonNull String password, String client) {

		if (StringUtils.isEmpty(client)) {
			client = SocioSeerConstant.CLIENT_WEB;
		}
		if (!StringUtils.isEmpty(email)) {
			email = email.toLowerCase();
		}
		User existingUser = userService.getUserByEmail(email);

		if (existingUser == null) {
			String message = String.format("No used found with email : %s", email);
			log.info(message);
			throw new BadCredentialsException(message);
		}
		validateUser(password, existingUser);
		AuthenticationToken existingAuthenticationToken = authenticationRepository
				.findOneByUserId(existingUser.getId());
		if (existingAuthenticationToken != null) {
			if (!tokenUtils.isTokenValid(existingAuthenticationToken.getAuthToken())) {
				deleteAuthenticationTokenByUserId(existingUser.getId());
			} else {
				/*return existingAuthenticationToken;*/

				String message = String.format("User already logged-in.");
				log.info(message);
				throw new BadCredentialsException(message);
			}
		}

		long creationTime = System.currentTimeMillis();
		String authToken = tokenUtils.generateToken(email, creationTime);
		AuthenticationToken savedToken = authenticationRepository
				.save(new AuthenticationToken(existingUser.getId(), authToken, client, creationTime));
		log.info(String.format("Auth token generated for user with email : %s", email));
		return savedToken;
	}

	/**
	 * <b>Get AuthenticationToken by userId</b>
	 * 
	 * @param userId
	 * @return AuthenticationToken
	 */
	@Override
	@SneakyThrows(SocioSeerException.class)
	public AuthenticationToken getAuthenticationTokenByUserId(@NonNull String userId) {
		return authenticationRepository.findOneByUserId(userId);
	}

	/**
	 * <b>Delete AuthenticationToken by userId</b>
	 * 
	 * @param userId
	 * @return AuthenticationToken
	 */
	@Override
	public void deleteAuthenticationTokenByUserId(@NonNull String userId) {
		authenticationRepository.deleteByUserId(userId);
	}

	/**
	 * <b>Validate Token</b>
	 * 
	 * @return returns Map<String, Object>
	 */
	@Override
	public Map<String, Object> validateToken(@NonNull String authenticationToken) {

		Map<String, Object> dataMap = new HashMap<>();
		boolean isTokenValid = false;
		AuthenticationToken existingAuthToken = null;
		try {
			existingAuthToken = authenticationRepository.findOneByAuthToken(authenticationToken);
			if (existingAuthToken == null) {
				log.info("Invalid auth token : " + authenticationToken);
				throw new IllegalArgumentException("Invalid auth token : " + authenticationToken);
			}
			isTokenValid = tokenUtils.isTokenValid(authenticationToken);
			dataMap.put(SocioSeerConstant.KEY_USER_ID, existingAuthToken.getUserId());
		} catch (Exception e) {
			log.error("Authentication token has expired.");
		}
		dataMap.put(SocioSeerConstant.KEY_IS_AUTH_TOKEN_VALID, isTokenValid);
		return dataMap;
	}

	/**
	 * <b>Forgot Password</b>
	 * 
	 * @param email
	 */
	@Override
	public void forgotPassword(@NonNull String email) {
		ForgotPassword forgotPassword = forgotPasswordService.save(email);
		forgotPasswordUrl = forgotPasswordUrl.replace("~", "#!/");
		String url = String.format(forgotPasswordUrl, forgotPassword.getToken());
		String message = String.format(emailMessage, url);
		EmailNotification emailNotification = EmailNotification.builder().toList(Arrays.asList(email))
				.subject(emailSubject).from(emailFrom).message(message).build();
		producer.produce(emailNotificationTopic, emailNotification);
	}

	/**
	 * <b>Validate User</b>
	 * 
	 * @param password
	 * @param user
	 */
	private void validateUser(String password, User user) {

		if (user.getStatus() != StatusConstants.ACTIVE && user.getStatus() != StatusConstants.SUPER_ADMIN) {
			log.info("User account is not active.");
			throw new UserAccountInactiveExcpetion("User account is not active.");
		}

		if (!encryptionUtil.matchPassword(user.getPassword(), password)) {
			String msg = String.format("Invalid user credentials for user  with email : %s", user.getEmail());
			log.info(msg);
			throw new BadCredentialsException(msg);
		}
	}

	/**
	 * <b>Reset Password</b>
	 * 
	 * @param map
	 */
	@Override
	public void restPassword(Map<String, String> map) {

		try {
			if (CollectionUtils.isEmpty(map)) {
				String message = "Input data not empty/null";
				log.info(message);
				throw new IllegalArgumentException(message);

			}
			if (!map.get("password").equals(map.get("confirmPassword"))) {
				String message = "Password and confirm password not match";
				log.info(message);
				throw new IllegalArgumentException(message);
			}

			ForgotPassword forgotPassword = forgotPasswordService.getForgotPasswordByToken(map.get("token"));
			if (ObjectUtils.isEmpty(forgotPassword)) {
				String message = String.format("Forgot token not found by token id %s", map.get("token"));
				log.info(message);
				throw new IllegalArgumentException(message);
			}

			User user = userService.get(forgotPassword.getUserId());
			if (ObjectUtils.isEmpty(user)) {
				String message = String.format("User not found by id %s", forgotPassword.getUserId());
				log.info(message);
				throw new IllegalArgumentException(message);
			}
			user.setPassword(encryptionUtil.encode(map.get("password")));
			user.setUpdatedDate(System.currentTimeMillis());
			userService.save(user);
		} catch (Exception e) {
			log.error("Error while reset password.");
			throw new SocioSeerException("Error while reset password", e);
		}
	}

}
