package edu.hawaii.its.casdemo.access;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;

import org.junit.jupiter.api.Test;

class AssertionDummyTest {

    AssertionDummy assertion;

    @Test
    public void construction() {
        assertion = new AssertionDummy();
        assertThat(assertion.getPrincipal(), is(nullValue()));

        assertion = new AssertionDummy("testy");
        assertThat(assertion.getPrincipal(), is(notNullValue()));
    }
}
