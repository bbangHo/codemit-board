package org.example.board.service;

import lombok.RequiredArgsConstructor;
import org.example.board.domain.Comment;
import org.example.board.domain.Member;
import org.example.board.domain.Post;
import org.example.board.dto.CommentDto;
import org.example.board.exception.ForbiddenException;
import org.example.board.exception.NotFoundException;
import org.example.board.repository.CommentRepository;
import org.example.board.repository.PostRepository;
import org.example.board.security.MemberPrincipal;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentService {
    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final MemberService memberService;

    @Transactional
    public CommentDto.Response create(Long postId, CommentDto.CreateRequest request, MemberPrincipal principal) {
        Post post = findPost(postId);
        Member member = memberService.find(principal.getId());

        Comment comment = Comment.builder()
                .content(request.content())
                .post(post)
                .author(member)
                .build();

        return CommentDto.Response.from(commentRepository.save(comment));
    }

    @Transactional
    public CommentDto.Response reComment(Long postId, CommentDto.CreateRequest request, MemberPrincipal principal) {
        Post post = findPost(postId);
        Comment parentComment = findComment(request.parentCommentId());
        Member member = memberService.find(principal.getId());

        Comment comment = Comment.builder()
                .content(request.content())
                .post(post)
                .author(member)
                .build();

        parentComment.reComment(comment);
        return CommentDto.Response.from(parentComment);
    }

    @Transactional(readOnly = true)
    public List<CommentDto.Response> findAll(Long postId) {
        findPost(postId);

        return commentRepository.findAllByPostId(postId, Sort.by(Sort.Direction.ASC, "createdAt")).stream()
                .map(CommentDto.Response::from).toList();
    }

    @Transactional
    public CommentDto.Response update(Long id, CommentDto.UpdateRequest request, MemberPrincipal principal) {
        Comment comment = findComment(id);
        checkOwner(comment, principal.getId());
        comment.update(request.content());
        return CommentDto.Response.from(comment);
    }

    @Transactional
    public void delete(Long id, MemberPrincipal principal) {
        Comment comment = findComment(id);
        checkOwner(comment, principal.getId());
        commentRepository.delete(comment);
    }

    private Post findPost(Long id) {
        return postRepository.findById(id).orElseThrow(() ->
                new NotFoundException("게시글을 찾을 수 없습니다."));
    }

    private Comment findComment(Long id) {
        return commentRepository.findById(id).orElseThrow(() ->
                new NotFoundException("댓글을 찾을 수 없습니다."));
    }

    private void checkOwner(Comment comment, Long authorId) {
        if (!comment.getAuthor().getId().equals(authorId)) {
            throw new ForbiddenException("작성자만 수정하거나 삭제할 수 있습니다.");
        }
    }
}
