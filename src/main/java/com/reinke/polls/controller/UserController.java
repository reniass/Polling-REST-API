package com.reinke.polls.controller;

import com.reinke.polls.exception.ResourceNotFoundException;
import com.reinke.polls.model.User;
import com.reinke.polls.payload.*;
import com.reinke.polls.repository.PollRepository;
import com.reinke.polls.repository.UserRepository;
import com.reinke.polls.repository.VoteRepository;
import com.reinke.polls.security.UserPrincipal;
import com.reinke.polls.service.PollService;
import com.reinke.polls.util.AppConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private VoteRepository voteRepository;

    @Autowired
    private PollRepository pollRepository;

    @Autowired
    private PollService pollService;

    // get currently logged in user
    @GetMapping("/user/me")
    public UserSummary getLoggedInUser(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        return new UserSummary(userPrincipal.getId(), userPrincipal.getUsername(), userPrincipal.getName());
    }

    // check if a username is available for registration
    @GetMapping("/user/checkUsernameAvailability")
    public UserIdentityAvailability checkUsernameAvailability(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        UserIdentityAvailability userIdentityAvailability = new UserIdentityAvailability();

        boolean available = !userRepository.existsByUsername(userPrincipal.getUsername());

        if (available) {
            userIdentityAvailability.setAvailable(true);
        } else {
            userIdentityAvailability.setAvailable(false);
        }

        return userIdentityAvailability;
    }

    // check if an email is available for registration
    @GetMapping("/user/checkEmailAvailability")
    public UserIdentityAvailability checkEmailAvailability(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        UserIdentityAvailability userIdentityAvailability = new UserIdentityAvailability();

        boolean available = !userRepository.existsByEmail(userPrincipal.getEmail());

        if (available) {
            userIdentityAvailability.setAvailable(true);
        } else {
            userIdentityAvailability.setAvailable(false);
        }

        return userIdentityAvailability;
    }

    // get the public profile of a user
    @GetMapping("/users/{username}")
    public UserProfile getUserProfile(@PathVariable("username") String username) {
        User user = userRepository.findByUsername(username).orElseThrow(() -> new ResourceNotFoundException(username + " does not exist" ,"", ""));

        UserProfile userProfile = new UserProfile(user.getId(), user.getUsername(), user.getName(), user.getCreatedAt(), pollRepository.countByCreatedBy(user.getId()), voteRepository.countVotesByUserId(user.getId()));

        return userProfile;
    }

    // get a paginated list of polls created by a given user
    @GetMapping("/users/{username}/polls")
    public PagedResponse<PollResponse> getPollsCreatedBy(@PathVariable(value = "username") String username,
                                                         @AuthenticationPrincipal UserPrincipal userPrincipal,
                                                         @RequestParam(defaultValue = AppConstants.DEFAULT_PAGE_NUMBER) String page,
                                                         @RequestParam(defaultValue = AppConstants.DEFAULT_PAGE_SIZE) String size) {

        return getPollsCreatedBy(username, userPrincipal, page, size);

    }

    // get a paginated list of polls in which a given user has voted
    @GetMapping("/")
    public PagedResponse<PollResponse> getPollsVotedBy(@PathVariable(value = "username") String username,
                                                       @AuthenticationPrincipal UserPrincipal userPrincipal,
                                                       @RequestParam(defaultValue = AppConstants.DEFAULT_PAGE_NUMBER) String page,
                                                       @RequestParam(defaultValue = AppConstants.DEFAULT_PAGE_SIZE) String size) {

        return getPollsVotedBy(username, userPrincipal, page, size);

    }
}
