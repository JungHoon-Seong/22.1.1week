package com.company.member.controller;

import java.io.PrintWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.company.member.model.vo.Member;
import com.company.member.service.MemberService;


@Controller
public class MemberController {

	@Autowired 
	private MemberService memberSerivce;
	
	private static final Logger logger = LoggerFactory.getLogger(MemberController.class);
	
	@GetMapping("login")
	public ModelAndView login(ModelAndView mv) {
		mv.setViewName("/login/login");
		logger.info("로그인 페이지에 온걸 환영하고 logger");
		return mv;
	}
	@PostMapping("login")
	public String login(ModelAndView mv,
			HttpSession session, 
			HttpServletRequest request,
			@RequestParam(value ="userId")String id,
			@RequestParam(value = "userPwd")String pwd,
			HttpServletResponse response) {
		
		
		Member vo = new Member(id, pwd);
		
				
		try {
			Member loginResult = memberSerivce.login(vo);
			PrintWriter out = response.getWriter();
			response.setContentType("text/html; charset=UTF-8");
			if(loginResult != null) {
				
				session.setAttribute("member", loginResult);
				//session.setAttribute("member", id);
				
				//out.println("<script>alert('로그인에 성공하였습니다.'); </script>");
				logger.info("로그인 성공");
				//mv.addObject("msg", "로그인에 성공하였습니다");
				//mv.setViewName("redirect:/board");
			}else{
				
				out.println("<script>alert('로그인에 실패하였습니다. 아이디와 비밀번호를 확인해주세요');   </script>");
				logger.info("로그인 실패");
				//mv.addObject("msg", "로그인에 실패하였습니다 아이디와 비밀번호를 확인해주세요.");
				//mv.setViewName("redirect:/login");
				return "/login/login";
				
			}
			
			out.flush();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
		
		
		//return mv;
		return "home";
	}
	/*
	@RequestMapping("logout")
	public ModelAndView logout(ModelAndView mv, HttpSession session) {
		String viewName = "";
		session.invalidate();
		viewName ="index";
		mv.setViewName(viewName);
		return mv;
	} */
	@RequestMapping(value="logout", method=RequestMethod.GET)
	public ModelAndView logout(ModelAndView mv, HttpSession session) {
		String viewName = "";
		session.invalidate();
		viewName ="home";
		mv.setViewName(viewName);
		return mv;
	}
	
	@RequestMapping(value="userjoin", method = RequestMethod.GET)
	public ModelAndView userEnrollment(ModelAndView mv)  {
		
		mv.setViewName("/enrollment/enrollment");
		return mv;
	}
	
	@RequestMapping(value="userjoin", method = RequestMethod.POST)
	public ModelAndView userEnrollment(ModelAndView mv, 
			@RequestParam(value="id") String id,
			@RequestParam(value="password") String password,
			@RequestParam(value="email") String email,
			@RequestParam(value="tel") String tel,
			@RequestParam(value="achk") char achk,
			HttpServletRequest request,
			HttpServletResponse response) {
		
		Member vo = new Member(id,password,email, tel, achk);
		
		try {
			int result = 0;
			result = memberSerivce.userjoin(vo);
			
			if(result == 1) {
				
				response.setContentType("text/html; charset=UTF-8");
				 
				PrintWriter out = response.getWriter();
				 
				out.println("<script>alert('회원가입에 성공하였습니다.'); location.href='./login';</script>");
				logger.info("회원가입 성공");
			}else if(result == 0) {
				logger.warn("회원가입 실패");
			}else {
				logger.warn("회원 가입중 알 수 없는 결과 발생");
			}
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		mv.setViewName("redirect:/login");
		return mv;
	}
}
