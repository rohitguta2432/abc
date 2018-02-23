package com.socioseer.restapp.service.impl;

import static com.socioseer.restapp.service.util.DateUtil.getCurrentTimeInMilliseconds;

import java.io.File;
import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.web.multipart.MultipartFile;
import com.google.common.collect.ImmutableMap;
import com.socioseer.common.constants.ModelConstants;
import com.socioseer.common.constants.StatusConstants;
import com.socioseer.common.domain.model.campaign.Media;
import com.socioseer.common.domain.model.campaign.enums.MediaType;
import com.socioseer.common.domain.model.request.MediaContent;
import com.socioseer.common.domain.model.request.MediaRequest;
import com.socioseer.common.dto.Filter;
import com.socioseer.common.exception.SocioSeerException;
import com.socioseer.restapp.dao.api.ClientDao;
import com.socioseer.restapp.dao.api.MediaDao;
import com.socioseer.restapp.service.api.MediaService;
import com.socioseer.restapp.service.util.DateUtil;
import com.socioseer.restapp.service.util.FileUtility;
import com.socioseer.restapp.service.util.MediaUtil;
import com.socioseer.restapp.service.util.QueryBuilder;
import com.socioseer.restapp.util.UrlUtil;

/**
 * <h3>MediaService Implementation</h3>
 * @author  OrangeMantra
 * @since   JDK 1.8
 * @version 1.0
 *
 */
@Slf4j
@Service
public class MediaServiceImpl implements MediaService {

	@Autowired
	private MediaDao mediaDao;

	@Autowired
	private ClientDao clientDao;

	@Value("${socio.seer.media.location}")
	private String mediaLocation;

	@Autowired
	private FileUtility fileUtility;

	@Autowired
	private MongoTemplate mongoTemplate;

	private final String mediaLocationFormat = "%s/%s/";
	private final String fileNameFormat = "%s.%s";

	@Autowired
	private UrlUtil urlUtil;

	/**
	 * <b>Save Media</b>
	 * @param media
	 * @return	returns Media
	 */
	@Override
	public Media save(@NonNull Media media) {
		try {
			validateMedia(media);
			long createdDate = Instant.now().getEpochSecond();
			media.setCreatedDate(createdDate);
			media.setUpdatedDate(createdDate);
			return mediaDao.save(media);
		} catch (Exception e) {
			String message = String.format("Error while saving media for client id : %s", media.getClientId());
			log.error(message, e);
			throw new SocioSeerException(message);
		}
	}

	/**
	 * <b>Save Media</b>
	 * @param mediaRequest
	 * @param contentList
	 * @return	returns Media
	 */
	@Override
	public List<Media> save(MediaRequest mediaRequest, MultipartFile[] contentList) {
		String uploadFolder = null;
		try {
			if (StringUtils.isEmpty(mediaRequest.getClientId())) {
				log.info("Client id can not be null/empty.");
				throw new IllegalArgumentException("Client id can not be null/empty.");
			}
			if (StringUtils.isEmpty(mediaRequest.getCreatedBy())) {
				log.info("Created by user id can not be null/empty.");
				throw new IllegalArgumentException("Created by user id can not be null/empty.");
			}

			uploadFolder = String.format(mediaLocationFormat, mediaLocation, mediaRequest.getClientId());
			if (!ArrayUtils.isEmpty(contentList)) {
				mediaRequest.setContentList(creatMediaContent(Arrays.asList(contentList), mediaRequest.getClientId()));
			}

			if (CollectionUtils.isEmpty(mediaRequest.getContentList())) {
				log.info("Content list can not be null/empty.");
				throw new IllegalArgumentException("Content list can not be null/empty.");
			}
			List<Media> mediaList = getMediaList(mediaRequest);
			return (List<Media>) mediaDao.save(mediaList);
		} catch (Exception e) {
			try {
				deleteMediaAttachments(mediaRequest.getContentList(), uploadFolder);
			} catch (IOException e1) {
				String message = String.format("Error while deleting media for client id : %s",
						mediaRequest.getClientId());
				log.error(message, e);
				throw new SocioSeerException(message);
			}
			String message = String.format("Error while saving media for client id : %s", mediaRequest.getClientId());
			log.error(message, e);
			throw new SocioSeerException(message);
		}
	}

