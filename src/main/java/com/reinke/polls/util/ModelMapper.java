package com.reinke.polls.util;

import com.reinke.polls.model.Poll;
import com.reinke.polls.model.User;
import com.reinke.polls.payload.ChoiceResponse;
import com.reinke.polls.payload.PollResponse;
import com.reinke.polls.payload.UserSummary;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ModelMapper {


    // PollResponse mapPollToPollResponse(Poll poll, Map<Long, Long> choiceVotesMap, User creator, Long userVote)
    public static PollResponse mapPollToPollResponse(Poll poll, Map<Long, Long> choiceVotesMap, User creator, Long userVote) {

        PollResponse pollResponse = new PollResponse();
        pollResponse.setId(poll.getId());
        pollResponse.setQuestion(poll.getQuestion());

        List<ChoiceResponse> choiceResponses = poll.getChoices()
                .stream().map((choice -> {
                    ChoiceResponse choiceResponse = new ChoiceResponse();
                    choiceResponse.setText(choice.getText());
                    choiceResponse.setVoteCount(choiceVotesMap.get(choice.getId()));
                    return choiceResponse;
                }))
                .collect(Collectors.toList());

        pollResponse.setChoices(choiceResponses);

        UserSummary userSummary = new UserSummary(creator.getId(), creator.getUsername(), creator.getName());
        pollResponse.setCreatedBy(userSummary);

        pollResponse.setCreationDateTime(poll.getCreatedAt());
        pollResponse.setExpirationDateTime(poll.getExpirationDateTime());


        boolean isExpired = poll.getExpirationDateTime().isBefore(Instant.now());
        pollResponse.setIsExpired(Boolean.valueOf(isExpired));


        if (userVote != null) {
            pollResponse.setSelectedChoice(userVote);
        }


        long totalVotes = pollResponse.getChoices().stream().mapToLong(choiceResponse -> choiceResponse.getVoteCount()).sum();
        pollResponse.setTotalVotes(Long.valueOf(totalVotes));

        return pollResponse;
    }
}
