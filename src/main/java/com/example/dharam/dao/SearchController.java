package com.example.dharam.dao;

import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.example.dharam.model.Contact;
import com.example.dharam.model.User;

@RestController
public class SearchController {
	@Autowired
	private UserRepo userRepo;
	
	@Autowired
	private ContactRepository contactRepository;
	
	@GetMapping("/search/{query}")
	public ResponseEntity<?> search(@PathVariable("query") String query, Principal principal){
		
		System.out.println(query);
	    User user = this.userRepo.getUserByUserName(principal.getName());
		List<Contact> contacts=this.contactRepository.findByKeywordsContaining(query,user);
		
		return ResponseEntity.ok(contacts);
	}
	
}
