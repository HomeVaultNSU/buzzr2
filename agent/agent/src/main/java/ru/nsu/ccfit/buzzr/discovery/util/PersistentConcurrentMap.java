package ru.nsu.ccfit.buzzr.discovery.util;

import java.io.*;
import java.util.concurrent.ConcurrentHashMap;

public class PersistentConcurrentMap<K, V> {

    private final File file;
    private final ConcurrentHashMap<K, V> map;

    public PersistentConcurrentMap(String filePath) {
        this.file = new File(filePath);
        this.map = loadFromDisk();
    }

    public V get(K key) {
        return map.get(key);
    }

    public V put(K key, V value) {
        V old = map.put(key, value);
        saveToDisk();
        return old;
    }

    public V remove(K key) {
        V old = map.remove(key);
        saveToDisk();
        return old;
    }

    public boolean containsKey(K key) {
        return map.containsKey(key);
    }

    public int size() {
        return map.size();
    }

    private synchronized void saveToDisk() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file))) {
            oos.writeObject(map);
        } catch (IOException e) {
            throw new RuntimeException("Failed to save map to disk: " + file, e);
        }
    }

    @SuppressWarnings("unchecked")
    private ConcurrentHashMap<K, V> loadFromDisk() {
        if (!file.exists()) {
            return new ConcurrentHashMap<>();
        }

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            return (ConcurrentHashMap<K, V>) ois.readObject();

        } catch (Exception e) {
            return new ConcurrentHashMap<>();
        }
    }
}
