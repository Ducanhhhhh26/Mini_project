package org.example.btnl_2504.controller;

import jakarta.validation.Valid;
import org.example.btnl_2504.model.entity.Todo;
import org.example.btnl_2504.repository.TodoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpSession;

@Controller
public class TodoController {
    @Autowired
    private TodoRepository todoRepository;

    @Autowired
    private MessageSource messageSource;

    @GetMapping("/")
    public String redirectToList() {
        return "redirect:/list";
    }

    @GetMapping("/login")
    public String showLogin(HttpSession session) {
        if (session.getAttribute("username") != null) {
            return "redirect:/list";
        }
        return "login";
    }

    @PostMapping("/login")
    public String processLogin(@RequestParam("username") String username, HttpSession session) {
        session.setAttribute("username", username);
        return "redirect:/list";
    }

    @GetMapping("/list")
    public String listTodo(Model model, HttpSession session) {
        String username = (String) session.getAttribute("username");
        if (username == null) {
            return "redirect:/login";
        }
        model.addAttribute("ownerName", username);
        model.addAttribute("todos", todoRepository.findAll());
        return "list";
    }

    @GetMapping("/form")
    public String showForm(Model model, HttpSession session) {
        if (session.getAttribute("username") == null) {
            return "redirect:/login";
        }
        model.addAttribute("todo", new Todo());
        return "form";
    }

    @GetMapping("/edit/{id}")
    public String editForm(@PathVariable("id") Long id, Model model, HttpSession session) {
        if (session.getAttribute("username") == null) {
            return "redirect:/login";
        }
        Todo todo = todoRepository.findById(id).orElse(new Todo());
        model.addAttribute("todo", todo);
        return "form";
    }

    @GetMapping("/delete/{id}")
    public String deleteTodo(@PathVariable("id") Long id, RedirectAttributes redirectAttributes, HttpSession session) {
        if (session.getAttribute("username") == null) {
            return "redirect:/login";
        }
        todoRepository.deleteById(id);
        String message = messageSource.getMessage("flash.delete.success", null, LocaleContextHolder.getLocale());
        redirectAttributes.addFlashAttribute("message", message);
        return "redirect:/list";
    }

    @PostMapping("/form")
    public String addTodo(@Valid @ModelAttribute("todo") Todo todo,
                          BindingResult bindingResult, HttpSession session) {
        if (session.getAttribute("username") == null) {
            return "redirect:/login";
        }
        if (bindingResult.hasErrors()) {
            return "form";
        }
        todoRepository.save(todo);
        return "redirect:/list";
    }
}
