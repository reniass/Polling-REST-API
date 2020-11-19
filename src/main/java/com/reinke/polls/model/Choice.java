package com.reinke.polls.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Table(name = "choices")
@NoArgsConstructor
@Data
public class Choice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String text;

    @ManyToOne
//    @JoinColumn(name = "poll_id")
    private Poll poll;


}
