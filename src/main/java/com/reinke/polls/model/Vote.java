package com.reinke.polls.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Table(
        uniqueConstraints = {
        @UniqueConstraint(columnNames = "user_id"),
        @UniqueConstraint(columnNames = "poll_id"),

})
@Data
@NoArgsConstructor
public class Vote {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "choice_id")
    private Choice choice;


    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;


    @ManyToOne
    @JoinColumn(name = "poll_id")
    private Poll poll;
}
