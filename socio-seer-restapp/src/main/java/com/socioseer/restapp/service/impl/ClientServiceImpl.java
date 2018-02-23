package com.socioseer.restapp.service.impl;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

import org.apache.commons.lang.WordUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import com.socioseer.common.constants.SocioSeerConstant;
import com.socioseer.common.constants.StatusConstants;
import com.socioseer.common.domain.User;
import com.socioseer.common.domain.model.Client;
import com.socioseer.common.domain.model.Client.CompetitiorsDefinition;
import com.socioseer.common.domain.model.Country;
import com.socioseer.common.domain.model.Industry;
import com.socioseer.common.dto.Filter;
import com.socioseer.common.exception.SocioSeerException;
import com.socioseer.restapp.dao.api.ClientDao;
import com.socioseer.restapp.service.api.ClientService;
import com.socioseer.restapp.service.api.CountryService;
import com.socioseer.restapp.service.api.IndustryService;
import com.socioseer.restapp.service.api.UserService;
import com.socioseer.restapp.service.util.DateUtil;
import com.socioseer.restapp.service.util.FileUtility;
import com.socioseer.restapp.service.util.QueryBuilder;
import com.socioseer.restapp.util.UrlUtil;

/**
 * <h3>ClientService Implementation</h3>
 * @author  OrangeMantra
 * @since   JDK 1.8
 * @version 1.0
 *
 */
@Slf4j
@Service
public class ClientServiceImpl implements ClientService {

	@Autowired
	private ClientDao clientDao;

	@Autowired
	private FileUtility fileUtility;

	@Autowired
	MongoTemplate mongoTemplate;

	@Autowired
	UserService userService;

	@Autowired
	private UrlUtil urlUtil;

	@Autowired
	private IndustryService industryService;
	
	@Autowired
	private CountryService countryService;

	private final String folderLocation = SocioSeerConstant.CLIENT_ROOT_FOLDER + File.separatorChar
			+ SocioSeerConstant.CLIENT_FOLDER;

	/**
	 * <b>Save Client</b>
	 * @param	client
	 * @return	returns Client
	 */
	@Override
	public Client save(@NonNull Client client) {
		validateClient(client);
		try {
			client.setCreatedDate(DateUtil.getCurrentTimeInMilliseconds());
			client.setUpdatedDate(DateUtil.getCurrentTimeInMilliseconds());
			client.setClientName(WordUtils.capitalizeFully(client.getClientName()));
			return clientDao.save(client);
		} catch (Exception e) {
			String message = String.format("Error while saving client with  name : %s", client.getClientName());
			log.error(message, e);
			throw new SocioSeerException(message);
		}
	}

	/**
	   * <b>Save Client</b>
	   * @param client			Client Object
	   * @param profilePicture	client profile image
	   * @return				returns Client
	   */
	@Override
	public Client save(@NonNull Client client, MultipartFile profilePicture) {

		validateClient(client);
		String hashedFileName = null;
		try {
			if (!ObjectUtils.isEmpty(profilePicture) && !profilePicture.isEmpty()) {
				hashedFileName = fileUtility.getFileName(profilePicture.getOriginalFilename());
				fileUtility.saveFile(profilePicture, SocioSeerConstant.CLIENT_ROOT_FOLDER,
						SocioSeerConstant.CLIENT_FOLDER, hashedFileName);
				client.setProfileImageName(profilePicture.getOriginalFilename());
				client.setHashedProfileImageName(hashedFileName);
			}
			client.setCreatedDate(DateUtil.getCurrentTimeInMilliseconds());
			client.setUpdatedDate(DateUtil.getCurrentTimeInMilliseconds());
			client.setProfileImageUrl(urlUtil.getUrl(folderLocation, client.getHashedProfileImageName()));
			client.setClientName(WordUtils.capitalizeFully(client.getClientName()));
			return clientDao.save(client);
		} catch (Exception e) {
			fileUtility.deleteFile(SocioSeerConstant.CLIENT_ROOT_FOLDER, SocioSeerConstant.CLIENT_FOLDER,
					hashedFileName);
			throw e;
		}
	}

