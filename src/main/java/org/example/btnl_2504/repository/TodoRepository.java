package org.example.btnl_2504.repository;

import org.example.btnl_2504.model.entity.Todo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TodoRepository extends JpaRepository<Todo, Long> {
}

