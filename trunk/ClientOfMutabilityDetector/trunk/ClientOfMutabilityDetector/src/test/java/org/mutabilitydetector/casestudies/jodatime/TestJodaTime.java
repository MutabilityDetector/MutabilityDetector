package org.mutabilitydetector.casestudies.jodatime;

import static org.mutabilitydetector.unittesting.AllowedReason.allowingForSubclassing;
import static org.mutabilitydetector.unittesting.AllowedReason.provided;
import static org.mutabilitydetector.unittesting.MutabilityAssert.assertImmutable;
import static org.mutabilitydetector.unittesting.MutabilityAssert.assertInstancesOf;
import static org.mutabilitydetector.unittesting.MutabilityMatchers.areEffectivelyImmutable;
import static org.mutabilitydetector.unittesting.MutabilityMatchers.areImmutable;
import net.ttsui.junit.rules.pending.PendingImplementation;
import net.ttsui.junit.rules.pending.PendingRule;

import org.joda.time.Chronology;
import org.joda.time.DateTimeField;
import org.joda.time.DurationField;
import org.joda.time.chrono.AssembledChronology;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.MethodRule;

@SuppressWarnings("deprecation")
public class TestJodaTime {

    @Rule
    public MethodRule pendingRule = new PendingRule();

    @Test public void testorg_joda_time_base_AbstractInstant() {
        assertInstancesOf(org.joda.time.base.AbstractInstant.class,
                          areImmutable(),
                          allowingForSubclassing());
    }
    
    @PendingImplementation("Partly due to issue 21, partly due to serialisation weirdness")
    @SuppressWarnings("unchecked")
    @Test public void testorg_joda_time_chrono_AssembledChronology() {
        assertInstancesOf(AssembledChronology.class, 
                          areEffectivelyImmutable(), 
                          provided(Chronology.class).isAlsoImmutable(),
                          provided(DateTimeField.class).isAlsoImmutable(),
                          provided(DurationField.class).isAlsoImmutable(), // DurationField is not specified as immutable
                          provided(Object.class).isAlsoImmutable(), // iParam
                          allowingForSubclassing());
    }
    
    @PendingImplementation
    @Test public void testorg_joda_time_chrono_BaseChronology() {
        assertImmutable(org.joda.time.chrono.BaseChronology.class);
    }
    
// Class is not visible
//    @PendingImplementation
//    @Test public void testorg_joda_time_chrono_BasicChronology() {
//        assertImmutable(org.joda.time.chrono.BasicChronology.class);
//    }
    
    // Class is not visible
//    @PendingImplementation
//    @Test public void testorg_joda_time_chrono_BasicFixedMonthChronology() {
//        assertImmutable(org.joda.time.chrono.BasicFixedMonthChronology.class);
//    }
    
    
//    Class is not visible
//    @PendingImplementation
//    @Test public void testorg_joda_time_chrono_BasicGJChronology() {
//        assertImmutable(org.joda.time.chrono.BasicGJChronology.class);
//    }

    @PendingImplementation
    @Test public void testorg_joda_time_chrono_BuddhistChronology() {
        assertImmutable(org.joda.time.chrono.BuddhistChronology.class);
    }
    
    @PendingImplementation
    @Test public void testorg_joda_time_chrono_CopticChronology() {
        assertImmutable(org.joda.time.chrono.CopticChronology.class);
    }
    
    @PendingImplementation
    @Test public void testorg_joda_time_chrono_EthiopicChronology() {
        assertImmutable(org.joda.time.chrono.EthiopicChronology.class);
    }
    
    @PendingImplementation
    @Test public void testorg_joda_time_chrono_GJChronology() {
        assertImmutable(org.joda.time.chrono.GJChronology.class);
    }
    @Test public void testorg_joda_time_chrono_GregorianChronology() {
        assertImmutable(org.joda.time.chrono.GregorianChronology.class);
    }
    
