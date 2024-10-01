package ar.edu.utn.dds.k3003.database;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.util.List;
import java.util.Optional;

public class Repository<T> {

    private final Class<T> entityClass;

    public Repository(Class<T> entityClass) {
        this.entityClass = entityClass;
    }

    public T save(T entity) {
        EntityManagerHelper.beginTransaction();
        EntityManager em = EntityManagerHelper.getEntityManager();
        try {
            em.persist(entity);
            EntityManagerHelper.commitTransaction();
            return entity; // Devolver la entidad con el ID generado
        } catch (Exception e) {
            EntityManagerHelper.rollbackTransaction();
            throw e;
        } finally {
            EntityManagerHelper.closeEntityManager();
        }
    }

    public Optional<T> findById(Object id) {
        EntityManager em = EntityManagerHelper.getEntityManager();
        try {
            return Optional.ofNullable(em.find(entityClass, id));
        } finally {
            EntityManagerHelper.closeEntityManager();
        }
    }

    public T update(T entity) {
        EntityManagerHelper.beginTransaction();
        EntityManager em = EntityManagerHelper.getEntityManager();
        try {
            T updatedEntity = em.merge(entity);
            EntityManagerHelper.commitTransaction();
            return updatedEntity; // Devolver la entidad actualizada
        } catch (Exception e) {
            EntityManagerHelper.rollbackTransaction();
            throw e;
        } finally {
            EntityManagerHelper.closeEntityManager();
        }
    }

    public void delete(T entity) {
        EntityManagerHelper.beginTransaction();
        EntityManager em = EntityManagerHelper.getEntityManager();
        try {
            em.remove(em.contains(entity) ? entity : em.merge(entity));
            EntityManagerHelper.commitTransaction();
        } catch (Exception e) {
            EntityManagerHelper.rollbackTransaction();
            throw e;
        } finally {
            EntityManagerHelper.closeEntityManager();
        }
    }

    public List<T> findAll() {
        EntityManager em = EntityManagerHelper.getEntityManager();
        try {
            TypedQuery<T> query = em.createQuery("SELECT e FROM " + entityClass.getSimpleName() + " e", entityClass);
            return query.getResultList();
        } finally {
            EntityManagerHelper.closeEntityManager();
        }
    }
}