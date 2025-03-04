package edu.hawaii.its.casdemo.util;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.util.Calendar;
import java.util.Date;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class DatesTest {

    protected LocalDate christmasLocalDate;
    protected LocalDate newYearsDay2000LocalDate;
    protected LocalDate dayMusicDiedLocalDate;

    @BeforeEach
    public void setUp() {
        christmasLocalDate = LocalDate.of(1962, Month.DECEMBER, 25);
        newYearsDay2000LocalDate = LocalDate.of(2000, Month.JANUARY, 1);
        dayMusicDiedLocalDate = LocalDate.of(1959, Month.FEBRUARY, 3);
    }

    private Calendar makeCalendar() {
        return Calendar.getInstance();
    }

    private Calendar makeCalendar(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);

        return cal;
    }

    private Calendar makeCalendar(LocalDate date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(Dates.toDate(date));

        return cal;
    }

    @Test
    public void daysBetween() {
        LocalDate date1 = Dates.newLocalDate(2019, Month.JUNE, 21);
        LocalDate date2 = Dates.newLocalDate(2019, Month.JUNE, 23);
        assertThat(Dates.daysBetween(date1, date2), equalTo(2L));
        assertThat(Dates.daysBetween(date2, date1), equalTo(-2L));

        date1 = Dates.newLocalDate(1962, Month.DECEMBER, 21);
        date2 = Dates.newLocalDate(2028, Month.APRIL, 29);
        Dates.daysBetween(date1, date2);
        assertThat(Dates.daysBetween(date1, date2), equalTo(23871L));
    }

    @Test
    public void newLocalDate() {
        assertNotNull(Dates.newLocalDate());
    }

    @Test
    public void newDate() {
        LocalDate localDate = null;
        Date date = Dates.toDate(localDate);
        assertThat(date, equalTo(null));
    }

    @Test
    public void testFindPreviousSunday() {
        LocalDate date1 = Dates.newLocalDate(2010, Month.AUGUST, 9); // A Monday.
        Calendar cal = Calendar.getInstance();
        cal.setTime(Dates.toDate(date1));

        for (int i = 0; i < 75; i++) {
            cal.add(Calendar.DATE, -13); // Move back some days.
            final LocalDate date2 = Dates.toLocalDate(cal.getTime());

            final LocalDate sunday = Dates.previousSunday(date2);
            Calendar calSunday = Calendar.getInstance();
            calSunday.setTime(Dates.toDate(sunday));

            assertEquals(Calendar.SUNDAY, calSunday.get(Calendar.DAY_OF_WEEK));
            assertTrue(!sunday.isAfter(date2));
        }

        LocalDate date4 = Dates.previousSunday(christmasLocalDate);
        Calendar cal4 = Calendar.getInstance();
        cal4.setTime(Dates.toDate(date4));
        assertEquals(Calendar.SUNDAY, cal4.get(Calendar.DAY_OF_WEEK));
        assertEquals(23, cal4.get(Calendar.DAY_OF_MONTH));
        assertEquals(1962, cal4.get(Calendar.YEAR));

        LocalDate date5 = Dates.previousSunday(newYearsDay2000LocalDate);
        Calendar cal5 = Calendar.getInstance();
        cal5.setTime(Dates.toDate(date5));
        assertEquals(Calendar.SUNDAY, cal5.get(Calendar.DAY_OF_WEEK));
        assertEquals(Calendar.DECEMBER, cal5.get(Calendar.MONTH));
        assertEquals(26, cal5.get(Calendar.DAY_OF_MONTH));
        assertEquals(1999, cal5.get(Calendar.YEAR));

        LocalDate date6 = Dates.newLocalDate(2010, Month.AUGUST, 1); // A Sunday.
        Calendar cal6 = Calendar.getInstance();
        cal6.setTime(Dates.toDate(date6));
        assertEquals(Calendar.SUNDAY, cal6.get(Calendar.DAY_OF_WEEK));
        assertEquals(Calendar.AUGUST, cal6.get(Calendar.MONTH));
        assertEquals(1, cal6.get(Calendar.DAY_OF_MONTH));
        assertEquals(2010, cal6.get(Calendar.YEAR));
    }

    @Test
    public void testFirstOfYear() {
        assertEquals(Dates.newLocalDate(1959, Month.FEBRUARY, 3), dayMusicDiedLocalDate);

        LocalDate date0 = Dates.firstOfYear(dayMusicDiedLocalDate);
        assertEquals(Dates.newLocalDate(1959, Month.JANUARY, 1), date0);

        LocalDate date1 = Dates.firstOfYear(christmasLocalDate);
        assertEquals(Dates.newLocalDate(1962, Month.JANUARY, 1), date1);

        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, 2012);
        cal.set(Calendar.HOUR_OF_DAY, 4);
        LocalDate date2 = Dates.firstOfYear(Dates.toLocalDate(cal.getTime()));
        assertEquals(Dates.newLocalDate(2012, Month.JANUARY, 1), date2);

        Calendar cal3 = Calendar.getInstance();
        cal3.setTime(cal.getTime());
        assertEquals(4, cal3.get(Calendar.HOUR_OF_DAY));
        LocalDate date3 = Dates.firstOfYear(Dates.toLocalDate(cal3.getTime()));
        assertEquals(Dates.newLocalDate(2012, Month.JANUARY, 1), date3);

        LocalDate dt = Dates.newLocalDate(2012, Month.FEBRUARY, 29);
        LocalDate date4 = Dates.firstOfYear(dt);
        assertEquals(Dates.newLocalDate(2012, Month.JANUARY, 1), date4);

        Calendar cal4 = Calendar.getInstance();
        cal4.set(Calendar.SECOND, 33);
        cal4.set(Calendar.MINUTE, 22);
        cal4.set(Calendar.HOUR_OF_DAY, 11);
        cal4.set(Calendar.DAY_OF_MONTH, 29);
        cal4.set(Calendar.MONTH, Calendar.FEBRUARY);
        cal4.set(Calendar.YEAR, 2012);
        Calendar cal5 = Calendar.getInstance();
        cal5.setTime(Dates.toDate(Dates.firstOfYear(Dates.toLocalDate(cal4.getTime()))));
        assertEquals(1, cal5.get(Calendar.DAY_OF_MONTH));
        assertEquals(Calendar.JANUARY, cal5.get(Calendar.MONTH));
        assertEquals(2012, cal5.get(Calendar.YEAR));
        assertEquals(0, cal5.get(Calendar.SECOND));
        assertEquals(0, cal5.get(Calendar.MINUTE));
        assertEquals(0, cal5.get(Calendar.HOUR_OF_DAY));
    }

    @Test
    public void testFirstOfPreviousYear() {
        LocalDate date0 = Dates.firstOfPreviousYear(dayMusicDiedLocalDate);
        assertEquals(Dates.newLocalDate(1958, Month.JANUARY, 1), date0);

        LocalDate date1 = Dates.firstOfPreviousYear(christmasLocalDate);
        assertEquals(Dates.newLocalDate(1961, Month.JANUARY, 1), date1);

        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, 2012);
        cal.set(Calendar.HOUR_OF_DAY, 4);
        LocalDate date2 = Dates.firstOfPreviousYear(Dates.toLocalDate(cal.getTime()));
        assertEquals(Dates.newLocalDate(2011, Month.JANUARY, 1), date2);

        Calendar cal3 = Calendar.getInstance();
        cal3.setTime(cal.getTime());
        assertEquals(4, cal3.get(Calendar.HOUR_OF_DAY));
        LocalDate date3 = Dates.firstOfPreviousYear(Dates.toLocalDate(cal3.getTime()));
        assertEquals(Dates.newLocalDate(2011, Month.JANUARY, 1), date3);

        LocalDate dt = Dates.newLocalDate(2012, Month.FEBRUARY, 29);
        LocalDate date4 = Dates.firstOfPreviousYear(dt);
        assertEquals(Dates.newLocalDate(2011, Month.JANUARY, 1), date4);

        Calendar cal4 = Calendar.getInstance();
        cal4.set(Calendar.SECOND, 33);
        cal4.set(Calendar.MINUTE, 22);
        cal4.set(Calendar.HOUR_OF_DAY, 11);
        cal4.set(Calendar.DAY_OF_MONTH, 29);
        cal4.set(Calendar.MONTH, Calendar.FEBRUARY);
        cal4.set(Calendar.YEAR, 2012);
        Calendar cal5 = Calendar.getInstance();
        cal5.setTime(Dates.toDate(Dates.firstOfPreviousYear(Dates.toLocalDate(cal4.getTime()))));
        assertEquals(1, cal5.get(Calendar.DAY_OF_MONTH));
        assertEquals(Calendar.JANUARY, cal5.get(Calendar.MONTH));
        assertEquals(2011, cal5.get(Calendar.YEAR));
        assertEquals(0, cal5.get(Calendar.SECOND));
        assertEquals(0, cal5.get(Calendar.MINUTE));
        assertEquals(0, cal5.get(Calendar.HOUR_OF_DAY));
    }

    @Test
    public void lastDayOfMonth() {

        // Just a bunch of random checks.
        assertEquals(30, Dates.lastDayOfMonth(Month.SEPTEMBER, 1962));
        assertEquals(31, Dates.lastDayOfMonth(Month.JANUARY, 2000));
        assertEquals(29, Dates.lastDayOfMonth(Month.FEBRUARY, 2000));
        assertEquals(29, Dates.lastDayOfMonth(Month.FEBRUARY, 2012));

        // Now just run some comparisons against
        // methods available from the Calendar class.
        Calendar cal = Calendar.getInstance();
        for (int year = 1979; year < 2039; year++) {
            cal.clear();
            cal.set(Calendar.YEAR, year);
            for (Month month : Month.values()) {
                cal.set(Calendar.MONTH, month.getValue() - 1);
                int lastDayOfMonth = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
                assertEquals(lastDayOfMonth, Dates.lastDayOfMonth(month, year));
            }
        }

    }

    @Test
    public void firstOfMonth() {
        LocalDate date1 = Dates.firstOfMonth(Month.DECEMBER, 1962);
        LocalDate date2 = Dates.newLocalDate(1962, Month.DECEMBER, 1);

        assertEquals(date1, date2);

        Calendar cal = makeCalendar(Dates.toDate(date1));
        assertEquals(1, cal.get(Calendar.DAY_OF_MONTH));
        assertEquals(1962, cal.get(Calendar.YEAR));
        assertEquals(Calendar.DECEMBER, cal.get(Calendar.MONTH));
        assertEquals(0, cal.get(Calendar.SECOND));
        assertEquals(0, cal.get(Calendar.MINUTE));
        assertEquals(0, cal.get(Calendar.HOUR_OF_DAY));
    }

    @Test
    public void firstOfNextMonth() {
        LocalDate date0 = Dates.newLocalDate(2010, Month.DECEMBER, 31);
        LocalDate date1 = Dates.newLocalDate(2011, Month.JANUARY, 1);
        LocalDate date2 = Dates.newLocalDate(2011, Month.JANUARY, 31);
        LocalDate date3 = Dates.newLocalDate(2011, Month.FEBRUARY, 1);
        LocalDate date4 = Dates.newLocalDate(2011, Month.MARCH, 1);
        LocalDate date5 = Dates.newLocalDate(2012, Month.FEBRUARY, 29);
        LocalDate date6 = Dates.newLocalDate(2012, Month.MARCH, 1);

        assertEquals(date1, Dates.firstOfNextMonth(date0));
        assertEquals(date3, Dates.firstOfNextMonth(date2));
        assertEquals(date4, Dates.firstOfNextMonth(date3));
        assertEquals(date6, Dates.firstOfNextMonth(date5));

        Calendar cal0 = Calendar.getInstance();
        cal0.set(Calendar.SECOND, 33);
        cal0.set(Calendar.MINUTE, 22);
        cal0.set(Calendar.HOUR_OF_DAY, 11);
        cal0.set(Calendar.DAY_OF_MONTH, 3);
        cal0.set(Calendar.MONTH, Calendar.FEBRUARY);
        cal0.set(Calendar.YEAR, 2012);
        Calendar cal = Calendar.getInstance();
        cal.setTime(Dates.toDate(Dates.firstOfNextMonth(Dates.toLocalDate(cal0.getTime()))));
        assertEquals(1, cal.get(Calendar.DAY_OF_MONTH));
        assertEquals(Calendar.MARCH, cal.get(Calendar.MONTH));
        assertEquals(2012, cal.get(Calendar.YEAR));
        assertEquals(0, cal.get(Calendar.SECOND));
        assertEquals(0, cal.get(Calendar.MINUTE));
        assertEquals(0, cal.get(Calendar.HOUR_OF_DAY));
    }

    @Test
    public void testMonthValues() {
        // The use of these methods is not recommended.
        assertEquals(Month.JANUARY, Month.of(1));
        assertEquals(Month.JANUARY, Month.valueOf("JANUARY"));
        assertEquals(Month.DECEMBER, Month.of(12));
        assertEquals(Month.DECEMBER, Month.valueOf("DECEMBER"));

        int month = 5;
        int year = 1999;
        int day = 1;
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.MILLISECOND, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MONTH, month);
        cal.set(Calendar.YEAR, year);
        cal.set(Calendar.DAY_OF_MONTH, day);
        final LocalDate from = Dates.toLocalDate(cal.getTime());

        assertEquals(from, Dates.newLocalDate(year, Month.of(month + 1), day));
        assertEquals(from, Dates.firstOfMonth(Month.of(month + 1), year));

        cal = Calendar.getInstance();
        cal.setTime(Dates.toDate(from));
        assertEquals(1999, cal.get(Calendar.YEAR));
        assertEquals(Calendar.JUNE, cal.get(Calendar.MONTH));
        assertEquals(1, cal.get(Calendar.DAY_OF_MONTH));
        assertEquals(0, cal.get(Calendar.HOUR_OF_DAY));
        assertEquals(0, cal.get(Calendar.SECOND));
        assertEquals(0, cal.get(Calendar.MINUTE));
        assertEquals(0, cal.get(Calendar.MILLISECOND));

        LocalDate first = Dates.firstOfMonth(Month.of(month + 1), year);
        cal = Calendar.getInstance();
        cal.setTime(Dates.toDate(first));
        assertEquals(1999, cal.get(Calendar.YEAR));
        assertEquals(Calendar.JUNE, cal.get(Calendar.MONTH));
        assertEquals(1, cal.get(Calendar.DAY_OF_MONTH));
        assertEquals(0, cal.get(Calendar.HOUR_OF_DAY));
        assertEquals(0, cal.get(Calendar.SECOND));
        assertEquals(0, cal.get(Calendar.MINUTE));
        assertEquals(0, cal.get(Calendar.MILLISECOND));
    }

    @Test
    public void fromOffset() {
        final LocalDate dateStart = Dates.newLocalDate(2011, Month.JANUARY, 1);

        Calendar cal0 = Calendar.getInstance();
        cal0.setTime(Dates.toDate(dateStart));

        Calendar cal1 = makeCalendar(dateStart);
        assertEquals(cal0, cal1);

        assertEquals(1, cal1.get(Calendar.DAY_OF_MONTH));
        assertEquals(Calendar.JANUARY, cal1.get(Calendar.MONTH));
        assertEquals(2011, cal1.get(Calendar.YEAR));
        assertEquals(0, cal1.get(Calendar.HOUR));
        assertEquals(0, cal1.get(Calendar.MINUTE));
        assertEquals(0, cal1.get(Calendar.SECOND));
        assertEquals(0, cal1.get(Calendar.MILLISECOND));

        LocalDate date = Dates.fromOffset(dateStart, 1);
        cal1 = makeCalendar(date);
        assertEquals(2, cal1.get(Calendar.DAY_OF_MONTH));
        assertEquals(Calendar.JANUARY, cal1.get(Calendar.MONTH));
        assertEquals(2011, cal1.get(Calendar.YEAR));
        assertEquals(0, cal1.get(Calendar.HOUR));
        assertEquals(0, cal1.get(Calendar.MINUTE));
        assertEquals(0, cal1.get(Calendar.SECOND));
        assertEquals(0, cal1.get(Calendar.MILLISECOND));

        date = Dates.fromOffset(date, 1);
        cal1 = makeCalendar(date);
        assertEquals(3, cal1.get(Calendar.DAY_OF_MONTH));
        assertEquals(Calendar.JANUARY, cal1.get(Calendar.MONTH));
        assertEquals(2011, cal1.get(Calendar.YEAR));
        assertEquals(0, cal1.get(Calendar.HOUR));
        assertEquals(0, cal1.get(Calendar.MINUTE));
        assertEquals(0, cal1.get(Calendar.SECOND));
        assertEquals(0, cal1.get(Calendar.MILLISECOND));

        date = Dates.fromOffset(date, -2);
        cal1 = makeCalendar(date);
        assertEquals(1, cal1.get(Calendar.DAY_OF_MONTH));
        assertEquals(Calendar.JANUARY, cal1.get(Calendar.MONTH));
        assertEquals(2011, cal1.get(Calendar.YEAR));
        assertEquals(0, cal1.get(Calendar.HOUR));
        assertEquals(0, cal1.get(Calendar.MINUTE));
        assertEquals(0, cal1.get(Calendar.SECOND));
        assertEquals(0, cal1.get(Calendar.MILLISECOND));

        date = Dates.fromOffset(date, -1);
        cal1 = makeCalendar(date);
        assertEquals(31, cal1.get(Calendar.DAY_OF_MONTH));
        assertEquals(Calendar.DECEMBER, cal1.get(Calendar.MONTH));
        assertEquals(2010, cal1.get(Calendar.YEAR));
        assertEquals(0, cal1.get(Calendar.HOUR));
        assertEquals(0, cal1.get(Calendar.MINUTE));
        assertEquals(0, cal1.get(Calendar.SECOND));
        assertEquals(0, cal1.get(Calendar.MILLISECOND));

        date = Dates.fromOffset(date, 32);
        cal1 = makeCalendar(date);
        assertEquals(1, cal1.get(Calendar.DAY_OF_MONTH));
        assertEquals(Calendar.FEBRUARY, cal1.get(Calendar.MONTH));
        assertEquals(2011, cal1.get(Calendar.YEAR));
        assertEquals(0, cal1.get(Calendar.HOUR));
        assertEquals(0, cal1.get(Calendar.MINUTE));
        assertEquals(0, cal1.get(Calendar.SECOND));
        assertEquals(0, cal1.get(Calendar.MILLISECOND));

        date = Dates.fromOffset(date, -1);
        cal1 = makeCalendar(date);
        assertEquals(31, cal1.get(Calendar.DAY_OF_MONTH));
        assertEquals(Calendar.JANUARY, cal1.get(Calendar.MONTH));
        assertEquals(2011, cal1.get(Calendar.YEAR));
        assertEquals(0, cal1.get(Calendar.HOUR));
        assertEquals(0, cal1.get(Calendar.MINUTE));
        assertEquals(0, cal1.get(Calendar.SECOND));
        assertEquals(0, cal1.get(Calendar.MILLISECOND));
    }

    @Test
    public void firstDateOfYear() {
        assertEquals(Dates.newLocalDate(2000, Month.JANUARY, 1), Dates.firstDateOfYear(2000));
        assertEquals(Dates.newLocalDate(2009, Month.JANUARY, 1), Dates.firstDateOfYear(2009));
        assertEquals(Dates.newLocalDate(2010, Month.JANUARY, 1), Dates.firstDateOfYear(2010));
        assertEquals(Dates.newLocalDate(2011, Month.JANUARY, 1), Dates.firstDateOfYear(2011));
        assertEquals(Dates.newLocalDate(2012, Month.JANUARY, 1), Dates.firstDateOfYear(2012));

        // Check that the hours/minutes/seconds are zero-ed.
        Date date = Dates.toDate(Dates.firstDateOfYear(2012));
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        assertEquals(0, cal.get(Calendar.HOUR_OF_DAY));
        assertEquals(0, cal.get(Calendar.MINUTE));
        assertEquals(0, cal.get(Calendar.SECOND));
        assertEquals(1, cal.get(Calendar.DAY_OF_MONTH));
        assertEquals(Calendar.JANUARY, cal.get(Calendar.MONTH));
        assertEquals(2012, cal.get(Calendar.YEAR));
    }

    @Test
    public void firstDateOfYearTwo() {
        for (int year = 2000; year < 2025; year++) {
            LocalDate date = Dates.newLocalDate(year, Month.JANUARY, 1);
            assertEquals(date, Dates.firstDateOfYear(year));
        }
    }

    @Test
    public void lastDateOfYear() {
        assertEquals(Dates.newLocalDate(2000, Month.DECEMBER, 31), Dates.lastDateOfYear(2000));
        assertEquals(Dates.newLocalDate(2009, Month.DECEMBER, 31), Dates.lastDateOfYear(2009));
        assertEquals(Dates.newLocalDate(2010, Month.DECEMBER, 31), Dates.lastDateOfYear(2010));
        assertEquals(Dates.newLocalDate(2011, Month.DECEMBER, 31), Dates.lastDateOfYear(2011));
        assertEquals(Dates.newLocalDate(2012, Month.DECEMBER, 31), Dates.lastDateOfYear(2012));

        // Check that the hours/minutes/seconds are zero-ed.
        LocalDate date = Dates.lastDateOfYear(2012);
        Calendar cal = Calendar.getInstance();
        cal.setTime(Dates.toDate(date));
        assertEquals(0, cal.get(Calendar.HOUR_OF_DAY));
        assertEquals(0, cal.get(Calendar.MINUTE));
        assertEquals(0, cal.get(Calendar.SECOND));
        assertEquals(31, cal.get(Calendar.DAY_OF_MONTH));
        assertEquals(Calendar.DECEMBER, cal.get(Calendar.MONTH));
        assertEquals(2012, cal.get(Calendar.YEAR));
    }

    @Test
    public void lastDateOfYearTwo() {
        LocalDate date = Dates.newLocalDate(2000, Month.JANUARY, 1);
        assertEquals(Dates.newLocalDate(2000, Month.DECEMBER, 31), Dates.lastDateOfYear(date));
        date = Dates.newLocalDate(2009, Month.JANUARY, 1);
        assertEquals(Dates.newLocalDate(2009, Month.DECEMBER, 31), Dates.lastDateOfYear(date));
        date = Dates.newLocalDate(2010, Month.JANUARY, 1);
        assertEquals(Dates.newLocalDate(2010, Month.DECEMBER, 31), Dates.lastDateOfYear(date));
        date = Dates.newLocalDate(2011, Month.JANUARY, 1);
        assertEquals(Dates.newLocalDate(2011, Month.DECEMBER, 31), Dates.lastDateOfYear(date));
        date = Dates.newLocalDate(2012, Month.FEBRUARY, 29);
        assertEquals(Dates.newLocalDate(2012, Month.DECEMBER, 31), Dates.lastDateOfYear(date));
    }

    @Test
    public void testMisc() {
        for (int year = 1962; year < 2050; year++) {
            final LocalDate endOfYear = Dates.newLocalDate(year, Month.DECEMBER, 31);

            Calendar cal = Calendar.getInstance();
            cal.set(Calendar.MILLISECOND, 0);
            cal.set(Calendar.SECOND, 0);
            cal.set(Calendar.MINUTE, 0);
            cal.set(Calendar.HOUR_OF_DAY, 0);
            cal.set(Calendar.YEAR, year);
            cal.set(Calendar.MONTH, Calendar.DECEMBER);
            cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH));

            assertThat(endOfYear, equalTo(Dates.toLocalDate(cal.getTime())));

            cal.add(Calendar.DAY_OF_MONTH, 1);
            LocalDate firstOfNextYear = Dates.firstOfMonth(Month.JANUARY, year + 1);
            assertThat(firstOfNextYear, equalTo(Dates.toLocalDate(cal.getTime())));

            firstOfNextYear = Dates.firstOfNextYear(endOfYear);
            assertThat(firstOfNextYear, equalTo(Dates.toLocalDate(cal.getTime())));

            firstOfNextYear = Dates.firstOfNextYear(Dates.lastDateOfYear(year));
            assertThat(firstOfNextYear, equalTo(Dates.toLocalDate(cal.getTime())));
        }
    }

    @Test
    public void month() {
        for (Month month : Month.values()) {
            int year = 2012;
            LocalDate lastOfMonth = Dates.lastDateOfMonth(month, year);
            assertEquals(month, Dates.month(lastOfMonth));
            LocalDate firstOfMonth = Dates.firstDateOfMonth(month, year);
            assertEquals(month, Dates.month(firstOfMonth));
        }
    }

    @Test
    public void currentYear() {
        Calendar cal = makeCalendar();
        int year = cal.get(Calendar.YEAR);
        assertEquals(year, Dates.currentYear());
    }

    @Test
    public void yearOfDate() {
        for (int year = 2000; year < 2050; year++) {
            assertEquals(year, Dates.yearOfDate(Dates.firstDateOfYear(year)));
            assertEquals(year, Dates.yearOfDate(Dates.lastDateOfYear(year)));
            assertEquals(year, Dates.yearOfDate(Dates.lastDateOfMonth(Month.FEBRUARY, year)));
        }
    }

    @Test
    public void dayOfWeek() {
        LocalDate lod = Dates.toLocalDate(Dates.toDate(Dates.firstDateOfYear(2012)));
        for (int i = 0; i < 366; i++) {
            DayOfWeek dayOfWeek = lod.getDayOfWeek();
            assertEquals(dayOfWeek, Dates.dayOfWeek(lod));
            lod = lod.plusDays(1);
        }
        assertEquals(Dates.newLocalDate(2013, Month.JANUARY, 1), lod);

        // Just a random one, a Friday.
        LocalDate date = Dates.newLocalDate(2012, Month.DECEMBER, 7);
        assertEquals(DayOfWeek.FRIDAY, Dates.dayOfWeek(date));

    }

    @Test
    public void dayOfMonth() {
        int checks = 0;
        Calendar cal = makeCalendar(Dates.toDate(Dates.firstDateOfYear(2013)));
        assertEquals(Dates.firstDateOfYear(2013), Dates.toLocalDate(cal.getTime()));
        assertEquals(Dates.newLocalDate(2013, Month.JANUARY, 1), Dates.toLocalDate(cal.getTime()));

        for (Month m : Month.values()) {
            cal.set(Calendar.MONTH, m.getValue() - 1);
            int maxDays = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
            for (int d = 1; d <= maxDays; d++) {
                LocalDate localDate = Dates.toLocalDate(cal.getTime());
                assertEquals(Dates.dayOfMonth(localDate), cal.get(Calendar.DAY_OF_MONTH));
                cal.add(Calendar.DAY_OF_MONTH, 1);
                checks++;
            }
            LocalDate localDate = Dates.toLocalDate(cal.getTime());
            assertEquals(1, Dates.dayOfMonth(localDate));
        }

        assertEquals(365, checks);
        assertEquals(Dates.newLocalDate(2014, Month.JANUARY, 1), Dates.toLocalDate(cal.getTime()));
    }

    @Test
    public void formatDate() {
        LocalDate date = null;
        String dateStr0 = Dates.formatDate(date, "MM/yyyy");
        assertEquals("", dateStr0);

        LocalDateTime dateTime = null;
        String dateStrE = Dates.formatDate(dateTime, "MM/yyyy");
        assertEquals("", dateStrE);

        date = Dates.newLocalDate(2012, Month.FEBRUARY, 29);
        String dateStr1 = Dates.formatDate(date, "MM/yyyy");
        assertEquals("02/2012", dateStr1);
        String dateStrA = Dates.formatDate(Dates.toDate(date));
        assertEquals("02/29/2012", dateStrA);

        String dateStrD = Dates.formatDate(date, null);
        assertEquals("2012-02-29", dateStrD);

        date = Dates.newLocalDate(2012, Month.FEBRUARY, 29);
        String dateStr2 = Dates.formatDate(date, "M/yyyy");
        assertEquals("2/2012", dateStr2);

        date = Dates.newLocalDate(2013, Month.NOVEMBER, 30);
        String dateStr3 = Dates.formatDate(date, "MM/yyyy");
        assertEquals("11/2013", dateStr3);

        date = Dates.newLocalDate(2012, Month.FEBRUARY, 29);
        String dateStr4 = Dates.formatDate(date, "MMM/yyyy");
        assertEquals("Feb/2012", dateStr4);

        date = Dates.newLocalDate(2013, Month.NOVEMBER, 30);
        String dateStr5 = Dates.formatDate(date, "MMM/yyyy");
        assertEquals("Nov/2013", dateStr5);

        date = Dates.newLocalDate(2013, Month.DECEMBER, 30);
        String dateStr6 = Dates.formatDate(date, "M/yyyy");
        assertEquals("12/2013", dateStr6);
        String dateStrB = Dates.formatDate(Dates.toDate(date));
        assertEquals("12/30/2013", dateStrB);

        LocalTime localTime = LocalTime.of(12, 34, 56);
        LocalDateTime localDateTime = LocalDateTime.of(date, localTime);
        String dateStrC = Dates.formatDate(localDateTime, "uuuu-MM-dd-HH-mm-ss");
        assertEquals("2013-12-30-12-34-56", dateStrC);

        // Default pattern if internal error.
        dateStrC = Dates.formatDate(localDateTime, null);
        assertEquals("2013-12-30T12:34:56", dateStrC);

        // Invalid pattern; defaults to yyyy-MM-dd.
        String dateStr7 = Dates.formatDate(date, "what?");
        assertEquals("2013-12-30", dateStr7);

        // Not advised usage.
        String dateStr8 = Dates.formatDate(date, null);
        assertEquals("2013-12-30", dateStr8);
    }

    @Test
    public void format() {
        LocalDate date = null;
        assertEquals("", Dates.formatDate(date));

        Calendar cal = Calendar.getInstance();
        date = Dates.toLocalDate(cal.getTime());
        String dateStr0 = new SimpleDateFormat("MM/dd/yyyy").format(Dates.toDate(date));
        String dateStr1 = Dates.formatDate(date);
        assertEquals(dateStr1, dateStr0);

        date = Dates.newLocalDate(2012, Month.FEBRUARY, 29);
        String dateStr2 = Dates.formatDate(date);
        assertEquals("02/29/2012", dateStr2);

        date = Dates.newLocalDate(2012, Month.FEBRUARY, 29);
        String dateStr3 = Dates.formatDate(date);
        assertEquals("02/29/2012", dateStr3);

        date = Dates.newLocalDate(2013, Month.NOVEMBER, 30);
        String dateStr4 = Dates.formatDate(date);
        assertEquals("11/30/2013", dateStr4);

        date = Dates.newLocalDate(2013, Month.DECEMBER, 30);
        String dateStr7 = Dates.formatDate(date);
        assertEquals("12/30/2013", dateStr7);

        java.sql.Date sqlDate =
                new java.sql.Date(Dates.toDate(date).getTime());
        String dateStr8 = Dates.formatDate(sqlDate);
        assertEquals("12/30/2013", dateStr8);
        assertEquals(date, Dates.toLocalDate(dateStr8, "M/d/yyyy"));
    }

    @Test
    public void dateToLocalDate() {
        LocalDate d = Dates.newLocalDate(2016, Month.OCTOBER, 31);
        assertEquals(31, d.getDayOfMonth());
        assertEquals(2016, d.getYear());
        assertEquals(Month.OCTOBER, d.getMonth());
    }

    @Test
    public void localDateTimeToDate() {
        LocalDate date = Dates.newLocalDate(2013, Month.DECEMBER, 30);
        LocalTime time = LocalTime.of(12, 34, 56);
        LocalDateTime dateTime = LocalDateTime.of(date, time);

        String dateStr = Dates.formatDate(dateTime, "uuuu-MM-dd-HH-mm-ss");
        assertEquals("2013-12-30-12-34-56", dateStr);

        dateStr = Dates.formatDate(dateTime, null);
        assertEquals("2013-12-30T12:34:56", dateStr);
    }

    @Test
    public void testConstructorIsPrivate() throws Exception {
        Constructor<Dates> constructor = Dates.class.getDeclaredConstructor();
        assertTrue(Modifier.isPrivate(constructor.getModifiers()));
        constructor.setAccessible(true);
        constructor.newInstance();
    }

    @Test
    public void toLocalDate() {
        final LocalDate TAX_DATE = Dates.newLocalDate(2017, Month.APRIL, 15);
        final LocalDate MAY_DATE = Dates.newLocalDate(2016, Month.MAY, 1);

        LocalDate d0 = Dates.toLocalDate("2017-04-15");
        assertThat(TAX_DATE, equalTo(d0));

        LocalDate d1 = Dates.toLocalDate("2017-4-15");
        assertThat(TAX_DATE, equalTo(d1));

        LocalDate d2 = Dates.toLocalDate("2016-05-01");
        assertThat(MAY_DATE, equalTo(d2));

        LocalDate d3 = Dates.toLocalDate("2016-5-01");
        assertThat(MAY_DATE, equalTo(d3));

        LocalDate d4 = Dates.toLocalDate("2016-05-1");
        assertThat(MAY_DATE, equalTo(d4));

        LocalDate d5 = Dates.toLocalDate("2016-5-1");
        assertThat(MAY_DATE, equalTo(d5));

        // Bad format.
        LocalDate d6 = Dates.toLocalDate("what?");
        assertThat(d6, equalTo(null));

        // Null argument.
        LocalDate d7 = Dates.toLocalDate((Date) null);
        assertThat(d7, equalTo(null));
    }

    @Test
    public void toLocalDateWithFormat() {
        String s = "19860701";
        String format = "yyyyMMdd";
        LocalDate dA = Dates.toLocalDate(s, format);
        assertThat(dA, equalTo(Dates.newLocalDate(1986, Month.JULY, 1)));

        s = "20110701";
        LocalDate dB = Dates.toLocalDate(s, format);
        assertThat(dB, equalTo(Dates.newLocalDate(2011, Month.JULY, 1)));

        s = null;
        LocalDate dC = Dates.toLocalDate(s, format);
        assertThat(dC, equalTo(null));

        s = "20110701";
        format = null;
        LocalDate dD = Dates.toLocalDate(s, format);
        assertThat(dD, equalTo(null));
    }

    @Test
    public void isOnOrAfter() {
        LocalDate christmas1962 = LocalDate.of(1962, Month.DECEMBER, 25);
        LocalDate christmas1963 = LocalDate.of(1963, Month.DECEMBER, 25);

        assertTrue(Dates.isOnOrAfter(christmas1963, christmas1962));
        assertTrue(Dates.isOnOrAfter(christmas1962, christmas1962));
        assertFalse(Dates.isOnOrAfter(christmas1962, christmas1963));
    }

    @Test
    public void isOnOrBefore() {
        LocalDate christmas1962 = LocalDate.of(1962, Month.DECEMBER, 25);
        LocalDate christmas1963 = LocalDate.of(1963, Month.DECEMBER, 25);

        assertFalse(Dates.isOnOrBefore(christmas1963, christmas1962));
        assertTrue(Dates.isOnOrBefore(christmas1962, christmas1962));
        assertTrue(Dates.isOnOrBefore(christmas1962, christmas1963));
    }
}
