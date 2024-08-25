package com.telerikacademy.web.smartgarageti.repositories;

import com.telerikacademy.web.smartgarageti.exceptions.EntityNotFoundException;
import com.telerikacademy.web.smartgarageti.models.FilteredUserOptions;
import com.telerikacademy.web.smartgarageti.models.User;
import com.telerikacademy.web.smartgarageti.repositories.contracts.UserRepository;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class UserRepositoryImpl implements UserRepository {
    private final SessionFactory sessionFactory;

    @Autowired
    public UserRepositoryImpl(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public User getByUsername(String username) {
        try (Session session = sessionFactory.openSession()) {
            return session.createQuery("from User where username = :username", User.class)
                    .setParameter("username", username)
                    .uniqueResult();
        }
    }

    @Override
    public User getUserByEmail(String email) {
        try (Session session = sessionFactory.openSession()) {
            return session.createQuery("from User where email = :email", User.class)
                    .setParameter("email", email)
                    .uniqueResult();
        }
    }

    @Override
    public User getUserById(int id) {
        try (Session session = sessionFactory.openSession()) {
            User user = session.get(User.class, id);
            if (user == null) {
                throw new EntityNotFoundException("User", id);
            }
            return user;
        }
    }

    @Override
    public void create(User user) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            session.persist(user);
            session.getTransaction().commit();
        }
    }

    @Override
    public void update(User user) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            session.merge(user);
            session.getTransaction().commit();
        }
    }

    @Override
    public List<User> getAllUsers(FilteredUserOptions filteredUserOptions, int page, int size) {
        try (Session session = sessionFactory.openSession()) {
            List<User> users = filterUserOptions(filteredUserOptions, session);
            int start = (page - 1) * size;
            return users.subList(start, Math.min(start + size, users.size()));
        }
    }

    private List<User> filterUserOptions(FilteredUserOptions filteredUserOptions, Session session) {
        List<String> filters = new ArrayList<>();
        Map<String, Object> parameters = new HashMap<>();
        StringBuilder queryString = new StringBuilder("SELECT DISTINCT u FROM User u ");
        queryString.append("JOIN u.clientCars cc ");
        queryString.append("JOIN cc.carServices cs ");
        queryString.append("JOIN cc.vehicle v ");
        queryString.append("JOIN v.brand b ");

        filteredUserOptions.getUsername().ifPresent(value -> {
            filters.add("u.username like :username");
            parameters.put("username", String.format("%%%s%%", value));
        });

        filteredUserOptions.getEmail().ifPresent(value -> {
            filters.add("u.email like :email");
            parameters.put("email", String.format("%%%s%%", value));
        });

        filteredUserOptions.getPhoneNumber().ifPresent(value -> {
            filters.add("u.phoneNumber like :phoneNumber");
            parameters.put("phoneNumber", String.format("%%%s%%", value));
        });

        filteredUserOptions.getVehicleBrand().ifPresent(value -> {
            filters.add("LOWER(b.name) like :vehicleBrand");
            parameters.put("vehicleBrand", String.format("%%%s%%", value.toLowerCase()));
        });

        filteredUserOptions.getVisitDateFrom().ifPresent(value -> {
            filters.add("cs.serviceDate >= :visitDateFrom");
            parameters.put("visitDateFrom", value);
        });

        filteredUserOptions.getVisitDateTo().ifPresent(value -> {
            filters.add("cs.serviceDate <= :visitDateTo");
            parameters.put("visitDateTo", value);
        });

        if (!filters.isEmpty()) {
            queryString.append("WHERE ").append(String.join(" AND ", filters));
        }

        queryString.append(generateOrderBy(filteredUserOptions));
        Query<User> query = session.createQuery(queryString.toString(), User.class);
        query.setProperties(parameters);
        return query.list();
    }

    private String generateOrderBy(FilteredUserOptions filteredUserOptions) {
        if (filteredUserOptions.getSortBy().isEmpty()) {
            return "";
        }

        String orderBy = "";
        switch (filteredUserOptions.getSortBy().get()) {
            case "username":
                orderBy = "username";
                break;
            case "serviceDate":
                orderBy = "cs.serviceDate";
                break;
            default:
                return "";
        }

        orderBy = String.format(" order by %s", orderBy);

        if (filteredUserOptions.getSortOrder().isPresent() && filteredUserOptions.getSortOrder().get().equalsIgnoreCase("desc")) {
            orderBy = String.format("%s desc", orderBy);
        }
        return orderBy;
    }
}
