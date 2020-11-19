package com.reinke.polls.payload;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class VoteRequest {

    private Long choiceId;
}
