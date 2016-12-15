package com.aut.yuxiang.lbs_middleware.lbs_policy;

import java.lang.reflect.*;
import java.util.*;
/**
 * Created by lingliang on 15/12/16.
 */

public class PrivateMethodTest {

    public PrivateMethodTest(){

    }

    //获得指定变量的值
    public static Object getValue(Object instance, String fieldName)
            throws  IllegalAccessException, NoSuchFieldException {

        Field field = getField(instance.getClass(), fieldName);
        // 参数值为true，禁用访问控制检查
        field.setAccessible(true);
        return field.get(instance);
    }

    //该方法实现根据变量名获得该变量的值
    private static Field getField(Class thisClass, String fieldName)
            throws NoSuchFieldException {

        if (thisClass == null) {
            throw new NoSuchFieldException("Error field !");
        }
        return  thisClass.getDeclaredField(fieldName);
//        return thisClass.getClass().getFields(fieldName);
    }

    public static Method getMethod(Object instance, String methodName)
            throws  NoSuchMethodException {

        Method accessMethod = getMethod(instance.getClass(), methodName);
        //参数值为true，禁用访问控制检查
//        accessMethod.setAccessible(true);

        return accessMethod;
    }

    private static Method getMethod(Class thisClass, String methodName)
            throws NoSuchMethodException {

        if (thisClass == null) {
            throw new NoSuchMethodException("Error method !");
        }
        Method [] ma = thisClass.getDeclaredMethods();
        for(Method m :ma) {
            if (m.getName().equals(methodName)) {
                m.setAccessible(true);
                return m;
            }
        }
        System.out.println("no such method found!");
        return getMethod(thisClass.getSuperclass(), methodName);
//            return thisClass.getDeclaredMethod(methodName,classTypes);

    }
}
