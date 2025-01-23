package edu.hawaii.its.casdemo.model;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class FeedbackTest {

    private Feedback feedback;

    @BeforeEach
    public void setUp() {
        feedback = new Feedback();
    }

    @Test
    public void construction() {
        assertNotNull(feedback);
    }

    @Test
    public void accessors() {
        assertNotNull(feedback);
        assertNull(feedback.getEmail());
        assertNull(feedback.getMessage());
        assertFalse(feedback.isCool());

        feedback.setEmail("u@v");
        feedback.setMessage("The Beast");
        feedback.setCool(true);
        assertThat(feedback.getEmail(), equalTo("u@v"));
        assertThat(feedback.getMessage(), equalTo("The Beast"));
        assertThat(feedback.isCool(), equalTo(true));

        assertThat(feedback.getException(), equalTo(null));
        feedback.setException(new Throwable("Mueller"));
        assertThat(feedback.getException(), not(equalTo(null)));
        assertThat(feedback.getExceptionStr(),
                containsString("java.lang.Throwable: Mueller"));

        feedback.setExceptionStr(null);
        assertThat(feedback.getExceptionStr(), equalTo(null));
        feedback.setExceptionStr("George Orwell");
        assertThat(feedback.getExceptionStr(), equalTo("George Orwell"));

        feedback.setException(null);
        assertThat(feedback.getException(), equalTo(null));
        assertThat(feedback.getExceptionStr(), equalTo(null));
    }

    @Test
    public void testToString() {
        assertThat(feedback.toString(), containsString("email=null, message=null"));

        feedback.setEmail("s@t");
        assertThat(feedback.toString(), containsString("Feedback [email=s@t,"));

        feedback.setMessage("live");
        assertThat(feedback.toString(), containsString("email=s@t, message=live"));
    }
}
