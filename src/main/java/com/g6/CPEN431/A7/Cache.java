package com.g6.CPEN431.A7;

import ca.NetSysLab.ProtocolBuffers.KeyValueResponse.KVResponse;
import com.google.protobuf.ByteString;

import java.util.Comparator;
import java.util.HashMap;
import java.util.PriorityQueue;

class CacheItem {
    ByteString key;
    KVResponse response;
    int frequency;

    CacheItem(ByteString key, KVResponse response, int frequency) {
        this.key = key;
        this.response = response;
        this.frequency = frequency;
    }
}

public class Cache {
    private HashMap<ByteString, KVResponse> cache;
    private HashMap<ByteString, Integer> frequency;
    private PriorityQueue<CacheItem> queue;
    private int cacheSize;

    public Cache(int cacheSize) {
        this.cacheSize = cacheSize;
        this.cache = new HashMap<ByteString, KVResponse>(cacheSize);
        this.frequency = new HashMap<ByteString, Integer>(cacheSize);
        this.queue = new PriorityQueue<CacheItem>(cacheSize, new CacheItemComparator());
    }

    public KVResponse get(ByteString reqId) {
        if (cache.containsKey(reqId)) {
            KVResponse response = cache.get(reqId);
            int count = frequency.get(reqId) + 1;
            frequency.put(reqId, count);
            CacheItem item = new CacheItem(reqId, response, count);
            queue.remove(item);
            queue.offer(item);
            return response;
        } else {
            return null;
        }
    }

    public void put(ByteString key, KVResponse response) {
        if (cache.size() >= cacheSize) {
            CacheItem item = queue.poll();
            cache.remove(item.key);
            frequency.remove(item.key);
        }
        cache.put(key, response);
        frequency.put(key, 1);
        queue.offer(new CacheItem(key, response, 1));
    }

    public void clear() {
        cache.clear();
        frequency.clear();
        queue.clear();
    }

    private class CacheItemComparator implements Comparator<CacheItem> {
        @Override
        public int compare(CacheItem item1, CacheItem item2) {
            return item1.frequency - item2.frequency;
        }
    }
}
