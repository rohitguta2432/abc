package com.socioseer.acl.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import com.socioseer.acl.config.AdminFeignClient;
import com.socioseer.common.domain.ResourceRoleMapping;
import com.socioseer.common.domain.Role;
import com.socioseer.common.domain.SecurityGroup;
import com.socioseer.common.dto.Response;
import com.socioseer.common.exception.SocioSeerException;

/**
 * <b>Resource Role Mapping Cache</b>
 * @author OrangeMantra
 * @since JDK 1.8
 * @version 1.0
 *
 */
@Slf4j
@Component
public class ResourceRoleMappingCache {

  private static UrlHashMap<ResourceRoleMapping> urlCache = new UrlHashMap<>();

  @Value("${socio.seer.secret.key}")
  private String secretKey;

  @Autowired
  private AdminFeignClient adminFeignClient;

/**
 * <b>Load Resource Mapping</b>
 */
  private void loadResourceRoleMapping() {

    final List<ResourceRoleMapping> resourceRoleMappings = getAllResourceRoleMapping();

    if (CollectionUtils.isEmpty(resourceRoleMappings)) {
      log.warn("No ACL configured.");
      return;
    }

    resourceRoleMappings.stream().forEach(rlm -> {
      urlCache.put(rlm.getUrl(), rlm);
    });

  }

  /**
   * Method to warm/reload cache.
   */
  public void reloadCache() {

    // TODO: Consider locking before clearing cache to avoid request
    // failing.
    log.info("Warming url cache.");
    urlCache.clear();
    loadResourceRoleMapping();
    log.info("Warming url cache completed.");
  }

  /**
   * Method to validate whether given user has access to requested resource.
   * 
   * @param roles - list of {@link SecurityGroup}
   * @param requestedResource - the requested URL/resource
   * @param httpMethod - the requested method
   * 
   * @return - true if user has access to requested resource, false otherwise.
   */
  public boolean hasAccess(final List<SecurityGroup> securityGroups,
      final String requestedResource, final String httpMethod) {

    if (CollectionUtils.isEmpty(securityGroups)) {
      return false;
    }

    if (urlCache.isEmpty()) {
      loadResourceRoleMapping();
    }

    if (!urlCache.containsKey(requestedResource)) {
      log.info(String.format("ACL not configured for requested resource : %s", requestedResource));
      return false;
    }

    final ResourceRoleMapping resourceRoleMapping = urlCache.get(requestedResource);

    // flattening security group roles
    final List<Role> roles =
        securityGroups.stream().flatMap(sg -> sg.getRoles().stream()).collect(Collectors.toList());

    final Optional<Role> requestedRole =
        roles
            .stream()
            .filter(
                role -> resourceRoleMapping.getRoles().stream()
                    .anyMatch(r -> r.getName().equals(role.getName()))).findFirst();

    if (!requestedRole.isPresent()) {
      return false;
    }
    return requestedRole.get().getPermissions().stream()
        .anyMatch(perm -> perm.equalsIgnoreCase(httpMethod));
  }

  /**
   * Method to fetch all resource URL mappings.
   * 
   * @return - list of {@link ResourceRoleMapping}
   */
  private List<ResourceRoleMapping> getAllResourceRoleMapping() {

    final ResponseEntity<Response<List<ResourceRoleMapping>>> response =
        adminFeignClient.fetchAllResourceRoleMapping(secretKey);

    if (response.getStatusCode() != HttpStatus.OK) {
      final String message =
          String
              .format(
                  "Admin api responded with error code : %d, while fetching all resource role mapping.",
                  response.getStatusCodeValue());
      log.error(message);
      throw new SocioSeerException(message);
    }
    return response.getBody().getData();
  }
}