	/**
	 * <b>Update Client</b>
	 * @param	clientId
	 * @param	client
	 * @return	returns Client
	 */
	@Override
	public Client update(@NonNull String clientId, @NonNull Client client) {

		Client existingClient = clientDao.findOne(clientId);
		if (ObjectUtils.isEmpty(existingClient)) {
			String message = String.format("No client found with id : %s", clientId);
			log.info(message);
			throw new IllegalArgumentException(message);
		}
		updateObject(client, existingClient);
		existingClient.setClientName(WordUtils.capitalizeFully(existingClient.getClientName()));
		return clientDao.save(existingClient);
	}

	/**
	   * <b>Update Client</b>
	   * @param clientId			clientId
	   * @param client				client object
	   * @param profilePicture		client profile image
	   * @return					returns Client
	   */
	@Override
	public Client update(@NonNull String clientId, Client client, MultipartFile profilePicture) {

		String hashedFileName = null;
		try {
			Client existingClient = clientDao.findOne(clientId);
			if (ObjectUtils.isEmpty(existingClient)) {
				String message = String.format("No client found with id : %s", clientId);
				log.info(message);
				throw new IllegalArgumentException(message);
			}
			if (!ObjectUtils.isEmpty(profilePicture) && !profilePicture.isEmpty()
					&& !profilePicture.getOriginalFilename().equalsIgnoreCase(client.getProfileImageName())) {
				fileUtility.deleteFile(SocioSeerConstant.CLIENT_ROOT_FOLDER, SocioSeerConstant.CLIENT_FOLDER,
						existingClient.getHashedProfileImageName());
				hashedFileName = fileUtility.getFileName(profilePicture.getOriginalFilename());
				fileUtility.saveFile(profilePicture, SocioSeerConstant.CLIENT_ROOT_FOLDER,
						SocioSeerConstant.CLIENT_FOLDER, hashedFileName);
				existingClient.setProfileImageName(profilePicture.getOriginalFilename());
				existingClient.setHashedProfileImageName(hashedFileName);
			}
			

			if (!ObjectUtils.isEmpty(client)) {
				updateObject(client, existingClient);
			}
			existingClient.setClientName(WordUtils.capitalizeFully(existingClient.getClientName()));
			return clientDao.save(existingClient);
		} catch (Exception e) {
			fileUtility.deleteFile(SocioSeerConstant.CLIENT_ROOT_FOLDER, SocioSeerConstant.CLIENT_FOLDER,
					hashedFileName);
			throw e;
		}
	}
	
	/**
	 * <b>Get Client by clientId</b>
	 * @param	clientId
	 * @return	returns Client
	 */
	@Override
	public Client get(@NonNull String clientId) {
		try {
			Client client = clientDao.findOne(clientId);
			if (!ObjectUtils.isEmpty(client)) {
				if (!StringUtils.isEmpty(client.getHashedProfileImageName())) {
					client.setProfileImageUrl(urlUtil.getUrl(folderLocation, client.getHashedProfileImageName()));
				}

			}
			if(!StringUtils.isEmpty(client.getClientInformation().getCountry())){
			Country country = countryService.get(client.getClientInformation().getCountry());
				if(!ObjectUtils.isEmpty(country)){
					client.setWoeid(country.getWoeid());
				}
			}
			return client;
		} catch (Exception e) {
			String message = String.format("Error while fetching client by id: %s", clientId);
			log.error(message, e);
			throw new SocioSeerException(message);
		}
	}

