package com.socioseer.restapp.service.api;

public interface CrudApi<T> {

	T save(T entity);

	T update(String id, T entity);

	T get(String id);

}
