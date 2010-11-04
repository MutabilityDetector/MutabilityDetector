package org.mutabilitydetector.casestudies.jodatime;

/*
 * Mutability Detector
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 * Further licensing information for this project can be found in 
 * 		license/LICENSE.txt
 */


import org.junit.Test;
import org.mutabilitydetector.unittesting.MutabilityAssert;

@SuppressWarnings("deprecation") 
public class ImmutableTypesInJodaTimeTest {

	
	@Test
	public void testorg_joda_time_base_AbstractInstant() {
	    MutabilityAssert.assertImmutable(org.joda.time.base.AbstractInstant.class);
	}

	@Test
	public void testorg_joda_time_chrono_AssembledChronology() {
	    MutabilityAssert.assertImmutable(org.joda.time.chrono.AssembledChronology.class);
	}

	@Test
	public void testorg_joda_time_chrono_BaseChronology() {
	    MutabilityAssert.assertImmutable(org.joda.time.chrono.BaseChronology.class);
	}

	@Test
	public void testorg_joda_time_chrono_BuddhistChronology() {
	    MutabilityAssert.assertImmutable(org.joda.time.chrono.BuddhistChronology.class);
	}

	@Test
	public void testorg_joda_time_chrono_CopticChronology() {
	    MutabilityAssert.assertImmutable(org.joda.time.chrono.CopticChronology.class);
	}

	@Test
	public void testorg_joda_time_chrono_EthiopicChronology() {
	    MutabilityAssert.assertImmutable(org.joda.time.chrono.EthiopicChronology.class);
	}

	@Test
	public void testorg_joda_time_chrono_GJChronology() {
	    MutabilityAssert.assertImmutable(org.joda.time.chrono.GJChronology.class);
	}

	@Test
	public void testorg_joda_time_chrono_GregorianChronology() {
	    MutabilityAssert.assertImmutable(org.joda.time.chrono.GregorianChronology.class);
	}

	@Test
	public void testorg_joda_time_chrono_IslamicChronology() {
	    MutabilityAssert.assertImmutable(org.joda.time.chrono.IslamicChronology.class);
	}

	@Test
	public void testorg_joda_time_chrono_ISOChronology() {
	    MutabilityAssert.assertImmutable(org.joda.time.chrono.ISOChronology.class);
	}

	@Test
	public void testorg_joda_time_chrono_JulianChronology() {
	    MutabilityAssert.assertImmutable(org.joda.time.chrono.JulianChronology.class);
	}

	@Test
	public void testorg_joda_time_chrono_LenientChronology() {
	    MutabilityAssert.assertImmutable(org.joda.time.chrono.LenientChronology.class);
	}

	@Test
	public void testorg_joda_time_chrono_LimitChronology() {
	    MutabilityAssert.assertImmutable(org.joda.time.chrono.LimitChronology.class);
	}

	@Test
	public void testorg_joda_time_chrono_StrictChronology() {
	    MutabilityAssert.assertImmutable(org.joda.time.chrono.StrictChronology.class);
	}

	@Test
	public void testorg_joda_time_chrono_ZonedChronology() {
	    MutabilityAssert.assertImmutable(org.joda.time.chrono.ZonedChronology.class);
	}

	@Test
	public void testorg_joda_time_DateMidnight() {
	    MutabilityAssert.assertImmutable(org.joda.time.DateMidnight.class);
	}

	@Test
	public void testorg_joda_time_DateTimeComparator() {
	    MutabilityAssert.assertImmutable(org.joda.time.DateTimeComparator.class);
	}

	@Test
	public void testorg_joda_time_DateTimeConstants() {
	    MutabilityAssert.assertImmutable(org.joda.time.DateTimeConstants.class);
	}

	@Test
	public void testorg_joda_time_DateTime() {
	    MutabilityAssert.assertImmutable(org.joda.time.DateTime.class);
	}

	@Test
	public void testorg_joda_time_DateTimeZone() {
	    MutabilityAssert.assertImmutable(org.joda.time.DateTimeZone.class);
	}

	@Test
	public void testorg_joda_time_Days() {
	    MutabilityAssert.assertImmutable(org.joda.time.Days.class);
	}

	@Test
	public void testorg_joda_time_Duration() {
	    MutabilityAssert.assertImmutable(org.joda.time.Duration.class);
	}

	@Test
	public void testorg_joda_time_field_AbstractReadableInstantFieldProperty() {
	    MutabilityAssert.assertImmutable(org.joda.time.field.AbstractReadableInstantFieldProperty.class);
	}

