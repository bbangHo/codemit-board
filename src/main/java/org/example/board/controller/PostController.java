package org.example.board.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.board.dto.CommentDto;
import org.example.board.dto.PostDto;
import org.example.board.service.CommentService;
import org.example.board.service.PostService;
import org.example.board.security.MemberPrincipal;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
public class PostController {
    private final PostService postService;
    private final CommentService commentService;

    @PostMapping
    public ResponseEntity<PostDto.Response> create(@Valid @RequestBody PostDto.CreateRequest request,
                                                    @AuthenticationPrincipal MemberPrincipal principal) {
        return ResponseEntity.status(HttpStatus.CREATED).body(postService.create(request, principal));
    }

    @GetMapping
    public Page<PostDto.Response> findAll(@PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC)
                                           Pageable pageable) {
        return postService.findAll(pageable);
    }

    @GetMapping("/{postId}")
    public PostDto.Response find(@PathVariable Long postId) {
        return postService.find(postId);
    }

    @PutMapping("/{postId}")
    public PostDto.Response update(@PathVariable Long postId, @Valid @RequestBody PostDto.UpdateRequest request,
                                   @AuthenticationPrincipal MemberPrincipal principal) {
        return postService.update(postId, request, principal);
    }

    @DeleteMapping("/{postId}")
    public ResponseEntity<Void> delete(@PathVariable Long postId, @AuthenticationPrincipal MemberPrincipal principal) {
        postService.delete(postId, principal);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{postId}/comments")
    public ResponseEntity<CommentDto.Response> createComment(@PathVariable Long postId,
                                                             @Valid @RequestBody CommentDto.CreateRequest request,
                                                             @AuthenticationPrincipal MemberPrincipal principal) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(commentService.create(postId, request, principal));
    }

    @GetMapping("/{postId}/comments")
    public List<CommentDto.Response> findComments(@PathVariable Long postId) {
        return commentService.findAll(postId);
    }
}
