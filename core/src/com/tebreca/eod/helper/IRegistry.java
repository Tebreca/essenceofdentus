package com.tebreca.eod.helper;

import java.util.Map;

public interface IRegistry<T extends IEntry> {

    T[] getAllUnordered();

    Map<String, T> getOrdered();

    boolean register(T entry);

    boolean replace(T entry);

    T getEntry(String key);

    boolean contains(String key);

}
