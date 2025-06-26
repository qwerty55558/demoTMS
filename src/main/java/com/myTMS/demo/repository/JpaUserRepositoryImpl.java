package com.myTMS.demo.repository;

import com.myTMS.demo.dao.users.Users;
import com.myTMS.demo.repository.interfaces.UserRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
public class JpaUserRepositoryImpl implements UserRepository {

    @PersistenceContext
    private EntityManager em;

    @Override
    public Optional<Users> findUserById(Long id) {
        try {
            return Optional.ofNullable(em.find(Users.class, id));
        } catch (Exception e) {
            log.info("findUserById Error", e);
            return Optional.empty();
        }
    }

    @Override
    public Optional<Users> findUserByEmail(String email) {
        try {
            String jpql = "select u from Users u where u.email = :email";
            TypedQuery<Users> query = em.createQuery(jpql, Users.class)
                    .setParameter("email", email);
            return Optional.ofNullable(query.getSingleResult());
        } catch (NoResultException e) {
            return Optional.empty();
        } catch (Exception e) {
            log.info("findUserByEmail Error", e);
            return Optional.empty();
        }
    }

    @Override
    public Optional<Users> findUserByEmailWithOrders(String name) {
        try {
            TypedQuery<Users> query = em.createQuery(
                    "SELECT u FROM Users u LEFT JOIN FETCH u.orders WHERE u.email = :email", Users.class);
            query.setParameter("email", name);
            return query.getResultList().stream().findFirst();
        } catch (Exception e) {
            log.info("findUserByEmailWithOrders Error", e);
            return Optional.empty();
        }
    }

    @Override
    public boolean createUser(Users user) {
        try {
            em.persist(user);
            return true;
        } catch (Exception e) {
            log.info("createUser error", e);
            return false;
        }
    }

    @Override
    public boolean updateUser(Users user) {
        try{
            Optional<Users> userByEmail = findUserByEmailWithOrders(user.getEmail());
            if (userByEmail.isPresent()) {
                em.remove(userByEmail.get());
                em.persist(user);
                return true;
            }
            return false;
        }catch (Exception e){
            log.info("updateUser error", e);
            return false;
        }
    }

    @Override
    public Boolean isDuplicatedByEmail(String email) {
        Optional<Users> userByEmail = findUserByEmail(email);
        return userByEmail.isPresent();
    }

    @Override
    public boolean saveUser(Users user) {
        try {
            em.merge(user);
            return true;
        } catch (Exception e) {
            log.info("saveUser error", e);
            return false;
        }
    }

    @Override
    public Long getUsersCount() {
        try {
            String jpql = "SELECT COUNT(u) FROM Users u";
            return em.createQuery(jpql, Long.class).getSingleResult();
        } catch (Exception e) {
            log.info("getUsersCount error", e);
            return -1L;
        }
    }

    public Map<LocalDate, Long> getUserSignUpTrendLast5Days() {
        LocalDate today = LocalDate.now();
        LocalDate fiveDaysAgo = today.minusDays(4); // 오늘 포함 5일

        String jpql = "SELECT FUNCTION('DATE', u.createdAt), COUNT(u) " +
                "FROM Users u " +
                "WHERE u.createdAt >= :startDate " +
                "GROUP BY FUNCTION('DATE', u.createdAt) " +
                "ORDER BY FUNCTION('DATE', u.createdAt)";

        List<Object[]> results = em.createQuery(jpql, Object[].class)
                .setParameter("startDate", fiveDaysAgo.atStartOfDay())
                .getResultList();

        Map<LocalDate, Long> trend = new LinkedHashMap<>();
        for (int i = 0; i < 5; i++) {
            trend.put(fiveDaysAgo.plusDays(i), 0L);
        }

        for (Object[] row : results) {
            LocalDate date = ((java.sql.Date) row[0]).toLocalDate();
            Long count = (Long) row[1];
            trend.put(date, count);
        }

        return trend;
    }

}
