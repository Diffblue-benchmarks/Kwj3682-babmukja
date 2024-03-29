package kr.co.babmukja.recipe.controller;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.UrlBasedViewResolver;

import com.google.gson.Gson;

import kr.co.babmukja.recipe.service.RecipeService;
import kr.co.babmukja.repository.domain.FileVO;
import kr.co.babmukja.repository.domain.Recipe;
import kr.co.babmukja.repository.domain.RecipeReview;



@Controller("kr.co.babmukja.recipe.controller.RecipeController")
@RequestMapping("/recipe")
public class RecipeController {
	
	@Autowired
	private RecipeService service;	

	@RequestMapping("/main.do")
	public void main(Model model) {
		List<Recipe> list = service.selectRecipe();
		List<Recipe> result = new ArrayList<>();
		for(Recipe recipe : list) {
			String imgpath = "";
			
			if(recipe.getImgPath() == null) {
				imgpath = "/babmukja/recipe/download.do?path=/&sysname=default.png";
				recipe.setImgPath(imgpath);
				result.add(recipe);
				System.out.println(imgpath);
				continue;
			}
			System.out.println(imgpath);
			String[] imgList = recipe.getImgPath().split(",");
			recipe.setImgPath(imgList[0]);
			result.add(recipe);
		}
		model.addAttribute("recipe", result);
	}
	
	//-----------------------------------------------------------------------
	@RequestMapping("/writeform.do")
	public void writeForm() {}
	
	
	@RequestMapping("/upload.do")
	@ResponseBody
	public Object upload(FileVO fileVO) throws Exception {
		SimpleDateFormat sdf = new SimpleDateFormat(
				"/yyyy/MM/dd"
		);
		String uploadRoot = "C:/bit2019/upload";
		String path = "/recipe" + sdf.format(new Date());
		File file = new File(uploadRoot + path);
		if (file.exists() == false) file.mkdirs();
		System.out.println("create root : " + uploadRoot + path + "/ <- file name here");
		
		MultipartFile mFile = fileVO.getAttach();
		
		if (mFile.isEmpty()) {
//				return;
				System.out.println("is empty");
			}
			String uName =  UUID.randomUUID().toString() + mFile.getOriginalFilename();
			mFile.transferTo(new File(uploadRoot + path + "/" + uName));

			
			fileVO.setPath(path);
			fileVO.setOrgname(mFile.getOriginalFilename());
			fileVO.setSysname(uName);
			System.out.println("file upload succeed.");

			return new Gson().toJson(fileVO);
	}
	
	
	@RequestMapping("/download.do")
	public void download(FileVO fileVO, HttpServletResponse response) throws Exception {
		System.out.println("Download.do");
		String uploadRoot = "c:/bit2019/upload";
		String path = fileVO.getPath();
		String sysname = fileVO.getSysname();
		
		System.out.println("path : "+ path);
		System.out.println("sysname : "+ sysname);
		
		System.out.println("file 생성");
		File f = new File(uploadRoot + path + "/" + sysname);
		
//		f = new File("c:/bit2019/upload/recipe/2019/05/10/767e829f-78ce-47ed-bd82-a0d85da1a9c820140510_221359.jpg");
		response.setHeader("Content-Type", "image/jpg");
		
		// 파일을 읽고 사용자에게 전송
		FileInputStream fis = new FileInputStream(f);
		BufferedInputStream bis = new BufferedInputStream(fis);
		
		OutputStream out = response.getOutputStream();
		BufferedOutputStream bos = new BufferedOutputStream(out);
		while(true) {
			int ch = bis.read();
			if(ch == -1) break;
			bos.write(ch);
		}
		
		bis.close();  fis.close();
		bos.close();  out.close();
	}
	
	
	@RequestMapping("/write.do")
	@ResponseBody
	public void write(Recipe recipe) {
		service.insertRecipe(recipe);
	}
	
	@RequestMapping("/detail.do")
	public ModelAndView detail(ModelAndView mav, int no) {
		Recipe recipe = service.selectRecipeByNo(no);
		service.addViewCnt(no);
		if(recipe == null) {
			System.out.println("recipe is null at no."+no);
			mav.setViewName("recipe/main");
			return mav;
		}
		mav.setViewName("recipe/detail");
		mav.addObject("recipe", recipe);
		return mav;
	}
	
	@RequestMapping("/updateform.do")
	public void updateForm(int no, Model model) {		
		model.addAttribute("recipe" ,service.updateForm(no));
	}
	
	@RequestMapping("/update.do")
	@ResponseBody
	public void update(Recipe recipe) {
		service.updateRecipe(recipe);
	}
	
	@RequestMapping("/delete.do")
	public String delete(int no) {		
		service.deleteRecipe(no);
		return UrlBasedViewResolver.REDIRECT_URL_PREFIX + "main.do";	
	}
	
	@RequestMapping("/recipeComment.do")
	@ResponseBody
	public void writeComment(RecipeReview review) {
		service.insertRecipeReview(review);
	}
	
}
