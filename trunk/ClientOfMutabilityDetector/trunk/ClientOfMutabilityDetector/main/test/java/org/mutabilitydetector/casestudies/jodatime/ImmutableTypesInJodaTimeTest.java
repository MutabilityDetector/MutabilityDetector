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


import static org.mutabilitydetector.unittesting.MutabilityAssert.assertImmutable;

import org.joda.time.DateMidnight;
import org.joda.time.DateTime;
import org.joda.time.DateTimeComparator;
import org.joda.time.DateTimeConstants;
import org.joda.time.DateTimeZone;
import org.joda.time.Days;
import org.joda.time.Duration;
import org.joda.time.Hours;
import org.joda.time.Instant;
import org.joda.time.Interval;
import org.joda.time.JodaTimePermission;
import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;
import org.joda.time.LocalTime;
import org.joda.time.Minutes;
import org.joda.time.Months;
import org.joda.time.MutableDateTime;
import org.joda.time.Partial;
import org.joda.time.Period;
import org.joda.time.PeriodType;
import org.joda.time.Seconds;
import org.joda.time.TimeOfDay;
import org.joda.time.Weeks;
import org.joda.time.YearMonthDay;
import org.joda.time.Years;
import org.joda.time.base.AbstractInstant;
import org.joda.time.chrono.AssembledChronology;
import org.joda.time.chrono.BaseChronology;
import org.joda.time.chrono.BuddhistChronology;
import org.joda.time.chrono.CopticChronology;
import org.joda.time.chrono.EthiopicChronology;
import org.joda.time.chrono.GJChronology;
import org.joda.time.chrono.GregorianChronology;
import org.joda.time.chrono.ISOChronology;
import org.joda.time.chrono.IslamicChronology;
import org.joda.time.chrono.JulianChronology;
import org.joda.time.chrono.LenientChronology;
import org.joda.time.chrono.LimitChronology;
import org.joda.time.chrono.StrictChronology;
import org.joda.time.chrono.ZonedChronology;
import org.joda.time.field.AbstractReadableInstantFieldProperty;
import org.joda.time.field.BaseDateTimeField;
import org.joda.time.field.BaseDurationField;
import org.joda.time.field.DecoratedDateTimeField;
import org.joda.time.field.DecoratedDurationField;
import org.joda.time.field.DelegatedDateTimeField;
import org.joda.time.field.DelegatedDurationField;
import org.joda.time.field.DividedDateTimeField;
import org.joda.time.field.FieldUtils;
import org.joda.time.field.ImpreciseDateTimeField;
import org.joda.time.field.LenientDateTimeField;
import org.joda.time.field.MillisDurationField;
import org.joda.time.field.OffsetDateTimeField;
import org.joda.time.field.PreciseDateTimeField;
import org.joda.time.field.PreciseDurationDateTimeField;
import org.joda.time.field.PreciseDurationField;
import org.joda.time.field.RemainderDateTimeField;
import org.joda.time.field.ScaledDurationField;
import org.joda.time.field.SkipDateTimeField;
import org.joda.time.field.SkipUndoDateTimeField;
import org.joda.time.field.StrictDateTimeField;
import org.joda.time.field.UnsupportedDateTimeField;
import org.joda.time.field.UnsupportedDurationField;
import org.joda.time.field.ZeroIsMaxDateTimeField;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.FormatUtils;
import org.joda.time.format.ISODateTimeFormat;
import org.joda.time.format.ISOPeriodFormat;
import org.joda.time.format.PeriodFormat;
import org.joda.time.tz.CachedDateTimeZone;
import org.joda.time.tz.DefaultNameProvider;
import org.joda.time.tz.FixedDateTimeZone;
import org.joda.time.tz.UTCProvider;
import org.junit.Test;

@SuppressWarnings("deprecation") 
public class ImmutableTypesInJodaTimeTest {

	
	@Test
	public void testorg_joda_time_base_AbstractInstant() {
	    assertImmutable(AbstractInstant.class);
	}

	@Test
	public void testorg_joda_time_chrono_AssembledChronology() {
	    assertImmutable(AssembledChronology.class);
	}

	@Test
	public void testorg_joda_time_chrono_BaseChronology() {
	    assertImmutable(BaseChronology.class);
	}

	@Test
	public void testorg_joda_time_chrono_BuddhistChronology() {
	    assertImmutable(BuddhistChronology.class);
	}

	@Test
	public void testorg_joda_time_chrono_CopticChronology() {
	    assertImmutable(CopticChronology.class);
	}

