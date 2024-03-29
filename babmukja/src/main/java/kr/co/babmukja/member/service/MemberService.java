package kr.co.babmukja.member.service;

import kr.co.babmukja.repository.domain.Member;

public interface MemberService {
	Member selectLogin (Member member); // 로그인
	
	void insertMember (Member member); // 회원가입
	
	int selectCheckNickName(String memNickname); // 닉네임 중복체크
	
	int selectCheckEmail(String memEmail); // 이메일 중복체크
	
	void updateEmailVerify(String memEmail); // 이메일 인증
	
	String selectSearchId(Member member); // 아이디 찾기
	
	String selectSearchPass(int certification); // 비밀번호 찾기
	
	void updateCertification(Member member); // 인증번호 등록
	
	int selectConfirmCertification(Member member); // 인증번호 확인
	
	void updateResetPass(Member member); // 비밀번호 재설정

}
