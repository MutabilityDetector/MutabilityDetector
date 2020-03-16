package org.mutabilitydetector.config;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URI;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.MonthDay;
import java.time.OffsetDateTime;
import java.time.OffsetTime;
import java.time.Period;
import java.time.Year;
import java.time.YearMonth;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

/*
 * #%L
 * MutabilityDetector
 * %%
 * Copyright (C) 2008 - 2015 Graham Allan
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import org.mutabilitydetector.ConfigurationBuilder;

/**
 * Non-exhaustive list of immutable classes from the standard JDK.
 *
 * @see String
 * @see Boolean
 * @see Byte
 * @see Character
 * @see Short
 * @see Integer
 * @see Long
 * @see Float
 * @see Double
 * @see BigDecimal
 * @see BigInteger
 * @see Class
 * @see Duration
 * @see Instant
 * @see LocalDate
 * @see LocalDateTime
 * @see LocalTime
 * @see MonthDay
 * @see OffsetDateTime
 * @see OffsetTime
 * @see Period
 * @see Year
 * @see YearMonth
 * @see ZoneOffset
 * @see ZonedDateTime
 * @see DateTimeFormatter
 */
public class JdkConfiguration extends ConfigurationBuilder {
	@Override
	public void configure() {
		hardcodeAsDefinitelyImmutable(String.class);
		hardcodeAsDefinitelyImmutable(Boolean.class);
		hardcodeAsDefinitelyImmutable(Byte.class);
		hardcodeAsDefinitelyImmutable(Character.class);
		hardcodeAsDefinitelyImmutable(Short.class);
		hardcodeAsDefinitelyImmutable(Integer.class);
		hardcodeAsDefinitelyImmutable(Long.class);
		hardcodeAsDefinitelyImmutable(Float.class);
		hardcodeAsDefinitelyImmutable(Double.class);
		hardcodeAsDefinitelyImmutable(BigDecimal.class);
		hardcodeAsDefinitelyImmutable(BigInteger.class);
		hardcodeAsDefinitelyImmutable(Class.class);
		hardcodeAsDefinitelyImmutable(URI.class);
		hardcodeAsDefinitelyImmutable(Duration.class);
		hardcodeAsDefinitelyImmutable(Instant.class);
		hardcodeAsDefinitelyImmutable(LocalDate.class);
		hardcodeAsDefinitelyImmutable(LocalDateTime.class);
		hardcodeAsDefinitelyImmutable(LocalTime.class);
		hardcodeAsDefinitelyImmutable(MonthDay.class);
		hardcodeAsDefinitelyImmutable(OffsetDateTime.class);
		hardcodeAsDefinitelyImmutable(OffsetTime.class);
		hardcodeAsDefinitelyImmutable(Period.class);
		hardcodeAsDefinitelyImmutable(Year.class);
		hardcodeAsDefinitelyImmutable(YearMonth.class);
		hardcodeAsDefinitelyImmutable(ZoneOffset.class);
		hardcodeAsDefinitelyImmutable(ZonedDateTime.class);
		hardcodeAsDefinitelyImmutable(DateTimeFormatter.class);

		hardcodeAsImmutableContainerType("java.util.Optional");
	}
}
