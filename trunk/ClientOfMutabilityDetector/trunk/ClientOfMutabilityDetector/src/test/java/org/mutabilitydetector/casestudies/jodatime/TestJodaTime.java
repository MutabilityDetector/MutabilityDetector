package org.mutabilitydetector.casestudies.jodatime;

import static org.mutabilitydetector.unittesting.AllowedReason.provided;
import static org.mutabilitydetector.unittesting.MutabilityAssert.assertImmutable;
import static org.mutabilitydetector.unittesting.MutabilityAssert.assertInstancesOf;
import static org.mutabilitydetector.unittesting.matchers.MutabilityMatchers.areImmutable;

import java.util.Locale;

import net.ttsui.junit.rules.pending.PendingImplementation;
import net.ttsui.junit.rules.pending.PendingRule;

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
import org.joda.time.PeriodType;
import org.joda.time.TimeOfDay;
import org.joda.time.Weeks;
import org.joda.time.YearMonthDay;
import org.joda.time.Years;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.PeriodFormatter;
import org.joda.time.format.PeriodPrinter;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.MethodRule;

@SuppressWarnings("deprecation")
public class TestJodaTime {

	@Rule
	public MethodRule pendingRule = new PendingRule();

	@Test
	public void testorg_joda_time_base_AbstractDuration() {
		assertImmutable(org.joda.time.base.AbstractDuration.class);
	}

	@Test
	public void testorg_joda_time_base_AbstractInstant() {
		assertImmutable(org.joda.time.base.AbstractInstant.class);
	}

	@Test
	public void testorg_joda_time_base_AbstractInterval() {
		assertImmutable(org.joda.time.base.AbstractInterval.class);
	}

	@Test
	public void testorg_joda_time_base_AbstractPeriod() {
		assertImmutable(org.joda.time.base.AbstractPeriod.class);
	}

	@Test
	public void testorg_joda_time_base_BaseDateTime() {
		assertImmutable(org.joda.time.base.BaseDateTime.class);
	}

	@Test
	public void testorg_joda_time_base_BaseSingleFieldPeriod() {
		assertImmutable(org.joda.time.base.BaseSingleFieldPeriod.class);
	}

	@Test
	public void testorg_joda_time_chrono_AssembledChronology() {
		assertImmutable(org.joda.time.chrono.AssembledChronology.class);
	}

	@Test
	public void testorg_joda_time_chrono_BaseChronology() {
		assertImmutable(org.joda.time.chrono.BaseChronology.class);
	}

	@Test
	public void testorg_joda_time_chrono_BasicChronology() {
		// Not visible
		// assertImmutable(org.joda.time.chrono.BasicChronology.class);
	}

	@Test
	public void testorg_joda_time_chrono_BasicFixedMonthChronology() {
		// Not visible
		// assertImmutable(org.joda.time.chrono.BasicFixedMonthChronology.class);
	}

	@Test
	public void testorg_joda_time_chrono_BasicGJChronology() {
		// Not visible
		// assertImmutable(org.joda.time.chrono.BasicGJChronology.class);
	}

	@Test
	public void testorg_joda_time_chrono_BuddhistChronology() {
		assertImmutable(org.joda.time.chrono.BuddhistChronology.class);
	}

	@Test
	public void testorg_joda_time_chrono_CopticChronology() {
		assertImmutable(org.joda.time.chrono.CopticChronology.class);
	}

	@Test
	public void testorg_joda_time_chrono_EthiopicChronology() {
		assertImmutable(org.joda.time.chrono.EthiopicChronology.class);
	}

	@Test
	public void testorg_joda_time_chrono_GJChronology() {
		assertImmutable(org.joda.time.chrono.GJChronology.class);
	}

	@Test
	public void testorg_joda_time_chrono_GregorianChronology() {
		assertImmutable(org.joda.time.chrono.GregorianChronology.class);
	}

	@Test
	public void testorg_joda_time_chrono_IslamicChronology() {
		assertImmutable(org.joda.time.chrono.IslamicChronology.class);
	}

	@Test
	public void testorg_joda_time_chrono_ISOChronology() {
		assertImmutable(org.joda.time.chrono.ISOChronology.class);
	}

	@Test
	public void testorg_joda_time_chrono_JulianChronology() {
		assertImmutable(org.joda.time.chrono.JulianChronology.class);
	}

	@Test
	public void testorg_joda_time_chrono_LenientChronology() {
		assertImmutable(org.joda.time.chrono.LenientChronology.class);
	}

	@Test
	public void testorg_joda_time_chrono_LimitChronology() {
		assertImmutable(org.joda.time.chrono.LimitChronology.class);
	}

	@Test
	public void testorg_joda_time_chrono_StrictChronology() {
		assertImmutable(org.joda.time.chrono.StrictChronology.class);
	}

	@Test
	public void testorg_joda_time_chrono_ZonedChronology() {
		assertImmutable(org.joda.time.chrono.ZonedChronology.class);
	}

