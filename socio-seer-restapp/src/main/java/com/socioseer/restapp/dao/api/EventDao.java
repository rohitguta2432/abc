package com.socioseer.restapp.dao.api;

import java.util.List;

import org.springframework.data.repository.PagingAndSortingRepository;
import com.socioseer.common.domain.model.Event;

/**
 * <h3>Event Dao</h3>
 * @author  OrangeMantra
 * @since   JDK 1.8
 * @version 1.0
 *
 */
public interface EventDao extends PagingAndSortingRepository<Event, String> {
	
	/**
	 * 
	 * @param eventName
	 * @return		returns Event list
	 */
	List<Event> getByEventName(String eventName);

}
