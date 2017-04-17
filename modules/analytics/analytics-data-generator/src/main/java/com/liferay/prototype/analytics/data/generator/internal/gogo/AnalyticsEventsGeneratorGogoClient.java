package com.liferay.prototype.analytics.data.generator.internal.gogo;

import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.prototype.analytics.data.binding.JSONObjectMapper;
import com.liferay.prototype.analytics.data.binding.stubs.AnalyticsEvents;
import com.liferay.prototype.analytics.generator.AnalyticsEventsGenerator;
import com.liferay.prototype.analytics.processor.AnalyticsMessageProcessor;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import java.net.URI;

import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.Callable;
import java.util.concurrent.ForkJoinPool;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Michael C. Han
 */
@Component(
	immediate = true,
	property = {
		"osgi.command.function=generate", "osgi.command.function=load",
		"osgi.command.scope=analytics"
	},
	service = AnalyticsEventsGeneratorGogoClient.class
)
public class AnalyticsEventsGeneratorGogoClient {

	public void generate(int mode, int count) {
		ForkJoinPool forkJoinPool = new ForkJoinPool();

		int maxThreads = forkJoinPool.getParallelism();

		int iterations = Math.floorDiv(count, maxThreads);

		int remainder = count - (iterations * maxThreads);

		Collection<Callable<Void>> callables = new ArrayList<>(maxThreads + 1);

		for (int i = 0; i < maxThreads; i++) {
			Callable<Void> callable = createCallable(mode, iterations);

			callables.add(callable);
		}

		if (remainder > 0) {
			Callable<Void> callable = createCallable(mode, remainder);

			callables.add(callable);
		}

		forkJoinPool.invokeAll(callables);
	}

	public void load(String fileURI) {
		URI uri = URI.create(fileURI);

		try (InputStream in = uri.toURL().openStream()) {
			BufferedReader bufferedReader = new BufferedReader(
				new InputStreamReader(in));

			String jsonString = null;

			while ((jsonString = bufferedReader.readLine()) != null) {
				AnalyticsEvents analyticsEvents = jsonObjectMapper.convert(
					jsonString);

				_analyticsMessageProcessor.processMessage(analyticsEvents);
			}
		}
		catch (Exception e) {
			_log.error(e, e);
		}
	}

	protected Callable<Void> createCallable(int mode, int iterations) {
		return () -> {
			for (int i = 0; i < iterations; i++) {
				AnalyticsEvents analyticsEvents =
					_analyticsEventsGenerator.generateEvents(mode);

				_analyticsMessageProcessor.processMessage(analyticsEvents);
			}

			return null;
		};
	}

	@Reference(
		target = "(model=com.liferay.prototype.analytics.data.binding.stubs.AnalyticsEvents)"
	)
	protected JSONObjectMapper<AnalyticsEvents> jsonObjectMapper;

	private static final Log _log = LogFactoryUtil.getLog(
		AnalyticsEventsGeneratorGogoClient.class);

	@Reference(
		target = "(model=com.liferay.prototype.analytics.data.binding.stubs.AnalyticsEvents)"
	)
	private AnalyticsEventsGenerator<AnalyticsEvents> _analyticsEventsGenerator;

	@Reference(target = "(messageFormat=FORMS)")
	private AnalyticsMessageProcessor<AnalyticsEvents>
		_analyticsMessageProcessor;

	@Reference
	private JSONFactory _jsonFactory;

}