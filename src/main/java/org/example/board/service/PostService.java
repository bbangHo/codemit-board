package org.example.board.service;

import lombok.RequiredArgsConstructor;
import org.example.board.domain.Member;
import org.example.board.domain.Post;
import org.example.board.dto.PostDto;
import org.example.board.exception.ForbiddenException;
import org.example.board.exception.NotFoundException;
import org.example.board.repository.PostRepository;
import org.example.board.security.MemberPrincipal;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PostService {
    private final PostRepository postRepository;
    private final MemberService memberService;

    @Transactional
    public PostDto.Response create(PostDto.CreateRequest request, MemberPrincipal principal) {
        Member member = memberService.find(principal.getId());

        return PostDto.Response.from(
                postRepository.save(Post.builder()
                        .title(request.title())
                        .content(request.content())
                        .author(member).build()));
    }

    @Transactional(readOnly = true)
    public Page<PostDto.Response> findAll(Pageable pageable) {
        return postRepository.findAllByOrderByCreatedAtDesc(pageable).map(PostDto.Response::from);
    }

    @Transactional(readOnly = true)
    public PostDto.Response find(Long id) {
        return PostDto.Response.from(getPost(id));
    }

    @Transactional
    public PostDto.Response update(Long id, PostDto.UpdateRequest request, MemberPrincipal principal) {
        Post post = getPost(id);
        checkOwner(post.getAuthor().getId(), principal.getId());
        post.update(request.title(), request.content());
        return PostDto.Response.from(post);
    }

    @Transactional
    public void delete(Long id, MemberPrincipal principal) {
        Post post = getPost(id);
        checkOwner(post.getAuthor().getId(), principal.getId());
        postRepository.delete(post);
    }

    private Post getPost(Long id) {
        return postRepository.findWithAuthorById(id).orElseThrow(() -> new NotFoundException("게시글을 찾을 수 없습니다."));
    }

    private void checkOwner(Long ownerId, Long requestId) {
        if (!ownerId.equals(requestId)) throw new ForbiddenException("작성자만 수정하거나 삭제할 수 있습니다.");
    }
}