	/**
	   * <b>Get All Clients</b>
	   * @param pageable
	   * @param filters
	   * @return			returns Client list
	   */
	@Override
	public List<Client> getAllClients(Pageable pageable, List<Filter> filters) {
		try {
			Query query = QueryBuilder.createQuery(filters, pageable);
			List<Client> clients = mongoTemplate.find(query, Client.class);
			clients.forEach(client ->{
			
				if(!ObjectUtils.isEmpty(client.getClientInformation())){
					if(!StringUtils.isEmpty(client.getClientInformation().getCountry())){
						Country country = countryService.get(client.getClientInformation().getCountry());
							if(!ObjectUtils.isEmpty(country)){
								client.setWoeid(country.getWoeid());
							}
						}
				}
			});
			return clients;
		} catch (Exception e) {
			log.error("Error while fetching clients.", e);
			throw new SocioSeerException("Error while fetching clients.");
		}
	}

	/**
	 * <b>Validate Client</b>
	 * @param client
	 */
	private void validateClient(Client client) {

		if (StringUtils.isEmpty(client.getClientName())) {
			log.info("Client name cannot be null/empty.");
			throw new IllegalArgumentException("Client name cannot be null/empty.");
		}
		
		client.setClientName(convertTitleCase(client.getClientName()));
		Client existedClient = clientDao.getByClientName(client.getClientName());
		if (!ObjectUtils.isEmpty(existedClient)) {
			String message = String.format("Client already existed by name %s", client.getClientName());
			log.info(message);
			throw new IllegalArgumentException(message);
		}
	}

	/**
	 * <b>Update Existing client with new client data</b>
	 * @param client
	 * @param existingClient
	 */
	private void updateObject(@NonNull Client client, Client existingClient) {

		if (client.getStatus() > 0)
			existingClient.setStatus(client.getStatus());

		if (client.getNoOfUsers() >= existingClient.getCountUsers()){
			if(client.getNoOfUsers() >= existingClient.getNoOfUsers()){
				existingClient.setNoOfUsers(client.getNoOfUsers());
			}
		}
		
		  existingClient.setCountUsers(client.getCountUsers());
		if (!StringUtils.isEmpty(client.getClientLogo()))
			existingClient.setClientLogo(client.getClientLogo());

		if (!StringUtils.isEmpty(client.getClientSocioSeerPlatformUrl()))
			existingClient.setClientSocioSeerPlatformUrl(client.getClientSocioSeerPlatformUrl());

		if (!StringUtils.isEmpty(client.getCreatedBy()))
			existingClient.setCreatedBy(client.getCreatedBy());

		if (!StringUtils.isEmpty(client.getLicenseType()))
			existingClient.setLicenseType(client.getLicenseType());

		if (client.getNoOfBrands() >= existingClient.getNoOfBrands())
			existingClient.setNoOfBrands(client.getNoOfBrands());
		
		if (!StringUtils.isEmpty(client.getSegment()))
			existingClient.setSegment(client.getSegment());

		if (!StringUtils.isEmpty(client.getSubSegment()))
			existingClient.setSubSegment(client.getSubSegment());

		if (client.getSubscriptionEndDate() > 0)
			existingClient.setSubscriptionEndDate(client.getSubscriptionEndDate());

		if (client.getSubscriptionStartDate() > 0)
			existingClient.setSubscriptionStartDate(client.getSubscriptionStartDate());

		if (!StringUtils.isEmpty(client.getUpdatedBy()))
			existingClient.setUpdatedBy(client.getUpdatedBy());

		if (!StringUtils.isEmpty(client.getWebUrl()))
			existingClient.setWebUrl(client.getWebUrl());

		existingClient.setUpdatedDate(DateUtil.getCurrentTimeInMilliseconds());

		updateIndustry(client, existingClient);
		updateClientInformation(client, existingClient);
		updateCompetitiorsDefinitions(client, existingClient);
	}

