package com.socioseer.restapp.service.api;

import java.util.List;
import java.util.Optional;

import org.springframework.web.multipart.MultipartFile;

import com.socioseer.common.domain.model.Brand;

/**
 * <h3>Brand Services</h3>
 * @author  OrangeMantra
 * @since   JDK 1.8
 * @version 1.0
 *
 */
public interface BrandService extends CrudApi<Brand> {

  /**
   * <b>Get brands list of client</b> 	
   * @param clientId	
   * @param status		brand status as integer
   * @return			returns brands list
   */
  Optional<List<Brand>> getBrandsByClientId(String clientId, int status);

  /**
   * <b>Get Brand by clientId and brandName </b>
   * @param clientId
   * @param brandName		
   * @return			returns Brand
   */
  Optional<Brand> getBrandsByClientAndBrandName(String clientId, String brandName);

  /**
   * <b>Save Brand</b>
   * @param brand	Brand Object
   * @param logo	multipart image file of brand logo
   * @return		returns Brand
   */
  Brand save(Brand brand, MultipartFile logo);

  /**
   * <b>Update Brand</b>
   * @param brandId	
   * @param brand	Brand Object
   * @param logo	multipart image file of brand logo
   * @return		returns Brand
   */
  Brand update(String brandId, Brand brand, MultipartFile logo);

  /**
   * <b>Delete Brand by brandId</b>
   * @param id			brandId
   * @param updatedBy	
   */
  void delete(String id, String updatedBy);
  
  /**
   * <b>Get brands list of client by clientId</b>
   * @param clientId
   * @return			returns brands list
   */
  List<Brand> getBrandsByClientId(String clientId);

}
