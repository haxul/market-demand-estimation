package com.haxul.cacheHandler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

@Component
@Slf4j
@RequiredArgsConstructor
public class RedisClient {

    private final Jedis jedis;

    public void set(String key, Object object) {
        try {
            if (key == null || object == null) return;
            jedis.set(key.getBytes(), serialize(object));
        } catch (Exception e) {
            log.error("RedisClient error: " + e);
        }
    }

    public <A> A get(String key, Class<A> clazz) {
        try {
            if (key == null || clazz == null) throw new ClassCastException();
            byte[] bytes = jedis.get(key.getBytes());
            Object cache = unSerialize(bytes);
            return cache == null ? null : clazz.cast(cache);
        } catch (ClassCastException e) {
            /*
                if someone mess up class types , stop running
             */
            throw new ClassCastException();
        } catch (Exception e) {
            log.error("RedisClient error: " + e);
            return null;
        }
    }

    private byte[] serialize(Object object) {
        try {
            if (object ==null) return null;
            var baos = new ByteArrayOutputStream();
            var oos = new ObjectOutputStream(baos);
            oos.writeObject(object);
            return baos.toByteArray();
        } catch (Exception e) {
            log.error("RedisClient error: " + e);
            return null;
        }
    }

    private Object unSerialize(byte[] bytes) {
        try {
            if (bytes == null || bytes.length == 0) return null;
            var bais = new ByteArrayInputStream(bytes);
            ObjectInputStream ois = new ObjectInputStream(bais);
            return ois.readObject();
        } catch (Exception e) {
            log.error("RedisClient error: " + e);
            return null;
        }
    }
}
