package com.example.dharam.dao;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.dharam.model.Contact;
import com.example.dharam.model.User;


public interface ContactRepository extends JpaRepository<Contact, Integer>{
	@Query("from Contact as c where c.user.id=:userid")
	public Page<Contact> findContactsByUser(@Param("userid") int userid,Pageable pageable);
	
	//for delete contact
	@Modifying
	@Query(value = "DELETE FROM Contact as c WHERE c.cid = :id")
	public void deleteById(@Param("id") int id);
	//Select c from Registration c where c.place like %:place%
	//for search contact by having name..
	@Query(value = "FROM Contact c WHERE c.fName LIKE %:keywords% AND c.user=:user")
	public List<Contact> findByKeywordsContaining(@Param("keywords") String keywords,User user);
	
	/*public List<Contact> findContactsByUser(@Param("userid") int userid); get all contacts details without pagination*/
}