    @PendingImplementation
    @Test public void testorg_joda_time_chrono_IslamicChronology() {
        assertImmutable(org.joda.time.chrono.IslamicChronology.class);
    }
    
    @PendingImplementation
    @Test public void testorg_joda_time_chrono_ISOChronology() {
        assertImmutable(org.joda.time.chrono.ISOChronology.class);
    }
    
    @PendingImplementation
    @Test public void testorg_joda_time_chrono_JulianChronology() {
        assertImmutable(org.joda.time.chrono.JulianChronology.class);
    }
    
    @PendingImplementation
    @Test public void testorg_joda_time_chrono_LenientChronology() {
        assertImmutable(org.joda.time.chrono.LenientChronology.class);
    }
    
    @PendingImplementation
    @Test public void testorg_joda_time_chrono_LimitChronology() {
        assertImmutable(org.joda.time.chrono.LimitChronology.class);
    }
    
    @PendingImplementation
    @Test public void testorg_joda_time_chrono_StrictChronology() {
        assertImmutable(org.joda.time.chrono.StrictChronology.class);
    }

    @PendingImplementation
    @Test public void testorg_joda_time_chrono_ZonedChronology() {
        assertImmutable(org.joda.time.chrono.ZonedChronology.class);
    }
    
//    Class is not visible
//    @PendingImplementation
//    @Test public void testorg_joda_time_convert_ConverterSet() {
//        assertImmutable(org.joda.time.convert.ConverterSet.class);
//    }
    
    @Test public void testorg_joda_time_DateMidnight() {
        assertImmutable(org.joda.time.DateMidnight.class);
    }
    @Test public void testorg_joda_time_DateTime() {
        assertImmutable(org.joda.time.DateTime.class);
    }
    
    @PendingImplementation
    @Test public void testorg_joda_time_DateTimeComparator() {
        assertImmutable(org.joda.time.DateTimeComparator.class);
    }
    
    @PendingImplementation
    @Test public void testorg_joda_time_DateTimeConstants() {
        assertImmutable(org.joda.time.DateTimeConstants.class);
    }
    
    @PendingImplementation
    @Test public void testorg_joda_time_DateTimeZone() {
        assertImmutable(org.joda.time.DateTimeZone.class);
    }
    @Test public void testorg_joda_time_Days() {
        assertImmutable(org.joda.time.Days.class);
    }
    @Test public void testorg_joda_time_Duration() {
        assertImmutable(org.joda.time.Duration.class);
    }
    
    @PendingImplementation
    @Test public void testorg_joda_time_field_AbstractReadableInstantFieldProperty() {
        assertImmutable(org.joda.time.field.AbstractReadableInstantFieldProperty.class);
    }
    
    @PendingImplementation
    @Test public void testorg_joda_time_field_BaseDateTimeField() {
        assertImmutable(org.joda.time.field.BaseDateTimeField.class);
    }
    
    @PendingImplementation
    @Test public void testorg_joda_time_field_BaseDurationField() {
        assertImmutable(org.joda.time.field.BaseDurationField.class);
    }
    
    @PendingImplementation
    @Test public void testorg_joda_time_field_DecoratedDateTimeField() {
        assertImmutable(org.joda.time.field.DecoratedDateTimeField.class);
    }
    
    @PendingImplementation
    @Test public void testorg_joda_time_field_DecoratedDurationField() {
        assertImmutable(org.joda.time.field.DecoratedDurationField.class);
    }
    
    @PendingImplementation
    @Test public void testorg_joda_time_field_DelegatedDateTimeField() {
        assertImmutable(org.joda.time.field.DelegatedDateTimeField.class);
    }
    
    @PendingImplementation
    @Test public void testorg_joda_time_field_DelegatedDurationField() {
        assertImmutable(org.joda.time.field.DelegatedDurationField.class);
    }
    
