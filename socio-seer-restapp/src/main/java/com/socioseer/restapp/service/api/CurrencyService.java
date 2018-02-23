package com.socioseer.restapp.service.api;

import java.util.List;

import org.springframework.data.domain.Pageable;
import com.socioseer.common.domain.model.Currency;
import com.socioseer.common.dto.Filter;

/**
 * <h3>Currency Services</h3>
 * @author  OrangeMantra
 * @since   JDK 1.8
 * @version 1.0
 *
 */
public interface CurrencyService extends CrudApi<Currency>{
  
  /**
   * <b>Delete Currency by currencyId</b>
   * @param id			currencyId
   * @param updatedBy
   */
  void delete(String id, String updatedBy);

  /**
   * <b>Get All Currency list</b>
   * @param pageable
   * @param filters
   * @return			returns Currency list
   */
  List<Currency> getAllCurrencies(Pageable pageable, List<Filter> filters);

  /**
   * <b>Get Currency Count</b>
   * @return	integer data
   */
  int count();

}
