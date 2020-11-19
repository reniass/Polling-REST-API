package com.reinke.polls.payload;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ChoiceRequest {

    private String text;
}