	@Test
	public void testorg_joda_time_convert_ConverterSet() {
		// Not visible
		// assertImmutable(org.joda.time.convert.ConverterSet.class);
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
	public void testorg_joda_time_DateTimeComparator() {
		assertImmutable(org.joda.time.DateTimeComparator.class);
	}

	@Test
	public void testorg_joda_time_DateTimeConstants() {
		assertImmutable(org.joda.time.DateTimeConstants.class);
	}

	@Test
	public void testorg_joda_time_DateTimeZone() {
		assertImmutable(org.joda.time.DateTimeZone.class);
	}

	@Test
	public void testorg_joda_time_Days() {
		assertImmutable(org.joda.time.Days.class);
	}

	@Test
	public void testorg_joda_time_Duration() {
		assertImmutable(org.joda.time.Duration.class);
	}

	@Test
	public void testorg_joda_time_field_AbstractReadableInstantFieldProperty() {
		assertImmutable(org.joda.time.field.AbstractReadableInstantFieldProperty.class);
	}

	@Test
	public void testorg_joda_time_field_BaseDateTimeField() {
		assertImmutable(org.joda.time.field.BaseDateTimeField.class);
	}

	@Test
	public void testorg_joda_time_field_BaseDurationField() {
		assertImmutable(org.joda.time.field.BaseDurationField.class);
	}

	@Test
	public void testorg_joda_time_field_DecoratedDateTimeField() {
		assertImmutable(org.joda.time.field.DecoratedDateTimeField.class);
	}

	@Test
	public void testorg_joda_time_field_DecoratedDurationField() {
		assertImmutable(org.joda.time.field.DecoratedDurationField.class);
	}

	@Test
	public void testorg_joda_time_field_DelegatedDateTimeField() {
		assertImmutable(org.joda.time.field.DelegatedDateTimeField.class);
	}

	@Test
	public void testorg_joda_time_field_DelegatedDurationField() {
		assertImmutable(org.joda.time.field.DelegatedDurationField.class);
	}

	@Test
	public void testorg_joda_time_field_DividedDateTimeField() {
		assertImmutable(org.joda.time.field.DividedDateTimeField.class);
	}

	@Test
	public void testorg_joda_time_field_FieldUtils() {
		assertImmutable(org.joda.time.field.FieldUtils.class);
	}

	@Test
	public void testorg_joda_time_field_ImpreciseDateTimeField() {
		assertImmutable(org.joda.time.field.ImpreciseDateTimeField.class);
	}

	@Test
	public void testorg_joda_time_field_LenientDateTimeField() {
		assertImmutable(org.joda.time.field.LenientDateTimeField.class);
	}

	@Test
	public void testorg_joda_time_field_MillisDurationField() {
		assertImmutable(org.joda.time.field.MillisDurationField.class);
	}

	@Test
	public void testorg_joda_time_field_OffsetDateTimeField() {
		assertImmutable(org.joda.time.field.OffsetDateTimeField.class);
	}

	@Test
	public void testorg_joda_time_field_PreciseDateTimeField() {
		assertImmutable(org.joda.time.field.PreciseDateTimeField.class);
	}

	@Test
	public void testorg_joda_time_field_PreciseDurationDateTimeField() {
		assertImmutable(org.joda.time.field.PreciseDurationDateTimeField.class);
	}

	@Test
	public void testorg_joda_time_field_PreciseDurationField() {
		assertImmutable(org.joda.time.field.PreciseDurationField.class);
	}

	@Test
	public void testorg_joda_time_field_RemainderDateTimeField() {
		assertImmutable(org.joda.time.field.RemainderDateTimeField.class);
	}

	@Test
	public void testorg_joda_time_field_ScaledDurationField() {
		assertImmutable(org.joda.time.field.ScaledDurationField.class);
	}

	@Test
	public void testorg_joda_time_field_SkipDateTimeField() {
		assertImmutable(org.joda.time.field.SkipDateTimeField.class);
	}

	@Test
	public void testorg_joda_time_field_SkipUndoDateTimeField() {
		assertImmutable(org.joda.time.field.SkipUndoDateTimeField.class);
	}

	@Test
	public void testorg_joda_time_field_StrictDateTimeField() {
		assertImmutable(org.joda.time.field.StrictDateTimeField.class);
	}

	@Test
	public void testorg_joda_time_field_UnsupportedDateTimeField() {
		assertImmutable(org.joda.time.field.UnsupportedDateTimeField.class);
	}

	@Test
	public void testorg_joda_time_field_UnsupportedDurationField() {
		assertImmutable(org.joda.time.field.UnsupportedDurationField.class);
	}

	@Test
	public void testorg_joda_time_field_ZeroIsMaxDateTimeField() {
		assertImmutable(org.joda.time.field.ZeroIsMaxDateTimeField.class);
	}

	@Test
	public void testorg_joda_time_format_DateTimeFormat() {
		assertImmutable(org.joda.time.format.DateTimeFormat.class);
	}

	@Test
	public void testorg_joda_time_format_DateTimeFormatter() {
		assertImmutable(org.joda.time.format.DateTimeFormatter.class);
	}

	@Test
	public void testorg_joda_time_format_DateTimeFormatterBuilder() {
		assertImmutable(org.joda.time.format.DateTimeFormatterBuilder.class);
	}

	@Test
	public void testorg_joda_time_format_FormatUtils() {
		assertImmutable(org.joda.time.format.FormatUtils.class);
	}

	@Test
	public void testorg_joda_time_format_ISODateTimeFormat() {
		assertImmutable(org.joda.time.format.ISODateTimeFormat.class);
	}

	@Test
	public void testorg_joda_time_format_ISOPeriodFormat() {
		assertImmutable(org.joda.time.format.ISOPeriodFormat.class);
	}

	@Test
	public void testorg_joda_time_format_PeriodFormat() {
		assertImmutable(org.joda.time.format.PeriodFormat.class);
	}

	@Test
	public void testorg_joda_time_format_PeriodFormatter() {
		assertImmutable(org.joda.time.format.PeriodFormatter.class);
	}

	@Test
	public void testorg_joda_time_format_PeriodFormatterBuilder() {
		assertImmutable(org.joda.time.format.PeriodFormatterBuilder.class);
	}

	@Test
	public void testorg_joda_time_Hours() {
		assertImmutable(org.joda.time.Hours.class);
	}

	@Test
	public void testorg_joda_time_Instant() {
		assertImmutable(org.joda.time.Instant.class);
	}

	@Test
	public void testorg_joda_time_Interval() {
		assertImmutable(org.joda.time.Interval.class);
	}

	@Test
	public void testorg_joda_time_JodaTimePermission() {
		assertImmutable(org.joda.time.JodaTimePermission.class);
	}

	@Test
	public void testorg_joda_time_LocalDate() {
		assertImmutable(org.joda.time.LocalDate.class);
	}

	@Test
	public void testorg_joda_time_LocalDateTime() {
		assertImmutable(org.joda.time.LocalDateTime.class);
	}

	@Test
	public void testorg_joda_time_LocalTime() {
		assertImmutable(org.joda.time.LocalTime.class);
	}

	@Test
	public void testorg_joda_time_Minutes() {
		assertImmutable(org.joda.time.Minutes.class);
	}

	@Test
	public void testorg_joda_time_Months() {
		assertImmutable(org.joda.time.Months.class);
	}

	@Test
	public void testorg_joda_time_MutableDateTime() {
		assertImmutable(org.joda.time.MutableDateTime.class);
	}

	@Test
	public void testorg_joda_time_Partial() {
		assertImmutable(org.joda.time.Partial.class);
	}

	@Test
	public void testorg_joda_time_Period() {
		assertImmutable(org.joda.time.Period.class);
	}

	@Test
	public void testorg_joda_time_PeriodType() {
		assertImmutable(org.joda.time.PeriodType.class);
	}

	@Test
	public void testorg_joda_time_ReadableDateTime() {
		assertImmutable(org.joda.time.ReadableDateTime.class);
	}

	@Test
	public void testorg_joda_time_ReadableDuration() {
		assertImmutable(org.joda.time.ReadableDuration.class);
	}

	@Test
	public void testorg_joda_time_ReadableInstant() {
		assertImmutable(org.joda.time.ReadableInstant.class);
	}

	@Test
	public void testorg_joda_time_ReadableInterval() {
		assertImmutable(org.joda.time.ReadableInterval.class);
	}

	@Test
	public void testorg_joda_time_ReadablePeriod() {
		assertImmutable(org.joda.time.ReadablePeriod.class);
	}

	@Test
	public void testorg_joda_time_Seconds() {
		assertImmutable(org.joda.time.Seconds.class);
	}

	@Test
	public void testorg_joda_time_TimeOfDay() {
		assertImmutable(org.joda.time.TimeOfDay.class);
	}

	@Test
	public void testorg_joda_time_tz_CachedDateTimeZone() {
		assertImmutable(org.joda.time.tz.CachedDateTimeZone.class);
	}

	@Test
	public void testorg_joda_time_tz_DateTimeZoneBuilder() {
		assertImmutable(org.joda.time.tz.DateTimeZoneBuilder.class);
	}

	@Test
	public void testorg_joda_time_tz_DefaultNameProvider() {
		assertImmutable(org.joda.time.tz.DefaultNameProvider.class);
	}

	@Test
	public void testorg_joda_time_tz_FixedDateTimeZone() {
		assertImmutable(org.joda.time.tz.FixedDateTimeZone.class);
	}

	@Test
	public void testorg_joda_time_tz_UTCProvider() {
		assertImmutable(org.joda.time.tz.UTCProvider.class);
	}

	@Test
	public void testorg_joda_time_tz_ZoneInfoProvider() {
		assertImmutable(org.joda.time.tz.ZoneInfoProvider.class);
	}

	@Test
	public void testorg_joda_time_Weeks() {
		assertImmutable(org.joda.time.Weeks.class);
	}

	@Test
	public void testorg_joda_time_YearMonthDay() {
		assertImmutable(org.joda.time.YearMonthDay.class);
	}

	@Test
	public void testorg_joda_time_Years() {
		assertImmutable(org.joda.time.Years.class);
	}

}
