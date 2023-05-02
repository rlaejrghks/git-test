package com.co.kr.controller;

import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.co.kr.domain.BoardListDomain;
import com.co.kr.domain.LoginDomain;
import com.co.kr.service.UploadService;
import com.co.kr.service.UserService;
import com.co.kr.util.AlertUtils;
import com.co.kr.util.CommonUtils;
import com.co.kr.util.Pagination;
import com.co.kr.vo.LoginVO;

import lombok.extern.slf4j.Slf4j;
import com.co.kr.service.TodoService;
import com.co.kr.domain.TodoListDomain;

@Controller
@Slf4j
@RequestMapping(value = "/")
public class UserController {

	@Autowired
	private UserService userService

	@Autowired
	private UploadService uploadService;

	@Autowired
	private TodoService todoService;
	
	//mac주소 가져오는 함수
	public String getLocalMacAddress() {
	 	String result = "";
		InetAddress ip;

		try {
			ip = InetAddress.getLocalHost();
			System.out.println(ip);
			NetworkInterface network = NetworkInterface.getByInetAddress(ip);
			System.out.println(network);
			byte[] mac = network.getHardwareAddress();
			System.out.println(mac);
		   
			StringBuilder sb = new StringBuilder();
			for (int i = 0; i < mac.length; i++) {
				sb.append(String.format("%02X%s", mac[i], (i < mac.length - 1) ? "-" : ""));
			}
				result = sb.toString();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (SocketException e){
			e.printStackTrace();
		}
		return result;
	 }
	
	// 초기화면 설정
	@RequestMapping(value = "board")
	public ModelAndView login(LoginVO loginDTO, HttpServletRequest request, HttpServletResponse response)
			throws IOException {
		HttpSession session = request.getSession();
		ModelAndView mav = new ModelAndView();

		Map<String, String> map = new HashMap();
		map.put("mbId", loginDTO.getId());
		map.put("mbPw", loginDTO.getPw());
		int dupleCheck = userService.mbDuplicationCheck(map);
		LoginDomain loginDomain = userService.mbGetId(map);

		if (dupleCheck == 0) {
			String alertText = "없는 아이디이거나 패스워드가 잘못되었습니다. 가입해주세요";
			String redirectPath = "/main";
			CommonUtils.redirect(alertText, redirectPath, response);
			return mav;
		}

		String IP = CommonUtils.getClientIP(request);
		session.setAttribute("ip", IP);
		session.setAttribute("id", loginDomain.getMbId());
		session.setAttribute("pw", loginDomain.getMbPw());
		session.setAttribute("mbLevel", loginDomain.getMbLevel());
		session.setAttribute("mac", getLocalMacAddress());

		List<BoardListDomain> items = uploadService.boardList();
		System.out.println("items ==> " + items);
		mav.addObject("items", items);
		mav.setViewName("board/boardList.html");
		return mav;
	};
	// 좌측 메뉴 클릭시 보드화면 이동 (로그인된 상태)
	@RequestMapping(value = "bdList")
	public ModelAndView bdList() {
		ModelAndView mav = new ModelAndView();
		List<BoardListDomain> items = uploadService.boardList();
		System.out.println("items ==> " + items);
		mav.addObject("items", items);
		mav.setViewName("board/boardList.html");
		return mav;
	}
	// 멤버 관리
	@RequestMapping(value = "adList")
	public ModelAndView admin(HttpServletRequest request) {
		ModelAndView mav = new ModelAndView();
		Map<String, Object> mapp = new HashMap();
		mapp = Pagination.pagination(userService.mbGetAll(), request);
		HashMap<String, Integer> map = new HashMap();
		Pagination pagination = new Pagination();
		map.put("rowNUM", Integer.parseInt(mapp.put("rowNUM", mapp).toString()));
		map.put("pageNum", Integer.parseInt(mapp.put("pageNum", mapp).toString()));
		map.put("startpage", Integer.parseInt(mapp.put("startpage", mapp).toString()));
		map.put("endpage", Integer.parseInt(mapp.put("endpage", mapp).toString()));
		map.put("offset", Integer.parseInt(mapp.put("offset", mapp).toString()));
		map.put("contentnum", 10);
		mav.addAllObjects(map);
		List<LoginDomain> items = userService.mbAllList(map);
		mav.addObject("items", items);
		mav.setViewName("admin/adminList.html");
		return mav;
	}

	// TodoList
	@RequestMapping(value = "tdList")
	public ModelAndView todoList() {
		ModelAndView mav = new ModelAndView();
		List<TodoListDomain> items = todoService.todoList();
		System.out.println("items ==> " + items);
		mav.addObject("items", items);
		mav.setViewName("todo/todoList.html");
		return mav;
	}

	// 회원가입 GET
	@RequestMapping(value = "signin", method = RequestMethod.GET)
	public void signin() {
		System.out.println("가입하기 GET");
	}

	// 회원가입 POST
	@RequestMapping(value = "signin", method = RequestMethod.POST)
	public ModelAndView signin(LoginDomain loginDomain, HttpServletResponse response, HttpServletRequest request,
			AlertUtils alert) throws IOException {
		ModelAndView mav = new ModelAndView();
		System.out.println("가입하기 POST");
		loginDomain.setMbId(request.getParameter("id"));
		loginDomain.setMbPw(request.getParameter("pw"));
		loginDomain.setMbIp(CommonUtils.getClientIP(request));
		loginDomain.setMbLevel(1);
		loginDomain.setMbUse("Y");
		if (check(request) == 1) {
			System.out.println(check(request));
			mav.addObject("data", new AlertUtils("아이디가 중복되었습니다.", "signin"));
			mav.setViewName("alert/alert");
			return mav;
		}
		userService.mbCreate(loginDomain);
		mav.addObject("data", new AlertUtils("회원가입이 완료되었습니다.", "/main"));
		System.out.println("가입 완료");
		mav.setViewName("alert/alert");
		return mav;
	}

	// 멤버관리에서 만들기 GET
	@RequestMapping(value = "create", method = RequestMethod.GET)
	public void create() {
		System.out.println("가입하기 GET");
	}

	// 멤버관리에서 만들기 POST
	@RequestMapping(value = "create", method = RequestMethod.POST)
	public ModelAndView create(LoginDomain loginDomain, HttpServletResponse response, HttpServletRequest request,
			AlertUtils alert) throws IOException {
		ModelAndView mav = new ModelAndView();
		System.out.println("가입하기 POST");
		loginDomain.setMbId(request.getParameter("id"));
		loginDomain.setMbPw(request.getParameter("pw"));
		loginDomain.setMbIp(CommonUtils.getClientIP(request));
		loginDomain.setMbLevel(1);
		loginDomain.setMbUse("Y");
		if (check(request) == 1) {
			System.out.println(check(request));
			mav.addObject("data", new AlertUtils("아이디가 중복되었습니다.", "adList"));
			mav.setViewName("alert/alert");
			return mav;
		}
		userService.mbCreate(loginDomain);
		mav.addObject("data", new AlertUtils("멤버 추가가 완료되었습니다.", "adList"));
		System.out.println("가입 완료");
		mav.setViewName("alert/alert");
		return mav;
	}

	// 아이디 중복확인
	@ResponseBody
	@RequestMapping(value = "check")
	public int check(HttpServletRequest request) {
		String id = request.getParameter("id");
		System.out.println(id);
		// 중복체크
		Map<String, String> map = new HashMap();
		map.put("mbId", id);
		int i = userService.mbDuplicationCheck(map);
		return i;
	}

	// 로그아웃
	@RequestMapping(value = "/logout", method = RequestMethod.GET)
	public String logout(HttpServletRequest request) throws Exception {
		System.out.println("로그아웃");
		HttpSession session = request.getSession();
		session.invalidate();
		return "redirect:/";
	}

	// 멤버 관리 회원 삭제
	@RequestMapping(value = "mbRemove", method = RequestMethod.GET)
	public ModelAndView remove(HttpServletRequest request) throws Exception {
		ModelAndView mav = new ModelAndView();
		HashMap<String, String> map = new HashMap<String, String>();

		System.out.println(request.getParameter("id"));
		map.put("mbId", request.getParameter("id"));
		System.out.println("map :" + map);
		System.out.println("회원탈퇴 전 파일과 게시글 지우기");
		uploadService.bdFileAllRemove(map);
		System.out.println("게시글 전체 지우기");
		uploadService.bdContentAllRemove(map);
		System.out.println("파일 전체 지우기");
		userService.mbRemove(map);
		System.out.println("회원탈퇴 완료");
		mav.addObject("data", new AlertUtils("멤버 삭제가 완료되었습니다.", "adList"));
		mav.setViewName("alert/alert");
		return mav;
	}

	// 멤버 관리 회원 수정
	@RequestMapping(value = "mbModify", method = RequestMethod.GET)
	public ModelAndView mbModify(HttpServletRequest request) {
		ModelAndView mav = new ModelAndView();
		HashMap<String, Integer> map = new HashMap();
		Map<String, Object> pag = new HashMap();
		pag = Pagination.pagination(userService.mbGetAll(), request);
		Pagination pagination = new Pagination();
		map.put("rowNUM", Integer.parseInt(pag.put("rowNUM", pag).toString()));
		map.put("pageNum", Integer.parseInt(pag.put("pageNum", pag).toString()));
		map.put("startpage", Integer.parseInt(pag.put("startpage", pag).toString()));
		map.put("endpage", Integer.parseInt(pag.put("endpage", pag).toString()));
		map.put("offset", Integer.parseInt(pag.put("offset", pag).toString()));
		map.put("contentnum", 10);
		mav.addAllObjects(map);
		List<LoginDomain> items = userService.mbAllList(map);
		String id = request.getParameter("id");
		String pw = request.getParameter("pw");
		String mbSeq = request.getParameter("mbSeq");
		Integer level = Integer.parseInt(request.getParameter("level"));
		mav.addObject("mbId", id);
		mav.addObject("mbPw", pw);
		mav.addObject("mbSeq", mbSeq);
		mav.addObject("items", items);
		mav.addObject("level", level);
		mav.setViewName("admin/adminEditList");
		return mav;
	}

	// 멤버 정보 업데이트
	@RequestMapping(value = "mbUpdate")
	public ModelAndView mbUpload(LoginDomain loginDomain, HttpServletRequest request) {
		ModelAndView mav = new ModelAndView();
		loginDomain.setMbId(request.getParameter("id"));
		loginDomain.setMbPw(request.getParameter("pw"));
		loginDomain.setMbIp(CommonUtils.getClientIP(request));
		loginDomain.setMbLevel(Integer.parseInt(request.getParameter("level")));
		loginDomain.setMbUse("Y");
		userService.mbUpdate(loginDomain);
		mav.addObject("data", new AlertUtils("멤버 수정이 완료되었습니다.", "adList"));
		System.out.println("멤버 수정 완료");
		mav.setViewName("alert/alert");
		return mav;
	}
	
	@RequestMapping(value="search")
	public ModelAndView getSearchList(HttpServletRequest request) {
		ModelAndView mav = new ModelAndView();
		Map<String, Object> mapp = new HashMap();
		mapp = Pagination.pagination(userService.mbGetAll(), request);
		HashMap<String, Integer> map = new HashMap();
		Pagination pagination = new Pagination();
		map.put("rowNUM", Integer.parseInt(mapp.put("rowNUM", mapp).toString()));
		map.put("pageNum", Integer.parseInt(mapp.put("pageNum", mapp).toString()));
		map.put("startpage", Integer.parseInt(mapp.put("startpage", mapp).toString()));
		map.put("endpage", Integer.parseInt(mapp.put("endpage", mapp).toString()));
		map.put("offset", Integer.parseInt(mapp.put("offset", mapp).toString()));
		map.put("contentnum", 10);
		mav.addAllObjects(map);

		HashMap<String, String> map2 = new HashMap<String, String>();
		map2.put("mbId", request.getParameter("searchId"));
		System.out.println("searchId"+request.getParameter("searchId"));
		mav.addAllObjects(map2);
		List<LoginDomain> items = userService.searchMemberById(map2);
		mav.addObject("items", items);
		mav.setViewName("admin/adminList.html");
		return mav;
	}

	@RequestMapping(value="searchContent")
	public ModelAndView searchContent(HttpServletRequest request) {
		ModelAndView mav = new ModelAndView();
		HashMap<String, String> map = new HashMap<String, String>();
		map.put("bdTitle",request.getParameter("searchtitle"));
		System.out.println("searchtitle : "+request.getParameter("searchtitle"));
		List<BoardListDomain> items = uploadService.searchBoardByTitle(map);
		System.out.println("items ==> " + items);
		mav.addObject("items", items);
		mav.setViewName("board/boardList.html");
		return mav;
	}
}