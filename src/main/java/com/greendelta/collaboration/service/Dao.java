package com.greendelta.collaboration.service;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;

import org.springframework.transaction.annotation.Transactional;

import com.greendelta.collaboration.model.AbstractEntity;

public class Dao<T extends AbstractEntity> {

	@PersistenceContext
	private EntityManager entityManager;
	private final Class<T> entityType;

	public Dao(Class<T> entityType) {
		this.entityType = entityType;
	}

	public T get(long id) {
		if (id < 1)
			return null;
		var entityManager = createManager();
		return entityManager.find(entityType, id);
	}

	public List<T> getAll() {
		var jpql = "SELECT o FROM " + entityType.getSimpleName() + " o";
		var entityManager = createManager();
		var query = entityManager.createQuery(jpql, entityType);
		return query.getResultList();
	}

	public List<T> getAll(String jpql, Map<String, Object> parameters) {
		return getAll(jpql, parameters, 0, 0);
	}

	public List<T> getAll(String jpql, Map<String, Object> parameters, int start, int limit) {
		var entityManager = createManager();
		var query = entityManager.createQuery(jpql, entityType);
		appendParameters(query, parameters);
		if (start > 0) {
			query.setFirstResult(start - 1);
		}
		if (limit > 0) {
			query.setMaxResults(limit);
		}
		return query.getResultList();
	}

	public <RT> List<RT> getAttributes(String jpql, Map<String, Object> parameters, Class<RT> resultClass) {
		var entityManager = createManager();
		var query = entityManager.createQuery(jpql, resultClass);
		appendParameters(query, parameters);
		return query.getResultList();
	}

	public List<T> getForAttribute(String attribute, Object value) {
		return getForAttribute(attribute, value, false);
	}

	public List<T> getForAttribute(String attribute, Object value, boolean ignoreCase) {
		return getForAttributes(Collections.singletonMap(attribute, value), ignoreCase);
	}

	public List<T> getForAttributes(Map<String, Object> parameters) {
		return getForAttributes(parameters, false);
	}

	public List<T> getForAttributes(Map<String, Object> parameters, boolean ignoreCase) {
		var jpql = "SELECT o FROM " + entityType.getSimpleName() + " o";
		if (parameters == null || parameters.isEmpty())
			return getAll(jpql, parameters);
		jpql += " WHERE ";
		int count = 0;
		var params = new HashMap<String, Object>();
		for (var parameter : parameters.entrySet()) {
			if (count != 0) {
				jpql += " AND ";
			}
			var value = parameter.getValue();
			count++;
			if (value == null) {
				jpql += "o." + parameter.getKey() + " IS NULL";
			} else if (value instanceof Collection) {
				jpql += "o." + parameter.getKey() + " IN :p" + count;
				params.put("p" + count, value);
			} else if (value.getClass().isArray()) {
				jpql += "o." + parameter.getKey() + " IN :p" + count;
				params.put("p" + count, new HashSet<>(Arrays.asList((Object[]) value)));
			} else if (ignoreCase && value instanceof String) {
				jpql += "LOWER(o." + parameter.getKey() + ") = :p" + count;
				params.put("p" + count, value.toString().toLowerCase());
			} else {
				jpql += "o." + parameter.getKey() + " = :p" + count;
				params.put("p" + count, value);
			}
		}
		return getAll(jpql, params);
	}

	public T getFirst(String jpql, Map<String, Object> parameters) {
		var list = getAll(jpql, parameters);
		if (list.isEmpty())
			return null;
		return list.get(0);
	}

	public T getFirstForAttribute(String attribute, Object value) {
		return getFirstForAttribute(attribute, value, false);
	}

	public T getFirstForAttribute(String attribute, Object value, boolean ignoreCase) {
		return getFirstForAttributes(Collections.singletonMap(attribute, value), ignoreCase);
	}

	public T getFirstForAttributes(Map<String, Object> parameters) {
		return getFirstForAttributes(parameters, false);
	}

	public T getFirstForAttributes(Map<String, Object> parameters, boolean ignoreCase) {
		var list = getForAttributes(parameters, ignoreCase);
		if (list.isEmpty())
			return null;
		return list.get(0);
	}

	public long getCount() {
		return getCountForAttributes(Collections.emptyMap());
	}

	public long getCount(String jpql, Map<String, Object> parameters) {
		var entityManager = createManager();
		var query = entityManager.createQuery(jpql, Long.class);
		appendParameters(query, parameters);
		var count = query.getSingleResult();
		return count == null ? 0 : count;
	}

	public long getCountForAttribute(String attribute, Object value) {
		return getCountForAttributes(Collections.singletonMap(attribute, value));
	}

	public long getCountForAttributes(Map<String, Object> parameters) {
		var jpql = "SELECT count(o) FROM " + entityType.getSimpleName() + " o";
		if (parameters.size() > 0) {
			jpql += " WHERE ";
			int count = 0;
			var internal = new HashMap<String, Object>();
			for (Entry<String, Object> parameter : parameters.entrySet()) {
				if (count != 0) {
					jpql += " AND ";
				}
				jpql += "o." + parameter.getKey() + " = :p" + ++count;
				internal.put("p" + count, parameter.getValue());
			}
			parameters = internal;
		}
		return getCount(jpql, parameters);
	}

	public long getLastId() {
		var query = "SELECT o FROM " + entityType.getSimpleName() + " o ORDER BY o.id DESC";
		var value = getFirst(query, Collections.emptyMap());
		if (value == null)
			return 1;
		return value.id + 1;
	}

	@Transactional
	public T insert(T entity) {
		if (entity == null)
			return null;
		entity.id = getLastId() + 1;
		var em = createManager();
		em.persist(entity);
		return entity;
	}

	@Transactional
	public Collection<T> insert(Collection<T> entities) {
		if (entities == null)
			return null;
		var em = createManager();
		for (var entity : entities) {
			entity.id = getLastId() + 1;
			em.persist(entity);
		}
		return entities;
	}

	@Transactional
	public T update(T entity) {
		if (entity == null)
			return null;
		var em = createManager();
		return em.merge(entity);
	}

	@Transactional
	public Collection<T> update(Collection<T> entities) {
		if (entities == null)
			return null;
		var em = createManager();
		for (T entity : entities) {
			em.merge(entity);
		}
		return entities;
	}

	@Transactional
	public void delete(T entity) {
		if (entity == null)
			return;
		var em = createManager();
		em.remove(em.merge(entity));
	}

	@Transactional
	public void delete(Collection<T> entities) {
		if (entities == null)
			return;
		var em = createManager();
		for (T entity : entities) {
			em.remove(em.merge(entity));
		}
	}

	public void delete(long id) {
		var entity = get(id);
		if (entity == null)
			return;
		delete(entity);
	}

	public T refresh(T entity) {
		if (entity == null)
			return null;
		var entityManager = createManager();
		entityManager.refresh(entity);
		return entity;
	}

	public Collection<T> query(String jpql, Map<String, Object> values) {
		var entityManager = createManager();
		var query = entityManager.createQuery(jpql, entityType);
		appendParameters(query, values);
		return query.getResultList();
	}

	@Transactional
	public void update(String jpql, Map<String, Object> values) {
		var entityManager = createManager();
		var query = entityManager.createQuery(jpql);
		appendParameters(query, values);
		query.executeUpdate();
	}

	private void appendParameters(Query query, Map<String, Object> values) {
		if (values != null) {
			for (var name : values.keySet()) {
				query.setParameter(name, values.get(name));
			}
		}
	}

	private EntityManager createManager() {
		return entityManager;
	}

}
