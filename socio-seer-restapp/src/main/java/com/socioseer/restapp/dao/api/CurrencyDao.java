package com.socioseer.restapp.dao.api;

import org.springframework.data.repository.PagingAndSortingRepository;
import com.socioseer.common.domain.model.Currency;

/**
 * <h3>Currency Dao</h3>
 * @author  OrangeMantra
 * @since   JDK 1.8
 * @version 1.0
 *
 */
public interface CurrencyDao extends PagingAndSortingRepository<Currency, String> {

}
