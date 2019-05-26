
package org.redrock.demo.service.Impl;

import org.redrock.demo.entity.User;
import org.redrock.demo.mapper.MainMapper;
import org.redrock.demo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private MainMapper mainMapper;

    @Override
    public boolean insertUser(User user) {
        if (mainMapper.checkUser(user.getUsername())==0) {
            mainMapper.insertUser(user);
            return true;
        }
        return false;
    }

    @Override
    public User checkLogin(String username, String password) {
        return mainMapper.checkLogin(username,password);
    }

//    @Override
//    public boolean checkFriend(String fromUser, String toUser) {
//        if (mainMapper.checkFriend(fromUser,toUser)!=0) {
//            return true;
//        }
//        return false;
//    }
}