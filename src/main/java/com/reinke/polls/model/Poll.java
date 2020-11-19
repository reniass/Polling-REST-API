package com.reinke.polls.model;

import com.reinke.polls.model.audit.UserDateAudit;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;


@Entity
@Table(name = "polls")
@Data
@NoArgsConstructor
public class Poll extends UserDateAudit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String question;

    @OneToMany(mappedBy = "poll",
            cascade = CascadeType.ALL,
            fetch = FetchType.EAGER
    )
    private List<Choice> choices = new ArrayList<>();

    @ManyToOne
    private User user;

    private Instant expirationDateTime;
//
//    public void addChoice(Choice choice) {
//        choices.add(choice);
//        choice.setPoll(this);
//
//
//    }
}
