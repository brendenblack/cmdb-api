package ca.bc.gov.nrs.infra.cmdb.domain.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UsernamePasswordSecret extends Secret
{
    private String username;
    private String password;
}
