package ar.edi.itn.dds.k3003.model;

import ar.edu.utn.dds.k3003.model.Heladera;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class PersistenceIT {

    static EntityManagerFactory entityManagerFactory ;
    EntityManager entityManager ;

    @BeforeAll
    public static void setUpClass() throws Exception {
        entityManagerFactory = Persistence.createEntityManagerFactory("tpdb");
    }
    @BeforeEach
    public void setup() throws Exception {
        entityManager = entityManagerFactory.createEntityManager();
    }
    @Test
    public void testConectar() {
// vacío, para ver que levante el ORM
    }
    @Test
    public void testGuardarYRecuperarHeladera() throws Exception {
        Heladera h1=new Heladera("miHeladera");
        entityManager.getTransaction().begin();
        entityManager.persist(h1); //se prepara insert
        entityManager.getTransaction().commit();//se ejecutan las sentencias
        entityManager.close();


        entityManager = entityManagerFactory.createEntityManager();
        Heladera h2 =(Heladera) entityManager.createQuery("from Heladera where nombre='miHeladera'").getSingleResult();

        assertEquals("miHeladera",h2.getNombre());
   }

}