	/**
	 * 
	 * @param client
	 * @param existingClient
	 */
	private void updateIndustry(Client client, Client existingClient) {
		if (!ObjectUtils.isEmpty(client.getIndustry())) {
			if (!StringUtils.isEmpty(client.getIndustry().getId()))
				existingClient.getIndustry().setId(client.getIndustry().getId());

			if (!StringUtils.isEmpty(client.getIndustry().getIndustryName()))
				existingClient.getIndustry().setIndustryName(client.getIndustry().getIndustryName());

			if (!StringUtils.isEmpty(client.getIndustry().getCreatedBy()))
				existingClient.getIndustry().setCreatedBy(client.getIndustry().getCreatedBy());

			if (!StringUtils.isEmpty(client.getIndustry().getUpdatedBy()))
				existingClient.getIndustry().setUpdatedBy(client.getIndustry().getUpdatedBy());

			if (!StringUtils.isEmpty(client.getIndustry().getCreatedDate()))
				existingClient.getIndustry().setCreatedDate(client.getIndustry().getCreatedDate());

			if (!StringUtils.isEmpty(client.getIndustry().getUpdatedDate()))
				existingClient.getIndustry().setUpdatedDate(client.getIndustry().getUpdatedDate());

		}

	}

	/**
	 * 
	 * @param client
	 * @param existingClient
	 */
	private void updateClientInformation(Client client, Client existingClient) {
		if (!ObjectUtils.isEmpty(client.getClientInformation())) {

			if (!ObjectUtils.isEmpty(existingClient.getClientInformation())) {

				if (!StringUtils.isEmpty(client.getClientInformation().getAddress()))
					existingClient.getClientInformation().setAddress(client.getClientInformation().getAddress());

				if (!StringUtils.isEmpty(client.getClientInformation().getCity()))
					existingClient.getClientInformation().setCity(client.getClientInformation().getCity());

				if (!StringUtils.isEmpty(client.getClientInformation().getDesignation()))
					existingClient.getClientInformation()
							.setDesignation(client.getClientInformation().getDesignation());

				if (!StringUtils.isEmpty(client.getClientInformation().getEmail()))
					existingClient.getClientInformation().setEmail(client.getClientInformation().getEmail());

				if (!StringUtils.isEmpty(client.getClientInformation().getMobileNumber()))
					existingClient.getClientInformation()
							.setMobileNumber(client.getClientInformation().getMobileNumber());

				if (!StringUtils.isEmpty(client.getClientInformation().getName()))
					existingClient.getClientInformation().setName(client.getClientInformation().getName());

				if (!StringUtils.isEmpty(client.getClientInformation().getCountry()))
					existingClient.getClientInformation().setCountry(client.getClientInformation().getCountry());

				if (!StringUtils.isEmpty(client.getClientInformation().getState()))
					existingClient.getClientInformation().setState(client.getClientInformation().getState());

				if (!StringUtils.isEmpty(client.getClientInformation().getOfficeLandline()))
					existingClient.getClientInformation()
							.setOfficeLandline(client.getClientInformation().getOfficeLandline());
			} else {

				Client.ClientInformation clientInformation = new Client.ClientInformation();

				if (!StringUtils.isEmpty(client.getClientInformation().getAddress()))
					clientInformation.setAddress(client.getClientInformation().getAddress());

				if (!StringUtils.isEmpty(client.getClientInformation().getCity()))
					clientInformation.setCity(client.getClientInformation().getCity());

				if (!StringUtils.isEmpty(client.getClientInformation().getDesignation()))
					clientInformation.setDesignation(client.getClientInformation().getDesignation());

				if (!StringUtils.isEmpty(client.getClientInformation().getEmail()))
					clientInformation.setEmail(client.getClientInformation().getEmail());

				if (!StringUtils.isEmpty(client.getClientInformation().getMobileNumber()))
					clientInformation.setMobileNumber(client.getClientInformation().getMobileNumber());

				if (!StringUtils.isEmpty(client.getClientInformation().getName()))
					clientInformation.setName(client.getClientInformation().getName());

				if (!StringUtils.isEmpty(client.getClientInformation().getCountry()))
					clientInformation.setCountry(client.getClientInformation().getCountry());

				if (!StringUtils.isEmpty(client.getClientInformation().getState()))
					clientInformation.setState(client.getClientInformation().getState());

				if (!StringUtils.isEmpty((client.getClientInformation().getOfficeLandline())))
					clientInformation.setOfficeLandline(client.getClientInformation().getOfficeLandline());
				existingClient.setClientInformation(clientInformation);
			}

		}

	}

