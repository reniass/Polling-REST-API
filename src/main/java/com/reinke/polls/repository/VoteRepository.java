package com.reinke.polls.repository;

import com.reinke.polls.model.ChoiceVoteCount;
import com.reinke.polls.model.Vote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface VoteRepository extends JpaRepository<Vote, Long> {

    long countVotesByChoiceId(Long choiceId);

    // find all polls id in which a given user give a vote on it
    @Query("SELECT v.poll.id FROM Vote v where v.user.id = :userId")
    List<Long> findVotedPollIdsByUserId(@Param("userId") Long userId);

    // return how many times a given user vote
    @Query("SELECT count(v) FROM Vote v WHERE v.user.id = :userId")
    long countVotesByUserId(@Param("userId") Long userId);

    // return vote which a given user was voted in a given poll
    @Query("SELECT v FROM Vote v WHERE v.user.id = :userId AND v.poll.id = :pollId")
    Optional<Vote> findVoteByUserIdAndPollId(@Param("userId") Long userId, @Param("pollId") Long pollId);

    // return list of votes a given user was voted in given polls
    @Query("SELECT v FROM Vote v WHERE v.user.Id = :userId AND v.poll.id in :pollIds")
    List<Vote> findVotesByUserIdAndByPollIdIn(@Param("userId") Long userId, @Param("pollIds") List<Long> pollIds);

    // return list of objects ChoiceVoteCount group by polls
    @Query("SELECT NEW com.reinke.polls.model.ChoiceVoteCount(v.choice.id, count(v.id)) FROM Vote v WHERE v.poll.id = :pollId GROUP BY v.choice.id")
    List<ChoiceVoteCount> countByPollIdGroupByChoiceId(@Param("pollId") Long pollId);

    // return list of objectsChoiceVoteCount in given polls group by polls
    @Query("SELECT NEW com.reinke.polls.model.ChoiceVoteCount(v.choice.id, count(v.id)) FROM Vote v WHERE v.poll.id in :pollIds GROUP BY v.choice.id")
    List<ChoiceVoteCount> countByPollIdInGroupByChoiceId(@Param("pollIds") List<Long> pollIds);
}
