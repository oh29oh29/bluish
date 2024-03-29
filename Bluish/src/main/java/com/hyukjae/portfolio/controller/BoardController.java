package com.hyukjae.portfolio.controller;

import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.hyukjae.portfolio.bean.BoardBean;
import com.hyukjae.portfolio.dao.BoardDaoImpl;

@Controller
public class BoardController {
	
	@Autowired
	BoardDaoImpl boardDaoImpl;
	
	public void setBoardDao(BoardDaoImpl boardDaoImpl){
		this.boardDaoImpl = boardDaoImpl;
	}
	
	@RequestMapping(value = "/boardlist", method = RequestMethod.GET)
	public String boardList(Locale locale, Model model, @RequestParam("pageNum")String pageNum, @RequestParam("category")String category) {
		
		int totalRecord = 0;	// 전체 레코드 수
		int numPerPage = 10;	// 페이지 당 레코드 수
		int pagePerBlock = 10;	// 블럭 당 페이지 수
		
		int totalPage = 0;		// 전체 페이지 수
		int totalBlock = 0;		// 전체 블럭 수
		
		int nowBlock = 1;		// 현재 블럭
		
		int start = 0;			// DB의 select 시작번호
		int end = 10;			// 시작번호로부터 가져올 select 개수
		
		int listSize = 0;		// 현재 읽어온 게시물의 수
		
		int number = 0;
		
		if(pageNum == null){
			pageNum="1";
		}
		
		List<BoardBean> list = null;
		
		int nowPage = Integer.parseInt(pageNum);	// 현재 페이지
		
		start = (nowPage * numPerPage) - numPerPage;
		end = start + numPerPage;
		
		String tblName = "tbl" + category;
		totalRecord = boardDaoImpl.getTotalCount(tblName);
		
		if(totalRecord > 0){
			list = boardDaoImpl.getTotalBoard(start, end, tblName); 
			number = totalRecord - (nowPage-1) * pagePerBlock;
		}
		
		totalPage = (int)Math.ceil((double)totalRecord / numPerPage);	// 전체 페이지 수
		nowBlock = (int)Math.ceil((double)nowPage / pagePerBlock);		// 현재 블럭 계산
		
		int pageCount = (totalRecord%numPerPage==0?totalRecord/numPerPage+0 : totalRecord/numPerPage+1);
		
		model.addAttribute("title", "게시판");
		model.addAttribute("view", "../board/board_page.jsp");
		model.addAttribute("board_view", "board_list_view.jsp");
		
		model.addAttribute("listBean", list);
		model.addAttribute("totalRecord", totalRecord);
		model.addAttribute("nowPage", nowPage);
		model.addAttribute("totalPage", totalPage);
		model.addAttribute("number", number);
		model.addAttribute("pageCount", pageCount);
		model.addAttribute("pagePerBlock", pagePerBlock);
		
		model.addAttribute("category", category);

		model.addAttribute("start", start);
		model.addAttribute("end", end);
		
		return "frame/frame";
	}
	
	@RequestMapping(value = "/boardinfo", method = RequestMethod.GET)
	public String boardInfo(Locale locale, Model model, @RequestParam("num")String num, @RequestParam("category")String category) {
		
		model.addAttribute("title", "게시판");
		model.addAttribute("view", "../board/board_page.jsp");
		model.addAttribute("board_view", "board_info_view.jsp");
		
		String tblName = "tbl" + category;
		BoardBean bean = boardDaoImpl.getOneBoard(num, tblName);
		
		model.addAttribute("bean", bean);
		model.addAttribute("num", num);
		model.addAttribute("category", category);

		return "frame/frame";
	}
	
	@RequestMapping(value = "/boardwrite", method = RequestMethod.GET)
	public String boardWrite(Locale locale, Model model, @RequestParam("category")String category, HttpSession session) {
		
		if(session.getAttribute("idKey") != null){
			model.addAttribute("title", "게시판");
			model.addAttribute("view", "../board/board_page.jsp");
			model.addAttribute("board_view", "board_write_view.jsp");
			model.addAttribute("category", category);
		}
		else{
			return "redirect:memberlogin"; 
		}
		return "frame/frame";
	}
	
	@RequestMapping(value = "/boardwrite", method = RequestMethod.POST)
	public String boardWriteSubmit(Locale locale, Model model, BoardBean bean, @RequestParam("category")String category) {
		
		model.addAttribute("title", "게시판");
		model.addAttribute("view", "../board/board_page.jsp");
		model.addAttribute("board_view", "board_info_view.jsp");
		
		String tblName = "tbl" + category;
		
		boardDaoImpl.writeBoard(bean, tblName);
		
		int num = boardDaoImpl.getMaxNum(tblName);
		bean = boardDaoImpl.getOneBoard(String.valueOf(num), tblName);
		
		model.addAttribute("bean", bean);
		
		return "frame/frame";
	}
	
	@RequestMapping(value = "/boarddelete", method = RequestMethod.POST)
	public String boardDelete(Locale locale, Model model, HttpSession session, @RequestParam("num")String num, @RequestParam("category")String category) {
		
		if(session.getAttribute("idKey") != null){
			
			String tblName = "tbl" + category;
			
			boardDaoImpl.deleteBoard(num, tblName);
		}
		else{
			return "redirect:memberlogin"; 
		}
		
		return "redirect:boardlist?pageNum=1&category="+category; 
	}
	
	@RequestMapping(value = "/boardupdateview", method = RequestMethod.POST)
	public String boardUpdate(Locale locale, Model model, HttpSession session, @RequestParam("num")String num, @RequestParam("category")String category) {
		
		if(session.getAttribute("idKey") != null){
			model.addAttribute("title", "게시판");
			model.addAttribute("view", "../board/board_page.jsp");
			model.addAttribute("board_view", "board_update_view.jsp");
			
			String tblName = "tbl" + category;
			BoardBean bean = boardDaoImpl.getOneBoard(num, tblName);
			
			model.addAttribute("bean", bean);
			model.addAttribute("num", num);
			model.addAttribute("category", category);
		}
		else{
			return "redirect:memberlogin"; 
		}
		return "frame/frame";
	}
	@RequestMapping(value = "/boardupdate", method = RequestMethod.POST)
	public String boardUpdateSubmit(Locale locale, Model model, @RequestParam("num")String num, @RequestParam("category")String category, 
			@RequestParam("subject")String subject, @RequestParam("content")String content) {
		
		model.addAttribute("title", "게시판");
		model.addAttribute("view", "../board/board_page.jsp");
		model.addAttribute("board_view", "board_info_view.jsp");
		
		String tblName = "tbl" + category;
		
		boardDaoImpl.updateBoard(num, subject, content, tblName);
		
		BoardBean bean = boardDaoImpl.getOneBoard(String.valueOf(num), tblName);
		
		model.addAttribute("bean", bean);
		model.addAttribute("num", num);
		model.addAttribute("category", category);
		
		return "frame/frame";
	}
}
