package com.example.userservice.repo;

import com.example.userservice.entity.CustomUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Date;
import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<CustomUser,Long> {
    List<CustomUser> findByBirthdayBetween(Date fromDate, Date toDate);
}
