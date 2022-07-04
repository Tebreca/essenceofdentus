package com.tebreca.eod.helper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RegistryHandler {

    private static final Map<Class<?>, IRegistry<?>> registryMap = new HashMap<>();
    private static final Map<Class<?>, List<RegistryExecutor<?,?>>> executors = new HashMap<>();

    public interface RegistryExecutor<T extends IRegistry<U>, U extends IEntry> {

        void register(T registry);
    }

    public static  <T extends IRegistry<U>, U extends IEntry> void addRegistry(T registry, Class<U> clazz) {
        registryMap.put(clazz, registry);
    }

    public static <T extends IRegistry<U>, U extends IEntry> boolean subscribe(RegistryExecutor<T, U> executor, Class<U> clazz){
        return executors.get(clazz).add(executor);
    }

    public static void executeRegistries(){
        registryMap.forEach((key, registry) -> executors.get(key).forEach((RegistryExecutor<?, ?> executor) -> execute(executor, registry)));
    }

    @SuppressWarnings("unchecked")
    private static <T extends IRegistry<U>, U extends IEntry> void execute(RegistryExecutor<T, U> executor, IRegistry<?> registry){
        executor.register((T) registry);
    }

}
