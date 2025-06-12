package com.example.repository;

import com.example.model.User;
import net.bytebuddy.implementation.bind.annotation.*;
import org.springframework.stereotype.Repository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.Callable;

@Repository
public class UserRepository {
    private static final Logger logger = LoggerFactory.getLogger(UserRepository.class);
    private final ConcurrentHashMap<Long, User> users = new ConcurrentHashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(1);

    public User save(User user) {
        if (user.getId() == null) {
            user = User.builder()
                    .id(idGenerator.getAndIncrement())
                    .name(user.getName())
                    .email(user.getEmail())
                    .build();
        }
        users.put(user.getId(), user);
        return user;
    }

    public User findById(Long id) {
        return users.get(id);
    }

    public List<User> findAll() {
        return new ArrayList<>(users.values());
    }

    public void deleteById(Long id) {
        users.remove(id);
    }

    // Byte Buddy interceptor for method timing
    @RuntimeType
    public static Object intercept(@Origin String method,
                                 @AllArguments Object[] args,
                                 @SuperCall Callable<?> callable,
                                 @This Object target) {
        long startTime = System.nanoTime();
        try {
            Object result = callable.call();
            long endTime = System.nanoTime();
            logger.info("Method {} took {} ns", method, (endTime - startTime));
            return result;
        } catch (Exception e) {
            long endTime = System.nanoTime();
            logger.error("Method {} failed after {} ns", method, (endTime - startTime), e);
            throw new RuntimeException(e);
        }
    }
} 