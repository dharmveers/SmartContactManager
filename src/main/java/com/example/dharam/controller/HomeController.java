package com.example.dharam.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import com.example.dharam.dao.UserRepo;
import com.example.dharam.helper.Message;
import com.example.dharam.model.User;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;
@Controller
public class HomeController {
	@Autowired
	private UserRepo userRepo;
	
	@Autowired
	private BCryptPasswordEncoder passwordEncoder;
	 
	@RequestMapping("/home")
	public String home(Model model) {
		    model.addAttribute("title", "Home :Smart Contact Manager");
		return "home";
	}
	@RequestMapping("/user_dashboard")
	public String dashboard(Model model) {
		    model.addAttribute("title", "Home :User-Dashboard");
		return "user_dashboard";
	}
	 
	@RequestMapping("/about")
	public String about(Model model) {
		    model.addAttribute("title", "About :Smart Contact Manager");
		return "about";
	}
	@RequestMapping("/signup")
	public String signup(Model model) {
		    model.addAttribute("title", "Signup :Smart Contact Manager");
		    model.addAttribute("user", new User());
		return "signup";
	}
	
	@RequestMapping("/login")
	public String customLogin(Model model) {
		    model.addAttribute("title", "Login :Smart Contact Manager");
		return "login";
	}
	/* handler for register user */
	
	@RequestMapping(value="/do_register", method = RequestMethod.POST)
	public String registerUser(@Valid @ModelAttribute("user") User user,BindingResult result,@RequestParam(value="agreement",defaultValue = "false") boolean agreement, Model model,String repass,HttpSession session) {
		
		try {
			 if(result.hasErrors()) {
				 model.addAttribute("user", user);
					return "signup";
				
			}else {
				if(!agreement) {
					model.addAttribute("user",user);
					session.setAttribute("message",new Message("You are not accepted terms and conditions", "alert-warning"));
					return "signup";
				}
				else if(!user.getPass().equals(repass)) {
					model.addAttribute("user",user);
					session.setAttribute("message", new Message("Password and Retype Password must be same","alert-danger"));
					return "signup";
				}
				user.setRole("USER");
				user.setStat(true);
				user.setPass(passwordEncoder.encode(user.getPass())); 
				userRepo.save(user);
				model.addAttribute(new User());
				session.setAttribute("message", new Message("Registered Successfully !!", "alert-success"));
				return "signup";
			}
			
		}catch (Exception e) {
			e.printStackTrace();
			model.addAttribute("user",user);
			session.setAttribute("message", new Message(e.getMessage(), "alert-error"));
			System.out.println(repass);
			return "signup";
		}
	}
}
