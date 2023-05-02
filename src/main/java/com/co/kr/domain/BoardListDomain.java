package com.co.kr.domain;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder(builderMethodName="builder")
public class BoardListDomain {

	private String bdSeq;
	private String mbId;
	private String bdTitle
	private String bdContent;
	private String bdCreateAt;
	private String bdUpdateAt;
}