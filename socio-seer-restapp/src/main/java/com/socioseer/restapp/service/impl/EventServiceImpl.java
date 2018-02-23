package com.socioseer.restapp.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import com.socioseer.common.constants.StatusConstants;
import com.socioseer.common.domain.SocialHandler;
import com.socioseer.common.domain.model.Event;
import com.socioseer.common.dto.Filter;
import com.socioseer.common.exception.SocioSeerException;
import com.socioseer.restapp.dao.api.EventDao;
import com.socioseer.restapp.service.api.EventService;
import com.socioseer.restapp.service.util.DateUtil;
import com.socioseer.restapp.service.util.QueryBuilder;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

/**
 * <h3>EventService Implementation</h3>
 * @author  OrangeMantra
 * @since   JDK 1.8
 * @version 1.0
 *
 */
@Slf4j
@Service
public class EventServiceImpl implements EventService {

	@Autowired
	private EventDao eventDao;

	@Autowired
	private MongoTemplate mongoTemplate;

	/**
	 * <b>Save Event</b>
	 * @param entity
	 * @return	returns Event
	 */
	@Override
	public Event save(Event entity) {
		try {
			validateEvent(entity);
			entity.setCreatedDate(DateUtil.getCurrentTimeInMilliseconds());
			entity.setUpdatedDate(DateUtil.getCurrentTimeInMilliseconds());
			return eventDao.save(entity);
		} catch (Exception e) {
			String message = "Error while saving event";
			log.error(message);
			throw new SocioSeerException(message);
		}
	}

	/**
	 * <b>Update Event</b>
	 * @param id
	 * @param entity
	 * @return	returns Event
	 */
	@Override
	public Event update(@NonNull String id, Event entity) {

		try {
			validateEvent(entity);
			Event existingEvent = eventDao.findOne(id);
			if (ObjectUtils.isEmpty(existingEvent)) {
				String message = String.format("Event not found by id %s", id);
				log.info(message);
				throw new IllegalArgumentException(message);
			}
			entity.setUpdatedDate(DateUtil.getCurrentTimeInMilliseconds());
			entity.setId(existingEvent.getId());
			return eventDao.save(entity);
		} catch (Exception e) {
			String message = "Error while updating event";
			log.error(message);
			throw new SocioSeerException(message);
		}
	}

	/**
	 * <b>Get Event by id</b>
	 * @param id
	 * @return	returns Event
	 */
	@Override
	public Event get(@NonNull String id) {
		try {
			return eventDao.findOne(id);
		} catch (Exception e) {
			String message = String.format("Error while fetching event by id %s", id);
			log.error(message);
			throw new SocioSeerException(message);
		}
	}

	/**
	 * <b>Get All Events</b>
	 * @param pageable
	 * @param filters
	 * @return			returns Event list
	 */
	@Override
	public List<Event> getAll(Pageable pageable, List<Filter> filters) {
		try {
			Query query = QueryBuilder.createQuery(filters, pageable);
			return mongoTemplate.find(query, Event.class);
		} catch (Exception e) {
			log.error("Error while fetching event.", e);
			throw new SocioSeerException("Error while fetching event");
		}
	}

	/**
	 * <b>Delete Event By id and deletedBy</b>
	 * @param id			eventId
	 * @param updatedBy
	 */
	@Override
	public void delete(@NonNull String id, @NonNull String updatedBy) {
		try {
			Event existingEvent = eventDao.findOne(id);
			if (ObjectUtils.isEmpty(existingEvent)) {
				String message = String.format("Event not found by id %s", id);
				log.info(message);
				throw new IllegalArgumentException(message);
			}
			existingEvent.setUpdatedDate(DateUtil.getCurrentTimeInMilliseconds());
			existingEvent.setUpdatedBy(updatedBy);
			existingEvent.setStatus(StatusConstants.DELETED);
			eventDao.save(existingEvent);
		} catch (Exception e) {
			String message = String.format("Error while deleting event by id %s", id);
			log.error(message);
			throw new SocioSeerException(message);
		}
	}

	/**
	 * <b>Validate Event</b>
	 * @param event
	 */
	private void validateEvent(Event event) {

		if (StringUtils.isEmpty(event.getEventName())) {
			String message = "Event name can not be null/empty";
			log.info(message);
			throw new IllegalArgumentException(message);
		}

		if (StringUtils.isEmpty(event.getCreatedBy())) {
			String message = "Created by can not be null/empty";
			log.info(message);
			throw new IllegalArgumentException(message);
		}

		if (StringUtils.isEmpty(event.getDescription())) {
			String message = "Description can not be null/empty";
			log.info(message);
			throw new IllegalArgumentException(message);
		}

		if (StringUtils.isEmpty(event.getStartDate())) {
			String message = "Start date can not be null/empty";
			log.info(message);
			throw new IllegalArgumentException(message);
		}
	}

}
