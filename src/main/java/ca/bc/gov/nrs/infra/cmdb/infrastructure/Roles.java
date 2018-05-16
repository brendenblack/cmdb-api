package ca.bc.gov.nrs.infra.cmdb.infrastructure;

import java.util.HashSet;
import java.util.Set;

public class Roles
{
    public final static String HAS_ROLE_SERVICE = "hasRole('ROLE_SERVICE')";
    public final static String HAS_ROLE_USER = "hasRole('ROLE_USER')";

    public static String hasRole(String... roles)
    {
        Set<String> roleNames = new HashSet<>();

        for (String role : roles)
        {
            roleNames.add("'" + role + "'");
        }

        String hasRoles = String.join(", ", roleNames);

        return "hasRole(" + hasRoles + ")";
    }
}
