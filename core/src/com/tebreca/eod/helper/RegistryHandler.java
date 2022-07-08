package com.tebreca.eod.helper;

import java.util.HashMap;
import java.util.Map;

public class RegistryHandler {

    private static final Map<Class<?>, IRegistry<?>> registryMap = new HashMap<>();

    public interface RegistryExecutor<T extends IRegistry<U>, U extends IEntry> {

        void register(T registry);
    }

    public static  <T extends IRegistry<U>, U extends IEntry> void addRegistry(T registry, Class<U> clazz) {
        registryMap.put(clazz, registry);

    }


    @SuppressWarnings("unchecked")
    private static <T extends IRegistry<U>, U extends IEntry> void execute(RegistryExecutor<T, U> executor, IRegistry<?> registry){
        executor.register((T) registry);
    }

}