    @PendingImplementation
    @Test public void testorg_joda_time_field_DividedDateTimeField() {
        assertImmutable(org.joda.time.field.DividedDateTimeField.class);
    }
    
    @Test public void testorg_joda_time_field_FieldUtils() {
        assertImmutable(org.joda.time.field.FieldUtils.class);
    }
    
    @PendingImplementation
    @Test public void testorg_joda_time_field_ImpreciseDateTimeField() {
        assertImmutable(org.joda.time.field.ImpreciseDateTimeField.class);
    }
    
    @PendingImplementation
    @Test public void testorg_joda_time_field_LenientDateTimeField() {
        assertImmutable(org.joda.time.field.LenientDateTimeField.class);
    }
    @Test public void testorg_joda_time_field_MillisDurationField() {
        assertImmutable(org.joda.time.field.MillisDurationField.class);
    }
    
    @PendingImplementation
    @Test public void testorg_joda_time_field_OffsetDateTimeField() {
        assertImmutable(org.joda.time.field.OffsetDateTimeField.class);
    }
    
    @PendingImplementation
    @Test public void testorg_joda_time_field_PreciseDateTimeField() {
        assertImmutable(org.joda.time.field.PreciseDateTimeField.class);
    }
    
    @PendingImplementation
    @Test public void testorg_joda_time_field_PreciseDurationDateTimeField() {
        assertImmutable(org.joda.time.field.PreciseDurationDateTimeField.class);
    }
    
    @PendingImplementation
    @Test public void testorg_joda_time_field_PreciseDurationField() {
        assertImmutable(org.joda.time.field.PreciseDurationField.class);
    }
    
    @PendingImplementation
    @Test public void testorg_joda_time_field_RemainderDateTimeField() {
        assertImmutable(org.joda.time.field.RemainderDateTimeField.class);
    }
    
    @PendingImplementation
    @Test public void testorg_joda_time_field_ScaledDurationField() {
        assertImmutable(org.joda.time.field.ScaledDurationField.class);
    }
    
    @PendingImplementation
    @Test public void testorg_joda_time_field_SkipDateTimeField() {
        assertImmutable(org.joda.time.field.SkipDateTimeField.class);
    }
    
    @PendingImplementation
    @Test public void testorg_joda_time_field_SkipUndoDateTimeField() {
        assertImmutable(org.joda.time.field.SkipUndoDateTimeField.class);
    }
    
    @PendingImplementation
    @Test public void testorg_joda_time_field_StrictDateTimeField() {
        assertImmutable(org.joda.time.field.StrictDateTimeField.class);
    }
    
    @PendingImplementation
    @Test public void testorg_joda_time_field_UnsupportedDateTimeField() {
        assertImmutable(org.joda.time.field.UnsupportedDateTimeField.class);
    }
    
    @PendingImplementation
    @Test public void testorg_joda_time_field_UnsupportedDurationField() {
        assertImmutable(org.joda.time.field.UnsupportedDurationField.class);
    }
    @Test public void testorg_joda_time_field_ZeroIsMaxDateTimeField() {
        assertImmutable(org.joda.time.field.ZeroIsMaxDateTimeField.class);
    }
    
    @PendingImplementation
    @Test public void testorg_joda_time_format_DateTimeFormat() {
        assertImmutable(org.joda.time.format.DateTimeFormat.class);
    }
    
    @PendingImplementation
    @Test public void testorg_joda_time_format_DateTimeFormatter() {
        assertImmutable(org.joda.time.format.DateTimeFormatter.class);
    }
    
    @Test public void testorg_joda_time_format_FormatUtils() {
        assertImmutable(org.joda.time.format.FormatUtils.class);
    }
    
    @PendingImplementation
    @Test public void testorg_joda_time_format_ISODateTimeFormat() {
        assertImmutable(org.joda.time.format.ISODateTimeFormat.class);
    }
    
    @PendingImplementation
    @Test public void testorg_joda_time_format_ISOPeriodFormat() {
        assertImmutable(org.joda.time.format.ISOPeriodFormat.class);
    }
    
