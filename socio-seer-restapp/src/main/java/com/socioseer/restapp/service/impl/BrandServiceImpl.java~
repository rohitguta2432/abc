package com.socioseer.restapp.service.impl;

import java.io.File;
import java.util.List;
import java.util.Optional;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

import org.apache.commons.lang.WordUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import com.socioseer.common.constants.SocioSeerConstant;
import com.socioseer.common.constants.StatusConstants;
import com.socioseer.common.constants.ValidationConstants;
import com.socioseer.common.domain.User;
import com.socioseer.common.domain.model.Brand;
import com.socioseer.common.domain.model.Client;
import com.socioseer.common.exception.SocioSeerException;
import com.socioseer.restapp.dao.api.BrandDao;
import com.socioseer.restapp.exception.ValidationException;
import com.socioseer.restapp.service.api.BrandService;
import com.socioseer.restapp.service.api.ClientService;
import com.socioseer.restapp.service.util.DateUtil;
import com.socioseer.restapp.service.util.FileUtility;
import com.socioseer.restapp.util.UrlUtil;

/**
 * <h3>BrandService Implementation</h3>
 * @author  OrangeMantra
 * @since   JDK 1.8
 * @version 1.0
 *
 */
@Slf4j
@Service
public class BrandServiceImpl implements BrandService {

  @Autowired
  private BrandDao brandDao;

  @Autowired
  private ClientService clientService;

  @Autowired
  private FileUtility fileUtility;

  @Autowired
  private UrlUtil urlUtil;

  private final String folderLocation = SocioSeerConstant.FOLDER_BRAND;

  /**
   * <b>Save Brand</b>
   * @param		brand
   * @return	returns Brand
   */
  @Override
  public Brand save(@NonNull Brand brand) {
	  brand.setName(convertTitleCase(brand.getName()));
	  isBrandExistedForClient(brand);
    try {
      brand.setCreatedDate(DateUtil.getCurrentTimeInMilliseconds());
      brand.setUpdatedDate(brand.getCreatedDate());
      return brandDao.save(brand);
    } catch (Exception e) {
      String message =
          String.format("Error while saving brand for client name : %s and id : %s", brand.getClientName(),brand.getClientId());
      log.error(message, e);
      throw new SocioSeerException(message);
    }
  }

  /**
   * <b>Save Brand</b>
   * @param		brand	object	
   * @param		logo	multipart image file for brand logo
   * @return	returns Brand
   */
  @Override
  public Brand save(@NonNull Brand brand, MultipartFile logo) {
    validateBrand(brand);
    canCreateMoreBrands(brand);
    String fileName = null;
    try {
      if (!ObjectUtils.isEmpty(logo) && !logo.isEmpty()) {
        fileName = fileUtility.getFileName(logo.getOriginalFilename());
        fileUtility.saveFile(logo, SocioSeerConstant.FOLDER_BRAND, brand.getClientId(), fileName);
        brand.setLogoName(logo.getOriginalFilename());
        brand.setHashedLogoName(fileName);
      }
      return save(brand);
    } catch (Exception e) {
      fileUtility.deleteFile(SocioSeerConstant.FOLDER_BRAND, brand.getClientId(), fileName);
      throw e;
    }
  }

  /**
   * <b>Update Brand</b>
   * @param		brandId
   * @param		brand
   * @return	returns brand
   */
  @Override
  public Brand update(@NonNull String brandId, @NonNull Brand brand) {
    validateBrand(brand);
    Brand existingBrand = brandDao.findOne(brandId);
    if (ObjectUtils.isEmpty(existingBrand)) {
      String message = String.format("No brand found with brand id : %s", brandId);
      log.info(message);
      throw new IllegalArgumentException(message);
    }

    try {
      brand.setId(brandId);
      brand.setCreatedDate(existingBrand.getCreatedDate());
      brand.setUpdatedDate(DateUtil.getCurrentTimeInMilliseconds());
      return brandDao.save(brand);
    } catch (Exception e) {
      String message = String.format("Error while updating brand with id : %s for client id : %s",
          brandId, brand.getClientId());
      log.error(message, e);
      throw new SocioSeerException(message);
    }
  }

