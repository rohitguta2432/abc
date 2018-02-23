package com.socioseer.restapp.web;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.socioseer.common.domain.model.campaign.Media;
import com.socioseer.common.domain.model.request.MediaRequest;
import com.socioseer.common.dto.Filter;
import com.socioseer.common.dto.Response;
import com.socioseer.restapp.service.api.MediaService;
import com.socioseer.restapp.util.JsonParser;
import com.socioseer.restapp.util.QueryParser;

/**
 * <h3>This Controller Manage the All API of Media .</h3>
 * @author  OrangeMantra
 * @since   JDK 1.8
 * @version 1.0
 *
 */
@RestController
@RequestMapping(value = "media", produces = MediaType.APPLICATION_JSON_VALUE)
public class MediaController {

  @Autowired
  private MediaService mediaService;

  /**
   * <b>Save Media</b>
   * @param mediaString	
   * @param mediaFiles	multipart files
   * @return			returns Media list
   * <b></br>URL FOR API :</b>	/api/admin/media
   */
  @RequestMapping(method = RequestMethod.POST)
  public ResponseEntity<Response<List<Media>>> saveMedia(
      @RequestParam(value = "media") String mediaString,
      @RequestParam(value = "files", required = false) MultipartFile[] mediaFiles) {
    return new ResponseEntity<>(
        new Response<>(HttpStatus.OK.value(), "Media saved successfully",
            mediaService.save(JsonParser.toObject(mediaString, MediaRequest.class), mediaFiles)),
        HttpStatus.OK);
  }

  /**
   * <b>Get Media by clientId</b>
   * @param clientId	
   * @param query
   * @param pageRequest
   * @return			returns media list	
   * <b></br>URL FOR API :</b>	/api/admin/media/{clientId}
   */
  @RequestMapping(value = "{clientId}", method = RequestMethod.GET)
  public ResponseEntity<Response<List<Media>>> getMediaByClientId(
      @PathVariable(value = "clientId") String clientId,@RequestParam(value = "q", required = false) String query, Pageable pageRequest) {
	  List<Filter> filters = QueryParser.parse(query);
   List<Media> mediaList = mediaService.getMediaByClientId(clientId,filters, pageRequest );
   List<Media> mediaCountList = mediaService.getMediaByClientId(clientId,filters,null);
    return new ResponseEntity<>(new Response<>(HttpStatus.OK.value(), "Media fetched successfully",
        mediaList, mediaCountList.size()),
        HttpStatus.OK);
  }

  /**
   * <b>Delete Media</b>
   * @param id			mediaId
   * @param updatedBy
   * @return			returns boolean data
   * <b></br>URL FOR API :</b>	/api/admin/media/delete/{mediaId}/{updatedBy}
   */
  @RequestMapping(value = "delete/{id}/{updatedBy}", method = RequestMethod.DELETE)
  public ResponseEntity<Response<Boolean>> delete(@PathVariable("id") String id,
      @PathVariable("updatedBy") String updatedBy) {
    mediaService.delete(id, updatedBy);
    return new ResponseEntity<>(
        new Response<>(HttpStatus.OK.value(), "Media deleted successfully.", true), HttpStatus.OK);
  }

  /**
   * <b>Get All Media</b>
   * @param query
   * @param pageable
   * @return			returns media list
   * <b></br>URL FOR API :</b>	/api/admin/media/all
   */
  @RequestMapping(value = "all", method = RequestMethod.GET)
  public ResponseEntity<Response<List<Media>>> getAllClients(
      @RequestParam(value = "q", required = false) String query, Pageable pageable) {
    List<Filter> filters = QueryParser.parse(query);
    return new ResponseEntity<>(
        new Response<>(HttpStatus.OK.value(), "Media fetched successfully",
            mediaService.getAll(pageable, filters), mediaService.getAll(null, filters).size()),
        HttpStatus.OK);
  }
}