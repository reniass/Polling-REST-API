package com.reinke.polls.service;

import com.reinke.polls.exception.BadRequestException;
import com.reinke.polls.exception.ResourceNotFoundException;
import com.reinke.polls.model.*;
import com.reinke.polls.payload.*;
import com.reinke.polls.repository.PollRepository;
import com.reinke.polls.repository.UserRepository;
import com.reinke.polls.repository.VoteRepository;
import com.reinke.polls.security.UserPrincipal;
import com.reinke.polls.util.AppConstants;
import com.reinke.polls.util.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class PollService {

    @Autowired
    private PollRepository pollRepository;

    @Autowired
    private VoteRepository voteRepository;

    @Autowired
    private UserRepository userRepository;

    // get all polls
    public PagedResponse<PollResponse> getAllPolls(UserPrincipal currentUser, int page, int size) {
        validatePageNumberAndSize(page, size);

        Pageable pageable = PageRequest.of(page, size);

        Page<Poll> pollsPage = pollRepository.findAll(pageable);

        User user = userRepository.findByUsername(currentUser.getUsername())
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", currentUser.getUsername()));


        List<PollResponse> pollsList = pollsPage.stream().map(poll -> {

            Optional<Vote> vote = voteRepository.findVoteByUserIdAndPollId(user.getId(), poll.getId());
            Long userVote = null;
            if (vote.get() != null) {
                userVote = vote.get().getId();
            }
            return ModelMapper.mapPollToPollResponse(poll, getChoiceVoteCountMap(Arrays.asList(poll.getId())), poll.getUser(), userVote);

            })
        .collect(Collectors.toList());


        PagedResponse<PollResponse> pagedResponse = new PagedResponse<>();
        pagedResponse.setContent(pollsList);
        pagedResponse.setPage(page);
        pagedResponse.setSize(size);
        pagedResponse.setTotalElements(pollsPage.getTotalElements());
        pagedResponse.setTotalPages(pollsPage.getTotalPages());

        if (page == pollsPage.getTotalPages() - 1) {
            pagedResponse.setLast(true);
        } else {
            pagedResponse.setLast(false);
        }

        return pagedResponse;
    }



    // get all polls created by a given user
    public PagedResponse<PollResponse> getPollsCreatedBy(String username, UserPrincipal currentUser, int page, int size) {
        validatePageNumberAndSize(page, size);

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));

        Pageable pageable = PageRequest.of(page, size);
        Page<Poll> pollsPage = pollRepository.findByCreatedBy(user.getId(), pageable);

        PagedResponse<PollResponse> pagedResponse = new PagedResponse<>();

        List<PollResponse> pollsResponse = pollsPage.stream().map(poll -> {

            Optional<Vote> vote = voteRepository.findVoteByUserIdAndPollId(currentUser.getId(), poll.getId());
            Long userVote = null;
            if (vote.get() != null) {
                userVote = vote.get().getId();
            }
            return ModelMapper.mapPollToPollResponse(poll, getChoiceVoteCountMap(Arrays.asList(poll.getId())), user, userVote);

        })
        .collect(Collectors.toList());

        pagedResponse.setContent(pollsResponse);
        pagedResponse.setPage(page);
        pagedResponse.setSize(size);
        pagedResponse.setTotalElements(pollsPage.getTotalElements());
        pagedResponse.setTotalPages(pollsPage.getTotalPages());

        if (page == pollsPage.getTotalPages() - 1) {
            pagedResponse.setLast(true);
        } else {
            pagedResponse.setLast(false);
        }

        return pagedResponse;
    }



    // get all polls voted by a given user
    public PagedResponse<PollResponse> getPollsVotedBy(String username, UserPrincipal currentUser, int page, int size) {
        validatePageNumberAndSize(page, size);

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));

        List<Long> pollIds = voteRepository.findVotedPollIdsByUserId(user.getId());

        Pageable pageable = PageRequest.of(page, size);
        Page<Poll> pollsPage = pollRepository.findByIdIn(pollIds, pageable);

        PagedResponse<PollResponse> pagedResponse = new PagedResponse<>();

        List<PollResponse> pollsResponse = pollsPage.stream().map(poll -> {

            Optional<Vote> vote = voteRepository.findVoteByUserIdAndPollId(currentUser.getId(), poll.getId());
            Long userVote = null;
            if (vote.get() != null) {
                userVote = vote.get().getId();
            }
            return ModelMapper.mapPollToPollResponse(poll, getChoiceVoteCountMap(Arrays.asList(poll.getId())), user, userVote);

            })
        .collect(Collectors.toList());

        pagedResponse.setContent(pollsResponse);
        pagedResponse.setPage(page);
        pagedResponse.setSize(size);
        pagedResponse.setTotalElements(pollsPage.getTotalElements());
        pagedResponse.setTotalPages(pollsPage.getTotalPages());

        if (page == pollsPage.getTotalPages() - 1) {
            pagedResponse.setLast(true);
        } else {
            pagedResponse.setLast(false);
        }

        return pagedResponse;
    }



    // create poll
    public Poll createPoll(PollRequest pollRequest) {
        Poll poll = new Poll();

        poll.setQuestion(pollRequest.getQuestion());

        List<Choice> choices = pollRequest.getChoices().stream()
                .map(choiceRequest -> {
                    Choice choice = new Choice();
                    choice.setText(choiceRequest.getText());
                    choice.setPoll(poll);

                    return choice;
                })
                .collect(Collectors.toList());

        poll.setChoices(choices);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();

        User user = userRepository.findById(userPrincipal.getId())
                .orElseThrow(() -> new ResourceNotFoundException("User", "id",  userPrincipal.getId()));

        poll.setUser(user);


        PollLength pollLength = pollRequest.getPollLength();
        Instant expirationDateTime = Instant.now()
                .plus(Duration.ofDays(pollLength.getDays()))
                .plus(Duration.ofHours(pollLength.getHours()));
        poll.setExpirationDateTime(expirationDateTime);

        return pollRepository.save(poll);
    }



                     // get poll by id
    public PollResponse getPollById(Long pollId, UserPrincipal userPrincipal) {
        User user = userRepository.findById(userPrincipal.getId()).orElseThrow(() -> new ResourceNotFoundException("User", "id", userPrincipal.getId()));

        Poll poll = pollRepository.findById(pollId).orElseThrow(() -> new ResourceNotFoundException("Poll", "id", pollId));


        PollResponse pollResponse = new PollResponse();
        pollResponse.setId(pollId);
        pollResponse.setQuestion(poll.getQuestion());



        List<ChoiceResponse> choiceResponses = poll.getChoices().stream()
                .map(choiceInStream -> {
                    ChoiceResponse choiceResponse = new ChoiceResponse();
                    choiceResponse.setId(choiceInStream.getId());
                    choiceResponse.setText(choiceInStream.getText());

                    long votesCount = voteRepository.countVotesByChoiceId(choiceInStream.getId());

                    choiceResponse.setVoteCount(votesCount);

                    return choiceResponse;
                })
                .collect(Collectors.toList());

        pollResponse.setChoices(choiceResponses);

        UserSummary userSummary = new UserSummary(user.getId(), user.getUsername(), user.getName());

        pollResponse.setCreatedBy(userSummary);

        pollResponse.setCreationDateTime(poll.getCreatedAt());

        pollResponse.setExpirationDateTime(poll.getExpirationDateTime());

        pollResponse.setIsExpired(poll.getExpirationDateTime().isBefore(Instant.now()));

        Optional<Vote> vote = voteRepository.findVoteByUserIdAndPollId(user.getId(), pollId);

        if (vote.get() != null) {
            pollResponse.setSelectedChoice(vote.get().getChoice().getId());
        }

        long votesCount  = pollResponse.getChoices().stream()
                .map(choiceResponse -> choiceResponse.getVoteCount())
                .count();

        pollResponse.setTotalVotes(votesCount);

        return pollResponse;
    }




    public PollResponse castVoteAndGetUpdatedPoll(Long pollId, VoteRequest voteRequest, UserPrincipal currentUser) {
        User user = userRepository.findById(currentUser.getId()).orElseThrow(() -> new ResourceNotFoundException("User", "id", currentUser.getId()));

        Poll poll = pollRepository.findById(pollId).orElseThrow(() -> new ResourceNotFoundException("Poll", "id", pollId));

        Choice choice = poll.getChoices().stream()
                .filter(choiceInStream -> choiceInStream.getId().equals(voteRequest.getChoiceId()))
                .findAny().get();

        Vote vote = new Vote();
        vote.setChoice(choice);
        vote.setPoll(poll);
        vote.setUser(user);

        voteRepository.save(vote);


        PollResponse pollResponse = new PollResponse();
        pollResponse.setId(pollId);
        pollResponse.setQuestion(poll.getQuestion());



        List<ChoiceResponse> choiceResponses = poll.getChoices().stream()
                .map(choiceInStream -> {
                    ChoiceResponse choiceResponse = new ChoiceResponse();
                    choiceResponse.setId(choiceInStream.getId());
                    choiceResponse.setText(choiceInStream.getText());

                    long votesCount = voteRepository.countVotesByChoiceId(choiceInStream.getId());

                    choiceResponse.setVoteCount(votesCount);

                    return choiceResponse;
                })
                .collect(Collectors.toList());

        pollResponse.setChoices(choiceResponses);

        UserSummary userSummary = new UserSummary(user.getId(), user.getUsername(), user.getName());

        pollResponse.setCreatedBy(userSummary);

        pollResponse.setCreationDateTime(poll.getCreatedAt());

        pollResponse.setExpirationDateTime(poll.getExpirationDateTime());

        pollResponse.setIsExpired(poll.getExpirationDateTime().isBefore(Instant.now()));

        pollResponse.setSelectedChoice(voteRequest.getChoiceId());

        long votesCount  = pollResponse.getChoices().stream()
                .map(choiceResponse -> choiceResponse.getVoteCount())
                .count();


        pollResponse.setTotalVotes(votesCount);

        return pollResponse;
    }




    public void validatePageNumberAndSize(int page, int size) {
        if(page < 0) {
            throw new BadRequestException("Page number cannot be less than zero.");
        }

        if(size > AppConstants.MAX_PAGE_SIZE) {
            throw new BadRequestException("Page size must not be greater than " + AppConstants.MAX_PAGE_SIZE);
        }
    }



    // Retrieve Vote Counts of every Choice belonging to the given pollIds
    public Map<Long, Long> getChoiceVoteCountMap(List<Long> pollIds) {
        Map<Long, Long> choiceVotes = new HashMap<>();

        for (Long pollId : pollIds) {
            List<ChoiceVoteCount> choiceVoteCounts = voteRepository.countByPollIdGroupByChoiceId(pollId);

            choiceVoteCounts.stream().forEach(choiceVoteCount -> choiceVotes.put(choiceVoteCount.getChoiceId(), choiceVoteCount.getVoteCount()));
        }

        return choiceVotes;
    }



    // Retrieve Votes done by the logged in user to the given pollIds
    public Map<Long, Long> getPollUserVoteMap(UserPrincipal currentUser, List<Long> pollIds) {
        Long userId = currentUser.getId();

        List<Vote> votes = voteRepository.findVotesByUserIdAndByPollIdIn(userId, pollIds);

        Map<Long, Long> pollUserVoteMap = new HashMap<>();

        votes.stream().forEach(vote -> pollUserVoteMap.put(vote.getPoll().getId(), vote.getChoice().getId()));

        return pollUserVoteMap;
    }




    // Get Poll Creator details of the given list of polls
    public Map<Long, User> getPollCreatorMap(List<Poll> polls) {
        Map<Long, User> pollCreatorMap = new HashMap<>();

        polls.stream().forEach(poll -> pollCreatorMap.put(poll.getId(), poll.getUser()));

        return pollCreatorMap;
    }

}
