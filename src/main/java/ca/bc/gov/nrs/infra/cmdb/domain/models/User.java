package ca.bc.gov.nrs.infra.cmdb.domain.models;

import lombok.Getter;
import lombok.Setter;

public abstract class User extends Entity
{
    @Getter
    @Setter
    private String displayName;
}
