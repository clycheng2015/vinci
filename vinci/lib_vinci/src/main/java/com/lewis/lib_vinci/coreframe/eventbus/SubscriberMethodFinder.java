package com.lewis.lib_vinci.coreframe.eventbus;

import com.lewis.lib_vinci.coreframe.eventbus.EventBus.Event;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;

/** 遍历subscriber Class的所有的method,查找onModuleEvent(Event evt) 方法
 * 
 * @author yulei 
 * @since 2014/10/14
 */
class SubscriberMethodFinder {
    private static final int MODIFIERS_IGNORE = Modifier.ABSTRACT | Modifier.STATIC;
    private static Map<Class<?>, Method> methodCache = new HashMap<Class<?>, Method>();

    Method findSubscriberMethods(Class<?> subscriberClass) {
        String eventHandleMethodName = EventBus.DEFAULT_METHOD_NAME;
        Method subscriberMethod = null;
        synchronized (methodCache) {
            subscriberMethod = methodCache.get(subscriberClass);
        }
        if (subscriberMethod != null) {
            return subscriberMethod;
        }

        Class<?> clazz = subscriberClass;

        while (clazz != null) {
            String name = clazz.getName();
            if (name.startsWith("java.") || name.startsWith("javax.") || name.startsWith("android.")) {
                //跳过对系统类的查找,提升性能
                break;
            }

            Method[] methods = clazz.getMethods();

            for (Method method : methods) {
                String methodName = method.getName();
                if (methodName.equals(eventHandleMethodName)) {
                    int modifiers = method.getModifiers();
                    if ((modifiers & Modifier.PUBLIC) != 0 && (modifiers & MODIFIERS_IGNORE) == 0) {
                        Class<?>[] parameterTypes = method.getParameterTypes();
                        if (parameterTypes.length == 1 && parameterTypes[0] == Event.class) {
                            subscriberMethod = method;
                            break;
                        }
                    }
                }
            }
            clazz = clazz.getSuperclass();
        }
        if (subscriberMethod == null) {
            throw new EventBusException("Subscriber " + subscriberClass + " has no public methods called "
                    + eventHandleMethodName);
        } else {
            synchronized (methodCache) {
                methodCache.put(subscriberClass, subscriberMethod);
            }
            return subscriberMethod;
        }
    }

    static void clearCaches() {
        synchronized (methodCache) {
            methodCache.clear();
        }
    }
}
