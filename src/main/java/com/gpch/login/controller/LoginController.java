package com.gpch.login.controller;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.gpch.login.model.User;
import com.gpch.login.service.UserService;

@Controller
public class LoginController {

    @Autowired
    private UserService userService;
    
    @Autowired
    private HttpSession session;

    @RequestMapping(value={"/", "/login"}, method = RequestMethod.GET)
    public ModelAndView login(User user , ModelAndView modelAndView, BindingResult result){
        	modelAndView.addObject("user", user);
        if (result.hasErrors()) {
            modelAndView.setViewName("login");
        }
        if(session.getAttribute("userName") != null) {
        	modelAndView.setViewName("redirect:/admin/home");
        	return   modelAndView;
        }
        modelAndView.setViewName("login");
        return modelAndView;
    }

    @RequestMapping(value={"/access-denied"}, method = RequestMethod.GET)
    public ModelAndView accessDenied(){
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("access-denied");
        return modelAndView;
    }

    @RequestMapping(value="/registration", method = RequestMethod.GET)
    public ModelAndView registration(){
        ModelAndView modelAndView = new ModelAndView();
        User user = new User();
        modelAndView.addObject("user", user);
        modelAndView.setViewName("registration");
        return modelAndView;
    }

    @RequestMapping(value = "/registration", method = RequestMethod.POST)
    public ModelAndView createNewUser(@Valid User user, BindingResult bindingResult) {
        ModelAndView modelAndView = new ModelAndView();
        User userExists = userService.findUserByEmail(user.getEmail());
        if (userExists != null) {
            bindingResult
                    .rejectValue("email", "error.user",
                            "Já existe um usuário com o email informado.");
        }
        if (bindingResult.hasErrors()) {
            modelAndView.setViewName("registration");
        } else {
            userService.saveUser(user);
            modelAndView.addObject("successMessage", "Usuário registrado com sucesso.");
            modelAndView.addObject("user", new User());
            modelAndView.setViewName("registration");

        }
        return modelAndView;
    }

    @RequestMapping(value="/admin/home", method = RequestMethod.GET)
    public ModelAndView home(){

        ModelAndView modelAndView = new ModelAndView();
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = userService.findUserByEmail(auth.getName());
 
      //Colocar dados na session
        session.setAttribute("userName", "Bem-Vindo " + user.getName() + " " + user.getLastName() + " (" + user.getEmail() + ")");
        session.setMaxInactiveInterval(15); //tempo de session ativa aciosa em milissegundos
        modelAndView.addObject("adminMessage","Conteúdo permitido apenas para administradores.");
        modelAndView.addObject("userMessage","Você não é um administrador do sistema.");
        modelAndView.setViewName("admin/home");
        return modelAndView;
    }


}
