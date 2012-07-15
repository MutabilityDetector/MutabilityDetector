package org.mutabilitydetector.casestudies;
import static org.mutabilitydetector.unittesting.AllowedReason.allowingForSubclassing;
import static org.mutabilitydetector.unittesting.AllowedReason.allowingNonFinalFields;
import static org.mutabilitydetector.unittesting.AllowedReason.provided;
import static org.mutabilitydetector.unittesting.MutabilityAssert.assertImmutable;
import static org.mutabilitydetector.unittesting.MutabilityAssert.assertInstancesOf;
import static org.mutabilitydetector.unittesting.MutabilityMatchers.areImmutable;

import java.util.Locale;

import javax.time.OffsetDateTime;
import javax.time.ZoneId;
import javax.time.ZoneOffset;
import javax.time.calendrical.PeriodUnit;
import javax.time.format.DateTimeFormatters;
import javax.time.zone.ZoneOffsetTransition;

import org.junit.Ignore;
import org.junit.Test;


public class ThreeTenTest {

	@Test
	public void testjavax_time_AmPm() {
		assertImmutable(javax.time.AmPm.class);
	}

	@Test
	public void testjavax_time_calendrical_DateAdjusters() {
		assertImmutable(javax.time.calendrical.DateAdjusters.class);
	}

	@Test
	public void testjavax_time_calendrical_DateTimeValueRange() {
		assertImmutable(javax.time.calendrical.DateTimeValueRange.class);
	}

	@Test
	public void testjavax_time_calendrical_LocalDateTimeField() {
		assertInstancesOf(javax.time.calendrical.LocalDateTimeField.class, areImmutable(), 
				provided(PeriodUnit.class).isAlsoImmutable(),
				provided(String.class).isAlsoImmutable());
		
	}

	@Test
	public void testjavax_time_calendrical_LocalDateTimeUnit() {
		assertInstancesOf(javax.time.calendrical.LocalDateTimeUnit.class, areImmutable(), 
				provided(String.class).isAlsoImmutable());
	}

	@Test
	public void testjavax_time_calendrical_ZoneResolvers() {
		assertImmutable(javax.time.calendrical.ZoneResolvers.class);
	}

	@Test
	public void testjavax_time_chrono_Chrono() {
		assertInstancesOf(javax.time.chrono.Chrono.class, areImmutable(), allowingForSubclassing());
	}

	@Test
	public void testjavax_time_chrono_ChronoDate() {
		assertInstancesOf(javax.time.chrono.ChronoDate.class, areImmutable(), allowingForSubclassing());
	}

	@Test
	public void testjavax_time_chrono_CopticChrono() {
		assertImmutable(javax.time.chrono.CopticChrono.class);
	}

	@Test
	public void testjavax_time_chrono_CopticDate() throws ClassNotFoundException {
		assertImmutable(Class.forName("javax.time.chrono.CopticDate"));
	}

	@Test
	public void testjavax_time_chrono_CopticEra() {
		assertImmutable(javax.time.chrono.CopticEra.class);
	}

	@Test
	public void testjavax_time_chrono_ISOChrono() {
		assertImmutable(javax.time.chrono.ISOChrono.class);
	}

	@Test
	public void testjavax_time_chrono_ISODate() throws ClassNotFoundException {
		assertImmutable(Class.forName("javax.time.chrono.ISODate"));
	}

	@Test
	public void testjavax_time_chrono_ISOEra() {
		assertImmutable(javax.time.chrono.ISOEra.class);
	}

	@Test
	public void testjavax_time_chrono_MinguoChrono() {
		assertImmutable(javax.time.chrono.MinguoChrono.class);
	}

	@Test
	public void testjavax_time_chrono_MinguoDate() throws ClassNotFoundException {
		assertImmutable(Class.forName("javax.time.chrono.MinguoDate"));
	}

	@Test
	public void testjavax_time_chrono_MinguoEra() {
		assertImmutable(javax.time.chrono.MinguoEra.class);
	}

	@Test
	public void testjavax_time_Clock() {
		assertInstancesOf(javax.time.Clock.class, areImmutable(), allowingForSubclassing());
	}

	@Test
	public void testjavax_time_DateTimes() {
		assertImmutable(javax.time.DateTimes.class);
	}

	@Test
	public void testjavax_time_DayOfWeek() {
		assertImmutable(javax.time.DayOfWeek.class);
	}

	@Test
	public void testjavax_time_Duration() {
		assertImmutable(javax.time.Duration.class);
	}

	@Test
	public void testjavax_time_extended_JulianDayField() {
		assertInstancesOf(javax.time.extended.JulianDayField.class, areImmutable(), 
				provided(PeriodUnit.class).isAlsoImmutable(),
				provided(String.class).isAlsoImmutable());
	}

