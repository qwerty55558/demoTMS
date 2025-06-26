package com.myTMS.demo.repository.interfaces;

import com.myTMS.demo.dao.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JPAPostRepository extends JpaRepository<Post, Long> {
    Page<Post> findByTitleContaining(String title, Pageable pageable);

    Page<Post> findByIsAnsweredFalse(Pageable pageable);

    Long countByIsAnsweredFalse();
}
