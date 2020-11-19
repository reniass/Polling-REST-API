package com.reinke.polls.payload;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class PollRequest {

    private String question;

    private List<ChoiceRequest> choices;

    private PollLength pollLength;

}
