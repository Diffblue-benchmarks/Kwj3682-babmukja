package kr.co.babmukja.admin.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import kr.co.babmukja.admin.service.AdminService;
import kr.co.babmukja.recipe.service.RecipeService;
import kr.co.babmukja.repository.domain.Page;
import kr.co.babmukja.store.service.StorePBService;

@Controller("kr.co.babmukja.admin.controller.AdminController")
@RequestMapping("/admin")
public class AdminController {
	
	@Autowired
	private AdminService service;

	@RequestMapping("/board.do")
	public void boardList(Page page, Model model) {
		
	}
	
	
	@RequestMapping("/main.do")
	public void main(){}
	
	@RequestMapping("/member.do")	
	public void list(Page page, Model model) {		
		Map<String, Object> list = service.listMember(page);
		
		model.addAttribute("list", list.get("list"));
		model.addAttribute("count", service.countMember(page));
		model.addAttribute("searchType", list.get("searchType"));
		model.addAttribute("input", list.get("input"));
		
		if(list.get("signDate1") != "") {			
			model.addAttribute("signDate1", list.get("signDate1"));
			model.addAttribute("signDate2", list.get("signDate2"));
		}

//		if(list.get("lastDate1") != "") {
//			model.addAttribute("lastDate1", list.get("lastDate1"));
//			model.addAttribute("lastDate2", list.get("lastDate2"));			
//		}
//		
	    model.addAttribute("pageResult", list.get("pageResult"));
	
	}
	
	@RequestMapping("/memberdetail.do")	
	public void detail(int no, Model model) {	
		model.addAttribute("d", service.selectMemberByNo(no));
		
	}

	// PB 스토어 index (상품조회 / 상품등록)
	@RequestMapping("/pbstoreindex.do")
	public void pbstoreindex() {}
	

}
