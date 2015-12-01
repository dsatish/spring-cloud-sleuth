package org.springframework.cloud.sleuth.instrument;

import static org.assertj.core.api.BDDAssertions.then;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.cloud.sleuth.Trace;
import org.springframework.cloud.sleuth.TraceManager;
import org.springframework.cloud.sleuth.trace.TraceContextHolder;

@RunWith(MockitoJUnitRunner.class)
public class TraceCallableTest {

	ExecutorService executor = Executors.newSingleThreadExecutor();
	TraceManager traceManager = Mockito.mock(TraceManager.class);

	@Test
	@Ignore("Will fail because trace is not removed after callable gets executed")
	public void should_remove_span_from_thread_local_after_finishing_work() throws Exception {
		givenCallableGetsSubmitted(thatSetsTraceInCurrentThreadLocalWithInitialTrace());

		Trace secondTrace = whenCallableGetsSubmitted(thatRetrievesTraceFromThreadLocal());

		then(secondTrace).isNull();
	}

	private Callable<Trace> thatSetsTraceInCurrentThreadLocalWithInitialTrace() {
		return new Callable<Trace>() {
			@Override
			public Trace call() throws Exception {
				TraceContextHolder.setCurrentTrace(Mockito.mock(Trace.class));
				return TraceContextHolder.getCurrentTrace();
			}
		};
	}

	private Callable<Trace> thatRetrievesTraceFromThreadLocal() {
		return new Callable<Trace>() {
			@Override
			public Trace call() throws Exception {
				return TraceContextHolder.getCurrentTrace();
			}
		};
	}

	private Trace givenCallableGetsSubmitted(Callable<Trace> callable) throws InterruptedException, java.util.concurrent.ExecutionException {
		return whenCallableGetsSubmitted(callable);
	}

	private Trace whenCallableGetsSubmitted(Callable<Trace> callable) throws InterruptedException, java.util.concurrent.ExecutionException {
		return executor.submit(new TraceCallable<>(traceManager, callable)).get();
	}


}