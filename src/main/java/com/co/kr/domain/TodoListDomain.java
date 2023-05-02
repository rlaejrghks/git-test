package com.co.kr.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder(builderMethodName="builder")
public class TodoListDomain {
	private String tdSeq;
	private String mbId;
	private String tdTitle;
	private String tdContent;
	private String tdCreateAt;
	private String tdUpdateAt;

}