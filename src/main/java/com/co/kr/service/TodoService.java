package com.co.kr.service;

import java.util.HashMap;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.co.kr.domain.TodoContentDomain;
import com.co.kr.domain.TodoFileDomain;
import com.co.kr.domain.TodoListDomain;
import com.co.kr.vo.FileListVO;

public interface TodoService {
	
	public int tdfileProcess(FileListVO fileListVO, MultipartHttpServletRequest request, HttpServletRequest httpReq);
	
	// 전체 리스트 조회
	public List<TodoListDomain> todoList();

	// 하나 삭제
	public void tdContentRemove(HashMap<String, Object> map);
	// 전체 삭제
	public void tdContentAllRemove(HashMap<String, String>map);
	// 하나 삭제
	public void tdFileRemove(TodoFileDomain todoFileDomain);
	
	// 전체 삭제
	public void tdFileAllRemove(HashMap<String, String> map);
	//select one
	public TodoListDomain todoSelectOne(HashMap<String, Object> map);

	//select one file
	public List<TodoFileDomain> todoSelectOneFile(HashMap<String, Object> map);
	
	public void tdContentUpdate(TodoContentDomain todoContentDomain);

	public void tdFileUpdate(TodoFileDomain todoFileDomain);
	
}