	@Test
	public void testorg_joda_time_field_BaseDateTimeField() {
	    MutabilityAssert.assertImmutable(org.joda.time.field.BaseDateTimeField.class);
	}

	@Test
	public void testorg_joda_time_field_BaseDurationField() {
	    MutabilityAssert.assertImmutable(org.joda.time.field.BaseDurationField.class);
	}

	@Test
	public void testorg_joda_time_field_DecoratedDateTimeField() {
	    MutabilityAssert.assertImmutable(org.joda.time.field.DecoratedDateTimeField.class);
	}

	@Test
	public void testorg_joda_time_field_DecoratedDurationField() {
	    MutabilityAssert.assertImmutable(org.joda.time.field.DecoratedDurationField.class);
	}

	@Test
	public void testorg_joda_time_field_DelegatedDateTimeField() {
	    MutabilityAssert.assertImmutable(org.joda.time.field.DelegatedDateTimeField.class);
	}

	@Test
	public void testorg_joda_time_field_DelegatedDurationField() {
	    MutabilityAssert.assertImmutable(org.joda.time.field.DelegatedDurationField.class);
	}

	@Test
	public void testorg_joda_time_field_DividedDateTimeField() {
	    MutabilityAssert.assertImmutable(org.joda.time.field.DividedDateTimeField.class);
	}

	@Test
	public void testorg_joda_time_field_FieldUtils() {
	    MutabilityAssert.assertImmutable(org.joda.time.field.FieldUtils.class);
	}

	@Test
	public void testorg_joda_time_field_ImpreciseDateTimeField() {
	    MutabilityAssert.assertImmutable(org.joda.time.field.ImpreciseDateTimeField.class);
	}

	@Test
	public void testorg_joda_time_field_LenientDateTimeField() {
	    MutabilityAssert.assertImmutable(org.joda.time.field.LenientDateTimeField.class);
	}

	@Test
	public void testorg_joda_time_field_MillisDurationField() {
	    MutabilityAssert.assertImmutable(org.joda.time.field.MillisDurationField.class);
	}

	@Test
	public void testorg_joda_time_field_OffsetDateTimeField() {
	    MutabilityAssert.assertImmutable(org.joda.time.field.OffsetDateTimeField.class);
	}

	@Test
	public void testorg_joda_time_field_PreciseDateTimeField() {
	    MutabilityAssert.assertImmutable(org.joda.time.field.PreciseDateTimeField.class);
	}

	@Test
	public void testorg_joda_time_field_PreciseDurationDateTimeField() {
	    MutabilityAssert.assertImmutable(org.joda.time.field.PreciseDurationDateTimeField.class);
	}

	@Test
	public void testorg_joda_time_field_PreciseDurationField() {
	    MutabilityAssert.assertImmutable(org.joda.time.field.PreciseDurationField.class);
	}

	@Test
	public void testorg_joda_time_field_RemainderDateTimeField() {
	    MutabilityAssert.assertImmutable(org.joda.time.field.RemainderDateTimeField.class);
	}

	@Test
	public void testorg_joda_time_field_ScaledDurationField() {
	    MutabilityAssert.assertImmutable(org.joda.time.field.ScaledDurationField.class);
	}

	@Test
	public void testorg_joda_time_field_SkipDateTimeField() {
	    MutabilityAssert.assertImmutable(org.joda.time.field.SkipDateTimeField.class);
	}

	@Test
	public void testorg_joda_time_field_SkipUndoDateTimeField() {
	    MutabilityAssert.assertImmutable(org.joda.time.field.SkipUndoDateTimeField.class);
	}

	@Test
	public void testorg_joda_time_field_StrictDateTimeField() {
	    MutabilityAssert.assertImmutable(org.joda.time.field.StrictDateTimeField.class);
	}

	@Test
	public void testorg_joda_time_field_UnsupportedDateTimeField() {
	    MutabilityAssert.assertImmutable(org.joda.time.field.UnsupportedDateTimeField.class);
	}

	@Test
	public void testorg_joda_time_field_UnsupportedDurationField() {
	    MutabilityAssert.assertImmutable(org.joda.time.field.UnsupportedDurationField.class);
	}

	@Test
	public void testorg_joda_time_field_ZeroIsMaxDateTimeField() {
	    MutabilityAssert.assertImmutable(org.joda.time.field.ZeroIsMaxDateTimeField.class);
	}

