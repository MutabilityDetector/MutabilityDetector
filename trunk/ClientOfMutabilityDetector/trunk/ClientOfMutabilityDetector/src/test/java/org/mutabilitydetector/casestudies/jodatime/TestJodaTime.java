package org.mutabilitydetector.casestudies.jodatime;

import static org.mutabilitydetector.unittesting.AllowedReason.provided;
import static org.mutabilitydetector.unittesting.MutabilityAssert.assertImmutable;
import static org.mutabilitydetector.unittesting.MutabilityAssert.assertInstancesOf;
import static org.mutabilitydetector.unittesting.matchers.MutabilityMatchers.areImmutable;

import org.joda.time.Chronology;
import org.joda.time.DateTimeZone;
import org.joda.time.Days;
import org.joda.time.Hours;
import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;
import org.joda.time.LocalTime;
import org.joda.time.Minutes;
import org.joda.time.Months;
import org.joda.time.Partial;
import org.joda.time.Period;
import org.joda.time.TimeOfDay;
import org.joda.time.Weeks;
import org.joda.time.YearMonthDay;
import org.joda.time.Years;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.PeriodFormatter;
import org.junit.Test;

@SuppressWarnings("deprecation") 
public class TestJodaTime {
    @Test
    public void testorg_joda_time_LocalDateTime() {
        assertInstancesOf(LocalDateTime.class, areImmutable(),provided(Chronology.class).isAlsoImmutable());
    }

    @Test
    public void testorg_joda_time_LocalTime() {
    	assertInstancesOf(LocalTime.class, areImmutable(),provided(Chronology.class).isAlsoImmutable());
    }

    @Test
    public void testorg_joda_time_format_DateTimeFormatter() {
        assertImmutable(DateTimeFormatter.class);
    }

    @Test
    public void testorg_joda_time_format_PeriodFormatter() {
        assertImmutable(PeriodFormatter.class);
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
    	assertInstancesOf(LocalDate.class, areImmutable(),provided(Chronology.class).isAlsoImmutable());
    }

    @Test
    public void testorg_joda_time_DateTimeZone() {
        assertImmutable(DateTimeZone.class);
    }

    @Test
    public void testorg_joda_time_Months() {
        assertImmutable(Months.class);
    }

    @Test
    public void testorg_joda_time_Years() {
        assertImmutable(Years.class);
    }

    @Test
    public void testorg_joda_time_YearMonthDay() {
        assertImmutable(YearMonthDay.class);
    }

    @Test
    public void testorg_joda_time_TimeOfDay() {
        assertImmutable(TimeOfDay.class);
    }

    @Test
    public void testorg_joda_time_Days() {
        assertImmutable(Days.class);
    }

    @Test
    public void testorg_joda_time_Weeks() {
        assertImmutable(Weeks.class);
    }

    @Test
    public void testorg_joda_time_Hours() {
        assertImmutable(Hours.class);
    }

    @Test
    public void testorg_joda_time_Period() {
        assertImmutable(Period.class);
    }

    @Test
    public void testorg_joda_time_Partial() {
        assertInstancesOf(Partial.class, areImmutable(),provided(Chronology.class).isAlsoImmutable());
    }

    @Test
    public void testorg_joda_time_Minutes() {
        assertImmutable(Minutes.class);
    }

}
