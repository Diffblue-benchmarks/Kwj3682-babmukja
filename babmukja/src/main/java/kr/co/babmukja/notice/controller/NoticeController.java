package kr.co.babmukja.notice.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.json.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.view.UrlBasedViewResolver;

import kr.co.babmukja.notice.service.NoticeService;
import kr.co.babmukja.notice.service.NoticeServiceImpl;
import kr.co.babmukja.repository.domain.Comment;
import kr.co.babmukja.repository.domain.Member;
import kr.co.babmukja.repository.domain.Notice;
import kr.co.babmukja.repository.domain.Page;

@Controller("kr.co.babmukja.notice.controller.NoticeController")
@RequestMapping("/notice")
public class NoticeController {
	@Autowired
	private NoticeService service;
	
	@RequestMapping("/comment-list.json")
	@ResponseBody
	public List<Comment> commentList(int no) {
		return service.commentList(no);
	}
	
	@RequestMapping("/writeForm.do")
	public void writeForm() {}
	
	@RequestMapping("/write.do")
	public String write(Notice notice) {
		notice.setWriter(notice.getWriter());
		notice.setTitle(notice.getTitle());
		notice.setContent(notice.getContent());					
		service.write(notice);
		System.out.println("컨텐츠 가져오기 :" + notice.getContent());
		return UrlBasedViewResolver.REDIRECT_URL_PREFIX + "list.do";
	}
	
	@RequestMapping("/updateForm.do")
	public void updateForm(int noticeNo, Model model) {
		model.addAttribute("notice", service.updateForm(noticeNo));	
	}
	
	@RequestMapping("/update.do")
	public String update(int noticeNo, Notice notice) {
		System.out.println("no:" + notice.getNoticeNo());
		System.out.println("title:" + notice.getTitle());
		System.out.println("content:" + notice.getContent());
        service.update(notice);
		return UrlBasedViewResolver.REDIRECT_URL_PREFIX + "list.do";
	}
	
	@RequestMapping("/delete.do")
	public String delete(int noticeNo) {
		service.delete(noticeNo);
		return UrlBasedViewResolver.REDIRECT_URL_PREFIX + "list.do";
	}	
	
	@RequestMapping("/detail.do")
	public void detail(int no, Model model){
		model.addAttribute("notice", service.detail(no)); 
	}
	
	@RequestMapping("/index.do")
	public void index() {}
	
//	@RequestMapping("/list.do")
//	public void list(Page page, Model model) {
//		Map<String, Object> result = service.list(page);
//		model.addAttribute("list", result.get("list"));
//		model.addAttribute("pageResult", result.get("pageResult"));		
//	}
	@RequestMapping("/list.do")
	public void list(Page page, Model model) {
//		int count = 0;
//		Map<String, Object> list = service.list(page);
//		model.addAttribute("list", service.retrieveNotice());
		List<Notice> list = service.selectNoticeList();
		for(Notice notice : list) {
			System.out.println(notice.getWriter());
			System.out.println(notice.getNoticeNo());
			System.out.println(notice.getRegDate());
			System.out.println(notice.getViewCnt());
		}
		model.addAttribute("list", service.selectNoticeList());
//		model.addAttribute("count", count);
//		model.addAttribute("pageResult", list.get("pageResult"));
	}	
	
	/**
     * 댓글 등록(Ajax)
     * @param notice
     * @param request
     * @return
     * @throws Exception
     */
    @RequestMapping(value="/notice/addComment.do")
    @ResponseBody
    public String ajax_addComment(@ModelAttribute("notice") Notice notice, HttpServletRequest request) throws Exception{
        
        HttpSession session = request.getSession();
        Member member = (Member)session.getAttribute("member");
        
        try{
        
            notice.setWriter(member.getMemId());        
            NoticeServiceImpl.addComment(notice);
            
        } catch (Exception e){
            e.printStackTrace();
        }
        
        return "success";
    }
    
    /**
     * 게시물 댓글 불러오기(Ajax)
     * @param notice
     * @param request
     * @return
     * @throws Exception
     */
    @RequestMapping(value="/notice/commentList.do", produces="application/json; charset=utf8")
    @ResponseBody
    public ResponseEntity ajax_commentList(@ModelAttribute("notice") Notice notice, HttpServletRequest request) throws Exception{
        
        HttpHeaders responseHeaders = new HttpHeaders();
        ArrayList<HashMap> hmlist = new ArrayList<HashMap>();
        
        // 해당 게시물 댓글
        List<Notice> comment = NoticeServiceImpl.selectNoticeCommentByNo(notice);
        
        if(comment.size() > 0){
            for(int i=0; i<comment.size(); i++){
                HashMap hm = new HashMap();
                hm.put("commentNo", comment.get(i).getCommentNO());
                hm.put("content", comment.get(i).getContent());
                hmlist.add(hm);
            }
            
        }
        
        JSONArray json = new JSONArray(hmlist);        
        return new ResponseEntity(json.toString(), responseHeaders, HttpStatus.CREATED);
        
    }
}
