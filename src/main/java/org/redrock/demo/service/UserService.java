package org.redrock.demo.service;

import org.redrock.demo.entity.User;

public interface UserService {

    boolean insertUser(User user);
    User checkLogin(String username,String password);
//    boolean checkFriend(String fromUser,String toUser);
}
