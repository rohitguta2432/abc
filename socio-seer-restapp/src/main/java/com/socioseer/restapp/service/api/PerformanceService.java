package com.socioseer.restapp.service.api;

import java.util.List;

import com.socioseer.common.domain.model.request.TeamReportRequest;
import com.socioseer.common.domain.model.response.TeamReport;

/**
 * <h3>Performance Services</h3>
 * @author  OrangeMantra
 * @since   JDK 1.8
 * @version 1.0
 *
 */
public interface PerformanceService {
	
	/**
	   * <b>Get TeamReport list</b>
	   * @param teamReportRequest
	   * @return	returns TeamReport list
	   */
	List<TeamReport> teamReport(TeamReportRequest teamReportRequest);
}
