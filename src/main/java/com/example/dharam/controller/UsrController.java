package com.example.dharam.controller;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.dharam.dao.ContactRepository;
import com.example.dharam.dao.UserRepo;
import com.example.dharam.helper.Message;
import com.example.dharam.model.Contact;
import com.example.dharam.model.User;
import com.example.dharam.model.dateAndTime;
import com.fasterxml.jackson.annotation.JsonCreator.Mode;


@Controller
@RequestMapping("/user")
public class UsrController {
	@Autowired
	private UserRepo repo;
	
	@Autowired
	private ContactRepository contactRepository;
	
	@Autowired
	private BCryptPasswordEncoder passwordEncoder;
	
	private String profilepic;
	private int page;
	
	@ModelAttribute
	protected void addCommonData(Model model,Principal principal) {
		String username=principal.getName();
		User user= repo.getUserByUserName(username);
		model.addAttribute("user", user);
	}
	
	
	@RequestMapping("/index")
	public String dashBoard(Model model ,@ModelAttribute("dat") dateAndTime dat) {
		DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
		Calendar cal = Calendar.getInstance();
		String temp=dateFormat.format(cal.getTime());
		dat.setDate(temp.substring(0,10));
		dat.setTime(temp.substring(11, 19));
		
		model.addAttribute("title","User-Dashboard");
		model.addAttribute("dat", dat);
		return "normal/user_dashboard";
	}
	//add contact handler
	@GetMapping("/add_contactDetails")
	public String addContact(Model model) {
		model.addAttribute("title","Add-Contact");
		model.addAttribute("contact", new Contact());
		
		return "normal/add_contact";
	}
	
	@RequestMapping(value="/process-contact",method = RequestMethod.POST)
	public String processContact(@Valid @ModelAttribute("contact") Contact contact, BindingResult Result, @RequestParam("image") MultipartFile file, Principal principal, Model model, HttpSession session) {
	    try {
	    	String name = principal.getName();
		    User user = repo.getUserByUserName(name);
		    String about = contact.getAbout().trim();
		    contact.setAbout(about); //trim large number of spaces and set value to contact
		    if(Result.hasErrors()) {
		    	model.addAttribute("contact", contact);
		    	System.out.println(about);
		    	System.out.println("profileImg "+contact.getProfileImg());
		    	session.setAttribute("message", new Message("Some input fields are not valid", "alert-danger"));
		    	return "normal/add_contact";
		    }
		    
		    //processing and upload file
		    if(file.isEmpty()) {
		    	//show the custom message
		    	contact.setProfileImg("default.jpg");
		    	System.out.println("no image file is choosen");
		    }else {
		    	//upload the file
				
				contact.setProfileImg(file.getOriginalFilename());
		    	File saveFile = new ClassPathResource("static/img").getFile();
		    	System.out.println(saveFile.getAbsolutePath()+File.separator+file.getOriginalFilename());
		    	Path path = Paths.get(saveFile.getAbsolutePath()+File.separator+file.getOriginalFilename());
		    	Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
		    	//System.out.println("image is uploaded successfully..");
		    }
		    contact.setUser(user);
		    user.getContacts().add(contact);
		    this.repo.save(user);
		    System.out.println("fileName :"+contact.getProfileImg());
		    session.setAttribute("message", new Message("Contact saved successfully", "alert-success"));
		    model.addAttribute("contact", new Contact());
			System.out.println(contact);
	    }catch (Exception e) {
			e.getStackTrace();
			session.setAttribute("message", new Message(e.getStackTrace().toString(), "alert-warning"));
		}
		
		return "normal/add_contact";
	}
	
	//handler for showContact
	//per page=5[n]
	//current page=0[page]

	@GetMapping("/show-Contacts/{page}")
	public String showContacts(@PathVariable("page") int page,Model model,Principal principal) {
		this.page=page;
		String username = principal.getName();
		User user = this.repo.getUserByUserName(username);
		Pageable pageable = PageRequest.of(page, 5);
 		Page<Contact> contacts = this.contactRepository.findContactsByUser(user.getId(),pageable);
 	    //List<Contact> contacts = this.contactRepository.findContactsByUser(user.getId()); get contact details without pagination 
		model.addAttribute("title", "Show User Contacts");
		model.addAttribute("contacts", contacts);
		model.addAttribute("currPage", page);
		model.addAttribute("totalPages", contacts.getTotalPages());							
		System.out.println(contacts);
		return "normal/showContacts";
	}
	
