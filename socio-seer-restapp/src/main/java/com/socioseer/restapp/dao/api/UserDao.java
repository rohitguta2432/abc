package com.socioseer.restapp.dao.api;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import com.socioseer.common.domain.User;

/**
 * Mixing Custom repository with {@link MongoRepository} can benefit us both of
 * the paradigms.
 *
 * @author mgarg
 */
@Repository
public interface UserDao extends PagingAndSortingRepository<User, String> {

	int countByClientId(String clientId);

	Optional<List<User>> findAllByClientId(String clientId, Pageable pageRequest);

	Optional<List<User>> getByBrandId(String brandId);

	Optional<List<User>> findAllByBrandId(String brandId, Pageable pageRequest);

	Optional<List<User>> getUserByclientIdAndStatus(String clientId, int active);

	User findOneByEmail(String email);
	
}
