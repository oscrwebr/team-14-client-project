package com.team14.clientProject.adminPage;

import com.team14.clientProject.loggingSystem.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@Controller
public class AdminController {

    @Autowired
    private SystemLogRepositoryImpl systemLogRepository;
    @Autowired
    private CommunicationLogRepositoryImpl communicationLogRepository;

    private final AdminService adminService;
    private List<CommunicationLog> communicationLogs;
    private List<SystemLog> systemLogs;
    private List<Object> combinedLogs;

    @Autowired
    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    @GetMapping("/admin")
    public ModelAndView getAdminPage() {
        ModelAndView modelAndView = new ModelAndView("admin/admin");
        List<User> users = adminService.getAllUsers();
        modelAndView.addObject("users", users);
        communicationLogs = communicationLogRepository.getLogs();
        systemLogs = systemLogRepository.getLogs();
        combinedLogs = new ArrayList<>();
        combinedLogs.addAll(communicationLogs);
        combinedLogs.addAll(systemLogs);
        combinedLogs.sort((o1, o2) -> {
            Timestamp t1 = o1 instanceof CommunicationLog ? Timestamp.valueOf(((CommunicationLog) o1).getTimestamp()) : Timestamp.valueOf(((SystemLog) o1).getTimestamp());
            Timestamp t2 = o2 instanceof CommunicationLog ? Timestamp.valueOf(((CommunicationLog) o2).getTimestamp()) : Timestamp.valueOf(((SystemLog) o2).getTimestamp());
            return t2.compareTo(t1);
        });
        modelAndView.addObject("logs", combinedLogs);


        return modelAndView;
    }

    @GetMapping("/admin/user/{id}")
    public ModelAndView getUserDetailsPage(@PathVariable("id") int id) {
        ModelAndView modelAndView = new ModelAndView("admin/userDetails");
        if (adminService.getUserById(id) == null) {
            modelAndView.setViewName("redirect:/admin");
            return modelAndView;
        }
        User user = adminService.getUserById(id);
        modelAndView.addObject("user", user);
        return modelAndView;
    }

    @GetMapping("/admin/add")
    public ModelAndView addNewUsers() {
        ModelAndView modelAndView = new ModelAndView("/admin/addNewUser");
        modelAndView.addObject("user", new User());
        this.systemLogRepository.addUserLog();
        return modelAndView;
    }

    @PostMapping("/admin/add")
    public ModelAndView addUser(@ModelAttribute User user) {
        ModelAndView modelAndView = new ModelAndView("/admin/addNewUser");
        try {
            adminService.addUser(user);
            modelAndView.addObject("error", "User added successfully!");
        } catch (Exception e) {
            modelAndView.addObject("error", e.getMessage());
        }
        return modelAndView;
    }



    @PostMapping("/admin/user/delete/{id}")
    public String deleteUser(@PathVariable("id") int id, RedirectAttributes redirectAttributes) {
        try {
            this.systemLogRepository.removeUserLog(id);
            adminService.deleteUser(id);
            redirectAttributes.addFlashAttribute("success", "User deleted successfully!");

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to delete user: " + e.getMessage());
        }
        return "redirect:/admin";
    }
}