	@Test
	public void testjavax_time_extended_MonthDay() {
		assertImmutable(javax.time.extended.MonthDay.class);
	}

	@Test
	public void testjavax_time_extended_QuarterOfYear() {
		assertImmutable(javax.time.extended.QuarterOfYear.class);
	}

	@Test
	public void testjavax_time_extended_QuarterYearField() {
		assertInstancesOf(javax.time.extended.QuarterYearField.class, areImmutable(), 
				provided(PeriodUnit.class).isAlsoImmutable(),
				provided(String.class).isAlsoImmutable());
	}

	@Test
	public void testjavax_time_extended_Year() {
		assertImmutable(javax.time.extended.Year.class);
	}

	@Test
	public void testjavax_time_extended_YearMonth() {
		assertImmutable(javax.time.extended.YearMonth.class);
	}

	@Test
	public void testjavax_time_format_DateTimeFormatSymbols() {
		assertImmutable(javax.time.format.DateTimeFormatSymbols.class);
	}

	@Test
	public void testjavax_time_format_DateTimeFormatter() {
		assertInstancesOf(javax.time.format.DateTimeFormatter.class, areImmutable(),
				provided(Locale.class).isAlsoImmutable(),
				provided("javax.time.format.DateTimeFormatterBuilder$CompositePrinterParser").isAlsoImmutable());
	}

	@Test
	public void testjavax_time_format_DateTimeFormatters() {
		assertImmutable(javax.time.format.DateTimeFormatters.class);
	}

	@Test
	public void testjavax_time_format_FormatStyle() {
		assertImmutable(javax.time.format.FormatStyle.class);
	}

	@Test
	public void testjavax_time_format_SignStyle() {
		assertImmutable(javax.time.format.SignStyle.class);
	}

	@Test
	public void testjavax_time_format_SimpleDateTimeFormatStyleProvider() {
		assertImmutable(DateTimeFormatters.getFormatStyleProvider().getClass());
	}

	@Test
	public void testjavax_time_format_SimpleDateTimeTextProvider() {
		assertImmutable(DateTimeFormatters.getTextProvider().getClass());
	}

	@Test
	public void testjavax_time_format_TextStyle() {
		assertImmutable(javax.time.format.TextStyle.class);
	}

	@Test
	public void testjavax_time_Instant() {
		assertImmutable(javax.time.Instant.class);
	}

	@Test
	public void testjavax_time_LocalDate() {
		assertImmutable(javax.time.LocalDate.class);
	}

	@Test
	public void testjavax_time_LocalDateTime() {
		assertImmutable(javax.time.LocalDateTime.class);
	}

	@Test
	public void testjavax_time_LocalTime() {
		assertImmutable(javax.time.LocalTime.class);
	}

	@Test
	public void testjavax_time_Month() {
		assertImmutable(javax.time.Month.class);
	}

	@Test
	public void testjavax_time_OffsetDate() {
		assertInstancesOf(javax.time.OffsetDate.class, areImmutable(),
				provided(ZoneOffset.class).isAlsoImmutable());
	}

	@Test
	public void testjavax_time_OffsetDateTime() {
		assertInstancesOf(javax.time.OffsetDateTime.class, areImmutable(),
				provided(ZoneOffset.class).isAlsoImmutable());
	}

	@Test
	public void testjavax_time_OffsetTime() {
		assertInstancesOf(javax.time.OffsetTime.class, areImmutable(),
				provided(ZoneOffset.class).isAlsoImmutable());
	}

	@Test
	public void testjavax_time_Period() {
		assertInstancesOf(javax.time.Period.class, areImmutable(),
				provided(PeriodUnit.class).isAlsoImmutable());
	}

	@Test
	public void testjavax_time_ZonedDateTime() {
		assertInstancesOf(javax.time.ZonedDateTime.class, areImmutable(),
				provided(ZoneId.class).isAlsoImmutable(),
				provided(OffsetDateTime.class).isAlsoImmutable());
	}

	@Test
	public void testjavax_time_ZoneId() {
		assertInstancesOf(javax.time.ZoneId.class, areImmutable(), allowingForSubclassing());
	}

	@Test
	public void testjavax_time_ZoneOffset() {
		assertInstancesOf(javax.time.ZoneOffset.class, areImmutable(),
				provided(String.class).isAlsoImmutable());
	}
	
	@Test
	public void testjavax_time_zone_ZoneOffsetInfo() {
		assertInstancesOf(javax.time.zone.ZoneOffsetInfo.class, areImmutable(),
				provided(ZoneOffset.class).isAlsoImmutable(),
				provided(ZoneOffsetTransition.class).isAlsoImmutable());
	}

