package com.co.kr.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;

import java.util.HashMap;
import java.io.IOException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.co.kr.service.TodoService;
import com.co.kr.util.CommonUtils;

import lombok.extern.slf4j.Slf4j;

import com.co.kr.vo.FileListVO;

import java.io.PrintWriter;
import java.text.ParseException;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.multipart.MultipartHttpServletRequest;
import javax.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.ModelAndView;

import com.co.kr.domain.BoardFileDomain;
import com.co.kr.domain.TodoFileDomain;
import com.co.kr.domain.TodoListDomain;
import com.co.kr.mapper.TodoMapper;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@Slf4j
@Controller
public class TodoController {
	@RequestMapping(value = "tddetail", method = RequestMethod.GET)
	public ModelAndView tdSelectOneCall(@ModelAttribute("fileListVO") FileListVO fileListVO,
			@RequestParam("tdSeq") String tdSeq, HttpServletRequest request) {
		ModelAndView mav = new ModelAndView();
		HashMap<String, Object> map = new HashMap<String, Object>();
		HttpSession session = request.getSession();

		map.put("tdSeq", Integer.parseInt(tdSeq));
		TodoListDomain todoListDomain = todoService.todoSelectOne(map);
		System.out.println("tdListDomain" + todoListDomain);
		List<TodoFileDomain> fileList = todoService.todoSelectOneFile(map);

		for (TodoFileDomain list : fileList) {
			String path = list.getUpFilePath().replaceAll("\\\\", "/");
			list.setUpFilePath(path);
		}
		mav.addObject("detail", todoListDomain);
		mav.addObject("files", fileList);
		mav.setViewName("/todo/todoList.html");
		session.setAttribute("files", fileList);
		return mav;
	}

	@Autowired
	private TodoService todoService;

	@PostMapping(value = "tdList")
	public ModelAndView todoList(HttpServletRequest request) {
		ModelAndView mav = new ModelAndView();
		HttpSession session = request.getSession();
		System.out.println(session.getId());
		List<TodoListDomain> items = todoService.todoList();
		System.out.println("items ==> " + items);
		mav.addObject("items", items);
		mav.setViewName("todo/todoList.html");
		return mav;
	}
	
	@RequestMapping(value = "tdupload")
	public ModelAndView tdUpload(FileListVO fileListVO, MultipartHttpServletRequest request, HttpServletRequest httpReq,
			HttpServletResponse response) throws IOException, ParseException {

		ModelAndView mav = new ModelAndView();
		int tdSeq = todoService.tdfileProcess(fileListVO, request, httpReq);
		fileListVO.setContent(""); // 초기화
		fileListVO.setTitle(""); // 초기화
		// 화면에서 넘어올때는 bdSeq String이라 string으로 변환해서 넣어즘
		mav = tdSelectOneCall(fileListVO, String.valueOf(tdSeq), request);
		mav.setViewName("todo/todoList.html");
		return mav;
	}

	@RequestMapping(value = "tdremove", method = RequestMethod.GET)
	public ModelAndView remove(TodoFileDomain todoFileDomain, String tdSeq, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		System.out.println(tdSeq + "번째 todo 삭제.");
		ModelAndView mav = new ModelAndView();
		HashMap<String, Object> map = new HashMap<String, Object>();
		HttpSession session = request.getSession();

		map.put("tdSeq", Integer.parseInt(tdSeq));
		todoService.tdContentRemove(map);

		todoFileDomain.setTdSeq(Integer.parseInt(tdSeq));
		System.out.println(tdSeq + "번째 게시글 사진 삭제");
		todoService.tdFileRemove(todoFileDomain);
		mav.setViewName("/todo/todoList.html");
		String alertText = "todo가 삭제되었습니다.";
		String redirectPath = "/main/tdList";
		CommonUtils.redirect(alertText, redirectPath, response);
		return mav;
	}

	@RequestMapping(value = "/tdedit", method = RequestMethod.GET)
	public ModelAndView edit(FileListVO fileListVO, @RequestParam("tdSeq") String tdSeq, HttpServletRequest request)
			throws IOException {
		ModelAndView mav = new ModelAndView();

		HashMap<String, Object> map = new HashMap<String, Object>();
		HttpSession session = request.getSession();

		map.put("tdSeq", Integer.parseInt(tdSeq));
		TodoListDomain todoListDomain = todoService.todoSelectOne(map);
		List<TodoFileDomain> fileList = todoService.todoSelectOneFile(map);

		for (TodoFileDomain list : fileList) {
			String path = list.getUpFilePath().replaceAll("\\\\", "/");
			list.setUpFilePath(path);
		}

		fileListVO.setSeq(todoListDomain.getTdSeq());
		fileListVO.setContent(todoListDomain.getTdContent());
		fileListVO.setTitle(todoListDomain.getTdTitle());
		fileListVO.setIsEdit("tdedit"); // upload 재활용하기위해서

		mav.addObject("detail", todoListDomain);
		mav.addObject("files", fileList);
		mav.addObject("fileLen", fileList.size());

		mav.setViewName("todo/todoEditList.html");
		return mav;
	}

	@PostMapping(value = "/tdeditSave")
	public ModelAndView tdeditSave(@ModelAttribute("fileListVO") FileListVO fileListVO,
			MultipartHttpServletRequest request, HttpServletRequest httpReq) throws IOException {
		ModelAndView mav = new ModelAndView();

		// 저장
		todoService.tdfileProcess(fileListVO, request, httpReq);
		mav = tdSelectOneCall(fileListVO, fileListVO.getSeq(), request);
		fileListVO.setContent(""); // 초기화
		fileListVO.setTitle(""); // 초기화
		mav.setViewName("todo/todoList.html");
		return mav;
	}
	@RequestMapping(value="/tdRemove", method = RequestMethod.GET)
	public String tdRemove(TodoFileDomain todoFileDomain, String tdSeq, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		System.out.println(tdSeq + "번째 게시글 삭제.");
		HashMap<String, Object> map = new HashMap<String, Object>();
		HttpSession session = request.getSession();

		map.put("tdSeq", Integer.parseInt(tdSeq));
		todoService.tdContentRemove(map);

		todoFileDomain.setTdSeq(Integer.parseInt(tdSeq));
		System.out.println(tdSeq + "번째 게시글 사진 삭제");
		return "redirect:/tdList";
	}
}
