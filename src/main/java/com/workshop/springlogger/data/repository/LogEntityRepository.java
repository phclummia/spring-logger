package com.workshop.springlogger.data.repository;

import com.workshop.springlogger.data.model.LogEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LogEntityRepository extends JpaRepository<LogEntity, Long> {
}
