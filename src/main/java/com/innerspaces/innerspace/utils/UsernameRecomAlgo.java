package com.innerspaces.innerspace.utils;

import com.innerspaces.innerspace.repositories.user.UserRepository;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class UsernameRecomAlgo {
    private final UserRepository userRepo;

    public UsernameRecomAlgo(UserRepository userRepo) {
        this.userRepo = userRepo;
    }


    public List<String> usernameRecommendation(String username)
    {
        Set<String> usernames = new HashSet<>();
        usernames.add(userRepo.findByUsernameLike(username).orElse("NO RECOMMENDED USERNAME"));
        List<String> suggestionList = new ArrayList<>();
        while(suggestionList.size() !=4)
        {
            long randomNum = (long) Math.floor(Math.random() * 1_00);
            String suggestion;
            suggestion =  username + randomNum;
            if(!username.contains(suggestion) && !suggestionList.contains(suggestion)){
                suggestionList.add(suggestion);
            }
        }

        return suggestionList;
    }
}
