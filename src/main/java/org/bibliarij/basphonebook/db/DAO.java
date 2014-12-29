package org.bibliarij.basphonebook.db;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Restrictions;

import java.util.List;

/**
 * Data Access Object context class
 * Created by Bibliarij on 02.08.2014.
 */
public class DAO {
    private static SessionFactory sessionFactory;

    /**
     * return configured <code>SessionFactory</code> object
     */
    private static SessionFactory getConnectionFactory(){
        if (sessionFactory == null){
            Configuration configuration = new Configuration().configure();
            StandardServiceRegistryBuilder ssrb = new StandardServiceRegistryBuilder().applySettings(configuration.getProperties());
            sessionFactory = configuration.buildSessionFactory(ssrb.build());

            return sessionFactory;
        }

        return sessionFactory;
    }

    /**
     * Save entity <code>Entry</code>
     */
    public static void saveEntry(Entry entry){
        Session session = getConnectionFactory().openSession();
        session.beginTransaction();
        session.saveOrUpdate(entry);
        session.getTransaction().commit();
        session.close();
    }

    /**
     * Delete entity <code>Entry</code>
     */
    public static void deleteEntry(Entry entry){
        Session session = getConnectionFactory().openSession();
        session.beginTransaction();
        session.delete(entry);
        session.getTransaction().commit();
        session.close();
    }

    /**
     * Get all <code>Entry</code> entities
     */
    public static List<Entry> getAllEntryEntities(){
        Session session = getConnectionFactory().openSession();
        Criteria criteria = session.createCriteria(Entry.class);
        List<Entry> entries = criteria.list();
        session.close();
        return entries;
    }
    /**
     * Get <code>Entry</code> entities matching value
     */
    public static List<Entry> findEntities(String value){
        Session session = getConnectionFactory().openSession();
        Criteria criteria = session.createCriteria(Entry.class);
        criteria.add(Restrictions.or(Restrictions.ilike("surnameNamePatronymic", value, MatchMode.ANYWHERE), Restrictions.ilike("address", value, MatchMode.ANYWHERE),
                Restrictions.ilike("phoneNumber", value, MatchMode.ANYWHERE)));
        List<Entry> entries = criteria.list();
        session.close();
        return entries;
    }
}
