package com.example.controller;

import com.example.model.User;
import com.example.service.UserService;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.SpanKind;
import io.opentelemetry.api.trace.Tracer;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final Tracer tracer;

    @PostMapping
    public ResponseEntity<User> createUser(@RequestBody User user) {
        Span span = tracer.spanBuilder("createUser")
                .setSpanKind(SpanKind.SERVER)
                .startSpan();
        
        try (var scope = span.makeCurrent()) {
            User createdUser = userService.createUser(user);
            return ResponseEntity.ok(createdUser);
        } catch (Exception e) {
            span.recordException(e);
            throw e;
        } finally {
            span.end();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> getUser(@PathVariable Long id) {
        Span span = tracer.spanBuilder("getUser")
                .setSpanKind(SpanKind.SERVER)
                .startSpan();
        
        try (var scope = span.makeCurrent()) {
            User user = userService.getUser(id);
            return ResponseEntity.ok(user);
        } catch (Exception e) {
            span.recordException(e);
            throw e;
        } finally {
            span.end();
        }
    }

    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() {
        Span span = tracer.spanBuilder("getAllUsers")
                .setSpanKind(SpanKind.SERVER)
                .startSpan();
        
        try (var scope = span.makeCurrent()) {
            List<User> users = userService.getAllUsers();
            return ResponseEntity.ok(users);
        } catch (Exception e) {
            span.recordException(e);
            throw e;
        } finally {
            span.end();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        Span span = tracer.spanBuilder("deleteUser")
                .setSpanKind(SpanKind.SERVER)
                .startSpan();
        
        try (var scope = span.makeCurrent()) {
            userService.deleteUser(id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            span.recordException(e);
            throw e;
        } finally {
            span.end();
        }
    }
} 