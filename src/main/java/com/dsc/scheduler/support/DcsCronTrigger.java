/*
 * Copyright 2002-2021 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.dsc.scheduler.support;

import org.springframework.lang.Nullable;
import org.springframework.scheduling.Trigger;
import org.springframework.scheduling.TriggerContext;
import org.springframework.scheduling.support.CronExpression;
import org.springframework.util.Assert;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.TimeZone;

/**
 * {@link Trigger} implementation for cron expressions.
 * Wraps a {@link CronExpression}.
 *
 * @author Juergen Hoeller
 * @author Arjen Poutsma
 * @since 3.0
 * @see CronExpression
 */
public class DcsCronTrigger implements Trigger {

	private final CronExpression expression;

	private String name;

	private String lockAtMostFor;

	private String lockAtLeastFor;

	private final ZoneId zoneId;


	/**
	 * Build a {@code CronTrigger} from the pattern provided in the default time zone.
	 * @param expression a space-separated list of time fields, following cron
	 * expression conventions
	 */
	public DcsCronTrigger(String expression) {
		this(expression, ZoneId.systemDefault());
	}



	/**
	 * Build a {@code CronTrigger} from the pattern provided in the given time zone.
	 * @param expression a space-separated list of time fields, following cron
	 * expression conventions
	 * @param timeZone a time zone in which the trigger times will be generated
	 */
	public DcsCronTrigger(String expression, TimeZone timeZone) {
		this(expression, timeZone.toZoneId());
	}

	/**
	 * Build a {@code CronTrigger} from the pattern provided in the given time zone.
	 * @param expression a space-separated list of time fields, following cron
	 * expression conventions
	 * @param zoneId a time zone in which the trigger times will be generated
	 * @since 5.3
	 * @see CronExpression#parse(String)
	 */
	public DcsCronTrigger(String expression, ZoneId zoneId) {
		Assert.hasLength(expression, "Expression must not be empty");
		Assert.notNull(zoneId, "ZoneId must not be null");

		this.expression = CronExpression.parse(expression);
		this.zoneId = zoneId;
	}

	public DcsCronTrigger(String expression, String name, String lockAtMostFor, String lockAtLeastFor) {
		this.expression = CronExpression.parse(expression);
		this.name = name;
		this.lockAtMostFor = lockAtMostFor;
		this.lockAtLeastFor = lockAtLeastFor;
		this.zoneId =  ZoneId.systemDefault();
	}

	public DcsCronTrigger(String expression,TimeZone timeZone, String name, String lockAtMostFor, String lockAtLeastFor) {
		this.expression = CronExpression.parse(expression);
		this.name = name;
		this.lockAtMostFor = lockAtMostFor;
		this.lockAtLeastFor = lockAtLeastFor;
		this.zoneId =  timeZone.toZoneId();
	}

	public DcsCronTrigger(CronExpression expression, String name, String lockAtMostFor, String lockAtLeastFor, ZoneId zoneId) {
		this.expression = expression;
		this.name = name;
		this.lockAtMostFor = lockAtMostFor;
		this.lockAtLeastFor = lockAtLeastFor;
		this.zoneId = zoneId;
	}


	/**
	 * Return the cron pattern that this trigger has been built with.
	 * @return string
	 */
	public String getExpression() {
		return this.expression.toString();
	}


	/**
	 * Determine the next execution time according to the given trigger context.
	 * <p>Next execution times are calculated based on the
	 * {@linkplain TriggerContext#lastCompletionTime completion time} of the
	 * previous execution; therefore, overlapping executions won't occur.
	 */
	@Override
	public Date nextExecutionTime(TriggerContext triggerContext) {
		Date date = triggerContext.lastCompletionTime();
		if (date != null) {
			Date scheduled = triggerContext.lastScheduledExecutionTime();
			if (scheduled != null && date.before(scheduled)) {
				// Previous task apparently executed too early...
				// Let's simply use the last calculated execution time then,
				// in order to prevent accidental re-fires in the same second.
				date = scheduled;
			}
		}
		else {
			date = new Date(triggerContext.getClock().millis());
		}
		ZonedDateTime dateTime = ZonedDateTime.ofInstant(date.toInstant(), this.zoneId);
		ZonedDateTime next = this.expression.next(dateTime);
		return (next != null ? Date.from(next.toInstant()) : null);
	}


	@Override
	public boolean equals(@Nullable Object other) {
		return (this == other || (other instanceof DcsCronTrigger &&
				this.expression.equals(((DcsCronTrigger) other).expression)));
	}

	@Override
	public int hashCode() {
		return this.expression.hashCode();
	}

	@Override
	public String toString() {
		return this.expression.toString();
	}

}
