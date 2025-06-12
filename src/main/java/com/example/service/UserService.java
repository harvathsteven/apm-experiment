package com.example.service;

import com.example.model.User;
import com.example.repository.UserRepository;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.SpanKind;
import io.opentelemetry.api.trace.Tracer;
import io.opentelemetry.context.Context;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final Tracer tracer;

    public User createUser(User user) {
        Span span = tracer.spanBuilder("createUser")
                .setSpanKind(SpanKind.INTERNAL)
                .startSpan();
        
        try (var scope = span.makeCurrent()) {
            span.setAttribute("user.name", user.getName());
            span.setAttribute("user.email", user.getEmail());
            
            return userRepository.save(user);
        } catch (Exception e) {
            span.recordException(e);
            throw e;
        } finally {
            span.end();
        }
    }

    public User getUser(Long id) {
        Span span = tracer.spanBuilder("getUser")
                .setSpanKind(SpanKind.INTERNAL)
                .startSpan();
        
        try (var scope = span.makeCurrent()) {
            span.setAttribute("user.id", id);
            
            User user = userRepository.findById(id);
            if (user == null) {
                span.setAttribute("user.found", false);
                throw new RuntimeException("User not found");
            }
            
            span.setAttribute("user.found", true);
            return user;
        } catch (Exception e) {
            span.recordException(e);
            throw e;
        } finally {
            span.end();
        }
    }

    public List<User> getAllUsers() {
        Span span = tracer.spanBuilder("getAllUsers")
                .setSpanKind(SpanKind.INTERNAL)
                .startSpan();
        
        try (var scope = span.makeCurrent()) {
            return userRepository.findAll();
        } catch (Exception e) {
            span.recordException(e);
            throw e;
        } finally {
            span.end();
        }
    }

    public void deleteUser(Long id) {
        Span span = tracer.spanBuilder("deleteUser")
                .setSpanKind(SpanKind.INTERNAL)
                .startSpan();
        
        try (var scope = span.makeCurrent()) {
            span.setAttribute("user.id", id);
            userRepository.deleteById(id);
        } catch (Exception e) {
            span.recordException(e);
            throw e;
        } finally {
            span.end();
        }
    }
} 