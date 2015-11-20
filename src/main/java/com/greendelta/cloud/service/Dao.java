package com.greendelta.cloud.service;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.TypedQuery;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.TypeLiteral;
import com.google.inject.persist.Transactional;
import com.greendelta.cloud.model.AbstractEntity;

import static org.openlca.cloud.util.Strings.concat;

class Dao<T extends AbstractEntity> {

	private Provider<EntityManager> entityManagerProvider;
	// used for getting detached objects (no cache, session, etc.)
	private Provider<EntityManagerFactory> entityManagerFactoryProvider;
	private Class<T> entityType;

	@Inject
	@SuppressWarnings("unchecked")
	public Dao(TypeLiteral<T> type,
			Provider<EntityManager> entityManagerProvider,
			Provider<EntityManagerFactory> entityManagerFactoryProvider) {
		this.entityType = (Class<T>) type.getRawType();
		this.entityManagerProvider = entityManagerProvider;
		this.entityManagerFactoryProvider = entityManagerFactoryProvider;
	}

	public T get(long id) {
		if (id < 1)
			return null;
		EntityManager entityManager = createManager();
		return entityManager.find(entityType, id);
	}

	public T getDetached(long id) {
		if (id < 1)
			return null;
		EntityManager entityManager = entityManagerFactoryProvider.get()
				.createEntityManager();
		try {
			T o = entityManager.find(entityType, id);
			return o;
		} finally {
			entityManager.close();
		}
	}

	public List<T> getAll() {
		EntityManager em = createManager();
		String jpql = concat("SELECT o FROM ", entityType.getSimpleName(), " o");
		TypedQuery<T> query = em.createQuery(jpql, entityType);
		return query.getResultList();
	}

	public List<T> getAll(String jpql, Map<String, ? extends Object> parameters) {
		EntityManager em = createManager();
		TypedQuery<T> query = em.createQuery(jpql, entityType);
		if (parameters != null)
			for (String parameter : parameters.keySet())
				query.setParameter(parameter, parameters.get(parameter));
		return query.getResultList();
	}

	public <RT> List<RT> getAttributes(String jpql,
			Map<String, ? extends Object> parameters, Class<RT> resultClass) {
		EntityManager em = createManager();
		TypedQuery<RT> query = em.createQuery(jpql, resultClass);
		if (parameters != null)
			for (String parameter : parameters.keySet())
				query.setParameter(parameter, parameters.get(parameter));
		return query.getResultList();
	}

	public List<T> getForAttribute(String attribute, Object value) {
		return getForAttributes(Collections.singletonMap(attribute, value));
	}

	public List<T> getForAttributes(Map<String, Object> parameters) {
		String jpql = concat("SELECT o FROM ", entityType.getSimpleName(), " o");
		if (parameters != null && parameters.size() > 0) {
			jpql += " WHERE ";
			int count = 0;
			Map<String, Object> internal = new HashMap<>();
			for (Entry<String, Object> parameter : parameters.entrySet()) {
				if (count != 0)
					jpql += " AND ";
				Object value = parameter.getValue();
				String comparator = "=";
				if (value instanceof Collection
						|| (value != null && value.getClass().isArray()))
					comparator = "IN";
				jpql += concat("o.", parameter.getKey(), " ", comparator, " :p", ++count);
				if (value != null && value.getClass().isArray()) {
					Set<Object> values = new HashSet<>();
					for (Object object : (Object[]) value)
						values.add(object);
					value = values;
				}
				internal.put(concat("p", count), value);
			}
			parameters = internal;
		}
		return getAll(jpql, parameters);
	}

	public T getFirst(String jpql, Map<String, Object> parameters) {
		List<T> list = getAll(jpql, parameters);
		if (list.isEmpty())
			return null;
		else
			return list.get(0);
	}

	public T getFirstForAttribute(String attribute, Object value) {
		return getFirstForAttributes(Collections.singletonMap(attribute, value));
	}

	public T getFirstForAttributes(Map<String, Object> parameters) {
		List<T> list = getForAttributes(parameters);
		if (list.isEmpty())
			return null;
		else
			return list.get(0);
	}

	public long getCount(String jpql, Map<String, ? extends Object> parameters) {
		EntityManager em = createManager();
		TypedQuery<Long> query = em.createQuery(jpql, Long.class);
		for (String parameter : parameters.keySet())
			query.setParameter(parameter, parameters.get(parameter));
		Long count = query.getSingleResult();
		return count == null ? 0 : count;
	}

	public long getCountForAttribute(String attribute, Object value) {
		return getCountForAttributes(Collections.singletonMap(attribute, value));
	}

	public long getCountForAttributes(Map<String, Object> parameters) {
		String jpql = concat("SELECT count(o) FROM ", entityType.getSimpleName(), " o");
		if (parameters != null && parameters.size() > 0) {
			jpql += " WHERE ";
			int count = 0;
			Map<String, Object> internal = new HashMap<>();
			for (Entry<String, Object> parameter : parameters.entrySet()) {
				if (count != 0)
					jpql += " AND ";
				jpql += concat("o.", parameter.getKey(), " = :p", ++count);
				internal.put("p" + count, parameter.getValue());
			}
			parameters = internal;
		}
		return getCount(jpql, parameters);
	}

	private long getNewId() {
		String query = concat("SELECT o FROM ", entityType.getSimpleName(), " o ORDER BY o.id DESC");
		T value = getFirst(query, Collections.emptyMap());
		if (value == null)
			return 1;
		return value.getId() + 1;
	}

	@Transactional(rollbackOn = Exception.class)
	public T insert(T entity) {
		if (entity == null)
			return null;
		entity.setId(getNewId());
		EntityManager em = createManager();
		em.persist(entity);
		return entity;
	}

	@Transactional(rollbackOn = Exception.class)
	public Collection<T> insert(Collection<T> entities) {
		if (entities == null)
			return null;
		EntityManager em = createManager();
		for (T entity : entities)
			em.persist(entity);
		return entities;
	}

	@Transactional(rollbackOn = Exception.class)
	public T update(T entity) {
		if (entity == null)
			return null;
		EntityManager em = createManager();
		return em.merge(entity);
	}

	@Transactional(rollbackOn = Exception.class)
	public Collection<T> update(Collection<T> entities) {
		if (entities == null)
			return null;
		EntityManager em = createManager();
		for (T entity : entities)
			em.merge(entity);
		return entities;
	}

	@Transactional(rollbackOn = Exception.class)
	public void delete(T entity) {
		if (entity == null)
			return;
		EntityManager em = createManager();
		em.remove(em.merge(entity));
	}

	@Transactional(rollbackOn = Exception.class)
	public void delete(Collection<T> entities) {
		if (entities == null)
			return;
		EntityManager em = createManager();
		for (T entity : entities)
			em.remove(em.merge(entity));

	}

	public void delete(long id) {
		T entity = get(id);
		if (entity != null)
			delete(entity);
	}

	public T refresh(T entity) {
		if (entity == null)
			return null;
		EntityManager entityManager = createManager();
		entityManager.refresh(entity);
		return entity;
	}

	public Collection<T> query(String jpql, Map<String, Object> values) {
		if (jpql == null)
			return Collections.emptyList();
		EntityManager entityManager = createManager();
		TypedQuery<T> query = entityManager.createQuery(jpql, entityType);
		if (values != null)
			for (String name : values.keySet())
				query.setParameter(name, values.get(name));
		Collection<T> result = query.getResultList();
		if (result == null)
			return Collections.emptyList();
		return result;

	}

	private EntityManager createManager() {
		return entityManagerProvider.get();
	}

}
