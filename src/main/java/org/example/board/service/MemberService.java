package org.example.board.service;

import lombok.RequiredArgsConstructor;
import org.example.board.domain.Member;
import org.example.board.dto.MemberDto;
import org.example.board.exception.ConflictException;
import org.example.board.exception.NotFoundException;
import org.example.board.repository.MemberRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MemberService {
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * 가입: 이메일 형식이 틀리거나, 이미 있는 이메일이거나, 비밀번호가 너무 짧으면 거절합니다. 완료
     * 비밀번호는 BCrypt 등으로 해시해서만 저장합니다. 원문을 저장하거나 응답에 내보내면 안 됩니다. 완료
     * 로그인 방식은 세션과 JWT 중에서 고르고, 고른 이유를 README 에 씁니다.
     * @param request
     * @return
     */
    @Transactional
    public MemberDto.Response signup(MemberDto.SignupRequest request) {
        if (memberRepository.existsByEmail(request.email())) {
            throw new ConflictException("이미 가입된 이메일입니다.");
        }

        Member member = Member.builder()
                .email(request.email())
                .password(passwordEncoder.encode(request.password()))
                .nickname(request.nickname())
                .build();

        return MemberDto.Response.from(memberRepository.save(member));
    }

    @Transactional(readOnly = true)
    public Member find(Long id) {
        return memberRepository.findById(id).orElseThrow(() ->
                new NotFoundException("회원을 찾을 수 없습니다."));
    }
}