  /**
   * <b>Update Brand</b>
   * @param		brandId
   * @param		brand	Brand Object
   * @param		logo	multipart image file for brand logo
   * @return	returns Brand
   */
  @Override
  public Brand update(@NonNull String brandId, Brand brand, MultipartFile logo) {
	  
	  isBrandExistedForClient(brand);
	  Brand existingBrand = brandDao.findOne(brandId);
	    if (ObjectUtils.isEmpty(existingBrand)) {
	      String message = String.format("No brand found with brand id : %s", brandId);
	      log.info(message);
	      throw new IllegalArgumentException(message);
	    }
	 String hashedFileName = null;
    String existedClientId = existingBrand.getClientId();
    String existedHashedLogoName = existingBrand.getHashedLogoName();
    if (!StringUtils.isEmpty(brand.getClientId()))
      validateClient(brand, existingBrand);
    if (!ObjectUtils.isEmpty(logo) && !logo.isEmpty()) {
      if (!existingBrand.getLogoName().equalsIgnoreCase(logo.getOriginalFilename())) {
        fileUtility.deleteFile(SocioSeerConstant.FOLDER_BRAND, existedClientId,
            existedHashedLogoName);
        hashedFileName = fileUtility.getFileName(logo.getOriginalFilename());
        fileUtility.saveFile(logo, SocioSeerConstant.FOLDER_BRAND, existingBrand.getClientId(),
            hashedFileName);
        existingBrand.setLogoName(logo.getOriginalFilename());
        existingBrand.setHashedLogoName(hashedFileName);
      }
    }

    try {
      if (!ObjectUtils.isEmpty(brand))
        updateObject(brand, existingBrand);
      return brandDao.save(existingBrand);
    } catch (Exception e) {
      fileUtility.deleteFile(brand.getClientId(), SocioSeerConstant.FOLDER_BRAND, hashedFileName);
      String message = String.format("Error while updating brand with id : %s for client id : %s",
          brandId, brand.getClientId());
      log.error(message, e);
      throw new SocioSeerException(message);
    }
  }

  /**
   * <b>Check Brand exist for client or not</b>
   * @param		brand
   *
   */	
  private void isBrandExistedForClient(Brand brand){
	  Optional<Brand> existingBrandForClient =
	            getBrandsByClientAndBrandName(brand.getClientId(), brand.getName());
	        if (existingBrandForClient.isPresent() && existingBrandForClient.get().getStatus() != 3) {
	          String message = String.format("A brand with name : %s is already present for this client",
	              brand.getName());
	          log.info(message);
	          throw new IllegalArgumentException(message);
	        }
  }
  
  /**
   * <b>Get Brand by id</b>
   * @param		brandId
   * @return	returns Brand
   */
  @Override
  public Brand get(@NonNull String brandId) {

    try {
      Brand brand = brandDao.findOne(brandId);
      if (!ObjectUtils.isEmpty(brand) && !StringUtils.isEmpty(brand.getHashedLogoName()))
        brand.setLogoName(urlUtil.getUrl(folderLocation + File.separator + brand.getClientId(),
            brand.getHashedLogoName()));
      return brand;
    } catch (Exception e) {
      String message = String.format("Error while fetching brand with id : %s", brandId);
      log.error(message, e);
      throw new SocioSeerException(message);
    }
  }

  /**
   * <b>Get Brand list by clientId and status</b>
   * @param		clientId
   * @param		status
   * @return	returns Brand list
   */
  @Override
  public Optional<List<Brand>> getBrandsByClientId(@NonNull String clientId, int status) {

    try {
      Optional<List<Brand>> brands = brandDao.findAllByClientIdAndStatus(clientId,
          status <= 0 ? StatusConstants.ENABLED : status);
      setBrandUrl(brands);
      return brands;
    } catch (Exception e) {
      String message = String.format(
          "Error while fetching brands for client id : %s with status : %d", clientId, status);
      log.error(message, e);
      throw new SocioSeerException(message);
    }
  }

  /**
   * <b>Get Brand by clientId and brandName</b>
   * @param		clientId
   * @param		brandName
   * @return	returns Brand
   */
  @Override
  public Optional<Brand> getBrandsByClientAndBrandName(@NonNull String clientId,
      @NonNull String brandName) {

    try {
      Optional<Brand> brand = brandDao.findOneByClientIdAndName(clientId, brandName);
      if (brand.isPresent())
        brand.get()
            .setLogoName(urlUtil.getUrl(folderLocation + File.separator + brand.get().getClientId(),
                brand.get().getHashedLogoName()));
      return brand;
    } catch (Exception e) {
      String message = String.format(
          "Error while fetching brand with client id : %s and brand name : %s", clientId, brandName);
      log.error(message, e);
      throw new SocioSeerException(message);
    }
  }

  /**
   * <b>Validate Brand</b>
   * @param brand
   */
  private void validateBrand(Brand brand) {

    if (StringUtils.isEmpty(brand.getName())) {
      log.info("Brand name cannot be empty/null.");
      throw new IllegalArgumentException("Brand name cannot be empty/null.");
    }

    if (StringUtils.isEmpty(brand.getClientId())) {
      log.info("Client id cannot be empty/null.");
      throw new IllegalArgumentException("Client id cannot be empty/null.");
    }

    if (StringUtils.isEmpty(brand.getCreatedBy())) {
      log.info("Created by user id cannot be empty/null.");
      throw new IllegalArgumentException("Created by user id cannot be empty/null.");
    }
  }

