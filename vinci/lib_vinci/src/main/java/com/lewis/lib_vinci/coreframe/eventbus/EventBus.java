package com.lewis.lib_vinci.coreframe.eventbus;


/** 全局的事件注册分发bus
 * 
 * 支持register和post sticky 事件
 * 支持事件priority: 高优的subscribers先获得事件,默认/最小是0
 * 支持线程在不同的线程post和register, 通过threadmode来区分接收事件的处理函数在哪个线程回调
 * 支持事件被cancel, cancel的含义有两个: 
 * 1)　post事件的线程想阻止事件的分发 
 * 2) PostThread模式下，高优的subscriber阻止事件的往下传递
 * 支持任意类型的event (string, int, boolean, class等),event 按名字来唯一区分，所以为了避免event定义重复，
 * event的名字规范定义为：
 *             ev_<模块名>_<事件名>
 *             比如: ev_fastpay_exit 表示fastpay模块的exit事件
 * 
 * 事件回调函数为 public void onModuleEvent(Event ev);
 * 
 * @author yulei
 * @since 2014/10/10
 */
public final class EventBus {
    private static EventBus mEventBusInstance;
    /**回调方法名*/
    public static final String DEFAULT_METHOD_NAME = "onModuleEvent";

    private static EventBusController mEventController;

    /** 
     * @return 单例
     */
    public static EventBus getInstance() {
        if (mEventBusInstance == null) {
            synchronized (EventBus.class) {
                if (mEventBusInstance == null) {
                    mEventBusInstance = new EventBus();
                }
            }
        }
        return mEventBusInstance;
    }

    private EventBus() {
        mEventController = new EventBusController();
    }

    /** 向event bus 注册一个事件
     * 
     * @param subscriber 注册的subscriber对象
     * @param eventKey 注册的事件key
     * @param priority 注册的优先级
     * @param mode poster要调用subscriber事件处理函数所在的线程
     */
    public void register(Object subscriber, String eventKey, int priority, ThreadMode mode) {
        mEventController.register(subscriber, eventKey, priority, false, mode);
    }

    /** 向event bus 注册一系列事件
     * 
     * @param subscriber 注册的subscriber对象
     * @param eventKeys 注册的事件key
     * @param priority 注册的优先级
     * @param mode poster要调用subscriber事件处理函数所在的线程
     */
    public void register(Object subscriber, String[] eventKeys, int priority, ThreadMode mode) {
        mEventController.register(subscriber, eventKeys, priority, false, mode);
    }
    /** 向event bus 注册一个sticky事件
     * 
     * @param subscriber 注册的subscriber对象
     * @param eventKey 注册的事件key
     * @param priority 注册的优先级
     * @param mode poster要调用subscriber事件处理函数所在的线程
     */
    public void registerSticky(Object subscriber, String eventKey, int priority, ThreadMode mode) {
        mEventController.register(subscriber, eventKey, priority, true, mode);
    }

    /** 向event bus 取消注册所有事件
     * 
     * @param subscriber 注册的subscriber对象
     */
    public synchronized void unregister(Object subscriber) {
        mEventController.unregister(subscriber);
    }

    /** 向event bus 取消注册一个事件
     * 
     * @param subscriber 注册的subscriber对象
     * @param eventKey 注册的事件key
     */
    public synchronized void unregister(Object subscriber, String eventKey) {
        mEventController.unregister(subscriber, eventKey);
    }

    /** 向event bus post一个事件
     * 
     * @param event 一个event的数据结构,包括eventKey和一个eventObj
     */
    public void post(Event event) {
        mEventController.post(event);
    }

    /** 向event bus post一个sticky事件
     * 
     * @param event 一个event的数据结构,包括eventKey和一个eventObj
     */
    public void postStickyEvent(Event event) {
        mEventController.postSticky(event);
    }

    /**
     * cancel事件
     * @param event 事件
     */
    public void cancelEventDelivery(Event event) {
        mEventController.cancelEventDelivery(event);
    }

    /**
     * remove所有sticky event 
     * @param eventKey eventKey
     */
    public void removeStickyEvent(String eventKey) {
        mEventController.removeStickyEvent(eventKey);
    }
 
    /**
     * remove所有sticky events
     */
    public void removeAllStickyEvents() {
        mEventController.removeAllStickyEvents();
    }
 
    /**
     * event
     */
    public class Event {
        /**event key */
        public String mEventKey;
        /**event content */
        public Object mEventObj;
        /**event 构造函数
         * @param eventKey event key
         * @param eventObj event content
         */
        public Event(String eventKey, Object eventObj) {
            mEventKey = eventKey;
            mEventObj = eventObj;
        }
    }

    /**
     * 线程模式
     *  
     * @author yulei
     * @since 2014/10/13
     */
    public enum ThreadMode {
        /** post 事件的线程 */
        PostThread,

        /** 主线程 */
        MainThread,

        /** 新线程 */
        Async
    }
}
