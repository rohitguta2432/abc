package com.socioseer.restapp.web;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.socioseer.common.constants.StatusConstants;
import com.socioseer.common.domain.model.Brand;
import com.socioseer.common.dto.Response;
import com.socioseer.restapp.service.api.BrandService;
import com.socioseer.restapp.util.JsonParser;

/**
 * <h3>This Controller Manage the All API of Brand.</h3>
 * @author  OrangeMantra
 * @since   JDK 1.8
 * @version 1.0
 *
 */
@RestController
@RequestMapping(value = "brand", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
public class BrandController {

  @Autowired
  private BrandService brandService;

  /**
   * <b>Save brand</b>
   * @param brandString	brand details json
   * @param logo		multipart image file of brand logo
   * @return			returns Brand
   * <b></br>URL FOR API :</b>	/api/admin/brand   			
   */
  @RequestMapping(method = RequestMethod.POST)
  public ResponseEntity<Response<Brand>> saveBrand(
      @RequestParam(value = "brand") String brandString, @RequestParam(value = "logo",
          required = false) MultipartFile logo) {
    return new ResponseEntity<Response<Brand>>(new Response<Brand>(HttpStatus.OK.value(),
        "Brand saved successfully.", brandService.save(
            JsonParser.toObject(brandString, Brand.class), logo)), HttpStatus.OK);
  }

  /**
   * <b>Update Brand </b>
   * @param brandId			
   * @param brandString		brand details json
   * @param logo			multipart image file of brand logo
   * @return				returns Brand
   * <b></br>URL FOR API :</b>	/api/admin/brand/{brandId}   
   */
  @RequestMapping(value = "{brandId}", method = RequestMethod.PUT)
  public ResponseEntity<Response<Brand>> updateBrand(@PathVariable("brandId") String brandId,
      @RequestPart(value = "brand" ,required = false) String brandString, @RequestParam(value = "logo",
          required = false) MultipartFile logo) {
    if (StringUtils.isEmpty(brandString)) {
      brandString = "{}";
    }
    return new ResponseEntity<Response<Brand>>(new Response<Brand>(HttpStatus.OK.value(),
        "Brand updated successfully.", brandService.update(brandId,
            JsonParser.toObject(brandString, Brand.class), logo)), HttpStatus.OK);
  }
  
  /**
   * <b>Get Brand </b>
   * @param brandId		
   * @return			returns Brand
   * <b></br>URL FOR API :</b>	/api/admin/brand/{brandId}   
   */
  @RequestMapping(value = "{brandId}", method = RequestMethod.GET)
  public ResponseEntity<Response<Brand>> getBrandById(@PathVariable("brandId") String brandId) {
    return new ResponseEntity<Response<Brand>>(new Response<Brand>(HttpStatus.OK.value(),
        "Brand fetched successfully.", brandService.get(brandId)), HttpStatus.OK);
  }

  /**
   * <b>Get brands list of client and brand status </b>
   * @param clientId		
   * @param status		brand status as integer
   * @return			returns brands list
   * <b></br>URL FOR API :</b>	/api/admin/brand/client/{clientId}   
   */
  @RequestMapping(value = "client/{clientId}", method = RequestMethod.GET)
  public ResponseEntity<Response<List<Brand>>> getBrandsByClientId(
      @PathVariable("clientId") String clientId, @RequestParam(value = "status",
          defaultValue = StatusConstants.ENABLED + "") int status) {
    return new ResponseEntity<Response<List<Brand>>>(new Response<List<Brand>>(
        HttpStatus.OK.value(), "Brands fetched successfully.", brandService.getBrandsByClientId(
            clientId, status).get()), HttpStatus.OK);
  }
  
  /**
   * <b>Delete the brand </b>
   * @param id			brandId
   * @param updatedBy
   * @return			returns boolean data
   * <b></br>URL FOR API :</b>	/api/admin/brand/delete/{id}/{updatedBy}  
   */
  @RequestMapping(value = "delete/{id}/{updatedBy}", method = RequestMethod.DELETE)
  public ResponseEntity<Response<Boolean>> delete(@PathVariable("id") String id,
      @PathVariable("updatedBy") String updatedBy) {
    brandService.delete(id, updatedBy);
    return new ResponseEntity<>(new Response<>(HttpStatus.OK.value(), "Brand deleted successfully.",
        true), HttpStatus.OK);
  }

}
