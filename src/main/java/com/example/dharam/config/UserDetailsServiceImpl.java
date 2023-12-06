package com.example.dharam.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.example.dharam.dao.UserRepo;
import com.example.dharam.model.User;

public class UserDetailsServiceImpl implements UserDetailsService{

	@Autowired
	private UserRepo repo; 
	
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		User user=repo.getUserByUserName(username);
		if(user==null) {
			throw new UsernameNotFoundException("User name not found !!");
		}
		
		return new UserDetailsImpl(user);
	}

}
