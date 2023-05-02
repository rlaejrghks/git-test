package com.co.kr.domain;

import lombok.Builder;
import lombok.Data;

@Data
@Builder(builderMethodName="builder")
public class TodoContentDomain {

	private Integer tdSeq;
	private String mbId;
	private String tdTitle;
	private String tdContent;

}