	@Test
	public void testorg_joda_time_chrono_EthiopicChronology() {
	    assertImmutable(EthiopicChronology.class);
	}

	@Test
	public void testorg_joda_time_chrono_GJChronology() {
	    assertImmutable(GJChronology.class);
	}

	@Test
	public void testorg_joda_time_chrono_GregorianChronology() {
	    assertImmutable(GregorianChronology.class);
	}

	@Test
	public void testorg_joda_time_chrono_IslamicChronology() {
	    assertImmutable(IslamicChronology.class);
	}

	@Test
	public void testorg_joda_time_chrono_ISOChronology() {
	    assertImmutable(ISOChronology.class);
	}

	@Test
	public void testorg_joda_time_chrono_JulianChronology() {
	    assertImmutable(JulianChronology.class);
	}

	@Test
	public void testorg_joda_time_chrono_LenientChronology() {
	    assertImmutable(LenientChronology.class);
	}

	@Test
	public void testorg_joda_time_chrono_LimitChronology() {
	    assertImmutable(LimitChronology.class);
	}

	@Test
	public void testorg_joda_time_chrono_StrictChronology() {
	    assertImmutable(StrictChronology.class);
	}

	@Test
	public void testorg_joda_time_chrono_ZonedChronology() {
	    assertImmutable(ZonedChronology.class);
	}

	@Test
	public void testorg_joda_time_DateMidnight() {
	    assertImmutable(DateMidnight.class);
	}

	@Test
	public void testorg_joda_time_DateTimeComparator() {
	    assertImmutable(DateTimeComparator.class);
	}

	@Test
	public void testorg_joda_time_DateTimeConstants() {
	    assertImmutable(DateTimeConstants.class);
	}

	@Test
	public void testorg_joda_time_DateTime() {
	    assertImmutable(DateTime.class);
	}

	@Test
	public void testorg_joda_time_DateTimeZone() {
	    assertImmutable(DateTimeZone.class);
	}

	@Test
	public void testorg_joda_time_Days() {
	    assertImmutable(Days.class);
	}

	@Test
	public void testorg_joda_time_Duration() {
	    assertImmutable(Duration.class);
	}

	@Test
	public void testorg_joda_time_field_AbstractReadableInstantFieldProperty() {
	    assertImmutable(AbstractReadableInstantFieldProperty.class);
	}

	@Test
	public void testorg_joda_time_field_BaseDateTimeField() {
	    assertImmutable(BaseDateTimeField.class);
	}

	@Test
	public void testorg_joda_time_field_BaseDurationField() {
	    assertImmutable(BaseDurationField.class);
	}

	@Test
	public void testorg_joda_time_field_DecoratedDateTimeField() {
	    assertImmutable(DecoratedDateTimeField.class);
	}

	@Test
	public void testorg_joda_time_field_DecoratedDurationField() {
	    assertImmutable(DecoratedDurationField.class);
	}

	@Test
	public void testorg_joda_time_field_DelegatedDateTimeField() {
	    assertImmutable(DelegatedDateTimeField.class);
	}

	@Test
	public void testorg_joda_time_field_DelegatedDurationField() {
	    assertImmutable(DelegatedDurationField.class);
	}

	@Test
	public void testorg_joda_time_field_DividedDateTimeField() {
	    assertImmutable(DividedDateTimeField.class);
	}

	@Test
	public void testorg_joda_time_field_FieldUtils() {
	    assertImmutable(FieldUtils.class);
	}

	@Test
	public void testorg_joda_time_field_ImpreciseDateTimeField() {
	    assertImmutable(ImpreciseDateTimeField.class);
	}

	@Test
	public void testorg_joda_time_field_LenientDateTimeField() {
	    assertImmutable(LenientDateTimeField.class);
	}

	@Test
	public void testorg_joda_time_field_MillisDurationField() {
	    assertImmutable(MillisDurationField.class);
	}

	@Test
	public void testorg_joda_time_field_OffsetDateTimeField() {
	    assertImmutable(OffsetDateTimeField.class);
	}

	@Test
	public void testorg_joda_time_field_PreciseDateTimeField() {
	    assertImmutable(PreciseDateTimeField.class);
	}

	@Test
	public void testorg_joda_time_field_PreciseDurationDateTimeField() {
	    assertImmutable(PreciseDurationDateTimeField.class);
	}

	@Test
	public void testorg_joda_time_field_PreciseDurationField() {
	    assertImmutable(PreciseDurationField.class);
	}

