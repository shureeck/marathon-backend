package dao;

public interface Dao {
    <T> T get();
    <T> T put(String string);
}