	@Test
	public void testjavax_time_zone_ZoneOffsetTransition() {
		assertInstancesOf(javax.time.zone.ZoneOffsetTransition.class, areImmutable(),
				provided(OffsetDateTime.class).isAlsoImmutable());
	}
	
	
	@Test @Ignore
	public void testjavax_time_format_DateTimeFormatterBuilder$CompositePrinterParser() throws ClassNotFoundException {
		/* Has two constructors, one which takes a list and copies it into an array. The second takes an array, but
		 * is only called from within the same class, passing the same array, which is never modified.
		 * 
		 * Would be nice to have actual array mutation checking right about now, rather than just panicking at
		 * the sight of an array.
		 */
		assertInstancesOf(Class.forName("javax.time.format.DateTimeFormatterBuilder$CompositePrinterParser"), areImmutable(), 
				provided(Locale.class).isAlsoImmutable());
	}

	@Test
	public void testjavax_time_zone_ResourceZoneRulesDataProvider() throws ClassNotFoundException {
		/*
		 * Has two fields of type HashSet. Both are constructed safely, from local variables that don't escape.
		 * However, both fields are accessible by public methods, meaning they can escape to a caller that would 
		 * mutate them.
		 * 
		 * Also has an AtomicReferenceArray of type Object. 
		 */
		assertInstancesOf(Class.forName("javax.time.zone.ResourceZoneRulesDataProvider"), 
						  areImmutable(),
						  provided(String.class).isAlsoImmutable(),
						  AssumingTheFields.named("regions", "versions").areNotModifiedByCallers(),
						  AssumingTheFields.named("rules").areModifiedAsPartAsAnUnobservableCachingStrategy());
	}
	
	@Test @Ignore
	public void testjavax_time_zone_StandardZoneRules() throws ClassNotFoundException {
		/*
		 * Has several mutable fields, both arrays and maps.
		 * 
		 * long[] standardTransitions: safely constructed inside constructor. Not accessible by callers.
		 * ZoneOffset[] standardOffsets: safely constructed inside constructor. Contains immutable type. Not accessible by callers.
		 * long[] savingsInstantTransitions: safely constructed inside constructor. Not accessible by callers.
		 * LocalDateTime[] savingsLocalTransitions: safely copied inside constructor. Contains immutable type. Not accessible by callers
		 * ZoneOffset[] wallOffsets: safely copied inside constructor. Contains immutable type. Not accessible by callers.
		 * ZoneOffsetTransitionRule[] lastRules: safely copied inside constructor. Not accessible by callers.
		 * ConcurrentMap<Integer, ZoneOffsetTransition[]> lastRulesCache: safely constructed. Used as a cache only. Not accessible by callers.
		 * 
		 * Interestingly, a lot of the arrays are passed as arguments to static methods. In this case they're trusted methods, 
		 * like Arrays.{equal, hashCode, binarySearch}, but if analysis were improved to detect this potential problem it would have to 
		 * take account of such 'safe' methods.
		 */
		assertImmutable(Class.forName("javax.time.zone.StandardZoneRules"));
	}

	@Test @Ignore
	public void testjavax_time_zone_ResourceZoneRulesVersion() throws ClassNotFoundException {
		/*
		 * Can be subclassed, though there's a limited (default) scope for that.
		 * 
		 * Has array fields, but prevents references to them escaping. 
		 * The array fields passed in are the same as those which are safely contained in the constructor
		 * of ResourceZoneRulesDataProvider.
		 */
		assertInstancesOf(Class.forName("javax.time.zone.ResourceZoneRulesDataProvider$ResourceZoneRulesVersion"), areImmutable(),
				provided(String.class).isAlsoImmutable(),
				provided("javax.time.zone.ResourceZoneRulesDataProvider").isAlsoImmutable());
	}

	@Test @Ignore("Contains a non-final boolean field which should be changed in source.")
	public void testjavax_time_zone_ZoneOffsetTransitionRule() {
		assertInstancesOf(javax.time.zone.ZoneOffsetTransitionRule.class, 
				          areImmutable(),
				          provided(ZoneOffset.class).isAlsoImmutable());
	}

	@Test 
	public void testjavax_time_zone_ZoneRulesGroup() {
		/*
		 *  Non-final field, of type AtomicReference, which is mutable. Never reassigned.
		 *  Could be made final from the looks of it.
		 */
		assertInstancesOf(javax.time.zone.ZoneRulesGroup.class,
		          areImmutable(),
		          provided(String.class).isAlsoImmutable(),
		          AssumingTheFields.named("versions").areModifiedAsPartAsAnUnobservableCachingStrategy(),
		          allowingNonFinalFields());
	}

}