	@Test
	public void testorg_joda_time_field_RemainderDateTimeField() {
	    assertImmutable(RemainderDateTimeField.class);
	}

	@Test
	public void testorg_joda_time_field_ScaledDurationField() {
	    assertImmutable(ScaledDurationField.class);
	}

	@Test
	public void testorg_joda_time_field_SkipDateTimeField() {
	    assertImmutable(SkipDateTimeField.class);
	}

	@Test
	public void testorg_joda_time_field_SkipUndoDateTimeField() {
	    assertImmutable(SkipUndoDateTimeField.class);
	}

	@Test
	public void testorg_joda_time_field_StrictDateTimeField() {
	    assertImmutable(StrictDateTimeField.class);
	}

	@Test
	public void testorg_joda_time_field_UnsupportedDateTimeField() {
	    assertImmutable(UnsupportedDateTimeField.class);
	}

	@Test
	public void testorg_joda_time_field_UnsupportedDurationField() {
	    assertImmutable(UnsupportedDurationField.class);
	}

	@Test
	public void testorg_joda_time_field_ZeroIsMaxDateTimeField() {
	    assertImmutable(ZeroIsMaxDateTimeField.class);
	}

	@Test
	public void testorg_joda_time_format_DateTimeFormat() {
	    assertImmutable(DateTimeFormat.class);
	}

	@Test
	public void testorg_joda_time_format_FormatUtils() {
	    assertImmutable(FormatUtils.class);
	}

	@Test
	public void testorg_joda_time_format_ISODateTimeFormat() {
	    assertImmutable(ISODateTimeFormat.class);
	}

	@Test
	public void testorg_joda_time_format_ISOPeriodFormat() {
	    assertImmutable(ISOPeriodFormat.class);
	}

	@Test
	public void testorg_joda_time_format_PeriodFormat() {
	    assertImmutable(PeriodFormat.class);
	}

	@Test
	public void testorg_joda_time_Hours() {
	    assertImmutable(Hours.class);
	}

	@Test
	public void testorg_joda_time_Instant() {
	    assertImmutable(Instant.class);
	}

	@Test
	public void testorg_joda_time_Interval() {
	    assertImmutable(Interval.class);
	}

	@Test
	public void testorg_joda_time_JodaTimePermission() {
	    assertImmutable(JodaTimePermission.class);
	}

	@Test
	public void testorg_joda_time_LocalDate() {
	    assertImmutable(LocalDate.class);
	}

	@Test
	public void testorg_joda_time_LocalDateTime() {
	    assertImmutable(LocalDateTime.class);
	}

	@Test
	public void testorg_joda_time_LocalTime() {
	    assertImmutable(LocalTime.class);
	}

	@Test
	public void testorg_joda_time_Minutes() {
	    assertImmutable(Minutes.class);
	}

	@Test
	public void testorg_joda_time_Months() {
	    assertImmutable(Months.class);
	}

	@Test
	public void testorg_joda_time_MutableDateTime() {
	    assertImmutable(MutableDateTime.class);
	}

	@Test
	public void testorg_joda_time_Partial() {
	    assertImmutable(Partial.class);
	}

	@Test
	public void testorg_joda_time_Period() {
	    assertImmutable(Period.class);
	}

	@Test
	public void testorg_joda_time_PeriodType() {
	    assertImmutable(PeriodType.class);
	}

	@Test
	public void testorg_joda_time_Seconds() {
	    assertImmutable(Seconds.class);
	}

	@Test
	public void testorg_joda_time_TimeOfDay() {
	    assertImmutable(TimeOfDay.class);
	}

	@Test
	public void testorg_joda_time_tz_CachedDateTimeZone() {
	    assertImmutable(CachedDateTimeZone.class);
	}

	@Test
	public void testorg_joda_time_tz_DefaultNameProvider() {
	    assertImmutable(DefaultNameProvider.class);
	}

	@Test
	public void testorg_joda_time_tz_FixedDateTimeZone() {
	    assertImmutable(FixedDateTimeZone.class);
	}

	@Test
	public void testorg_joda_time_tz_UTCProvider() {
	    assertImmutable(UTCProvider.class);
	}

	@Test
	public void testorg_joda_time_Weeks() {
	    assertImmutable(Weeks.class);
	}

	@Test
	public void testorg_joda_time_YearMonthDay() {
	    assertImmutable(YearMonthDay.class);
	}

	@Test
	public void testorg_joda_time_Years() {
	    assertImmutable(Years.class);
	}


}
