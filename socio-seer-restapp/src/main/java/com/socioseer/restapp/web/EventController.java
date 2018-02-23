package com.socioseer.restapp.web;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.socioseer.common.domain.model.Event;
import com.socioseer.common.dto.Response;
import com.socioseer.restapp.service.api.EventService;
import com.socioseer.restapp.util.QueryParser;

/**
 * <h3>This Controller Manage the All API of Event.</h3>
 * @author  OrangeMantra
 * @since   JDK 1.8
 * @version 1.0
 *
 */
@RestController
@RequestMapping(value = "event", produces = MediaType.APPLICATION_JSON_VALUE)
public class EventController {

	@Autowired
	private EventService eventService;

	/**
	 * <b>Save Event</b>
	 * @param event		event json
	 * @return			returns Event
	 * <b></br>URL FOR API :</b>	/api/admin/event
	 */
	@RequestMapping(method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Response<Event>> save(@RequestBody Event event) {
		return new ResponseEntity<>(
				new Response<>(HttpStatus.OK.value(), "Event saved successfully", eventService.save(event)),
				HttpStatus.OK);
	}

	/**
	 * <b>Update Event</b>
	 * @param id		eventId
	 * @param event		event json
	 * @return			returns Event
	 * <b></br>URL FOR API :</b>	/api/admin/event/{eventId}
	 */
	@RequestMapping(value = "{id}", method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Response<Event>> update(@PathVariable String id, @RequestBody Event event) {
		return new ResponseEntity<>(
				new Response<>(HttpStatus.OK.value(), "Event updated successfully", eventService.update(id, event)),
				HttpStatus.OK);
	}

	/**
	 * <b>Get Event by eventId</b>
	 * @param id	eventId
	 * @return		returns Event
	 * <b></br>URL FOR API :</b>	/api/admin/event/{eventId}
	 */
	@RequestMapping(value = "{id}", method = RequestMethod.GET, consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Response<Event>> get(@PathVariable(value = "id") String id) {
		return new ResponseEntity<>(
				new Response<>(HttpStatus.OK.value(), "Events fetched successfully", eventService.get(id)),
				HttpStatus.OK);
	}

	/**
	 * <b>Get All Event</b>
	 * @param query
	 * @param pageable	
	 * @return			returns Event list
	 * <b></br>URL FOR API :</b>	/api/admin/event/all
	 */
	@RequestMapping(value = "all", method = RequestMethod.GET, consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Response<List<Event>>> getByEventName(@RequestParam(value = "q", required = false) String query,
	          Pageable pageable) {
		return new ResponseEntity<>(new Response<>(HttpStatus.OK.value(), "Events fetched successfully",
				eventService.getAll(pageable, QueryParser.parse(query)),eventService.getAll(null, QueryParser.parse(query)).size()), HttpStatus.OK);
	}

	/**
	 * <b>Delete Event</b>
	 * @param eventId
	 * @param updatedBy	
	 * @return			returns boolean
	 * <b></br>URL FOR API :</b>	/api/admin/event/delete/{eventId}/{updatedBy}
	 */
	@RequestMapping(value = "delete/{eventId}/{updatedBy}", method = RequestMethod.DELETE, consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Response<Boolean>> delete(@PathVariable(value = "eventId") String eventId,
			@PathVariable(value = "updatedBy") String updatedBy) {
		eventService.delete(eventId, updatedBy);
		return new ResponseEntity<>(new Response<>(HttpStatus.OK.value(), "Event deleted successfully", true),
				HttpStatus.OK);
	}
}