/*
 * Copyright (C) 2014 Credoo Inc. All rights reserved.
 */
package com.lewis.lib_vinci.utils;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;


import com.lewis.lib_vinci.LibConstants;

import java.lang.reflect.Field;
//import com.paic.apollon.coreframework.plugins.pluginproxy.BasePluginProxyActivity;

/**
 * 
 * 
 * @author kanfei
 * @since 2013-9-26
 */
public final class ResUtils {

    /**  */
    private ResUtils() {

    }

    /** key for id */
    private static final String ID = "id";
    /** key for string */
    private static final String STRING = "string";
    /** key for layout */
    private static final String LAYOUT = "layout";
    /** key for style */
    private static final String STYLE = "style";
    /** key for drawable */
    private static final String DRAWABLE = "drawable";
    /** key for color */
    private static final String COLOR = "color";
    /** key for anim */
    private static final String ANIM = "anim";
    /** key for array */
    private static final String ARRAY = "array";
    /** key for attr */
    private static final String ATTR = "attr";
    /** key for dimen */
    private static final String DIMEN = "dimen";
    /** key for xml */
    private static final String XML = "xml";
    /** key for raw */
    private static final String RAW = "raw";

    /**
     * 
     * @param context
     *            {@linkplain Context}
     * @param name
     *            res name
     * @return res id
     */
    public static int id(Context context, String name) {
        return getIdentifier(context, ID, name);
    }

    /**
     * 
     * @param context
     *            {@linkplain Context}
     * @param name
     *            res name
     * @return res id
     */
    public static int string(Context context, String name) {
        return getIdentifier(context, STRING, name);
    }

    /**
     * 
     * @param context
     *            {@linkplain Context}
     * @param name
     *            res name
     * @return res id
     */
    public static int layout(Context context, String name) {
        return getIdentifier(context, LAYOUT, name);
    }

    /**
     * 
     * @param context
     *            {@linkplain Context}
     * @param name
     *            res name
     * @return res id
     */
    public static int style(Context context, String name) {
        return getIdentifier(context, STYLE, name);
    }

    /**
     * 
     * @param context
     *            {@linkplain Context}
     * @param name
     *            res name
     * @return res id
     */
    public static int drawable(Context context, String name) {
        return getIdentifier(context, DRAWABLE, name);
    }

    /**
     * 
     * @param context
     *            {@linkplain Context}
     * @param name
     *            res name
     * @return res id
     */
    public static int color(Context context, String name) {
        return getIdentifier(context, COLOR, name);
    }

    /**
     * 
     * @param context
     *            {@linkplain Context}
     * @param name
     *            res name
     * @return res id
     */
    public static int anim(Context context, String name) {
        return getIdentifier(context, ANIM, name);
    }

    /**
     * 
     * @param context
     *            {@linkplain Context}
     * @param name
     *            res name
     * @return res id
     */
    public static int array(Context context, String name) {
        return getIdentifier(context, ARRAY, name);
    }

    /**
     * 
     * @param context
     *            {@linkplain Context}
     * @param name
     *            res name
     * @return res id
     */
    public static int attr(Context context, String name) {
        return getIdentifier(context, ATTR, name);
    }

    /**
     * 
     * @param context
     *            {@linkplain Context}
     * @param name
     *            res name
     * @return res id
     */
    public static int dimen(Context context, String name) {
        return getIdentifier(context, DIMEN, name);
    }

    /**
     * 
     * @param context
     *            {@linkplain Context}
     * @param name
     *            res name
     * @return res id
     */
    public static int xml(Context context, String name) {
        return getIdentifier(context, XML, name);
    }
    
    /**
     * 
     * @param context
     *            {@linkplain Context}
     * @param name
     *            res name
     * @return res id
     */
    public static int raw(Context context, String name) {
        return getIdentifier(context, RAW, name);
    }

    /**
     * 
     * @param context
     *            {@linkplain Context}
     * @param name
     *            res name
     * @return res
     */
    public static String getString(Context context, String name) {
        //避免mtj中的crash
        if (context == null || context.getResources() == null) {
            return "";
        }
        return context.getResources().getString(string(context, name));
    }

