package com.example.jen.controller;

import com.example.jen.dto.BoardDto;
import com.example.jen.service.BoardService;
import com.example.jen.service.CommentService;
import com.example.jen.dto.CommentDto;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.util.List;

@Controller
@RequestMapping("/board")
@RequiredArgsConstructor
public class BoardController {

    private final BoardService boardService;
    private final CommentService commentService;

    @GetMapping("/list")
    public String list(Model model) {
        model.addAttribute("boards", boardService.getList());
        return "board/list";
    }

    @GetMapping("/detail/{id}")
    public String detail(@PathVariable("id") Long id, Model model, @AuthenticationPrincipal UserDetails userDetails,
            HttpServletRequest request, HttpServletResponse response) {

        // 조회수 중복 방지 (쿠키 확인)
        Cookie[] cookies = request.getCookies();
        boolean isViewed = false;
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals("boardView") && cookie.getValue().contains("[" + id + "]")) {
                    isViewed = true;
                    break;
                }
            }
        }

        if (!isViewed) {
            boardService.increaseViewCount(id);
            Cookie viewCookie = new Cookie("boardView", "[");
            if (cookies != null) {
                for (Cookie cookie : cookies) {
                    if (cookie.getName().equals("boardView")) {
                        viewCookie.setValue(cookie.getValue() + "[" + id + "]");
                        break;
                    }
                }
            } else {
                viewCookie.setValue("[" + id + "]");
            }
            viewCookie.setPath("/");
            viewCookie.setMaxAge(60 * 60 * 24); // 1일
            response.addCookie(viewCookie);
        }

        BoardDto dto = boardService.getDetail(id);
        List<CommentDto> comments = commentService.getList(id);

        model.addAttribute("board", dto);
        model.addAttribute("comments", comments);

        if (userDetails != null) {
            model.addAttribute("currentUser", userDetails.getUsername());
        } else {
            model.addAttribute("currentUser", "");
        }
        return "board/detail";
    }

    // 좋아요 토글
    @PostMapping("/detail/{id}/like")
    public String toggleLike(@PathVariable("id") Long id, @AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails != null) {
            boardService.toggleLike(id, userDetails.getUsername());
        }
        return "redirect:/board/detail/" + id;
    }

    // 댓글 관련 Endpoint 분리
    @PostMapping("/detail/{id}/comment")
    public String writeComment(@PathVariable("id") Long id, @RequestParam("content") String content,
            @AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails != null) {
            commentService.create(id, content, userDetails.getUsername());
        }
        return "redirect:/board/detail/" + id;
    }

    @PostMapping("/detail/{boardId}/comment/{commentId}/delete")
    public String deleteComment(@PathVariable("boardId") Long boardId, @PathVariable("commentId") Long commentId,
            @AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails != null) {
            commentService.delete(commentId, userDetails.getUsername());
        }
        return "redirect:/board/detail/" + boardId;
    }

    @GetMapping("/write")
    public String writeForm() {
        return "board/write";
    }

    @PostMapping("/write")
    public String write(BoardDto dto, @AuthenticationPrincipal UserDetails userDetails) {
        boardService.create(dto, userDetails.getUsername());
        return "redirect:/board/list";
    }

    @GetMapping("/edit/{id}")
    public String editForm(@PathVariable("id") Long id, Model model, @AuthenticationPrincipal UserDetails userDetails) {
        BoardDto dto = boardService.getDetail(id);
        if (!dto.getWriter().equals(userDetails.getUsername())) {
            return "redirect:/board/detail/" + id;
        }
        model.addAttribute("board", dto);
        return "board/edit";
    }

    @PostMapping("/edit/{id}")
    public String edit(@PathVariable("id") Long id, BoardDto dto, @AuthenticationPrincipal UserDetails userDetails) {
        boardService.update(id, dto, userDetails.getUsername());
        return "redirect:/board/detail/" + id;
    }

    @PostMapping("/delete/{id}")
    public String delete(@PathVariable("id") Long id, @AuthenticationPrincipal UserDetails userDetails) {
        boardService.delete(id, userDetails.getUsername());
        return "redirect:/board/list";
    }
}
