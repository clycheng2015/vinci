package com.lewis.lib_vinci.coreframe.eventbus;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.test.AndroidTestCase;
import com.lewis.lib_vinci.coreframe.eventbus.EventBus.Event;
import com.lewis.lib_vinci.coreframe.eventbus.EventBus.ThreadMode;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class EventBusTest extends AndroidTestCase {
    EventBus mEventBus = null;
    protected final AtomicInteger mEventCount = new AtomicInteger();
    protected final List<Event> mEventsReceived = new ArrayList<Event>();

    protected volatile Event mLastEvent;
    protected volatile Thread mLastThread;

    private BackgroundPosterThread mBackgroundPosterThread;
    private int mLastPrio = Integer.MAX_VALUE;
    private boolean mFailFlag = false;
    private final List<Object> mRegisters = new ArrayList<Object>();
    private MainPostHandler mMainPoster = null;
    
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        mEventBus = EventBus.getInstance();
        mBackgroundPosterThread = new BackgroundPosterThread();
        mBackgroundPosterThread.start();
        mMainPoster = new MainPostHandler(Looper.getMainLooper());
    }

    @Override
    protected void tearDown() throws Exception {
        mBackgroundPosterThread.shutdown();
        mBackgroundPosterThread.join();
        super.tearDown();
    }

    public void testEbWithHandlerThreadMode() {
        String eventKey = "ev_test_helloworld2";
        String eventObj = "Hello World2";
        register(new BaseSubscriber(), eventKey, 0, ThreadMode.MainThread);
        mBackgroundPosterThread.post(mEventBus.new Event(eventKey, eventObj));
        waitForEventCount(1, 1000);
        assertEquals(eventKey, mLastEvent.mEventKey);
        assertEquals(eventObj, mLastEvent.mEventObj);
        assertEquals(Looper.getMainLooper().getThread(), mLastThread);
        unregisterAll();
    }

    public void testEbWithAsyncThreadMode() {
        String eventKey = "ev_test_helloworld3";
        String eventObj = "Hello World3";

        new SubscriberThread(eventKey).start();
        mBackgroundPosterThread.post(mEventBus.new Event(eventKey, eventObj));
        waitForEventCount(1, 1000);
        assertEquals(eventKey, mLastEvent.mEventKey);
        assertEquals(eventObj, mLastEvent.mEventObj);
        assertNotSame(Looper.getMainLooper().getThread(), mLastThread);
        EventBus.getInstance().unregister(this, eventKey);

    }
   
    public void testPostFromMain() throws InterruptedException {
         register(new BaseSubscriber(), "ev_test_post_in_main", 0, ThreadMode.PostThread);
         mMainPoster.post(mEventBus.new Event("ev_test_post_in_main", "Hello_post_in_main"));
         waitForEventCount(1, 1000);
         assertEquals("ev_test_post_in_main", mLastEvent.mEventKey);
         assertFalse(mLastThread.equals(Thread.currentThread()));
         assertTrue(mLastThread.equals(Looper.getMainLooper().getThread()));
    }

    public void testCancelEventDelivery() {
        CancelSubscriber canceler = new CancelSubscriber(true);
        register(new CancelSubscriber(false), "ev_test_cancel", 0, ThreadMode.PostThread);
        register(new CancelSubscriber(false), "ev_test_cancel", 3, ThreadMode.PostThread);
        register(canceler, "ev_test_cancel", 2, ThreadMode.PostThread);
        register(new CancelSubscriber(false), "ev_test_cancel", 1, ThreadMode.PostThread);
        Event event = mEventBus.new Event("ev_test_cancel", "this is event for cancel");
        mEventBus.post(event);
        int eventCount = mEventCount.intValue();
        assertEquals(2, eventCount);
        //先清除mEventCount
        for (int i = 0; i < eventCount; i++) {
            mEventCount.decrementAndGet();
        }
        
        mEventBus.unregister(canceler, "ev_test_cancel");
        mEventBus.post(event);
        assertEquals(3, mEventCount.intValue()); 
    }

    public void testEventPriority() {
        register(new PrioSubscriber(1), "ev_test_priority", 1, ThreadMode.PostThread);
        register(new PrioSubscriber(3), "ev_test_priority", 3, ThreadMode.PostThread);
        registerSticky(new PrioSubscriber(5), "ev_test_priority", 5, ThreadMode.PostThread);
        register(new PrioSubscriber(2), "ev_test_priority", 2, ThreadMode.PostThread);
        registerSticky(new PrioSubscriber(0), "ev_test_priority", 0, ThreadMode.PostThread);
 
        mEventBus.post(mEventBus.new Event("ev_test_priority", "this is test for priority"));

        waitForEventCount(5, 10000);
        assertEquals(false, mFailFlag);

        unregisterAll();
    }

    public void testStickyEvent() {
        mEventBus.postStickyEvent(mEventBus.new Event("ev_test_sticky", "this is for sticky test"));
        registerSticky(new BaseSubscriber(), "ev_test_sticky", 1, ThreadMode.PostThread);
        assertEquals(1, mEventCount.intValue());
    } 
    
    protected void trackEvent(Event event) {
        mLastEvent = event;
        mLastThread = Thread.currentThread();
        synchronized (mEventsReceived) {
            if (mEventsReceived != null) {
                mEventsReceived.add(event);
            }
        }

        // Must the the last one because we wait for this
        mEventCount.incrementAndGet();
    }

    private void waitForEventCount(int expectedCount, int maxMillis) {
        for (int i = 0; i < maxMillis; i++) {
            int currentCount = mEventCount.get();
            if (currentCount == expectedCount) {
                break;
            } else if (currentCount > expectedCount) {
                fail("Current count (" + currentCount + ") is already higher than expected count (" + expectedCount
                        + ")");
            } else {
                try {
                    Thread.sleep(1);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        assertEquals(expectedCount, mEventCount.get());
    }

    private void register(Object subscriber, String eventKey, int priority, ThreadMode mode) {
        mEventBus.getInstance().register(subscriber, eventKey, priority, mode);
        mRegisters.add(subscriber);   
    }
    
    private void registerSticky(Object subscriber, String eventKey, int priority, ThreadMode mode) {
        mEventBus.getInstance().registerSticky(subscriber, eventKey, priority, mode);
        mRegisters.add(subscriber);   
    }
    
    private void unregisterAll() {
        for (Object subscriber : mRegisters) {
            mEventBus.getInstance().unregister(subscriber);
        }    
    }
  
    /************************ subscribers **********************/
    private final class BaseSubscriber {
        public void onModuleEvent(Event event) {
            trackEvent(event);
        }
    }

    private final class CancelSubscriber {
        private final boolean cancel;

        public CancelSubscriber(boolean cancel) {
            this.cancel = cancel;
        }

        public void onModuleEvent(Event event) {
            trackEvent(event);
            if (cancel) {
                mEventBus.cancelEventDelivery(event);
            }
        }
    }
    
    private final class PrioSubscriber {

        final int prio;

        public PrioSubscriber(int prio) {
            this.prio = prio;
            // TODO Auto-generated constructor stub
        }

        public void onModuleEvent(Event event) {
            if (prio > mLastPrio) {
                mFailFlag = true;
            }
            mLastPrio = prio;

            trackEvent(event);
        }

    }
    
    /************************ posters **********************/
    class MainPostHandler extends Handler {
        public MainPostHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
            mEventBus.getInstance().post((Event)(msg.obj));
        }

        void post(Event event) {
            sendMessage(obtainMessage(0, event));
        }

    }

    class BackgroundPosterThread extends Thread {
        volatile boolean running = true;
        private final List<Event> eventQ = new ArrayList<Event>();
        
        public BackgroundPosterThread() {
            super("BackgroundPosterThread");
        }

        @Override
        public void run() {
            while (running) {
                Event event = pollEvent();
                if (event != null) {
                    EventBus.getInstance().post(event);
                }
            }
        }

        private synchronized Event pollEvent() {
            Event event = null;
            synchronized (eventQ) {
                if (eventQ.isEmpty()) {
                    try {
                        eventQ.wait(1000);
                    } catch (InterruptedException e) {
                    }
                }
                if (!eventQ.isEmpty()) {
                    event = eventQ.remove(0);
                }
            }
            return event;
        }

        void shutdown() {
            running = false;
            synchronized (eventQ) {
                eventQ.notifyAll();
            }
        }

        void post(Event event) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            synchronized (eventQ) {
                eventQ.add(event);
                eventQ.notifyAll();
            }
        }
    }

    class SubscriberThread extends Thread {
        boolean running = true;
        String eventKey = "";
        
        SubscriberThread(String evKey) {
            eventKey = evKey;
        }
        public void shutdown() {
            running = false;
        }
 
        @Override
        public void run() {
            try {
                System.out.println("+++++++++ register event " + eventKey);
                EventBus.getInstance().register(this, eventKey, 0, ThreadMode.Async);
                while (running) {
                    double random = Math.random();
                    if (random > 0.6d) {
                        Thread.sleep(0, (int) (1000000 * Math.random()));
                    } else if (random > 0.3d) {
                        Thread.yield();
                    }
                }
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        } 
        
        public void onModuleEvent(Event event) {
            assertNotSame(Looper.getMainLooper(), Looper.myLooper());
            trackEvent(event);
            EventBus.getInstance().unregister(this, eventKey);
        }

    }

}