	/**
	 * 
	 * @param client
	 * @param existingClient
	 */
	private void updateCompetitiorsDefinitions(Client client, Client existingClient) {
		if (!CollectionUtils.isEmpty(client.getCompetitiorsDefinitions())) {
			List<CompetitiorsDefinition> competitiorsDefinitions = new ArrayList<CompetitiorsDefinition>();
			client.getCompetitiorsDefinitions().forEach(competitiorsDefinition -> {
				CompetitiorsDefinition competitior = new CompetitiorsDefinition();
				if (!StringUtils.isEmpty(competitiorsDefinition.getHandles()))
					competitior.setHandles(competitiorsDefinition.getHandles());
				if (!StringUtils.isEmpty(competitiorsDefinition.getKeywords()))
					competitior.setKeywords(competitiorsDefinition.getKeywords());
				if (!StringUtils.isEmpty(competitiorsDefinition.getName()))
					competitior.setName(competitiorsDefinition.getName());
				competitiorsDefinitions.add(competitior);
			});
			existingClient.setCompetitiorsDefinitions(competitiorsDefinitions);
		}

	}

	@Override
	public List<Client> getAllClients(Pageable pageable) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * <b>Delete Client by id</b>
	 * @param	id
	 * @param	updatedBy
	 */
	@Override
	public void delete(String id, String updatedBy) {

		try {
			Client client = clientDao.findOne(id);
			validateClientDelete(client, updatedBy);
			client.setStatus(StatusConstants.DELETED);
			client.setUpdatedBy(updatedBy);
			client.setUpdatedDate(DateUtil.getCurrentTimeInMilliseconds());
			client = clientDao.save(client);
			List<User> users = userService.getUsersByClientId(client.getId(), null, null);
			users.forEach(user ->{
				user.setStatus(StatusConstants.DELETED);
				userService.update(user.getId(), user);
			});
			
			String message = String.format("Client deleted by client id %s", id);
			log.info(message);
		} catch (Exception e) {
			String message = String.format("Error while fetching client by client id : %s", id);
			log.error(message, e);
			throw new SocioSeerException(message);
		}
	}

	/**
	 * 
	 * @param client
	 * @param id
	 */
	private void validateClientDelete(Client client, String id) {
		if (ObjectUtils.isEmpty(client)) {
			String message = String.format("Error client not found by client id : %s", id);
			log.info(message);
			throw new IllegalArgumentException(message);
		}
		if (client.getStatus() == StatusConstants.DELETED) {
			String message = String.format("Error client already deleted");
			log.info(message);
			throw new IllegalArgumentException(message);
		}
	}

	/**
	   * <b>Change Client status</b>
	   * @param id			clientId
	   * @param status		as Integer 	
	   * @param updatedBy
	   */
	@Override
	public void changeStatus(@NonNull String id, int status, @NonNull String updatedBy) {
		if (status < 0) {
			String message = String.format("Invalid status %s", status);
			log.info(message);
			throw new IllegalArgumentException(message);
		}
		try {
			Client client = clientDao.findOne(id);
			if (ObjectUtils.isEmpty(client)) {
				String message = String.format("Client not found by id %s", id);
				log.info(message);
				throw new IllegalArgumentException(message);
			}
			client.setStatus(status);
			client.setUpdatedBy(updatedBy);
			
			List<User> users = userService.getUsersByClientId(client.getId(), null, null);
			users.forEach(user ->{
				if(user.getStatus()!=StatusConstants.DELETED){
					user.setStatus(status);
				}
				userService.update(user.getId(), user);
			});
			clientDao.save(client);
		} catch (Exception e) {
			String message = String.format("Error while fetching client by id : %s", id);
			log.error(message, e);
			throw new SocioSeerException(message);
		}
	}

	private String convertTitleCase(String word) {
		return WordUtils.capitalizeFully(word);
	}
	
}
