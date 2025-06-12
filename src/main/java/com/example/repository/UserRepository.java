package com.example.repository;

import com.example.model.User;
import net.bytebuddy.implementation.bind.annotation.*;
import org.springframework.stereotype.Repository;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.Callable;

@Repository
public class UserRepository {
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
                                 @SuperCall Callable<?> callable) {
        long startTime = System.nanoTime();
        try {
            return callable.call();
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            long endTime = System.nanoTime();
            System.out.printf("Method %s took %d ns%n", method, (endTime - startTime));
        }
    }
} 