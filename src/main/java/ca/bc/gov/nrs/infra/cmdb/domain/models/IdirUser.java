package ca.bc.gov.nrs.infra.cmdb.domain.models;


import com.google.common.collect.ImmutableSet;
import lombok.Getter;
import lombok.Setter;
import org.neo4j.ogm.annotation.Index;

import java.util.HashSet;
import java.util.Set;

@Getter
public class IdirUser extends User
{

    /**
     * OGM requires a public no-args constructor
     *
     * @deprecated use the provided {@link Builder} instead
     */
    @Deprecated
    public IdirUser() { }

    IdirUser(String idir)
    {
        this.idir = idir;
    }

    @Index(unique = true)
    private String idir;

    @Setter
    private String email;

    private Set<String> knownAliases = new HashSet<>();

    public boolean addAlias(String alias)
    {
        return this.knownAliases.add(alias);
    }

    //region builder
    public static OptionalParameters of(String idir)
    {
        return new Builder(idir);
    }

    public interface OptionalParameters
    {
        OptionalParameters email(String email);

        OptionalParameters displayName(String displayName);

        IdirUser build();
    }



    public static class Builder implements OptionalParameters
    {
        private final String idir;
        private String email;
        private String displayName;

        Builder(String idir)
        {
            this.idir = idir;
        }

        @Override
        public OptionalParameters email(String email)
        {
            this.email = email;
            return this;
        }

        @Override
        public OptionalParameters displayName(String displayName)
        {
            this.displayName = displayName;
            return this;
        }

        @Override
        public IdirUser build()
        {
            IdirUser user = new IdirUser(this.idir);
            user.setEmail(this.email);
            user.setDisplayName(this.displayName);

            return user;
        }
    }
    //endregion


}
