package com.example.demo.repository;

import com.example.demo.entity.Hashtag;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
@Transactional
public interface HashtagRepository extends JpaRepository<Hashtag, Long> {
}