	//handler for showing specific contact details
	 
	@RequestMapping("/{cid}/contact")
	public String showAllDetails(@PathVariable("cid") int cid, Model model, Principal principal) {
		 	
		 Optional<Contact> optional= contactRepository.findById(cid);
		 Contact contact = optional.get();
		 //security bug user can see only there contacts details
		 String username = principal.getName();
		 User user = this.repo.getUserByUserName(username);
		 if(user.getId()==contact.getUser().getId()) {
			 this.profilepic=contact.getProfileImg();
			 model.addAttribute("contact", contact);
			 model.addAttribute("currPage", page);
			 String fullName=contact.getfName()+" "+contact.getLastName();
			 model.addAttribute("title",fullName);
		 }
		//System.out.println("CID :"+cid);
		return "normal/all_Details";
	}
	//handler for delete contact details
	@GetMapping("/delete/{cid}")
	public String deleteContact(@PathVariable("cid") int cid, Model model, Principal principal ,HttpSession session) {
		//System.out.println("currentPage="+page);
		String username=principal.getName();
		User user = this.repo.getUserByUserName(username);
		Contact contact = contactRepository.findById(cid).get();
		try {
		if(user.getId()==contact.getUser().getId()) {
			
			    this.contactRepository.deleteById(cid);
				File saveFile = new ClassPathResource("static/img").getFile();
				Path path = Paths.get(saveFile.getAbsolutePath()+File.separator+contact.getProfileImg());
				System.out.println("path"+path);
				if(!(contact.getProfileImg().equals("default.jpg"))) {
					Files.delete(path);
				}
			    model.addAttribute("contact",contact);
			    session.setAttribute("message", new Message("Contact are deleted successfully", "alert-success"));
		}else {
			session.setAttribute("message", new Message("You don't have permission to delete this contact", "alert-danger"));
		}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return "redirect:/user/show-Contacts/0";
	}
	
	//handler for open update contact form
	@PostMapping("/update-contact/{cid}")
	public String openUpdateContact(@PathVariable("cid") int cid,  Model model){
		Contact contact = contactRepository.findById(cid).get();
		model.addAttribute("contact",contact);
		System.out.println("updateContact....");
		System.out.println("image ="+contact.getProfileImg());
		return "normal/update-contact";
	}
	//handler for update contact
	@PostMapping("/update-contact")
	public String update(@ModelAttribute Contact contact, Model model ,Principal principal) {
		contact.setProfileImg(profilepic);
		User user = this.repo.getUserByUserName(principal.getName());
		contact.setUser(user);
		this.contactRepository.save(contact);
		model.addAttribute("contact", new Contact());
		return "redirect:/user/show-Contacts/0";
	}
	
	//handler for myProfile
	@GetMapping("/profile-page")
	public String profile(Model model, Principal principal) {
		    User user = this.repo.getUserByUserName(principal.getName());
		    
			model.addAttribute("title", "My Profile");
			model.addAttribute("user", user);
		return "normal/myProfile";
	}
	
	//handler for settings
	@GetMapping("/settings")
	public String openSettings() {
		
		return "normal/change_pass";
	}
	
	//handler for password change
	@PostMapping("/changePassword")
	public String pwsd(@RequestParam("oldPassword") String oldPassword,@RequestParam("newPassword") String newPassword ,Principal principal ,HttpSession session) {
			
			try {
				User user= this.repo.getUserByUserName(principal.getName());
				if(this.passwordEncoder.matches(oldPassword, user.getPass())) {
					user.setPass(this.passwordEncoder.encode(newPassword));
					this.repo.save(user);
					session.setAttribute("message", new Message("Password changed Successfully", "alert-success"));
				}else {
					session.setAttribute("message", new Message("old Password and current password must be same", "alert-danger"));
				}
			}catch (Exception e) {
				e.getStackTrace();
			}
		return "normal/change_pass";
	}
}
