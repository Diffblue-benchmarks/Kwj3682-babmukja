package kr.co.babmukja.member.controller;

import java.io.UnsupportedEncodingException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.mail.MessagingException;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.UrlBasedViewResolver;

import kr.co.babmukja.member.service.MemberService;
import kr.co.babmukja.repository.domain.MailHandler;
import kr.co.babmukja.repository.domain.Member;

@Controller("kr.co.babmukja.member.controller.MemberController")
@RequestMapping("/member")
public class MemberController {
	@Autowired
	private MemberService service;

	@Autowired
	private BCryptPasswordEncoder passEncoder; // 암호화

	@Autowired
	private JavaMailSender mailSender; // 이메일 인증

	// 로그인
	@RequestMapping("/loginform.do")
	public void loginForm() {}

	// 로그인 처리
	@RequestMapping("/login.do")
	public String login(Member member, HttpSession session) {

		String pass = passEncoder.encode(member.getMemPass());
		Member mem = service.selectLogin(member);
		
		boolean passMatch = passEncoder.matches(member.getMemPass(), mem.getMemPass());

		if (mem != null && passMatch) {
			if (mem.getVerify() == 0) {
				System.out.println("회원 이메일 인증안함");
				return UrlBasedViewResolver.REDIRECT_URL_PREFIX + "/member/loginform.do?email=0";
			} else {
				System.out.println("이메일 인증 함");
				session.setAttribute("user", mem);
			}
			return UrlBasedViewResolver.REDIRECT_URL_PREFIX + "/admin/main.do";
		} else {
			System.out.println("로그인 실패");
			return UrlBasedViewResolver.REDIRECT_URL_PREFIX + "/member/loginform.do?fail=1";
			// complete라는 변수를 만들어서 성공했을 때 1을 넘겨주고 화면에 alert창이 보여지지 않게
			// 1이 넘어오지 않았을 때는 실패 했으니까 화면에 alert창을 보여주게
		}
	}

	// 로그아웃 처리
	@RequestMapping("/logout.do")
	public String logOut(HttpSession session) {
		session.invalidate(); // 세션 삭제
		return UrlBasedViewResolver.REDIRECT_URL_PREFIX + "/admin/main.do";
	}

	// 회원가입 폼
	@RequestMapping("/signupform.do")
	public void signUpForm() {}

	// 회원가입 처리
	@RequestMapping("/signup.do")
	public String signUp(Member member) throws UnsupportedEncodingException, MessagingException {
		
		// 암호화
		String inputPass = member.getMemPass();
		String pass = passEncoder.encode(inputPass);
		member.setMemPass(pass);
		
		service.insertMember(member);

		// 이메일 인증
		MailHandler sendMail = new MailHandler(mailSender);
		StringBuffer sb = new StringBuffer();
		sendMail.setSubject("[밥먹자] 이메일 인증");
		sendMail.setText(sb.append("<h2>밥먹자 메일인증 입니다♬ 아래 링크를 눌러서 이메일 인증을 해주세요.</h2>")
				.append("<a href='http://localhost/babmukja/member/confirm.do?MemEmail=" + member.getMemEmail())
				.append(" 'target='_blank'>밥먹자 이메일 인증하기</a>").toString());
		sendMail.setFrom("babmukja@babmukja.com", "밥먹자");
		sendMail.setTo(member.getMemEmail());
		ExecutorService executor = Executors.newWorkStealingPool();

		// 멀티쓰레드 -> 이메일 보내는 시간이 오래걸리니까
		executor.execute(() -> {
			try {
				sendMail.send();
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
		System.out.println(sb.toString());
		
		return UrlBasedViewResolver.REDIRECT_URL_PREFIX + "/member/emailform.do";
	}

	// 우편번호
	@RequestMapping("/jusopopup.do")
	public void jusoPopUp() {}

	// 이메일 인증
	@RequestMapping("/emailform.do")
	public void emailForm() {}

	// 이메일 인증확인
	@RequestMapping("/emailcheck.do")
	public void emailCheck() {}

	// 이메일 확인
	@RequestMapping("/confirm.do")
	public String mSuccess(Member member) {
		
		// 이메일 인증하면 1로 변경
		member.setMemEmail(member.getMemEmail());
		service.updateEmailVerify(member.getMemEmail());
		return UrlBasedViewResolver.REDIRECT_URL_PREFIX + "/member/emailcheck.do";
	}

	// 닉네임 중복체크
	@RequestMapping("/checknickname.do")
	@ResponseBody
	public int checkNickName(String memNickname) {
		System.out.println(memNickname);
		return service.selectCheckNickName(memNickname);
	}

	// 이메일 중복체크
	@RequestMapping("/checkemail.do")
	@ResponseBody
	public int checkEmail(String memEmail) {
		System.out.println(memEmail);
		return service.selectCheckEmail(memEmail);
	}

	// 아이디 찾기
	@RequestMapping("/searchid.do")
	public void searchIdForm() {}

	// 아이디 찾기 처리
	@RequestMapping("/findid.do")
	@ResponseBody
	public String searchId(Member member) {
		System.out.println(service.selectSearchId(member));
		return service.selectSearchId(member);
	}

	// 비밀번호 찾기
	@RequestMapping("/searchpass.do")
	public void searchPass() {}

	// 비밀번호 인증메일
	@RequestMapping("/passmail.do")
	@ResponseBody
	public void passMail(Member member) throws UnsupportedEncodingException, MessagingException {
		MailHandler sendMail = new MailHandler(mailSender);
		StringBuffer sb = new StringBuffer();

		// Subject -> 제목
		// Text -> 내용
		// From -> 보내는 메일 주소
		// To -> 받는 메일 주소
		String cert = RandomNum();
		sendMail.setSubject("[밥먹자] 이메일 인증번호");
		sendMail.setText(sb.append("<h2>밥먹자 이메일 인증번호 입니다!</h2>").append("밥먹자 이메일 인증번호는 " + '[' + cert + ']' + " 입니다.")
				.toString());
		sendMail.setFrom("babmukja@babmukja.com", "밥먹자");
		sendMail.setTo(member.getMemEmail());

		// 멀티 쓰레드 사용
		ExecutorService executor = Executors.newWorkStealingPool();
		executor.execute(() -> {
			try {
				sendMail.send();
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
		System.out.println(sb.toString());

		// 인증번호 값 DB에 넣기
		member.setCertification(Integer.parseInt(cert));
		service.updateCertification(member);
	}

	// 인증번호 랜덤숫자 생성
	public static String RandomNum() {
		StringBuffer buffer = new StringBuffer();
		for (int i = 0; i <= 5; i++) {
			int n = (int) (Math.random() * 10);
			buffer.append(n);
		}
		return buffer.toString();
	}

	// 인증번호 확인
	@RequestMapping("/checknum.do")
	@ResponseBody
	public int certificationCheck(Member member) {
		return service.selectConfirmCertification(member);
	}

	// 비밀번호 재설정
	@RequestMapping("/resetpass.do")
	public void resetPass(Member member, Model model) {
		model.addAttribute("email", member.getMemEmail());
	}

	// 비밀번호 재설정(암호화)
	@RequestMapping("/repass.do")
	public String rePass(Member member) {

		// 암호화
		String inputPass = member.getMemPass();
		String pass = passEncoder.encode(inputPass);
		member.setMemPass(pass);
		
		service.updateResetPass(member);
		return UrlBasedViewResolver.REDIRECT_URL_PREFIX + "/member/loginform.do";
	}
}