	@Test
	public void testorg_joda_time_format_DateTimeFormat() {
	    MutabilityAssert.assertImmutable(org.joda.time.format.DateTimeFormat.class);
	}

	@Test
	public void testorg_joda_time_format_FormatUtils() {
	    MutabilityAssert.assertImmutable(org.joda.time.format.FormatUtils.class);
	}

	@Test
	public void testorg_joda_time_format_ISODateTimeFormat() {
	    MutabilityAssert.assertImmutable(org.joda.time.format.ISODateTimeFormat.class);
	}

	@Test
	public void testorg_joda_time_format_ISOPeriodFormat() {
	    MutabilityAssert.assertImmutable(org.joda.time.format.ISOPeriodFormat.class);
	}

	@Test
	public void testorg_joda_time_format_PeriodFormat() {
	    MutabilityAssert.assertImmutable(org.joda.time.format.PeriodFormat.class);
	}

	@Test
	public void testorg_joda_time_Hours() {
	    MutabilityAssert.assertImmutable(org.joda.time.Hours.class);
	}

	@Test
	public void testorg_joda_time_Instant() {
	    MutabilityAssert.assertImmutable(org.joda.time.Instant.class);
	}

	@Test
	public void testorg_joda_time_Interval() {
	    MutabilityAssert.assertImmutable(org.joda.time.Interval.class);
	}

	@Test
	public void testorg_joda_time_JodaTimePermission() {
	    MutabilityAssert.assertImmutable(org.joda.time.JodaTimePermission.class);
	}

	@Test
	public void testorg_joda_time_LocalDate() {
	    MutabilityAssert.assertImmutable(org.joda.time.LocalDate.class);
	}

	@Test
	public void testorg_joda_time_LocalDateTime() {
	    MutabilityAssert.assertImmutable(org.joda.time.LocalDateTime.class);
	}

	@Test
	public void testorg_joda_time_LocalTime() {
	    MutabilityAssert.assertImmutable(org.joda.time.LocalTime.class);
	}

	@Test
	public void testorg_joda_time_Minutes() {
	    MutabilityAssert.assertImmutable(org.joda.time.Minutes.class);
	}

	@Test
	public void testorg_joda_time_Months() {
	    MutabilityAssert.assertImmutable(org.joda.time.Months.class);
	}

	@Test
	public void testorg_joda_time_MutableDateTime() {
	    MutabilityAssert.assertImmutable(org.joda.time.MutableDateTime.class);
	}

	@Test
	public void testorg_joda_time_Partial() {
	    MutabilityAssert.assertImmutable(org.joda.time.Partial.class);
	}

	@Test
	public void testorg_joda_time_Period() {
	    MutabilityAssert.assertImmutable(org.joda.time.Period.class);
	}

	@Test
	public void testorg_joda_time_PeriodType() {
	    MutabilityAssert.assertImmutable(org.joda.time.PeriodType.class);
	}

	@Test
	public void testorg_joda_time_Seconds() {
	    MutabilityAssert.assertImmutable(org.joda.time.Seconds.class);
	}

	@Test
	public void testorg_joda_time_TimeOfDay() {
	    MutabilityAssert.assertImmutable(org.joda.time.TimeOfDay.class);
	}

	@Test
	public void testorg_joda_time_tz_CachedDateTimeZone() {
	    MutabilityAssert.assertImmutable(org.joda.time.tz.CachedDateTimeZone.class);
	}

	@Test
	public void testorg_joda_time_tz_DefaultNameProvider() {
	    MutabilityAssert.assertImmutable(org.joda.time.tz.DefaultNameProvider.class);
	}

	@Test
	public void testorg_joda_time_tz_FixedDateTimeZone() {
	    MutabilityAssert.assertImmutable(org.joda.time.tz.FixedDateTimeZone.class);
	}

	@Test
	public void testorg_joda_time_tz_UTCProvider() {
	    MutabilityAssert.assertImmutable(org.joda.time.tz.UTCProvider.class);
	}

	@Test
	public void testorg_joda_time_Weeks() {
	    MutabilityAssert.assertImmutable(org.joda.time.Weeks.class);
	}

	@Test
	public void testorg_joda_time_YearMonthDay() {
	    MutabilityAssert.assertImmutable(org.joda.time.YearMonthDay.class);
	}

	@Test
	public void testorg_joda_time_Years() {
	    MutabilityAssert.assertImmutable(org.joda.time.Years.class);
	}


}
