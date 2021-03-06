package com.liferay.prototype.analytics.data.generator.form;

import com.liferay.prototype.analytics.data.binding.stubs.Event;
import com.liferay.prototype.analytics.data.binding.stubs.Location;
import com.liferay.prototype.analytics.data.binding.stubs.Properties;
import com.liferay.prototype.analytics.data.generator.internal.EventBuilder;
import com.liferay.prototype.analytics.data.generator.internal.configuration.AnalyticsEventsGeneratorConfiguration;

import java.math.BigDecimal;
import java.math.RoundingMode;

import java.text.DateFormat;

import java.util.List;
import java.util.OptionalDouble;
import java.util.OptionalInt;
import java.util.Random;

/**
 * @author Michael C. Han
 */
public abstract class BaseFormEventGenerator implements FormEventGenerator {

	public long addFormEvents(
		Random random, List<Event> events, DateFormat format, long timestamp) {

		timestamp = getNextTimestamp(random, timestamp);

		int entityId = random.nextInt(50000);

		EventBuilder formEnterEventBuilder = createFormEventBuilder(
			format, "form-enter", timestamp, entityId);

		events.add(formEnterEventBuilder.getEvent());

		float percentage = random.nextFloat();

		boolean completForm = false;

		if (percentage < getFormCompletionPercentage()) {
			completForm = true;
		}

		timestamp = createFormFieldEvents(
			random, events, format, timestamp, completForm, entityId);

		timestamp = getNextTimestamp(random, timestamp);

		if (completForm) {
			EventBuilder formExitEventBuilder = createFormEventBuilder(
				format, "form-submit", timestamp, entityId);

			OptionalInt optionalInt = random.ints(1, 300, 1200).findAny();

			int formTime = optionalInt.orElse(300);

			formExitEventBuilder.setAdditionalInfoTime(
				random.nextInt(formTime));

			events.add(formExitEventBuilder.getEvent());
		}

		return timestamp;
	}

	@Override
	public void populateLocation(Random random, Location location) {
		OptionalDouble lat = random.doubles(1, 30.0, 45.0).findAny();

		lat.ifPresent(
			value -> {
				BigDecimal bigDecimal = new BigDecimal(value);

				bigDecimal = bigDecimal.setScale(3, RoundingMode.FLOOR);

				location.setLatitude(bigDecimal.doubleValue());
			});

		OptionalDouble lon = random.doubles(1, 70, 125.0).findAny();

		lon.ifPresent(
			value -> {
				BigDecimal bigDecimal = new BigDecimal(value);

				bigDecimal = bigDecimal.setScale(3, RoundingMode.FLOOR);

				location.setLongitude(bigDecimal.negate().doubleValue());
			});
	}

	protected long addFormCancelEvent(
		String lastFieldId, String lastFieldName, int entityId,
		List<Event> events, DateFormat dateFormat, long timestamp,
		Random random) {

		timestamp = getNextTimestamp(random, timestamp);

		EventBuilder formEventBuilder = createFormEventBuilder(
			dateFormat, "form-cancel", timestamp, entityId);

		Properties properties = formEventBuilder.getProperties();

		properties.setLastElementId(lastFieldId);
		properties.setLastElementName(lastFieldName);

		formEventBuilder.setAdditionalInfoTime(random.nextInt(600));

		events.add(formEventBuilder.getEvent());

		return timestamp;
	}

