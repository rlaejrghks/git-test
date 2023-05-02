package com.co.kr.mapper;
import java.util.HashMap;
import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.co.kr.domain.TodoContentDomain;
import com.co.kr.domain.TodoFileDomain;
import com.co.kr.domain.TodoListDomain;

@Mapper
public interface TodoMapper {

	//list
	public List<TodoListDomain> todoList();
	public void tdcontentUpload(TodoContentDomain todoContentDomain);
	//file upload
	public void tdfileUpload(TodoFileDomain todoFileDomain);

	//content update
	public void tdContentUpdate(TodoContentDomain todoContentDomain);
	//file updata
	public void tdFileUpdate(TodoFileDomain todoFileDomain);

	//content delete 
	public void tdContentRemove(HashMap<String, Object> map);
	//All Content delete
	public void tdContentAllRemove(HashMap<String, String>map);
	
	//file delete 
	public void tdFileRemove(TodoFileDomain todoFileDomain);
	//All File delete
	public void tdFileAllRemove(HashMap<String, String> map);
	
	//select one
	public TodoListDomain todoSelectOne(HashMap<String, Object> map);

	//select one file
	public List<TodoFileDomain> todoSelectOneFile(HashMap<String, Object> map);
	
	List<TodoListDomain> todoListByMbId(String mbId);
}
//content upload