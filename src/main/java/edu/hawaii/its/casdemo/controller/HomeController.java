package edu.hawaii.its.casdemo.controller;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttribute;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import edu.hawaii.its.casdemo.access.User;
import edu.hawaii.its.casdemo.access.UserContextService;
import edu.hawaii.its.casdemo.model.Feedback;
import edu.hawaii.its.casdemo.model.Message;
import edu.hawaii.its.casdemo.service.EmailService;
import edu.hawaii.its.casdemo.service.MessageService;

@Controller
public class HomeController {

    private static final Logger logger = LoggerFactory.getLogger(HomeController.class);

    @Autowired
    private EmailService emailService;

    @Autowired
    private MessageService messageService;

    @Autowired
    private UserContextService userContextService;

    @GetMapping(value = { "/", "/home" })
    public String home(Model model) {
        logger.debug("User at home. ");

        int messageId = Message.JUMBOTRON_MESSAGE;
        Message message = messageService.findMessage(messageId);
        model.addAttribute("jumbotron", message.getText());

        return "home";
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping(value = "/login")
    public String login() {
        logger.debug("User at login.");
        return "redirect:/user";
    }

    @GetMapping(value = { "/holiday", "/holidays" })
    public String holiday() {
        logger.debug("User at holiday.");
        return "holiday";
    }

    @GetMapping(value = { "/holidaygrid", "/holidaysgrid" })
    public String holidaygrid() {
        logger.debug("User at holidaygrid.");
        return "holiday-grid";
    }

    @GetMapping(value = { "/campus", "/campuses" })
    public String campus() {
        logger.debug("User at campus.");
        return "campus";
    }

    @GetMapping(value = { "/help/contact", "/help/contacts" })
    public String contact() {
        logger.debug("User at contact.");
        return "help/contact";
    }

    @GetMapping(value = { "/help/faq", "/help/faqs" })
    public String faq() {
        logger.debug("User at faq.");
        return "help/faq";
    }

    @GetMapping(value = "/help/fonts")
    public String fonts() {
        logger.debug("User at fonts.");
        return "help/fonts";
    }

    @PreAuthorize("hasRole('USER')")
    @GetMapping("/user")
    public String adminUser(Model model) {
        logger.debug("User at /user.");

        User user = userContextService.getCurrentUser();
        model.addAttribute("user", user);

        return "user/user";
    }

    @GetMapping(value = "/404")
    public String invalid() {
        logger.debug("invalid; 404 redirect");
        return "redirect:/";
    }

    @GetMapping(value = "/error-login")
    public String errorLogin(Model model, HttpServletRequest request,
                             @SessionAttribute(value = "login.error.message", required = false) String errormsg,
                             @SessionAttribute(value = "login.error.exception.message", required = false) String exceptionmsg) {
        model.addAttribute("loginErrorMessage", errormsg);
        model.addAttribute("loginErrorExceptionMessage", exceptionmsg);
        request.getSession().setAttribute("login.error.message", null);
        request.getSession().setAttribute("login.error.exception.message", null);
        return "error-login";
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/user/data")
    public String userData() {
        logger.debug("User at user/data.");
        emailService.sendCasData(userContextService.getCurrentUser());
        return "redirect:/user";
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/feedback/{error}")
    public String feedbackError(RedirectAttributes redirectAttributes, @PathVariable String error) {
        Feedback feedback = new Feedback(new Exception(error));
        redirectAttributes.addFlashAttribute("feedback", feedback);
        return "redirect:/feedback";
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/feedback")
    public String feedbackForm(Model model, Feedback feedback) {
        model.addAttribute("feedback", feedback);
        return "feedback/form";
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/feedback")
    public String feedbackSubmit(@ModelAttribute Feedback feedback) {
        logger.debug("feedback: {}", feedback);
        User user = userContextService.getCurrentUser();
        emailService.sendFeedbackData(user, feedback);
        return "feedback/result";
    }

    @GetMapping("/actuator/")
    public String actuator() {
        return "redirect:/actuator/health";
    }

    public EmailService getEmailService() {
        return emailService;
    }

    public void setEmailService(EmailService emailService) {
        this.emailService = emailService;
    }

    @RequestMapping("/headers")
    @ResponseBody
    public Map<String, String> headers(HttpServletRequest request) {
        logger.info("User at /headers");

        Map<String, String> headers = new LinkedHashMap<>();

        // Raw headers
        Collections.list(request.getHeaderNames())
                .forEach(name -> headers.put("Header: " + name, request.getHeader(name)));

        // ServletRequest details
        headers.put("RequestURL", request.getRequestURL().toString());
        headers.put("RequestURI", request.getRequestURI());
        headers.put("Scheme", request.getScheme()); // http or https
        headers.put("ServerName", request.getServerName()); // resolved host
        headers.put("ServerPort", String.valueOf(request.getServerPort()));
        headers.put("RemoteAddr", request.getRemoteAddr());
        headers.put("ContextPath", request.getContextPath());

        // Forwarded headers (emphasized)
        headers.put("X-Forwarded-Proto", request.getHeader("X-Forwarded-Proto"));
        headers.put("X-Forwarded-Host", request.getHeader("X-Forwarded-Host"));
        headers.put("X-Forwarded-Port", request.getHeader("X-Forwarded-Port"));
        headers.put("X-Forwarded-For", request.getHeader("X-Forwarded-For"));
        headers.put("Forwarded", request.getHeader("Forwarded"));
        headers.put("X-Forwarded-Prefix", request.getHeader("X-Forwarded-Prefix"));

        return headers;
    }

}
