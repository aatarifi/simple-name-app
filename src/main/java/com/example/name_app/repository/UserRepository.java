package com.example.name_app.repository;

import com.example.name_app.entity.UserProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<UserProfile, Long> {
    // This is blank on purpose!
    // Spring handles all the save, findAll, and delete code automatically.
}
