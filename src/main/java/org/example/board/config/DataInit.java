package org.example.board.config;

import lombok.RequiredArgsConstructor;
import org.example.board.domain.Comment;
import org.example.board.domain.Member;
import org.example.board.domain.Post;
import org.example.board.repository.CommentRepository;
import org.example.board.repository.MemberRepository;
import org.example.board.repository.PostRepository;
import org.example.board.service.CommentService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Component
@Profile("!test")
@RequiredArgsConstructor
public class DataInit implements CommandLineRunner {
    private final MemberRepository memberRepository;
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final PasswordEncoder passwordEncoder;
    private final CommentService commentService;

    @Override
    @Transactional
    public void run(String... args) {
        if (memberRepository.existsByEmail("user1@example.com")) {
            return;
        }

        List<Member> members = new ArrayList<>();
        for (int i = 1; i <= 3; i++) {
            members.add(memberRepository.save(Member.builder()
                    .email("user" + i + "@example.com")
                    .password(passwordEncoder.encode("password123"))
                    .nickname("user" + i)
                    .build()));
        }

        List<Post> firstPosts = new ArrayList<>();
        for (int memberIndex = 0; memberIndex < members.size(); memberIndex++) {
            Member member = members.get(memberIndex);

            for (int postIndex = 1; postIndex <= 10; postIndex++) {
                Post post = Post.builder()
                        .title(postIndex == 1 ? "댓글 테스트 게시글" : "user" + (memberIndex + 1) + " 게시글 " + postIndex)
                        .content(postIndex == 1 ? "댓글과 대댓글을 확인하기 위한 게시글입니다." : "초기 데이터 게시글 본문입니다.")
                        .author(member)
                        .build();
                Post savedPost = postRepository.save(post);
                if (postIndex == 1) {
                    firstPosts.add(savedPost);
                }
            }
        }

        Comment comment1 = commentRepository.save(Comment.builder()
                .content("테스트 댓글 1")
                .author(members.get(0))
                .post(firstPosts.get(0))
                .parentComment(null)
                .build());

        Comment comment2 = commentRepository.save(Comment.builder()
                .content("테스트 댓글 2")
                .author(members.get(1))
                .post(firstPosts.get(0))
                .parentComment(null)
                .build());

        comment1.reComment(commentRepository.save(Comment.builder()
                .content("user3이 작성한 대댓글")
                .author(members.get(2))
                .post(firstPosts.get(2))
                .build()));

        comment2.reComment(commentRepository.save(Comment.builder()
                .content("user3이 작성한 대댓글")
                .author(members.get(2))
                .post(firstPosts.get(2))
                .build()));
    }
}