  /**
   * <b>Validate Brand limit</b>
   * @param brand
   */
  private void canCreateMoreBrands(Brand brand) {
    Client client = clientService.get(brand.getClientId());
    if (ObjectUtils.isEmpty(client)) {
      String message = String.format("Client not found by client id : %s", brand.getClientId());
      log.info(message);
      throw new IllegalArgumentException(message);
    }
    List<Brand> brands = getBrandsByClientId(brand.getClientId());
    if(!CollectionUtils.isEmpty(brands)){
    int totalBrandCount =(int)  brands.stream().filter(brn ->brn.getStatus()!=3).count();
    if (++totalBrandCount > client.getNoOfBrands()) {
      String message = String.format("Cannot create more than %d brands for client : %s",
          client.getNoOfBrands(), client.getClientName());
      log.info(message);
      throw new ValidationException(message, ValidationConstants.NO_MORE_BRAND);
    }
    }
  }

  /**
   * <b>Delete Brand</b>
   * @param		id
   * @param		updatedBy
   */
  @Override
  public void delete(String id, String updatedBy) {

    try {
      Brand brand = brandDao.findOne(id);
      validateBrandDelete(brand, updatedBy);
      brand.setUpdatedBy(updatedBy);
      brand.setStatus(StatusConstants.DELETED);
      brand.setUpdatedDate(DateUtil.getCurrentTimeInMilliseconds());
      brand = brandDao.save(brand);
      String message = String.format("Brand deleted by brand id %s", updatedBy);
      log.info(message);
    } catch (Exception e) {
      String message = String.format("Error while fetching brand by brand id : %s", id);
      log.error(message, e);
      throw new SocioSeerException(message);
    }
  }

  /**
   * <b>Validate Brand for Delete</b>
   * @param brand
   * @param id
   */
  private void validateBrandDelete(Brand brand, String id) {
    if (ObjectUtils.isEmpty(brand)) {
      String message = String.format("Error brand not found by brand id : %s", id);
      log.info(message);
      throw new IllegalArgumentException(message);
    }
    if (brand.getStatus() == StatusConstants.DELETED) {
      String message = String.format("Error role already deleted");
      log.info(message);
      throw new IllegalArgumentException(message);
    }
  }

  /**
   * <b>Validate Client</b>
   * @param brand
   * @param existedbrand
   */
  private void validateClient(Brand brand, Brand existedbrand) {
    if (StringUtils.isEmpty(brand.getClientId())) {
      String message = String.format("Error client id not found");
      log.info(message);
      throw new IllegalArgumentException(message);
    }
    Client client = clientService.get(brand.getClientId());
    if (ObjectUtils.isEmpty(client)) {
      String message = String.format("Error client id %s not existed", brand.getClientId());
      log.info(message);
      throw new IllegalArgumentException(message);
    }

    existedbrand.setClientId(brand.getClientId());

  }

  /**
   * <b>Update Brand with new Data</b>
   * @param brand
   * @param existedbrand
   */
  private void updateObject(Brand brand, Brand existedbrand) {

    if (!StringUtils.isEmpty(brand.getClientName())) {
      existedbrand.setClientName(brand.getClientName());
    }

    if (!StringUtils.isEmpty(brand.getName())) {
      existedbrand.setName(brand.getName());
    }

    if (brand.getStatus() < 0) {
      existedbrand.setStatus(brand.getStatus());
    }

    if (StringUtils.isEmpty(brand.getUpdatedBy())) {
      String message = String.format("Error updated by id %s not found",brand.getId());
      log.info(message);
      throw new IllegalArgumentException(message);
    }
    existedbrand.setUpdatedBy(brand.getUpdatedBy());
    existedbrand.setUpdatedDate(DateUtil.getCurrentTimeInMilliseconds());

  }

  /**
   * <b>Set Brand URL</b>
   * @param brands
   */
  private void setBrandUrl(Optional<List<Brand>> brands) {
    brands.get().forEach(brand -> {
      if (!StringUtils.isEmpty(brand.getHashedLogoName())) {
        brand.setLogoName(urlUtil.getUrl(folderLocation + File.separator + brand.getClientId(),
            brand.getHashedLogoName()));
      }
    });
  }

  /**
   * <b>Get Brand By clientId</b>
   * @param		clientId
   * @return 	returns Brand list
   */
  @Override
  public List<Brand> getBrandsByClientId(@NonNull String clientId) {

    try {
      return brandDao.getBrandsByClientId(clientId);
    } catch (Exception e) {
      String message = String.format("Error while fetching brand by client id : %s", clientId);
      log.error(message, e);
      throw new SocioSeerException(message);
    }
  }
  
  private String convertTitleCase(String word) {
		return WordUtils.capitalizeFully(word);
	}

}
