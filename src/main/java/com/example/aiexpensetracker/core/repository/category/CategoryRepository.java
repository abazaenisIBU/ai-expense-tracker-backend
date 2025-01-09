package com.example.aiexpensetracker.core.repository.category;

import com.example.aiexpensetracker.core.model.Category;
import com.example.aiexpensetracker.core.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Repository interface for performing CRUD operations on the Category entity.
 * Extends JpaRepository to provide basic database interaction methods.
 */
public interface CategoryRepository extends JpaRepository<Category, Long> {

    /**
     * Retrieves a list of categories associated with a specific user.
     *
     * @param user the User entity for whom the categories are to be retrieved.
     * @return a list of Category entities associated with the given user.
     */
    List<Category> findByUser(User user);
}
