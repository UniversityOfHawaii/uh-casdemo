package edu.hawaii.its.casdemo.access;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;
import java.util.Map;

import org.apereo.cas.client.authentication.AttributePrincipal;
import org.apereo.cas.client.authentication.AttributePrincipalImpl;
import org.apereo.cas.client.validation.Assertion;

public class AssertionDummy implements Assertion {

    @Serial
    private static final long serialVersionUID = 11L;

    private String username;

    // Constructor.
    public AssertionDummy() {
        // Empty.
    }

    // Constructor.
    public AssertionDummy(String username) {
        this.username = username;
    }

    @Override
    public Map<String, Object> getAttributes() {
        return null;
    }

    @Override
    public Map<String, Serializable> getContext() {
        return Map.of();
    }

    @Override
    public Date getAuthenticationDate() {
        return null;
    }

    @Override
    public AttributePrincipal getPrincipal() {
        if (username != null) {
            return new AttributePrincipalImpl(username);
        }
        return null;
    }

    @Override
    public Date getValidFromDate() {
        return null;
    }

    @Override
    public Date getValidUntilDate() {
        return null;
    }

    @Override
    public boolean isValid() {
        return false;
    }

}
