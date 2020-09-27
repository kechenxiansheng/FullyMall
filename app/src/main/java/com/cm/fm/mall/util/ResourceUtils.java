package com.cm.fm.mall.util;

import android.content.Context;

import java.lang.reflect.Field;

/**
 * 资源工具类
 * @author cm
 * Date 2020/7/16
 */
public class ResourceUtils {

    public static int getAnimId(Context context, String resName) {
        return context.getResources().getIdentifier(resName, "anim", context.getPackageName());
    }
    public static int getAnimatorId(Context context, String resName) {
        return context.getResources().getIdentifier(resName, "animator", context.getPackageName());
    }
    public static int getAttrId(Context context, String resName) {
        return context.getResources().getIdentifier(resName, "attr", context.getPackageName());
    }

    public static int getBoolId(Context context, String resName) {
        return context.getResources().getIdentifier(resName, "bool", context.getPackageName());
    }

    public static int getColorId(Context context, String resName) {
        return context.getResources().getIdentifier(resName, "color", context.getPackageName());
    }

    public static int getDimenId(Context context, String resName) {
        return context.getResources().getIdentifier(resName, "dimen", context.getPackageName());
    }

    public static int getDrawableId(Context context, String resName) {
        return context.getResources().getIdentifier(resName, "drawable", context.getPackageName());
    }

    public static int getId(Context context, String resName) {
        return context.getResources().getIdentifier(resName, "id", context.getPackageName());
    }

    public static int getIntegerId(Context context, String resName) {
        return context.getResources().getIdentifier(resName, "integer", context.getPackageName());
    }

    public static int getInterpolatorId(Context context, String resName) {
        return context.getResources().getIdentifier(resName, "interpolator", context.getPackageName());
    }

    public static int getLayoutId(Context context, String resName) {
        return context.getResources().getIdentifier(resName, "layout", context.getPackageName());
    }

    public static int getPluralsId(Context context, String resName) {
        return context.getResources().getIdentifier(resName, "plurals", context.getPackageName());
    }

    public static int getStringId(Context context, String resName) {
        return context.getResources().getIdentifier(resName, "string", context.getPackageName());
    }

    public static int getStyleId(Context context, String resName) {
        return context.getResources().getIdentifier(resName, "style", context.getPackageName());
    }

    public static int getStyleableId(Context context, String resName) {
        return context.getResources().getIdentifier(resName, "styleable", context.getPackageName());
    }

    public static int getXmlId(Context context, String resName) {
        return context.getResources().getIdentifier(resName, "xml", context.getPackageName());
    }

    public static int getMipmapId(Context context, String resName) {
        return context.getResources().getIdentifier(resName, "mipmap", context.getPackageName());
    }
    public static int getArrayId(Context context, String resName) {
        return context.getResources().getIdentifier(resName, "array", context.getPackageName());
    }

    /**
     * 通过反射来读取int[]类型资源Id
     * @param context
     * @param name
     * @return
     */
    public static final int[] getResourceDeclareStyleableIntArray(Context context, String name) {
        try {
            Field[] fields2 = Class.forName(context.getPackageName() + ".R$styleable" ).getFields();
            for (Field f : fields2 ){
                if (f.getName().equals(name)){
                    int[] ret = (int[])f.get(null);
                    return ret;
                }
            }
        } catch (Throwable t){

        }
        return null;
    }

}
