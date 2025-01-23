package edu.hawaii.its.casdemo.service;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.Month;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;

import com.fasterxml.jackson.databind.ObjectMapper;

import edu.hawaii.its.casdemo.configuration.SpringBootWebApplication;
import edu.hawaii.its.casdemo.model.Holiday;
import edu.hawaii.its.casdemo.model.Type;
import edu.hawaii.its.casdemo.util.Dates;

@SpringBootTest(classes = { SpringBootWebApplication.class })
@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
public class HolidayServiceTest {

    @Autowired
    private HolidayService holidayService;

    @Test
    public void findHolidays() {
        List<Holiday> holidays = holidayService.findHolidays();
        int size = holidays.size();
        assertTrue(size > 0);
        int index = size - 1;

        Holiday h0 = holidays.get(index);
        assertNotNull(h0);
        Holiday h1 = holidayService.findHoliday(h0.getId());
        assertEquals(h0.getId(), h1.getId());
        assertEquals(h0, h1);

        // Check that the caching is working.
        Holiday h2 = holidayService.findHolidays().get(index);
        Holiday h3 = holidayService.findHolidays().get(index);
        assertEquals(h2, h3);
        assertSame(h2, h3);

        assertThat(h2.getId(), equalTo(1001));
        assertThat(h2.getDescription(), equalTo("New Year's Day"));
    }

    @Test
    public void findTypeById() {
        final Integer id = 2;
        Type t0 = holidayService.findType(id);
        Type t1 = holidayService.findType(id);
        assertThat(t0.getId(), equalTo(id));
        assertThat(t1.getId(), equalTo(id));
        assertEquals(t0, t1);
        assertSame(t0, t1); // Check if caching is working.

        // Invalid ID value.
        Type t3 = holidayService.findType(666);
        assertThat(t3, equalTo(null));
    }

    @Test
    public void findTypes() {
        List<Type> types = holidayService.findTypes();

        Type ht = types.get(0);
        assertThat(ht.getId(), equalTo(2));
        assertThat(ht.getDescription(), equalTo("Federal"));

        ht = types.get(1);
        assertThat(ht.getId(), equalTo(3));
        assertThat(ht.getDescription(), equalTo("UH"));

        ht = types.get(2);
        assertThat(ht.getId(), equalTo(4));
        assertThat(ht.getDescription(), equalTo("State"));
    }

    @Test
    public void dateFormatting() throws Exception {
        final String DATE_FORMAT = Dates.DATE_FORMAT_FULL;

        SimpleDateFormat df = new SimpleDateFormat(DATE_FORMAT);
        df.setTimeZone(TimeZone.getTimeZone("HST"));

        LocalDate obsDate = Dates.newLocalDate(2014, Month.DECEMBER, 20);
        LocalDate offDate = obsDate.plusDays(200);

        Holiday holiday = new Holiday();
        holiday.setObservedDate(obsDate);
        holiday.setOfficialDate(offDate);

        String toParse = "December 20, 2014, Saturday";
        Date parseDate = df.parse(toParse);
        assertNotNull(obsDate);

        ObjectMapper mapper = new ObjectMapper();
        String result = mapper.writeValueAsString(holiday);
        assertThat(result, containsString(toParse));
    }

    @Test
    public void findHolidayById() {
        Holiday h1 = holidayService.findHoliday(1001);

        assertEquals("New Year's Day", h1.getDescription());

        Holiday h2 = holidayService.findHoliday(1002);
        assertEquals("Dr. Martin Luther King, Jr. Day", h2.getDescription());

        Holiday h4 = holidayService.findHoliday(1004);
        assertEquals("Prince Jonah Kuhio Kalanianaole Day", h4.getDescription());

        // Invalid ID value.
        Holiday h9 = holidayService.findHoliday(666);
        assertThat(h9, equalTo(null));

        assertEquals(3, h1.getHolidayTypes().size());
        assertEquals(3, h2.getHolidayTypes().size());
        assertEquals(2, h4.getHolidayTypes().size());

        List<Type> types = h1.getHolidayTypes();
        assertThat(types.size(), equalTo(3));
        assertThat(types.get(0).getId(), equalTo(2));
        assertThat(types.get(1).getId(), equalTo(4));

        types = h2.getHolidayTypes();
        assertThat(types.size(), equalTo(3));
        assertThat(types.get(0).getId(), equalTo(2));
        assertThat(types.get(1).getId(), equalTo(4));

        types = h4.getHolidayTypes();
        assertThat(types.size(), equalTo(2));
        assertThat(types.get(0).getId(), equalTo(4));
        assertThat(types.get(1).getId(), equalTo(3));
    }
}
