package com.shunlai.net.util;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import kotlin.TypeCastException;

/**
 * @author Liu
 * @Date 2021/3/14
 * @mobile 18711832023
 */
public class CoreHttpUtil {
    public static Type getClassType(Object object){
        Object paramObject=object;
        Type[] arrayOfType=paramObject.getClass().getGenericInterfaces();
        if (arrayOfType.length>0){
            paramObject=arrayOfType[0];
            if (paramObject!=null){
                paramObject=((ParameterizedType)paramObject).getActualTypeArguments()[0];
                if (paramObject!=null){
                    return (Type) paramObject;
                }
                throw new ClassCastException("subscriber interface cast type failed");
            }
            throw new TypeCastException("null cannot be cast to non-null type java.lang.reflect.ParameterizedType");
        }
        paramObject=paramObject.getClass().getGenericSuperclass();
        if (paramObject!=null){
            paramObject=((ParameterizedType)paramObject).getActualTypeArguments()[0];
            if (paramObject!=null){
                return (Type)paramObject;
            }
            throw new ClassCastException("subscriber interface cast type failed");
        }

        throw new TypeCastException("null cannot be cast to non-null type java.lang.reflect.ParameterizedType");
    }
}