	/**
	 * <b>Get Media list</b>
	 * @param mediaRequest
	 * @return	returns Media list
	 */
	private List<Media> getMediaList(MediaRequest mediaRequest) {
		List<Media> mediaList = new ArrayList<Media>();
		String clientId = mediaRequest.getClientId();
		List<String> keywords = mediaRequest.getKeywords();
		long createdDate = getCurrentTimeInMilliseconds();
		String createdBy = mediaRequest.getCreatedBy();
		String url = mediaRequest.getUrl();
		String onlineLinkUrl = mediaRequest.getOnlineLinkUrl();
		mediaList = mediaRequest.getContentList().stream()
				.map(content -> new Media(clientId, keywords, url, content.getMediaType(),
						content.getOriginalFileName(), content.getHashFileName(), StatusConstants.ACTIVE, createdDate,
						createdBy, createdDate, createdBy, onlineLinkUrl))
				.collect(Collectors.toList());
		return mediaList;
	}

	/**
	 * <b>Update Media</b>
	 * @param mediaId
	 * @param media
	 * @return	returns Media
	 */
	@Override
	public Media update(@NonNull String mediaId, Media media) {
		try {
			validateMedia(media);
			Media existingMedia = mediaDao.findOne(mediaId);
			if (ObjectUtils.isEmpty(existingMedia)) {
				String message = String.format("No media found to update with id : %s", media);
				log.info(message);
				throw new IllegalArgumentException(message);
			}
			media.setId(mediaId);
			media.setCreatedDate(existingMedia.getCreatedDate());
			media.setUpdatedDate(DateUtil.getCurrentTimeInMilliseconds());
			return mediaDao.save(media);
		} catch (Exception e) {
			String message = String.format("Error while updating media with id : %s", mediaId);
			log.error(message, e);
			throw new SocioSeerException(message);
		}
	}

	/**
	 * <b>Get Media by id</b>
	 * @param mediaId
	 * @return	returns Media
	 */
	@Override
	public Media get(@NonNull String mediaId) {
		try {
			return mediaDao.findOne(mediaId);
		} catch (Exception e) {
			String message = String.format("Error while fetching media by id : %s", mediaId);
			log.error(message, e);
			throw new SocioSeerException(message);
		}
	}

	/**
	 * <b>Get Media list by clientId</b>
	 * @param clientId
	 * @param filters
	 * @param pageRequest
	 * @return	returns Media list
	 */
	@Override
	public List<Media> getMediaByClientId(@NonNull String clientId, List<Filter> filters, Pageable pageRequest) {
		try {
			Query query = QueryBuilder.createQuery(filters, ImmutableMap.of(ModelConstants.CLIENT_ID, clientId),
					pageRequest);
			List<Media> medias = mongoTemplate.find(query, Media.class);
			if (!CollectionUtils.isEmpty(medias)) {
				setImageUrl(Optional.of(medias));
			}
			return medias;
		} catch (Exception e) {
			String message = String.format("Error while fetching media by client id : %s", clientId);
			log.error(message, e);
			throw new SocioSeerException(message);
		}
	}

	/**
	 * <b>Set image url</b>
	 * @param medias
	 */
	@Override
	public void setImageUrl(Optional<List<Media>> medias) {

		if (medias.isPresent()) {
			medias.get().forEach(media -> {
				media.setUrl(urlUtil.getUrl(media.getClientId() + File.separator + media.getMediaType(),
						media.getHashFileName()));
			});

		}

	}

	/**
	 * <b>Check Media exists or not</b>
	 * @param mediaId
	 * @return	returns boolean
	 */
	@Override
	public boolean exists(@NonNull String mediaId) {
		try {
			return mediaDao.exists(mediaId);
		} catch (Exception e) {
			String message = String.format("Error while fetching media by media id : %s", mediaId);
			log.error(message, e);
			throw new SocioSeerException(message);
		}
	}

