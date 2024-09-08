package server.dao;


import java.util.List;

public interface GenericDAO<T> {
    void add(T t);
    T getById(int id);
    List<T> getAll();
    void update(T t);
    void delete(int id);
}