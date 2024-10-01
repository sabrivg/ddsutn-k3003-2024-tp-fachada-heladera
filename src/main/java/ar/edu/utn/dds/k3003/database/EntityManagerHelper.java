package ar.edu.utn.dds.k3003.database;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.util.HashMap;
import java.util.Map;

public class EntityManagerHelper {

    private static final EntityManagerFactory emf;
    private static final ThreadLocal<EntityManager> threadLocal;

    static {
        emf = Persistence.createEntityManagerFactory(System.getenv().get("PERSISTENCE_UNIT"), iniciarVariablesBaseDatos());
        threadLocal = new ThreadLocal<>();
    }

    public static EntityManager getEntityManager() {
        EntityManager em = threadLocal.get();
        if (em == null || !em.isOpen()) {
            em = emf.createEntityManager();
            threadLocal.set(em);
        }
        return em;
    }

    public static void closeEntityManager() {
        EntityManager em = threadLocal.get();
        if (em != null) {
            em.close();
            threadLocal.remove();
        }
    }

    public static void beginTransaction() {
        getEntityManager().getTransaction().begin();
    }

    public static void commitTransaction() {
        getEntityManager().getTransaction().commit();
    }

    public static void rollbackTransaction() {
        try {
            getEntityManager().getTransaction().rollback();
        } catch (Exception e) {
            // Handle exception if needed
        }
    }

    public static void closeEntityManagerFactory() {
        emf.close();
    }

    // Configurar las variables de entorno
    private static Map<String, Object> iniciarVariablesBaseDatos() {
        Map<String, String> env = System.getenv();
        Map<String, Object> configOverrides = new HashMap<>();
        String[] keys = new String[]{
                "PERSISTENCE_UNIT", // nombre de la base de datos
                "javax.persistence.jdbc.url",
                "javax.persistence.jdbc.user",
                "javax.persistence.jdbc.password",
                "javax.persistence.jdbc.driver",
                "hibernate.hbm2ddl.auto",
                "hibernate.connection.pool_size",
                "hibernate.show_sql"
        };
        for (String key : keys) {
            if (env.containsKey(key)) {
                configOverrides.put(key, env.get(key));
            }
        }
        return configOverrides;
    }

}