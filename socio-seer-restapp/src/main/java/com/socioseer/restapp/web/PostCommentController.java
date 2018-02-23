package com.socioseer.restapp.web;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.socioseer.common.domain.model.post.PostComment;
import com.socioseer.common.dto.Response;
import com.socioseer.restapp.service.api.PostCommectService;

/**
 * <h3>This Controller Manage the All API of PostComment .</h3>
 * @author  OrangeMantra
 * @since   JDK 1.8
 * @version 1.0
 *
 */
@RestController
@RequestMapping(value = "post-comment", produces = MediaType.APPLICATION_JSON_VALUE)
public class PostCommentController {
	
	@Autowired
	private PostCommectService postCommectService;
	
	/**
	 * <b>Save PostComment</b> 
	 * @param postComment	postComment json
	 * @return				returns PostComment
	 * <b></br>URL FOR API :</b>	/api/admin/post-comment
	 */
	@RequestMapping(method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
	 public ResponseEntity<Response<PostComment>> save(@RequestBody PostComment postComment) {
	   return new ResponseEntity<>(new Response<>(HttpStatus.OK.value(),
	       "Post Comment saved successfully", postCommectService.save(postComment)), HttpStatus.OK);
	}
	 
	/**
	 * <b>Update PostComment</b>
	 * @param postComment	postComment json
	 * @param id			postCommentId
	 * @return		returns PostComment
	 * <b></br>URL FOR API :</b>	/api/admin/post-comment/{id}
	 */
	@RequestMapping(value ="{id}",method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_VALUE)
	 public ResponseEntity<Response<PostComment>> update(@RequestBody PostComment postComment ,@PathVariable (name="id") String id) {
	   return new ResponseEntity<>(new Response<>(HttpStatus.OK.value(),
	       "Post Comment updated successfully", postCommectService.update(id, postComment)), HttpStatus.OK);
	}
	 
	/**
	 * <b>Get PostComment by Id</b>
	 * @param id	postCommentId
	 * @return		returns PostComment
	 * <b></br>URL FOR API :</b>	/api/admin/post-comment/{id}
	 */
	 @RequestMapping(value ="{id}",method = RequestMethod.GET, consumes = MediaType.APPLICATION_JSON_VALUE)
	  public ResponseEntity<Response<PostComment>> get(@PathVariable (name="id") String id) {
	    return new ResponseEntity<>(new Response<>(HttpStatus.OK.value(),
	        "Post Comment fetched successfully", postCommectService.get(id)), HttpStatus.OK);
	  }
	 
	 /**
	  * <b>Get PostComment by userId</b>
	  * @param userId	
	  * @return			returns PostComment list
	  * <b></br>URL FOR API :</b>	/api/admin/post-comment/user/{userId}
	  */
	 @RequestMapping(value ="user/{userId}",method = RequestMethod.GET, consumes = MediaType.APPLICATION_JSON_VALUE)
	  public ResponseEntity<Response<List<PostComment>>> getByUserId(@PathVariable (name="userId") String userId) {
	    return new ResponseEntity<>(new Response<>(HttpStatus.OK.value(),
	        "Post Comment fetched successfully", postCommectService.getByUserId(userId)), HttpStatus.OK);
	  }
	 
	 /**
	  * <b>Get PostComment by postId</b>
	  * @param postId
	  * @return		returns PostComment list
	  * <b></br>URL FOR API :</b>	/api/admin/post-comment/user/{userId}
	  */
	 @RequestMapping(value ="post/{postId}",method = RequestMethod.GET, consumes = MediaType.APPLICATION_JSON_VALUE)
	  public ResponseEntity<Response<List<PostComment>>> getByPostId(@PathVariable (name="postId") String postId) {
	    return new ResponseEntity<>(new Response<>(HttpStatus.OK.value(),
	        "Post Comment fetched successfully", postCommectService.getByPostId(postId)), HttpStatus.OK);
	  }

}