	protected long addFormFieldEventsPair(
		String fieldId, String fieldName, int entityId, List<Event> events,
		Random random, DateFormat dateFormat, long timestamp, int duration) {

		timestamp = getNextTimestamp(random, timestamp);

		EventBuilder formFieldEnterEventBuilder = createFormEventBuilder(
			dateFormat, "form-field-enter", timestamp, entityId);

		Properties fieldEnterProperties =
			formFieldEnterEventBuilder.getProperties();

		fieldEnterProperties.setElementId(fieldId);
		fieldEnterProperties.setElementName(fieldName);

		events.add(formFieldEnterEventBuilder.getEvent());

		timestamp = getNextTimestamp(random, timestamp);

		EventBuilder formFieldExitEventBuilder = createFormEventBuilder(
			dateFormat, "form-field-exit", timestamp, entityId);

		Properties fieldExitProperties =
			formFieldExitEventBuilder.getProperties();

		fieldExitProperties.setElementId(fieldId);
		fieldExitProperties.setElementName(fieldName);

		formFieldExitEventBuilder.setAdditionalInfoTime(duration);

		events.add(formFieldEnterEventBuilder.getEvent());

		return timestamp;
	}

	protected EventBuilder createFormEventBuilder(
		DateFormat dateFormat, String eventType, long timestamp, int entityId) {

		EventBuilder eventBuilder = new EventBuilder(
			analyticsEventsGeneratorConfiguration, dateFormat);

		eventBuilder.setEventType(eventType);
		eventBuilder.setTimestamp(timestamp);

		Properties properties = eventBuilder.getProperties();

		String formName = getFormName().substring(
			0, getFormName().length() - 1);

		properties.setEntityName(formName);

		properties.setEntityId(entityId);

		return eventBuilder;
	}

	protected abstract long createFormFieldEvents(
		Random random, List<Event> events, DateFormat format, long timestamp,
		boolean completForm, int entityId);

	protected void doPopulateLocation(
		Random random, float latStart, float latEnd, float longStart,
		float longEnd, Location location) {

		OptionalDouble lat = random.doubles(1, latStart, latEnd).findAny();

		lat.ifPresent(
			value -> {
				BigDecimal bigDecimal = new BigDecimal(value);

				bigDecimal = bigDecimal.setScale(3, RoundingMode.FLOOR);

				location.setLatitude(bigDecimal.doubleValue());
			});

		OptionalDouble lon = random.doubles(1, longStart, longEnd).findAny();

		lon.ifPresent(
			value -> {
				BigDecimal bigDecimal = new BigDecimal(value);

				bigDecimal = bigDecimal.setScale(3, RoundingMode.FLOOR);

				location.setLongitude(bigDecimal.negate().doubleValue());
			});
	}

	protected abstract float getFormCompletionPercentage();

	protected long getNextTimestamp(Random random, long timestamp) {
		timestamp += Math.round(random.nextDouble() * 10000);

		return timestamp;
	}

	protected void populateBOS(Random random, Location location) {
		doPopulateLocation(random, 41.4f, 43.0f, 70.8f, 72.0f, location);
	}

	protected void populateDFW(Random random, Location location) {
		doPopulateLocation(random, 29.6f, 33.44f, 94.2f, 98.2f, location);
	}

	protected void populateLAX(Random random, Location location) {
		doPopulateLocation(random, 33.6f, 34.96f, 116f, 119.95f, location);
	}

	protected void populateNYC(Random random, Location location) {
		doPopulateLocation(random, 40.1f, 41.2f, 73.4f, 74.5f, location);
	}

	protected void populateORD(Random random, Location location) {
		doPopulateLocation(random, 41.0f, 42.4f, 86.2f, 88.2f, location);
	}

	protected void populatePHX(Random random, Location location) {
		doPopulateLocation(random, 38.5f, 40.6f, 75.5f, 78.0f, location);
	}

	protected void populateSE(Random random, Location location) {
		doPopulateLocation(random, 29.8f, 34.5f, 81.5f, 85.6f, location);
	}

	protected void populateSEA(Random random, Location location) {
		doPopulateLocation(random, 44.75f, 48.2f, 121.4f, 124.0f, location);
	}

	protected void populateSFO(Random random, Location location) {
		doPopulateLocation(random, 37.0f, 38.2f, 121.4f, 122.8f, location);
	}

	protected volatile AnalyticsEventsGeneratorConfiguration
		analyticsEventsGeneratorConfiguration;

}