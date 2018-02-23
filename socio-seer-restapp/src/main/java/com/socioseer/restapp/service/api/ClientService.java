package com.socioseer.restapp.service.api;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import com.socioseer.common.domain.model.Client;
import com.socioseer.common.dto.Filter;

/**
 * <h3>Client Services</h3>
 * @author  OrangeMantra
 * @since   JDK 1.8
 * @version 1.0
 *
 */
public interface ClientService extends CrudApi<Client> {

	/**
	   * <b>Get All Clients</b>
	   * @param pageable
	   * @param filters
	   * @return			returns Client list
	   */
  List<Client> getAllClients(Pageable pageable);

  /**
   * <b>Save Client</b>
   * @param client			Client Object
   * @param profilePicture	client profile image
   * @return				returns Client
   */
  Client save(Client client, MultipartFile profilePicture);

  /**
   * <b>Update Client</b>
   * @param clientId			clientId
   * @param client				client object
   * @param profilePicture		client profile image
   * @return					returns Client
   */
  Client update(String clientId, Client client, MultipartFile profilePicture);

  /**
   * <b>Get All Clients</b>
   * @param pageable
   * @param filters
   * @return			returns Client list
   */
  List<Client> getAllClients(Pageable pageable, List<Filter> filters);

  /**
   * <b>Delete Client by id</b>
   * @param id			clientId
   * @param updatedBy
   */
  void delete(String id, String updatedBy);

  /**
   * <b>Change Client status</b>
   * @param id			clientId
   * @param status		as Integer 	
   * @param updatedBy
   */
  void changeStatus(String id, int status, String updatedBy);

}