    /**
     * 
     * @param context
     *            {@linkplain Context}
     * @param name
     *            res name
     * @return res
     */
    public static int getColor(Context context, String name) {
        Log.d("aaa", "name is " + name + "+++ color id is " + color(context, name));
        return context.getResources().getColor(color(context, name));
    }

    /**
     * 
     * @param context
     *            {@linkplain Context}
     * @param name
     *            res name
     * @return res
     */
    public static Drawable getDrawable(Context context, String name) {
        return context.getResources().getDrawable(drawable(context, name));
    }

    /**
     * 
     * @param context
     *            {@linkplain Context}
     * @param name
     *            res name
     * @return res
     */
    public static String[] getStringArray(Context context, String name) {
        return context.getResources().getStringArray(array(context, name));
    }

    /**
     * 
     * @param context
     *            {@linkplain Context}
     * @param name
     *            res name
     * @return res
     */
    public static float getDimension(Context context, String name) {
        return context.getResources().getDimension(dimen(context, name));
    }

    /**
     * 
     * @param context
     *            {@linkplain Context}
     * @param name
     *            res name
     * @return res
     */
    public static Animation getAnimation(Context context, String name) {
        return AnimationUtils.loadAnimation(context, anim(context, name));
    }

    /**
     * 
     * @param context
     *            {@linkplain Context}
     * @param type
     *            res type
     * @param attrName
     *            res name
     * @return res id
     */
    private static int getIdentifier(Context context, String type, String attrName) {

        if (context == null) {
            throw new NullPointerException("the context is null");
        }

        if (type == null || type.trim().length() == 0) {
            throw new NullPointerException("the type is null or empty");
        }

        if (attrName == null || attrName.trim().length() == 0) {
            throw new NullPointerException("the attrNme is null or empty");
        }

        Resources res = context.getResources();
        String packageName = "";
        int id  = 0;
        
    /*    if (context instanceof BasePluginProxyActivity && ((BasePluginProxyActivity) context).isLoadAsPlugin()) {
//            //in plugin
            packageName = ((BasePluginProxyActivity) context).getPluginName();
            id = res.getIdentifier(attrName, type, packageName);
            if(id==0){
            	packageName = context.getPackageName();
            	id = res.getIdentifier(attrName,type, packageName);
            }

        } else {*/
            //not in searchbox & not plugin
            packageName = context.getPackageName(); //集成的apk的包名
            id = res.getIdentifier(attrName, type,packageName);
//        }

        if (LibConstants.DEBUG) {
            Log.d("ResUtils", "context instance is " + context+"");
            Log.d("ResUtils", "packake name is " + packageName + " attrName is " + attrName + ", context instance is "
                    + context+"=====ID==="+id);
        }
        return id;
    }
    /**

    * 对于 context.getResources().getIdentifier 无法获取的数据 , 或者数组

    * 资源反射值

    * @paramcontext

    * @param name

    * @param type

    * @return

    */

    private static Object getResourceId(Context context, String name, String type) {

	    String className = context.getPackageName() +".R";
	
	    try {
	
		    Class<?> cls = Class.forName(className);
		
		    for (Class<?> childClass : cls.getClasses()) {
			
			    String simple = childClass.getSimpleName();
			
			    if (simple.equals(type)) {
			
				    for (Field field : childClass.getFields()) {
				
					    String fieldName = field.getName();
					
					    if (fieldName.equals(name)) {
						
						    System.out.println(fieldName);
						
						    return field.get(fieldName);
					
					    }
				
				    }
			
			    }
		
		    }
		
	    } catch (Exception e) {
	
	    e.printStackTrace();
	
	    }
	
	    return null;

    }

    /**

    *context.getResources().getIdentifier 无法获取到 styleable 的数据

    * @paramcontext

    * @param name

    * @return

    */

    public static int getStyleable(Context context, String name) {

    	return ((Integer)getResourceId(context, name,"styleable")).intValue();

    }

    /**

    * 获取 styleable 的 ID 号数组

    * @paramcontext

    * @param name

    * @return

    */

    public static int[] getStyleableArray(Context context, String name) {

    	return (int[])getResourceId(context, name,"styleable");

    }


}