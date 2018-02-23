package com.socioseer.authentication.service.impl;

import lombok.NonNull;
import lombok.SneakyThrows;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.socioseer.authentication.repository.UserRepository;
import com.socioseer.authentication.service.api.UserService;
import com.socioseer.common.domain.User;

/**
 * <b>User Service Implementation</b>
 * @author OrangeMantra
 * @since JDK 1.8
 * @version 1.0
 *
 */
@Service
public class UserServiceImpl implements UserService {

	@Autowired
	private UserRepository userRepository;

	/**
	 * <b>Get User By email</b>
	 * @param email
	 * @return returns User
	 */
	@Override
	@SneakyThrows
	public User getUserByEmail(@NonNull String email) {
		return userRepository.findOneByEmail(email);
	}

	/**
	 * <b>Save User</b>
	 * @param user
	 * @return returns User
	 */
	@Override
	public User save(User user) {
		return userRepository.save(user);
	}
	
	/**
	 * <b>Get User by userId</b>
	 * @param userId
	 * @return returns User
	 */
	@Override
	public User get(@NonNull String userId) {
		return userRepository.findOne(userId);
	}
	
}