	/**
	 * <b>Validate Media</b>
	 * @param media
	 */
	private void validateMedia(Media media) {

		if (StringUtils.isEmpty(media.getClientId())) {
			log.info("Client id can not be null/empty.");
			throw new IllegalArgumentException("Client id can not be null/empty.");
		}
		if (StringUtils.isEmpty(media.getCreatedBy())) {
			log.info("Created by user id can not be null/empty.");
			throw new IllegalArgumentException("Created by user id can not be null/empty.");
		}

		if (clientDao.findOne(media.getClientId()) == null) {
			String message = String.format("Client not found by client id %d", media.getClientId());
			log.info(message);
			throw new IllegalArgumentException(message);
		}

	}

	/**
	 * <b>Create Media Content</b>
	 * @param mediaFiles
	 * @param clientId
	 * @return returns MediaContent list
	 */
	private List<MediaContent> creatMediaContent(List<MultipartFile> mediaFiles, String clientId) {
		return mediaFiles.stream().map(media -> {
			return saveFile(media, clientId);
		}).collect(Collectors.toList());
	}

	/**
	 * <b>Save Media File</b>
	 * @param media
	 * @param clientId
	 * @return	returns MediaContent
	 */
	private MediaContent saveFile(MultipartFile media, String clientId) {
		String originalFileName = media.getOriginalFilename();
		MediaType mediaType = MediaUtil.getMediaType(originalFileName);
		if (ObjectUtils.isEmpty(mediaType)) {
			throw new SocioSeerException("Unknown Media Type " + media.getOriginalFilename());
		}
		String fileName = String.format(fileNameFormat, UUID.fromString(UUID.randomUUID().toString()).toString(),
				FilenameUtils.getExtension(originalFileName));

		fileUtility.saveFile(media, clientId, mediaType.name(), fileName);
		return new MediaContent(StringUtils.EMPTY, mediaType, originalFileName, fileName);
	}

	/**
	 * <b>Delete Media Attachments</b>
	 * @param mediaContentList
	 * @param uploadFolder
	 * @throws IOException
	 */
	private void deleteMediaAttachments(List<MediaContent> mediaContentList, String uploadFolder) throws IOException {

		if (StringUtils.isEmpty(uploadFolder) || CollectionUtils.isEmpty(mediaContentList)) {
			return;
		}
		File file;
		for (MediaContent mediaContent : mediaContentList) {
			file = new File(uploadFolder + File.separator + mediaContent.getMediaType().toString() + File.separator
					+ mediaContent.getHashFileName());
			if (!file.exists()) {
				continue;
			}
			FileUtils.forceDelete(file);
		}
	}

	/**
	 * <b>Delete Media by id and deletedBy</b>
	 * @param id
	 * @param updatedBy
	 */
	@Override
	public void delete(String id, String updatedBy) {

		try {
			Media media = mediaDao.findOne(id);
			validateMediaDelete(media, id);
			media.setStatus(StatusConstants.DELETED);
			media.setUpdatedBy(updatedBy);
			media.setUpdatedDate(DateUtil.getCurrentTimeInMilliseconds());
			media = mediaDao.save(media);
			String message = String.format("Media deleted by media id %s", id);
			log.info(message);
		} catch (Exception e) {
			String message = String.format("Error while fetching media by media id : %s", id);
			log.error(message, e);
			throw new SocioSeerException(message);
		}
	}

	/**
	 * <b>Validate Media Delete</b>
	 * @param media
	 * @param id
	 */
	private void validateMediaDelete(Media media, String id) {
		if (ObjectUtils.isEmpty(media)) {
			String message = String.format("Error media not found by media id : %s", id);
			log.info(message);
			throw new IllegalArgumentException(message);
		}
		if (media.getStatus() == StatusConstants.DELETED) {
			String message = String.format("Error role already deleted");
			log.info(message);
			throw new IllegalArgumentException(message);
		}
	}

	/**
	 * <b>Get All Media list</b>
	 * @param pageable
	 * @param filters
	 * @return	returns Media list
	 */
	@Override
	public List<Media> getAll(Pageable pageable, List<Filter> filters) {
		try {
			Query query = QueryBuilder.createQuery(filters, pageable);
			List<Media> medias = mongoTemplate.find(query, Media.class);
			Optional<List<Media>> mediasList = Optional.of(medias);
			if (mediasList.isPresent()) {
				setImageUrl(mediasList);
			}
			return mediasList.get();
		} catch (Exception e) {
			log.error("Error while fetching medias.", e);
			throw new SocioSeerException("Error while fetching medias");
		}
	}
}
