package com.socioseer.restapp.service.util;

import java.util.List;
import java.util.stream.Collectors;

import com.socioseer.common.domain.Role;
import com.socioseer.common.domain.SecurityGroup;

public class RoleUtil {

	public static boolean doUserHaveRole(List<SecurityGroup> securityGroups, String roleName) {
		List<Role> roles = securityGroups.stream().flatMap(sg -> sg.getRoles().stream())
				.collect(Collectors.toList());
		return roles.stream().anyMatch(role -> role.getName().equalsIgnoreCase(roleName));
	}

}
