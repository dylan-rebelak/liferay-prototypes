package com.liferay.prototype.analytics.internal.generator;

import com.liferay.portal.configuration.metatype.bnd.util.ConfigurableUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.uuid.PortalUUID;
import com.liferay.prototype.analytics.data.binding.stubs.AdditionalInfo;
import com.liferay.prototype.analytics.data.binding.stubs.AnalyticsEvents;
import com.liferay.prototype.analytics.data.binding.stubs.Context;
import com.liferay.prototype.analytics.data.binding.stubs.Event;
import com.liferay.prototype.analytics.data.binding.stubs.Location;
import com.liferay.prototype.analytics.data.binding.stubs.Properties;
import com.liferay.prototype.analytics.generator.AnalyticsEventsGenerator;
import com.liferay.prototype.analytics.internal.generator.configuration.AnalyticsEventsGeneratorConfiguration;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.OptionalDouble;
import java.util.OptionalInt;
import java.util.OptionalLong;
import java.util.Random;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Michael C. Han
 */
@Component(
	configurationPid = "com.liferay.prototype.analytics.internal.generator.configuration.AnalyticsEventsGeneratorConfiguration",
	immediate = true,
	property = {
		"model=com.liferay.prototype.analytics.data.binding.stubs.AnalyticsEvents"
	},
	service = AnalyticsEventsGenerator.class
)
public class DefaultAnalyticsEventsGenerator
	implements AnalyticsEventsGenerator<AnalyticsEvents> {

	@Override
	public AnalyticsEvents generateEvents() {
		AnalyticsEvents analyticsEvents = new AnalyticsEvents();

		Random random = new Random();

		analyticsEvents.setApplicationId(
			_analyticsEventsGeneratorConfiguration.applicationId());

		analyticsEvents.setChannel(
			_analyticsEventsGeneratorConfiguration.channel());

		analyticsEvents.setContext(createContext(random));

		List<Event> events = createEvents(random);

		analyticsEvents.setEvents(events);

		analyticsEvents.setMessageFormat(
			_analyticsEventsGeneratorConfiguration.messageFormat());

		return analyticsEvents;
	}

	@Activate
	@Modified
	protected void activate(Map<String, Object> properties) throws Exception {
		_analyticsEventsGeneratorConfiguration =
			ConfigurableUtil.createConfigurable(
				AnalyticsEventsGeneratorConfiguration.class, properties);

		DateFormat format = new SimpleDateFormat(_DATE_FORMAT_STRING);

		_timestampStart = format.parse(
			_analyticsEventsGeneratorConfiguration.timestampStart());

		_timestampEnd = format.parse(
			_analyticsEventsGeneratorConfiguration.timestampEnd());
	}

	protected long addFormEvents(
		Random random, List<Event> events, DateFormat format, long timestamp) {

		timestamp += Math.round(random.nextDouble() * 100000);

		Event formEnter = createEvent(random, format, "form-enter", timestamp);

		events.add(formEnter);

		float percentage = random.nextFloat();

		boolean submitForm = false;

		if (percentage > 0.3) {
			submitForm = true;
		}

		int numFields = 20;

		if (submitForm) {
			timestamp = generateFormFieldEvents(
				random, events, format, timestamp, numFields);

			timestamp += Math.round(random.nextDouble() * 100000);

			Event formExit = createEvent(
				random, format, "form-submit", timestamp);

			events.add(formExit);
		}
		else {
			numFields = Math.round(numFields * random.nextFloat());

			timestamp = generateFormFieldEvents(
				random, events, format, timestamp, numFields);

			timestamp += Math.round(random.nextDouble() * 100000);

			Event formExit = createEvent(
				random, format, "form-cancel", timestamp);

			events.add(formExit);
		}

		return timestamp;
	}

	protected long addViewEvents(
		Random random, List<Event> events, DateFormat format, long timestamp) {

		OptionalInt optionalInt = random.ints(1, 5, 20).findAny();

		int numEvents = optionalInt.orElse(5);

		for (int i = 0; i < numEvents; i++) {
			timestamp += Math.round(random.nextDouble() * 100000);

			Event event = createEvent(random, format, "view", timestamp);

			events.add(event);
		}

		return timestamp;
	}

	protected Context createContext(Random random) {
		Context context = new Context();

		context.setCompanyId(
			_analyticsEventsGeneratorConfiguration.companyId());
		context.setDeviceId(StringUtil.randomId());
		context.setDeviceType(randomDeviceType(random));
		context.setLanguageId("en_US");
		context.setLocation(randomLocation(random));
		context.setSignedIn(true);
		context.setSessionId(portalUUID.generate());
		context.setUserId(randomUserId(random));

		return context;
	}

	protected Event createEvent(
		Random random, DateFormat format, String eventType, long timestamp) {

		Event event = new Event();

		event.setEvent(eventType);

		event.setGroupId(_analyticsEventsGeneratorConfiguration.groupId());

		Date date = new Date(timestamp);

		event.setTimestamp(format.format(date));

		AdditionalInfo additionalInfo = new AdditionalInfo();

		additionalInfo.setTime(random.nextInt(3000));

		event.setAdditionalInfo(additionalInfo);

		return event;
	}

	protected Event createEvent(
		Random random, DateFormat format, String eventType, long timestamp,
		String formFieldId) {

		Event event = createEvent(random, format, eventType, timestamp);

		Properties properties = new Properties();

		properties.setElementId(formFieldId);

		event.setProperties(properties);

		return event;
	}

	protected List<Event> createEvents(Random random) {
		List<Event> events = new ArrayList<>();

		long timestampStart = randomTimestampStart(random);

		DateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");

		long timestamp = timestampStart;

		Event appOpen = createEvent(random, format, "app-open", timestamp);

		events.add(appOpen);

		timestamp = addViewEvents(random, events, format, timestamp);

		float percentage = random.nextFloat();

		boolean enterForm = false;

		if (percentage > 0.4) {
			enterForm = true;
		}

		if (enterForm) {
			timestamp = addFormEvents(random, events, format, timestamp);

			timestamp = addViewEvents(random, events, format, timestamp);
		}

		timestamp += Math.round(random.nextDouble() * 10000);

		Event appClose = createEvent(random, format, "app-close", timestamp);

		events.add(appClose);

		return events;
	}

	protected long generateFormFieldEvents(
		Random random, List<Event> events, DateFormat format, long timestamp,
		int numFields) {

		for (int i = 0; i < numFields; i++) {
			timestamp += Math.round(random.nextDouble() * 100000);

			String fieldName = "field" + i;

			Event formFieldEnter = createEvent(
				random, format, "form-field-enter", timestamp, fieldName);

			events.add(formFieldEnter);

			Event formFieldExit = createEvent(
				random, format, "form-field-exit", timestamp, fieldName);

			events.add(formFieldExit);
		}

		return timestamp;
	}

	protected String randomDeviceType(Random random) {
		String[] deviceTypes =
			_analyticsEventsGeneratorConfiguration.deviceTypes();

		int index = random.nextInt(deviceTypes.length);

		return deviceTypes[index];
	}

	protected Location randomLocation(Random random) {
		Location location = new Location();

		OptionalDouble lon = random.doubles(1, 30.0, 45.0).findAny();

		lon.ifPresent(location::setLongitude);

		OptionalDouble lat = random.doubles(1, 70, 125.0).findAny();

		lat.ifPresent(location::setLatitude);

		return location;
	}

	protected long randomTimestampStart(Random random) {
		OptionalLong optionalLong = random.longs(
			1, _timestampStart.getTime(), _timestampEnd.getTime()).findAny();

		return optionalLong.orElse(System.currentTimeMillis());
	}

	protected int randomUserId(Random random) {
		return random.nextInt(5000);
	}

	@Reference
	protected PortalUUID portalUUID;

	private static final String _DATE_FORMAT_STRING =
		"yyyy-MM-dd'T'HH:mm:ss.SSSZ";

	private volatile AnalyticsEventsGeneratorConfiguration
		_analyticsEventsGeneratorConfiguration;
	private Date _timestampEnd;
	private Date _timestampStart;

}