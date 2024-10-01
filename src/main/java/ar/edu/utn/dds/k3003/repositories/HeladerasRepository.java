package ar.edu.utn.dds.k3003.repositories;
import ar.edu.utn.dds.k3003.database.Repository;
import ar.edu.utn.dds.k3003.model.Heladera;
import ar.edu.utn.dds.k3003.model.Temperatura;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class HeladerasRepository extends Repository <Heladera> {


    public HeladerasRepository() {
        super(Heladera.class);
    }
}
