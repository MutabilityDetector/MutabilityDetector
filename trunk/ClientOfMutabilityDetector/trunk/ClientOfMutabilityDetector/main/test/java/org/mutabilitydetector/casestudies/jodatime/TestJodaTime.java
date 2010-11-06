package org.mutabilitydetector.casestudies.jodatime;

import org.junit.Test;
import static org.mutabilitydetector.unittesting.MutabilityAssert.assertImmutable;

@SuppressWarnings("deprecation") 
public class TestJodaTime {
    @Test
    public void testorg_joda_time_LocalDateTime() {
        assertImmutable(org.joda.time.LocalDateTime.class);
    }

    @Test
    public void testorg_joda_time_LocalTime() {
        assertImmutable(org.joda.time.LocalTime.class);
    }

    @Test
    public void testorg_joda_time_format_DateTimeFormatter() {
        assertImmutable(org.joda.time.format.DateTimeFormatter.class);
    }

    @Test
    public void testorg_joda_time_format_PeriodFormatter() {
        assertImmutable(org.joda.time.format.PeriodFormatter.class);
    }

    @Test
    public void testorg_joda_time_Seconds() {
        assertImmutable(org.joda.time.Seconds.class);
    }

    @Test
    public void testorg_joda_time_DateMidnight() {
        assertImmutable(org.joda.time.DateMidnight.class);
    }

    @Test
    public void testorg_joda_time_DateTime() {
        assertImmutable(org.joda.time.DateTime.class);
    }

    @Test
    public void testorg_joda_time_Duration() {
        assertImmutable(org.joda.time.Duration.class);
    }

    @Test
    public void testorg_joda_time_LocalDate() {
        assertImmutable(org.joda.time.LocalDate.class);
    }

    @Test
    public void testorg_joda_time_DateTimeZone() {
        assertImmutable(org.joda.time.DateTimeZone.class);
    }

    @Test
    public void testorg_joda_time_Months() {
        assertImmutable(org.joda.time.Months.class);
    }

    @Test
    public void testorg_joda_time_Years() {
        assertImmutable(org.joda.time.Years.class);
    }

    @Test
    public void testorg_joda_time_YearMonthDay() {
        assertImmutable(org.joda.time.YearMonthDay.class);
    }

    @Test
    public void testorg_joda_time_TimeOfDay() {
        assertImmutable(org.joda.time.TimeOfDay.class);
    }

    @Test
    public void testorg_joda_time_Days() {
        assertImmutable(org.joda.time.Days.class);
    }

    @Test
    public void testorg_joda_time_Weeks() {
        assertImmutable(org.joda.time.Weeks.class);
    }

    @Test
    public void testorg_joda_time_Hours() {
        assertImmutable(org.joda.time.Hours.class);
    }

    @Test
    public void testorg_joda_time_Period() {
        assertImmutable(org.joda.time.Period.class);
    }

    @Test
    public void testorg_joda_time_Partial() {
        assertImmutable(org.joda.time.Partial.class);
    }

    @Test
    public void testorg_joda_time_Minutes() {
        assertImmutable(org.joda.time.Minutes.class);
    }

}
