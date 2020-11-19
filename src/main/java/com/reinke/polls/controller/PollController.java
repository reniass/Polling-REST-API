package com.reinke.polls.controller;

import com.reinke.polls.model.Poll;
import com.reinke.polls.payload.PagedResponse;
import com.reinke.polls.payload.PollRequest;
import com.reinke.polls.payload.PollResponse;
import com.reinke.polls.payload.VoteRequest;
import com.reinke.polls.security.UserPrincipal;
import com.reinke.polls.service.PollService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/polls")
public class PollController {

    @Autowired
    private PollController pollController;

    @Autowired
    private PollService pollService;

    // cos jest zle

    //create poll
    @PostMapping
    public ResponseEntity<Poll> createPoll(@RequestBody PollRequest pollRequest) {

        Poll poll = pollService.createPoll(pollRequest);

        return ResponseEntity.status(HttpStatus.CREATED).body(poll);

    }


    //getPoll
    @GetMapping
    public PagedResponse<PollResponse> getPolls(@AuthenticationPrincipal UserPrincipal userPrincipal,
                                                @RequestParam(value = "page") int page,
                                                @RequestParam(value = "size") int size) {

        return pollService.getAllPolls(userPrincipal, page, size);

    }

    // getPollById
    @GetMapping("/{pollId}")
    public PollResponse getPollById(@PathVariable(value = "pollId") Long pollId,
                                    @AuthenticationPrincipal UserPrincipal userPrincipal) {

        return pollService.getPollById(pollId, userPrincipal);

    }

    // castVote
    @PostMapping("/{pollId}/votes")
    public PollResponse castVote(@PathVariable(value = "pollId") Long pollId,
                                 @RequestBody VoteRequest voteRequest,
                                 @AuthenticationPrincipal UserPrincipal userPrincipal) {

        return pollService.castVoteAndGetUpdatedPoll(pollId, voteRequest, userPrincipal);

    }

}
