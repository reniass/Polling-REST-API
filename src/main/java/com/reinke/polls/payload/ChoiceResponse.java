package com.reinke.polls.payload;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ChoiceResponse {

    private Long id;
    private String text;
    private Long voteCount;

}
