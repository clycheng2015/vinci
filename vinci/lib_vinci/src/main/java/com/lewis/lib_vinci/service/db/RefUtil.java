/**
 *
 */
package com.lewis.lib_vinci.service.db;

import android.content.ContentValues;
import android.database.Cursor;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * java 反射 工具类
 */
public class RefUtil {

    /**
     * @param
     * @param obj
     * @param
     * @param
     * @param
     * @param
     * @param
     * @param
     * @param
     * @return Object
     * @throws IllegalArgumentException
     * @throws SecurityException
     * @throws InstantiationException
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     * @throws NoSuchMethodException
     * @throws
     * @Title: 复制类内容
     * @Description: TODO
     */
    public static Object copyObject(Object obj)
            throws IllegalArgumentException, SecurityException,
            InstantiationException, IllegalAccessException,
            InvocationTargetException, NoSuchMethodException {

        Object copy = null;

        // 获取class
        Class oclass = obj.getClass();

        // 使用构造函数
        copy = oclass.getConstructor(new Class[]{}).newInstance(
                new Object[]{});

        // 获取所有属性
        Field[] fields = oclass.getDeclaredFields();

        for (int i = 0; i < fields.length; i++) {

            Field field = fields[i];

            // 字段名
            String fieldname = field.getName();

            String fieldnameletter = fieldname.substring(0, 1).toUpperCase();

            // 获得相应属性的getXXX和setXXX方法名称
            String getName = "get" + fieldnameletter + fieldname.substring(1);
            String setName = "set" + fieldnameletter + fieldname.substring(1);

            // 获取相应的方法
            Method getMethod = oclass.getMethod(getName, new Class[]{});
            Method setMethod = oclass.getMethod(setName,
                    new Class[]{field.getType()});

            // 调用源对象的getXXX（）方法
            Object value = getMethod.invoke(obj, new Object[]{});

            // 调用拷贝对象的setXXX（）方法
            setMethod.invoke(copy, new Object[]{value});

        }
        return copy;
    }

    /**
     * @param
     * @param oClass
     * @param
     * @param cursor
     * @param
     * @param
     * @param
     * @param
     * @param
     * @param
     * @param
     * @param
     * @return Object
     * @throws IllegalArgumentException
     * @throws SecurityException
     * @throws InstantiationException
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     * @throws NoSuchMethodException
     * @throws NoSuchFieldException
     * @throws
     * @Title: 将数据库查出的内容 转换成 类
     * @Description: TODO
     */
    public static Object crateObjectByCursor(Class oClass, Cursor cursor)
            throws IllegalArgumentException, SecurityException,
            InstantiationException, IllegalAccessException,
            InvocationTargetException, NoSuchMethodException,
            NoSuchFieldException {

        Object rObject = null;

        String[] couns = cursor.getColumnNames();

        // 使用构造函数
        rObject = oClass.getConstructor(new Class[]{}).newInstance(
                new Object[]{});

        for (int i = 0; i < couns.length; i++) {

            Field field = oClass.getDeclaredField(couns[i]);

            // 字段名
            String fieldname = field.getName();

            String fieldnameletter = fieldname.substring(0, 1).toUpperCase();

            // 获得相应属性的getXXX和setXXX方法名称

            String setName = "set" + fieldnameletter + fieldname.substring(1);

            // 获取相应的方法

            Method setMethod = oClass.getMethod(setName,
                    new Class[]{field.getType()});

            Object value = getObject(field.getType(), cursor,
                    cursor.getColumnIndex(couns[i]));

            // 调用拷贝对象的setXXX（）方法
            setMethod.invoke(rObject, new Object[]{value});

        }

        return rObject;
    }

