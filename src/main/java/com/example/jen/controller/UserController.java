package com.example.jen.controller;

import com.example.jen.entity.User;
import com.example.jen.repository.UserRepository;
import com.example.jen.service.BoardService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/mypage")
@RequiredArgsConstructor
public class UserController {

    private final UserRepository userRepository;
    private final BoardService boardService;

    @GetMapping
    public String mypage(Model model, @AuthenticationPrincipal UserDetails userDetails) {
        User user = userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다."));

        model.addAttribute("user", user);
        model.addAttribute("myBoards", boardService.getListByUser(user.getUsername()));
        return "user/mypage";
    }
}
