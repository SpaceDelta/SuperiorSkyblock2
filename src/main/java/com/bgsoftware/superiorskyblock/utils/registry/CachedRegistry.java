package com.bgsoftware.superiorskyblock.utils.registry;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

public abstract class CachedRegistry<K, V> extends Registry<K, V> {

    private final LoadingCache<K, V> cache;

    public CachedRegistry(CacheLoader<K, V> loader) {
        cache = CacheBuilder.newBuilder()
                .maximumSize(100)
                .expireAfterAccess(2, TimeUnit.MINUTES)
                .build(loader);
    }

    @Override
    public V get(K key) {
    }


}
