package com.scaffold.chat.datatransfer;

import java.util.List;

import com.scaffold.security.domains.UserCredentials;

public class MemberAddOrRemoveRequest{
	
	private List<UserCredentials> add;
	private List<UserCredentials> remove;
	
	public List<UserCredentials> getAdd() {
		return add;
	}
	public void setAdd(List<UserCredentials> add) {
		this.add = add;
	}
	public List<UserCredentials> getRemove() {
		return remove;
	}
	public void setRemove(List<UserCredentials> remove) {
		this.remove = remove;
	}
	
	
}
