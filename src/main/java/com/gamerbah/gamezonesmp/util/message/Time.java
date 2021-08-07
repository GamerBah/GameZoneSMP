package com.gamerbah.gamezonesmp.util.message;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public enum Time {
	SECONDS(1000),
	MINUTES(SECONDS.milliseconds * 60),
	HOURS(MINUTES.milliseconds * 60),
	DAYS(HOURS.milliseconds * 24),
	WEEKS(DAYS.milliseconds * 7),
	MONTHS(WEEKS.milliseconds * 4),
	YEARS(MONTHS.milliseconds * 12);

	@Getter
	private final long milliseconds;

	public static long remainingPunishTime(LocalDateTime expiration) {
		int hour   = LocalDateTime.now().getHour();
		int minute = LocalDateTime.now().getMinute();
		int second = LocalDateTime.now().getSecond();

		int hourExp   = expiration.getHour();
		int minuteExp = expiration.getMinute();
		int secondExp = expiration.getSecond();
		return ((hourExp - hour) * 60 * 60 * 1000) + ((minuteExp - minute) * 60 * 1000) + ((secondExp - second) * 1000);
	}

	public static String toString(long milliseconds, boolean shortened) {
		long   seconds = (milliseconds / SECONDS.milliseconds) % 60;
		long   minutes = (milliseconds / MINUTES.milliseconds) % 60;
		long   hours   = (milliseconds / HOURS.milliseconds) % 24;
		long   days    = (milliseconds / DAYS.milliseconds) % 7;
		long   weeks   = (milliseconds / WEEKS.milliseconds) % 4;
		long   months  = (milliseconds / MONTHS.milliseconds) % 12;
		long   years   = (milliseconds / YEARS.milliseconds);
		String sl;
		String ml;
		String hl;
		String dl;
		String wl;
		String mnl;

		if (!shortened) {
			if (seconds == 1) {
				sl = " second";
			} else {
				sl = " seconds";
			}
			if (minutes == 1) {
				ml = " minute";
			} else {
				ml = " minutes";
			}
			if (hours == 1) {
				hl = " hour";
			} else {
				hl = " hours";
			}
			if (days == 1) {
				dl = " day";
			} else {
				dl = " days";
			}
			if (weeks == 1) {
				wl = " week";
			} else {
				wl = " weeks";
			}
			if (months == 1) {
				mnl = " month";
			} else {
				mnl = " months";
			}

			if (years <= 0) {
				if (months <= 0) {
					if (weeks <= 0) {
						if (days <= 0) {
							if (hours <= 0) {
								if (minutes <= 0) {
									return seconds + sl;
								}
								return minutes + ml + (seconds == 0 ? "" : " and " + seconds + sl);
							}
							return hours + hl + (minutes == 0 ? "" : " and " + minutes + ml);
						}
						return days + dl + (hours == 0 ? "" : ", " + hours + hl);
					}
					return weeks + wl + (days == 0 ? "" : " and " + days + dl);
				}
				return months + mnl;
			}
			return "over a year";
		} else {
			sl = " sec";
			ml = " min";
			if (hours == 1) {
				hl = " hr";
			} else {
				hl = " hrs";
			}
			if (days == 1) {
				dl = " day";
			} else {
				dl = " days";
			}
			if (weeks == 1) {
				wl = " week";
			} else {
				wl = " weeks";
			}
			if (months == 1) {
				mnl = " month";
			} else {
				mnl = " months";
			}

			if (years <= 0) {
				if (months <= 0) {
					if (weeks <= 0) {
						if (days <= 0) {
							if (hours <= 0) {
								if (minutes <= 0) {
									return seconds + sl;
								}
								return minutes + " min" + (seconds == 0 ? "" : " " + seconds + sl);
							}
							return hours + hl + (minutes == 0 ? "" : " " + minutes + ml);
						}
						return days + dl + (hours == 0 ? "" : " & " + hours + hl);
					}
					return weeks + wl + (days == 0 ? "" : " & " + days + dl);
				}
				return months + mnl;
			}
			return "over a year";
		}
	}
}