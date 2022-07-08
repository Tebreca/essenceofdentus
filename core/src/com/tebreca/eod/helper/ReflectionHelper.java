package com.tebreca.eod.helper;

import com.esotericsoftware.kryo.Kryo;

import java.lang.reflect.Field;
import java.util.Arrays;

public class ReflectionHelper {

    private ReflectionHelper(){

    }

    public static void register(Kryo kryo, Class<?> pojo) {
        kryo.register(pojo);
        Arrays.stream(pojo.getDeclaredFields()).map(Field::getType).filter(field -> kryo.getClassResolver().getRegistration(field) == null)//
                .forEach(clazz-> register(kryo, clazz));
    }
}
