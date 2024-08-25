package com.telerikacademy.web.smartgarageti.repositories;

import com.telerikacademy.web.smartgarageti.exceptions.EntityNotFoundException;
import com.telerikacademy.web.smartgarageti.models.User;
import com.telerikacademy.web.smartgarageti.repositories.contracts.UserRepository;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class UserRepositoryImpl implements UserRepository {
    private final SessionFactory sessionFactory;

    @Autowired
    public UserRepositoryImpl(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public User getByUsername(String username) {
        try(Session session = sessionFactory.openSession()){
            return session.createQuery("from User where username = :username", User.class)
                    .setParameter("username", username)
                    .uniqueResult();
        }
    }

    @Override
    public User getUserByEmail(String email) {
        try(Session session = sessionFactory.openSession()){
            return session.createQuery("from User where email = :email", User.class)
                    .setParameter("email", email)
                    .uniqueResult();
        }
    }

    @Override
    public User getUserById(int id) {
        try(Session session = sessionFactory.openSession()){
          User user = session.get(User.class, id);
            if(user == null){
                throw new EntityNotFoundException("User", id);
            }
            return user;
        }
    }

    @Override
    public void create(User user) {
        try(Session session = sessionFactory.openSession()){
            session.beginTransaction();
            session.persist(user);
            session.getTransaction().commit();
        }
    }

    @Override
    public void update(User user) {
        try(Session session = sessionFactory.openSession()){
            session.beginTransaction();
            session.merge(user);
            session.getTransaction().commit();
        }
    }

    @Override
    public List<User> getAllUsers() {
        try(Session session = sessionFactory.openSession()){
            return session.createQuery("from User", User.class)
                    .list();
        }
    }
}