    /**
     * @param
     * @param oClass
     * @param
     * @param cursor
     * @param
     * @param columnIndex
     * @param
     * @return Object
     * @throws
     * @Title: 获取属性
     * @Description: TODO
     */
    public static Object getObject(Class oClass, Cursor cursor, int columnIndex) {

        String type = oClass.toString();

        Object obj = null;

        if (type.equals(String.class.toString())) {

            obj = cursor.getString(columnIndex);
        } else if (type.equals(int.class.toString())
                || type.equals(Integer.class.toString())) {

            obj = cursor.getInt(columnIndex);

        } else if (type.equals(short.class.toString())
                || type.equals(Short.class.toString())) {

            obj = cursor.getShort(columnIndex);
        } else if (type.equals(double.class.toString())
                || type.equals(Double.class.toString())) {

            obj = cursor.getDouble(columnIndex);
        } else if (type.equals(Long.class.toString())
                || type.equals(long.class.toString())) {

            obj = cursor.getLong(columnIndex);
        } else if (type.equals(Float.class.toString())
                || type.equals(float.class.toString())) {

            obj = cursor.getFloat(columnIndex);
        } else if (type.equals(boolean.class.toString())
                || type.equals(Boolean.class.toString())) {

            obj = cursor.getInt(columnIndex);

            Integer re = (Integer) obj;

            if (re == 0) {
                obj = false;
            } else
                obj = true;

        }

        return obj;

    }

    /**
     * 获得属性名称和值的集合
     */
    public static ContentValues getContentValues(Object obj) {
        ContentValues contentValues = new ContentValues();
        try {
            Class c = obj.getClass();
            Method[] methods = c.getMethods();
            for (Method m : methods) {

                String mName = m.getName();
                if (mName.startsWith("get") && !mName.startsWith("getClass")) {
                    String fieldName = mName.substring(3, mName.length());
                    Object value = m.invoke(obj, new Object[]{});
                    setObjectToContentValues(fieldName.toLowerCase(), value,
                            contentValues);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return contentValues;
    }

    /**
     * @param @param key
     * @param @param value
     * @param @param contentValues
     * @return void
     * @throws
     * @Title: setObjectToContentValues
     * @Description: bean转数据库存储
     */
    public static void setObjectToContentValues(String key, Object value,
                                                ContentValues contentValues) {

        if (key.equals("id") || null == value) {

            return;
        }

        key = key.toLowerCase();
        Class c = value.getClass();
        String type = c.toString();
        Object obj = null;

        if (type.equals(String.class.toString())) {
            contentValues.put(key, (String) value);
        } else if (type.equals(int.class.toString())
                || type.equals(Integer.class.toString())) {

            contentValues.put(key, (Integer) value);

        } else if (type.equals(short.class.toString())
                || type.equals(Short.class.toString())) {

            contentValues.put(key, (Short) value);

        } else if (type.equals(double.class.toString())
                || type.equals(Double.class.toString())) {

            contentValues.put(key, (Double) value);
        } else if (type.equals(Long.class.toString())
                || type.equals(long.class.toString())) {

            contentValues.put(key, (Long) value);
        } else if (type.equals(float.class.toString())
                || type.equals(Float.class.toString())) {

            contentValues.put(key, (Float) value);
        } else if (type.equals(boolean.class.toString())
                || type.equals(Boolean.class.toString())) {

            Boolean v = (Boolean) value;
            int re = 0;
            if (v)
                re = 1;

            contentValues.put(key, re);
        }

    }

    public static String toString(Object obj) {

        StringBuffer strBuf = new StringBuffer();

        try {

            Class c = obj.getClass();
            String head = c.getSimpleName() + "==>";
            Method[] methods = c.getMethods();
            strBuf.append(head);
            for (Method m : methods) {

                String mName = m.getName();
                if (mName.startsWith("get") && !mName.startsWith("getClass")) {
                    String fieldName = mName.substring(3, mName.length());

                    Object value = m.invoke(obj, new Object[]{});

                    String name = mName.replace("get", "");

                    strBuf.append(name + " : " + value + ">>" + "\n");

                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return strBuf.toString();
    }

}
