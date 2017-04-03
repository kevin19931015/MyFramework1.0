package cn.kevin.framework.core;

import java.io.Serializable;

public class Result implements Serializable{
	private ResultType type;
	private String name;
	private String targetUri;
	public ResultType getType() {
		return type;
	}
	public void setType(ResultType type) {
		this.type = type;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getTargetUri() {
		return targetUri;
	}
	public void setTargetUri(String targetUri) {
		this.targetUri = targetUri;
	}
	@Override
	public String toString() {
		return "Result [type=" + type + ", name=" + name + ", targetUri="
				+ targetUri + "]";
	}
	

}
