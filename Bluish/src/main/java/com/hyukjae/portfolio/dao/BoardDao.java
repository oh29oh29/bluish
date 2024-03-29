package com.hyukjae.portfolio.dao;

import java.util.List;
import java.util.Map;

import com.hyukjae.portfolio.bean.BoardBean;

public interface BoardDao {
	
	public List<BoardBean> getTotalBoard(int start, int end, String tblName);
	
	public int getTotalCount(String tblName);
	
	public BoardBean getOneBoard(String num, String tblName);
	
	public void setCount(Map<String, String> map);
	
	public void writeBoard(BoardBean bean, String tblName);
	
	public int getMaxNum(String tblName);
	
	public void deleteBoard(String num, String tblName);
	
	public void updateBoard(String num, String subject, String content, String tblName);
	
}