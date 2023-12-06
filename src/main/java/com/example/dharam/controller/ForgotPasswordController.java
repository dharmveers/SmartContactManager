package com.example.dharam.controller;

import java.security.Principal;
import java.util.Random;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.dharam.dao.UserRepo;
import com.example.dharam.model.User;
import com.example.dharam.services.EmailService;

@Controller
public class ForgotPasswordController {
	
	@Autowired
	private UserRepo userRepo;
	
	@Autowired
	private EmailService emailService;
	
	@Autowired
	private BCryptPasswordEncoder passwordEncoder;
	
	Random rand = new Random(10000);
	
	@RequestMapping("/forgot")	
	public String forgotPassword() {
	
		return "forgot-Password";
	}
	
	@PostMapping("/send_Email_OTP")
	public String sendOTP(@RequestParam("email") String email , HttpSession session) {
		 
		int otp = rand.nextInt(99999);
		System.out.println("OTP :"+otp);
		//code for send otp to email
		String subject="OTP from SmartContactManager";
		String message=""
						+"<div>"
						+"<h1>"
						+"OTP is : "
						+otp
						+"</h1>"
						+"</div>";
	
		String to = email;
		boolean flag = this.emailService.sendEmail(subject, message, to);
		if(flag) {
			session.setAttribute("myotp", otp);
			session.setAttribute("email", email);
			return "send-OTP";
		}else {
			session.setAttribute("message", "Please check your email id again....");
			return "forgot-Password";
		}
		
	}
	//handler for otp verification
	@PostMapping("/verifyOTP")
	public String verifyOTP(@RequestParam("otp") int otp, HttpSession session) {
		int myOTP=(int) session.getAttribute("myotp");
		String email=(String) session.getAttribute("email");
		if(myOTP==otp) {
			User user=this.userRepo.getUserByUserName(email);
			if(user.equals(null)) {
				session.setAttribute("message", "User does not exist!!");
				return "send-OTP";
			}else {
			return "password-change";
		  }
		}else {
			session.setAttribute("message", "You are entered wrong OTP");
			return "send-OTP";
		}
	
	}
	//password changing handler
	@PostMapping("/changedPassword")
	public String changingPassword(@RequestParam("changedpass") String changedpass , HttpSession session) {
		User user=this.userRepo.getUserByUserName((String)session.getAttribute("email"));
		System.out.println(user);
		user.setPass(passwordEncoder.encode(changedpass));
		this.userRepo.save(user);
		session.setAttribute("message", "Your Password is changed successfully");
		return "login";
	}
	
}
