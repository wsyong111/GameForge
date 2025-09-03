package io.github.wsyong11.gameforge.framework.event;

import io.github.wsyong11.gameforge.framework.event.bus.SimpleEventBus;
import io.github.wsyong11.gameforge.framework.event.ex.EventDispatchException;
import io.github.wsyong11.gameforge.framework.event.reflect.SubscribeEvent;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class SimpleEventBusTest {
	@Test
	void testPostSync() throws EventDispatchException {
		SimpleEventBus bus = new SimpleEventBus();

		List<String> called = new ArrayList<>();

		IEventListener<MyEvent> listener1 = event -> called.add("listener1");
		IEventListener<MyEvent> listener2 = event -> called.add("listener2");

		bus.register(MyEvent.class, EventPriority.HIGH, listener1);
		bus.register(MyEvent.class, EventPriority.LOW, listener2);

		MyEvent event = new MyEvent();
		bus.post(event);

		// 检查优先级顺序
		assertEquals(List.of("listener1", "listener2"), called);
	}

	@Test
	void testCancellableEvent() throws EventDispatchException {
		SimpleEventBus bus = new SimpleEventBus();

		bus.register(MyEvent.class, EventPriority.NORMAL, CancellableEvent::cancel);

		MyEvent event = new MyEvent();
		boolean result = bus.post(event);

		assertFalse(result);
		assertTrue(event.isCanceled());
	}

	@Test
	void testExceptionHandling() {
		SimpleEventBus bus = new SimpleEventBus();

		IEventListener<MyEvent> listener = event -> {
			throw new RuntimeException("oops");
		};

		bus.register(MyEvent.class, EventPriority.NORMAL, listener);

		EventDispatchException ex = assertThrows(EventDispatchException.class, () -> bus.post(new MyEvent()));
		assertEquals("oops", ex.getCause().getMessage());
	}

	@Test
	void smartRegister() {
		SimpleEventBus bus = new SimpleEventBus();

		final boolean[] result = {false};

		bus.register(new IEventListener<MyEvent>() {
			@Override
			public void onEvent(@NotNull MyEvent event) {
				result[0] = true;
			}
		});

		bus.post(new MyEvent());

		assertTrue(result[0]);
	}

	@Test
	void classRegister() {
		try {
			SimpleEventBus bus = new SimpleEventBus();

			bus.register(TestHandler.class);

			bus.post(new MyEvent());

			assertTrue(TestHandler.handled);
		} finally {
			TestHandler.handled = false;
		}
	}

	@Test
	void instanceRegister() {
		try {
			SimpleEventBus bus = new SimpleEventBus();

			bus.register(new TestHandler());

			bus.post(new MyEvent());

			assertTrue(TestHandler.handled);
		} finally {
			TestHandler.handled = false;
		}
	}

	public static class TestHandler {
		public static boolean handled = false;

		@SubscribeEvent
		protected static void staticEvent(@NotNull MyEvent event) {
			handled = true;
		}

		@SubscribeEvent
		protected void event(@NotNull MyEvent event) {
			handled = true;
		}
	}
}
