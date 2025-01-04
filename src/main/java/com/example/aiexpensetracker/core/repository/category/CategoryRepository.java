package com.example.aiexpensetracker.core.repository.category;

import com.example.aiexpensetracker.core.model.Category;
import com.example.aiexpensetracker.core.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    List<Category> findByUser(User user);
}
