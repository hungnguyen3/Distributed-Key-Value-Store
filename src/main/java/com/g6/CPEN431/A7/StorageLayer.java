package com.g6.CPEN431.A7;

import com.google.protobuf.ByteString;

import java.util.HashMap;
import java.util.Map;

public class StorageLayer {
    public class ValueVersionPair {
        public ByteString value;
        public Integer version;

        public ValueVersionPair(ByteString value, Integer version) {
            this.value = value;
            this.version = version;
        }
    }

    private Map<ByteString, ValueVersionPair> store = new HashMap<>();

    public ValueVersionPair get(ByteString key) {
        if (!store.containsKey(key)) {
            return null;
        }

        return store.get(key);
    }

    public void put(ByteString key, ByteString value, Integer version) {
        store.put(key, new ValueVersionPair(value, version));
    }

    public ValueVersionPair remove(ByteString key) {
        return store.remove(key);
    }

    public void clear() {
        store.clear();
    }
}
