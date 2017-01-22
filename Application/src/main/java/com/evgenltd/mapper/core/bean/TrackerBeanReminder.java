package com.evgenltd.mapper.core.bean;

import com.evgenltd.mapper.core.util.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.ParametersAreNonnullByDefault;

/**
 * Project: mapper
 * Author:  Evgeniy Lebedev
 * Created: 22-01-2017 14:46
 */
@Component
@ParametersAreNonnullByDefault
public class TrackerBeanReminder {

	@Autowired
	@SuppressWarnings("unused")
	private TrackerBean trackerBean;

	@Scheduled(fixedDelay = Constants.TRACKER_REMINDER_DELAY)
	@SuppressWarnings("unused")
	public void remind() {
		trackerBean.doTrackerWork();
	}

}
