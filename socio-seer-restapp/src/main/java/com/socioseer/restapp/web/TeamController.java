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
import com.socioseer.common.domain.Team;
import com.socioseer.common.dto.Response;
import com.socioseer.restapp.service.api.TeamService;
import com.socioseer.restapp.util.QueryParser;


/**
 * <h3>This Controller Manage the All API of Team.</h3>
 * @author  OrangeMantra
 * @since   JDK 1.8
 * @version 1.0
 *
 */ 
@RestController
@RequestMapping(value = "team", produces = MediaType.APPLICATION_JSON_VALUE)
public class TeamController {

  @Autowired
  private TeamService teamService;

  
  /**
   * <b>Save Team</b>
   * @param  team         Team Details in Json format
   * @return              returns Team Object
   * <b></br>URL FOR API :</b>   /api/admin/team
   */ 
  @RequestMapping(method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<Response<Team>> save(@RequestBody Team team) {
    return new ResponseEntity<>(
        new Response<>(HttpStatus.OK.value(), "Team saved successfully.", teamService.save(team)),
        HttpStatus.OK);
  }

  /**
   * <b>Update Team</b>
   * @param teamId      teamId must be AlphaNumeric and pass as path variable.
   * @param team        team Details in Json format
   * @return            returns the Team Object
   * <b></br>URL FOR API :</b> /api/admin/team/{teamId}
   */ 
  @RequestMapping(value = "{teamId}", method = RequestMethod.PUT,
      consumes = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<Response<Team>> update(@PathVariable("teamId") String teamId,
      @RequestBody Team team) {
    return new ResponseEntity<>(new Response<>(HttpStatus.OK.value(), "Team updated successfully.",
        teamService.update(teamId, team)), HttpStatus.OK);
  }

  /**
   * <b>Get Team by teamId</b>
   * @param  teamId  teamId must be AlphaNumeric and pass as path variable.
   * @return         returns the Team Object if found.
   * <b></br>URL FOR API :</b> /api/admin/team/{teamId}
   */ 
  @RequestMapping(value = "{teamId}")
  public ResponseEntity<Response<Team>> getTeamById(@PathVariable("teamId") String teamId) {
    return new ResponseEntity<>(new Response<>(HttpStatus.OK.value(), "Team fetched successfully.",
        teamService.get(teamId)), HttpStatus.OK);
  }

  /**
   * <b>Get Teams by clientId</b>
   * @param clientId    clientId must be AlphaNumeric and pass as path variable.
   * @param query       criteria parameters.
   * @param pageable
   * @return           returns the teams list
   * <b></br>URL FOR API :</b> /api/admin/team/client/{clientId}
   */ 
  @RequestMapping(value = "client/{clientId}")
  public ResponseEntity<Response<List<Team>>> getTeamByClientId(
      @PathVariable("clientId") String clientId,
      @RequestParam(value = "q", required = false) String query, Pageable pageable) {
    return new ResponseEntity<>(new Response<>(HttpStatus.OK.value(), "Team fetched successfully by client id.",
        teamService.getTeamByClientId(pageable, QueryParser.parse(query),clientId),
        teamService.getTeamByClientId(null, QueryParser.parse(query),clientId).size()), HttpStatus.OK);

  }

  /**
   * <b>Get Team by teamName</b>
   * @param teamName    teamName must be AlphaNumeric and pass as path variable.
   * @return  returns the Team
   * <b></br>URL FOR API :</b> /api/admin/team/name/{teamName}
   */ 
  @RequestMapping(value = "name/{teamName}")
  public ResponseEntity<Response<Team>> getByTeamName(@PathVariable("teamName") String teamName) {
    return new ResponseEntity<>(new Response<>(HttpStatus.OK.value(), "Team fetched successfully.",
        teamService.getTeamByName(teamName).get()), HttpStatus.OK);
  }

  /**
   * <b>Delete team by teamId</b>
   * @param id           id must be AlphaNumeric and pass as path variable.
   * @param updatedBy    updatedBy must be AlphaNumeric and pass as path variable.
   * @return             returns true if deleted successfully.
   * <b></br>URL FOR API :</b> /api/admin/team/delete/{id}/{updatedBy}
   */ 
  @RequestMapping(value = "delete/{id}/{updatedBy}", method = RequestMethod.DELETE)
  public ResponseEntity<Response<Boolean>> delete(@PathVariable("id") String id,
      @PathVariable("updatedBy") String updatedBy) {
    teamService.delete(id, updatedBy);
    return new ResponseEntity<>(
        new Response<>(HttpStatus.OK.value(), "Team deleted successfully.", true), HttpStatus.OK);
  }
  
  
  /**
   * <b>Change Status of Team</b>
   * @param id           id must be AlphaNumeric and pass as path variable.
   * @param status       status must be integer and in {1,2,3} and pass as path variable.
   * @param updatedBy    updatedBy must be AlphaNumeric and pass as path variable.
   * @return             returns true if status updated successfully.
   * <b></br>URL FOR API :</b> /api/admin/team/status/{id}/{status}/{updatedBy}
   */ 
  @RequestMapping(value = "status/{id}/{status}/{updatedBy}", method = RequestMethod.PUT)
  public ResponseEntity<Response<Boolean>> changeStatus(@PathVariable("id") String id,
      @PathVariable("status") int status, @PathVariable("updatedBy") String updatedBy) {
    teamService.changeStatus(id, status, updatedBy);
    return new ResponseEntity<>(
        new Response<>(HttpStatus.OK.value(), "Team status updated successfully.", true),
        HttpStatus.OK);
  }
  
  /**
   * <b>Get All Team</b>
   * @param query        query be criteria parameters and pass as path variable.
   * @param pageable     
   * @return             returns Team List.
   * <b></br>URL FOR API :</b> /api/admin/team/all
   */ 
  @RequestMapping(value = "all", method = RequestMethod.GET)
  public ResponseEntity<Response<List<Team>>> getAll(@RequestParam(value = "q", required = false) String query,
          Pageable pageable) {
      return new ResponseEntity<>(new Response<>(HttpStatus.OK.value(), "Teams fetched successfully.",
              teamService.getAllTeams(pageable, QueryParser.parse(query)),teamService.getAllTeams(null, QueryParser.parse(query)).size()), HttpStatus.OK);
  }

}
