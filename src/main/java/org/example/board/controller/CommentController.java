package org.example.board.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.board.dto.CommentDto;
import org.example.board.service.CommentService;
import org.example.board.security.MemberPrincipal;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/comments")
@RequiredArgsConstructor
public class CommentController {
    private final CommentService commentService;

    @PostMapping("parentCommentId")
    public CommentDto.Response update(@PathVariable Long parentCommentId,
                                      @Valid @RequestBody CommentDto.CreateRequest request,
                                      @AuthenticationPrincipal MemberPrincipal principal) {
        return commentService.reComment(parentCommentId, request, principal);
    }

    @PutMapping("/{commentId}")
    public CommentDto.Response update(@PathVariable Long commentId,
                                      @Valid @RequestBody CommentDto.UpdateRequest request,
                                      @AuthenticationPrincipal MemberPrincipal principal) {
        return commentService.update(commentId, request, principal);
    }

    @DeleteMapping("/{commentId}")
    public ResponseEntity<Void> delete(@PathVariable Long commentId, @AuthenticationPrincipal MemberPrincipal principal) {
        commentService.delete(commentId, principal);
        return ResponseEntity.noContent().build();
    }
}
