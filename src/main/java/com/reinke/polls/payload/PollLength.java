package com.reinke.polls.payload;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class PollLength {

    private Integer days;

    private Integer hours;
}
