package com.socioseer.restapp.service.api;

import java.util.List;

import org.springframework.data.domain.Pageable;

import com.socioseer.common.domain.model.Event;
import com.socioseer.common.dto.Filter;

/**
 * <h3>Event Service</h3>
 * @author  OrangeMantra
 * @since   JDK 1.8
 * @version 1.0
 *
 */
public interface EventService extends CrudApi<Event> {

	/**
	 * <b>Delete Event By id and deletedBy</b>
	 * @param id			eventId
	 * @param updatedBy
	 */
	void delete(String id, String updatedBy);

	/**
	 * <b>Get All Events</b>
	 * @param pageable
	 * @param filters
	 * @return			returns Event list
	 */
	List<Event> getAll(Pageable pageable, List<Filter> filters);

}
