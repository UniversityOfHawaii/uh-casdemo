package edu.hawaii.its.casdemo.model;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import edu.hawaii.its.casdemo.util.Dates;

public class YearHolidayHolderTest {

    private YearHolidayHolder holder;

    @BeforeEach
    public void setUp() {
        holder = new YearHolidayHolder();
    }

    @Test
    public void construction() {
        assertNotNull(holder);
        assertTrue(holder.isEmpty());

        holder = new YearHolidayHolder(null);
        assertNotNull(holder);
        assertTrue(holder.isEmpty());

        List<Holiday> holidays = new ArrayList<>();
        holder = new YearHolidayHolder(holidays);
        assertNotNull(holder);
        assertTrue(holder.isEmpty());
    }

    @Test
    public void isEmpty() {
        assertTrue(holder.isEmpty());

        LocalDate date = Dates.firstDateOfYear(2017);
        Holiday h = new Holiday(date, date);
        List<Holiday> holidays = new ArrayList<>();
        holidays.add(h);
        holder = new YearHolidayHolder(holidays);
        assertFalse(holder.isEmpty());
    }

    @Test
    public void addNullHoliday() {
        assertTrue(holder.isEmpty());
        List<Holiday> holidays = new ArrayList<>();
        holidays.add(null); // Null Holiday.
        holder = new YearHolidayHolder(holidays);
        assertTrue(holder.isEmpty());
    }

    @Test
    public void getYears() {
        assertTrue(holder.isEmpty());

        List<Holiday> holidays = new ArrayList<>();
        for (int year = 2000; year <= 2015; year++) {
            LocalDate date = Dates.firstDateOfYear(year);
            holidays.add(new Holiday(date, date));
        }

        holder = new YearHolidayHolder(holidays);
        assertFalse(holder.isEmpty());

        Set<Integer> years = holder.getYears();
        assertThat(years.size(), equalTo(16));
    }

    @Test
    public void getYear() {
        assertThat(holder.getYear(), equalTo(0));
        holder.setYear(null);
        assertThat(holder.getYear(), equalTo(0));
        holder.setYear(2022);
        assertThat(holder.getYear(), equalTo(2022));

        holder = new YearHolidayHolder() {
            @Override
            protected Integer currentYear() {
                return 2016;
            }
        };
        assertTrue(holder.isEmpty());
        assertThat(holder.currentYear(), equalTo(2016));
        assertThat(holder.getYear(), equalTo(0)); // Weird.

        List<Holiday> holidays = new ArrayList<>();
        for (int year = 2000; year <= 2017; year++) {
            LocalDate date = Dates.firstDateOfYear(year);
            holidays.add(new Holiday(date, date));
        }

        holder = new YearHolidayHolder(holidays) {
            @Override
            protected Integer currentYear() {
                return 2016;
            }
        };
        assertFalse(holder.isEmpty());
        assertThat(holder.currentYear(), equalTo(2016));
        assertThat(holder.getYear(), equalTo(2016));

        Set<Integer> years = holder.getYears();
        assertThat(years.size(), equalTo(18));

        // The year will be the current year if
        // the years range spans accross it.
        assertThat(holder.getYear(), equalTo(2016));

        holidays = new ArrayList<>();
        for (int year = 2000; year <= 2014; year++) {
            LocalDate date = Dates.firstDateOfYear(year);
            holidays.add(new Holiday(date, date));
        }

        holder = new YearHolidayHolder(holidays) {
            @Override
            protected Integer currentYear() {
                return 2016;
            }
        };
        assertFalse(holder.isEmpty());
        assertThat(holder.currentYear(), equalTo(2016));
        assertThat(holder.getYear(), equalTo(2014));

        // The year will be the highest year if the
        // current year is outside the range of years.
        assertThat(holder.getYear(), equalTo(2014));

        holder.setYear(2013);
        assertThat(holder.getYear(), equalTo(2013));

        holidays = new ArrayList<>();
        for (int year = 2014; year <= 2015; year++) {
            for (Month month : Month.values()) {
                LocalDate date = Dates.newLocalDate(year, month, 1);
                holidays.add(new Holiday(date, date));
            }
        }
        holder = new YearHolidayHolder(holidays) {
            @Override
            protected Integer currentYear() {
                return 2016;
            }
        };
        assertThat(holder.getYear(), equalTo(2015));
    }

    @Test
    public void getHolidays() {
        List<Holiday> holidays = holder.getHolidays(null);
        assertThat(holidays.size(), equalTo(0));

        holidays = holder.getHolidays(2016);
        assertThat(holidays.size(), equalTo(0));

        for (int day = 1; day <= 31; day++) {
            LocalDate date = Dates.newLocalDate(2016, Month.JANUARY, day);
            holidays.add(new Holiday(date, date));
        }
        holder = new YearHolidayHolder(holidays);
        holidays = holder.getHolidays(2016);
        assertThat(holidays.size(), equalTo(31));

        holidays = new ArrayList<>();
        for (Month month : Month.values()) {
            LocalDate date = Dates.firstDateOfMonth(month, 2016);
            holidays.add(new Holiday(date, date));
        }
        holder = new YearHolidayHolder(holidays);
        holidays = holder.getHolidays(2016);
        assertThat(holidays.size(), equalTo(12));
    }

    @Test
    public void currentYear() {
        assertNotNull(holder.currentYear());
        assertThat(holder.currentYear(), equalTo(Dates.currentYear()));
    }
}
