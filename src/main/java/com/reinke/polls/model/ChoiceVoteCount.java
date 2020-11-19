package com.reinke.polls.model;

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class ChoiceVoteCount {

    private Long choiceId;
    private Long voteCount;
}
