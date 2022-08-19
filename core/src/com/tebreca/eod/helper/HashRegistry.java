package com.tebreca.eod.helper;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

public abstract class HashRegistry<T extends IEntry> implements IRegistry<T> {

    protected HashRegistry(){
        addEntries();
        RegistryHandler.addRegistry(this, getTClass());
    }
    Map<String, T> hashMap = new HashMap<>();

    @Override
    public T[] getAllUnordered() {
        return hashMap.values().toArray(createArray(0));
    }

    protected abstract T[] createArray(int size);

    protected abstract Class<T> getTClass();

    @Override
    public Map<String, T> getOrdered() {
        return Collections.unmodifiableMap(hashMap);
    }

    @Override
    public boolean register(T entry) {
        String key = entry.getID();
        if(!contains(key)){
            this.hashMap.put(key, entry);
            return true;
        }
        return false;
    }

    @Override
    public boolean replace(T entry) {
        String key = entry.getID();
        if(contains(key)){
            this.hashMap.replace(key, entry);
            return true;
        }
        return false;
    }

    @Override
    public boolean contains(String key) {
        return hashMap.containsKey(key);
    }

    @Override
    public T getEntry(String key) {
        return this.hashMap.get(key);
    }

    @Override
    public Stream<T> stream() {
        return hashMap.values().stream();
    }
}
