package com.reinke.polls.payload;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;

@Data
@NoArgsConstructor
public class PollResponse {

    private Long id;

    private String question;

    private List<ChoiceResponse> choices;

    private UserSummary createdBy;

    private Instant creationDateTime;

    private Instant expirationDateTime;

    private Boolean isExpired;

    private Long selectedChoice;

    private Long totalVotes;
}
