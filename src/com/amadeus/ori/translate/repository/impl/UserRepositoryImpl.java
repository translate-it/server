package com.amadeus.ori.translate.repository.impl;

import com.amadeus.ori.translate.domain.User;
import com.amadeus.ori.translate.repository.UserRepository;

public class UserRepositoryImpl extends AbstractRepositoryImpl<User> implements UserRepository {

    static {
    	factory().register(User.class); 
    }
}
