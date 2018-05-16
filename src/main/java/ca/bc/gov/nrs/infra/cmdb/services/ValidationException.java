package ca.bc.gov.nrs.infra.cmdb.services;

import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

public class ValidationException extends IllegalStateException
{
    @Getter
    private Map<String,String> validationErrors = new HashMap<>();

    public ValidationException(String message)
    {
        super(message);
    }

    public ValidationException(Class _class, Map<String,String> validationErrors)
    {
        super("There were errors validating the " + _class.getName() + " object. Check #getValidationErrors for a list of invalid fields");
        this.validationErrors = validationErrors;
    }
}
