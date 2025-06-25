package com.myTMS.demo.service;

import com.myTMS.demo.dao.Post;
import com.myTMS.demo.repository.interfaces.JPAPostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class PostService {
    private final JPAPostRepository postRepository;

    @Transactional(readOnly = true)
    public Page<Post> getPostList(Pageable pageable) {
        return postRepository.findAll(pageable);
    }

    public void savePost(Post post) {
        postRepository.save(post);
    }

    @Transactional(readOnly = true)
    public Optional<Post> getPost(Long id){
        return postRepository.findById(id);
    }

    public void deletePost(Long id){
        postRepository.deleteById(id);
    }

    public void updatePost(Post post) {
        postRepository.save(post);
    }

    @Transactional(readOnly = true)
    public Page<Post> getPostsByTitle(String title, Pageable pageable) {
        return postRepository.findByTitleContaining(title, pageable);
    }

    public Page<Post> getUnansweredPosts(Integer pageNum, Integer pageSize){
        return postRepository.findByIsAnsweredFalse(PageRequest.of(pageNum, pageSize, Sort.by("id").ascending()));
    }

    @Transactional(readOnly = true)
    public String getCountUnansweredPosts(){
        return String.valueOf(postRepository.countByIsAnsweredFalse());
    }
}
