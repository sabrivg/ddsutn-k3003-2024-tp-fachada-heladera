package ar.edu.utn.dds.k3003.repositories;


import ar.edu.utn.dds.k3003.database.EntityManagerHelper;
import ar.edu.utn.dds.k3003.database.Repository;
import ar.edu.utn.dds.k3003.model.Heladera;
import ar.edu.utn.dds.k3003.model.Temperatura;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class TemperaturaRepository extends Repository <Temperatura> {


    public TemperaturaRepository() {
        super(Temperatura.class);
    }

    public List<Temperatura> findAllById(Long idHeladera) {
        EntityManager em = EntityManagerHelper.getEntityManager();
        try {
            List<Temperatura> listaTemps = em.createQuery("from Temperatura where heladera_id="+idHeladera, Temperatura.class).getResultList();
              return listaTemps;
        } finally {
            EntityManagerHelper.closeEntityManager();
        }


    }
}
