package com.reinke.polls.repository;

import com.reinke.polls.model.Poll;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Sort;

import java.util.List;
import java.util.Optional;

@Repository
public interface PollRepository extends JpaRepository<Poll, Long> {

    // find by id
    @Override
    Optional<Poll> findById(Long pollId);

    // find by created by
    Page<Poll> findByCreatedBy(Long userId, Pageable pageable);

    // count how many poll was created by a given user id
    long countByCreatedBy(Long userId);

    // find by id in
    Page<Poll> findByIdIn(List<Long> pollIds, Pageable pageable);

    // find by id in and sort results
    List<Poll> findByIdIn(List<Long> pollIds, Sort sort);


}