    @PendingImplementation
    @Test public void testorg_joda_time_format_PeriodFormat() {
        assertImmutable(org.joda.time.format.PeriodFormat.class);
    }
    
    @PendingImplementation
    @Test public void testorg_joda_time_format_PeriodFormatter() {
        assertImmutable(org.joda.time.format.PeriodFormatter.class);
    }
    @Test public void testorg_joda_time_Hours() {
        assertImmutable(org.joda.time.Hours.class);
    }
    @Test public void testorg_joda_time_Instant() {
        assertImmutable(org.joda.time.Instant.class);
    }
    @Test public void testorg_joda_time_Interval() {
        assertImmutable(org.joda.time.Interval.class);
    }
    
    @PendingImplementation
    @Test public void testorg_joda_time_JodaTimePermission() {
        assertImmutable(org.joda.time.JodaTimePermission.class);
    }
    
    @PendingImplementation
    @Test public void testorg_joda_time_LocalDate() {
        assertImmutable(org.joda.time.LocalDate.class);
    }
    
    @PendingImplementation
    @Test public void testorg_joda_time_LocalDateTime() {
        assertImmutable(org.joda.time.LocalDateTime.class);
    }
    
    @PendingImplementation
    @Test public void testorg_joda_time_LocalTime() {
        assertImmutable(org.joda.time.LocalTime.class);
    }
    @Test public void testorg_joda_time_Minutes() {
        assertImmutable(org.joda.time.Minutes.class);
    }
    @Test public void testorg_joda_time_MonthDay() {
        assertImmutable(org.joda.time.MonthDay.class);
    }
    @Test public void testorg_joda_time_Months() {
        assertImmutable(org.joda.time.Months.class);
    }
    
    @PendingImplementation
    @Test public void testorg_joda_time_Partial() {
        assertImmutable(org.joda.time.Partial.class);
    }
    @Test public void testorg_joda_time_Period() {
        assertImmutable(org.joda.time.Period.class);
    }
    
    @PendingImplementation
    @Test public void testorg_joda_time_PeriodType() {
        assertImmutable(org.joda.time.PeriodType.class);
    }
    @Test public void testorg_joda_time_Seconds() {
        assertImmutable(org.joda.time.Seconds.class);
    }
    @Test public void testorg_joda_time_TimeOfDay() {
        assertImmutable(org.joda.time.TimeOfDay.class);
    }
    
    @PendingImplementation
    @Test public void testorg_joda_time_tz_CachedDateTimeZone() {
        assertImmutable(org.joda.time.tz.CachedDateTimeZone.class);
    }
    
    @PendingImplementation
    @Test public void testorg_joda_time_tz_DefaultNameProvider() {
        assertImmutable(org.joda.time.tz.DefaultNameProvider.class);
    }
    
    @PendingImplementation
    @Test public void testorg_joda_time_tz_FixedDateTimeZone() {
        assertImmutable(org.joda.time.tz.FixedDateTimeZone.class);
    }
    @Test public void testorg_joda_time_tz_UTCProvider() {
        assertImmutable(org.joda.time.tz.UTCProvider.class);
    }
    
    @PendingImplementation
    @Test public void testorg_joda_time_tz_ZoneInfoProvider() {
        assertImmutable(org.joda.time.tz.ZoneInfoProvider.class);
    }
    @Test public void testorg_joda_time_Weeks() {
        assertImmutable(org.joda.time.Weeks.class);
    }
    @Test public void testorg_joda_time_YearMonth() {
        assertImmutable(org.joda.time.YearMonth.class);
    }
    @Test public void testorg_joda_time_YearMonthDay() {
        assertImmutable(org.joda.time.YearMonthDay.class);
    }
    @Test public void testorg_joda_time_Years() {
        assertImmutable(org.joda.time.Years.class);
    }
}
