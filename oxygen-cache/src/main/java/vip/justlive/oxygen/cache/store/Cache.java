/*
 * Copyright (C) 2019 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package vip.justlive.oxygen.cache.store;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import vip.justlive.oxygen.core.config.ConfigFactory;
import vip.justlive.oxygen.core.config.CoreConf;
import vip.justlive.oxygen.core.exception.Exceptions;
import vip.justlive.oxygen.core.util.ClassUtils;

/**
 * 缓存接口
 *
 * @author wubo
 */
public interface Cache {

  Map<String, Cache> CACHES = new ConcurrentHashMap<>(4, 1);

  /**
   * 获取缓存对象
   *
   * @param key cache key
   * @return the cached object or null
   */
  Object get(String key);

  /**
   * 获取缓存对象
   *
   * @param key cache key
   * @param clazz the type of cache
   * @param <T> type
   * @return the cached object or null
   */
  <T> T get(String key, Class<T> clazz);

  /**
   * 批量获取缓存对象
   *
   * @param keys cache keys
   * @return return key-value objects
   */
  Map<String, Object> get(String... keys);

  /**
   * 判断缓存是否存在
   *
   * @param key cache key
   * @return true if key exists
   */
  default boolean exists(String key) {
    return get(key) != null;
  }

  /**
   * 向缓存中存放对象，当前缓存中不存在key值时执行
   *
   * @param key cache key
   * @param value cache value
   * @return exist cache value
   */
  Object putIfAbsent(String key, Object value);

  /**
   * 向缓存中存放对象，当前缓存中不存在key值时执行
   *
   * @param key cache key
   * @param value cache value
   * @param duration duration
   * @param unit time unit
   * @return exist cache value
   */
  Object putIfAbsent(String key, Object value, long duration, TimeUnit unit);

  /**
   * 向缓存中存放对象
   *
   * @param key cache key
   * @param value cache value
   * @return exist cache value
   */
  Object set(String key, Object value);

  /**
   * 向缓存中存放对象
   *
   * @param key cache key
   * @param value cache value
   * @param duration duration
   * @param unit time unit
   * @return exist cache value
   */
  Object set(String key, Object value, long duration, TimeUnit unit);


  /**
   * 替换缓存中key对应的value
   *
   * @param key cache key
   * @param value cache value
   * @return exist cache value
   */
  Object replace(String key, Object value);

  /**
   * 替换缓存中key对应的value
   *
   * @param key cache key
   * @param value cache value
   * @param duration duration
   * @param unit time unit
   * @return exist cache value
   */
  Object replace(String key, Object value, long duration, TimeUnit unit);

  /**
   * Return all keys
   *
   * @return 返回键的集合
   */
  Collection<String> keys();

  /**
   * Remove items from the cache
   *
   * @param keys Cache key
   */
  void remove(String... keys);

  /**
   * Clear the cache
   */
  void clear();

  /**
   * 获取默认cache
   *
   * @return cache
   */
  static Cache cache() {
    return cache(Cache.class.getSimpleName());
  }

  /**
   * 根据缓存名称获取cache
   *
   * @param name cache name
   * @return cache
   */
  static Cache cache(String name) {
    if (!CACHES.containsKey(name)) {
      CACHES.putIfAbsent(name, createCache(name));
    }
    return CACHES.get(name);
  }

  /**
   * 获取缓存名称集合
   *
   * @return 缓存名称集合
   */
  static Collection<String> cacheNames() {
    return CACHES.keySet();
  }

  /**
   * 清除缓存
   */
  static void clearAll() {
    for (Cache cache : CACHES.values()) {
      cache.clear();
    }
    CACHES.clear();
  }

  /**
   * 创建cache
   *
   * @param name 缓存名称
   * @return cache
   */
  static Cache createCache(String name) {
    CoreConf config = ConfigFactory.load(CoreConf.class);
    if (config.getCacheImplClass() != null && config.getCacheImplClass().length() > 0) {
      try {
        Class<?> clazz = ClassUtils.forName(config.getCacheImplClass());
        return (Cache) clazz.getConstructor(String.class).newInstance(name);
      } catch (Exception e) {
        throw Exceptions.wrap(e);
      }
    } else {
      return new LocalCacheImpl(name);
    }
  }
}
