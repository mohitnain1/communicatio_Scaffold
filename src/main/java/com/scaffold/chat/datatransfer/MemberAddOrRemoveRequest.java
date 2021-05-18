package com.scaffold.chat.datatransfer;

import java.util.List;

public class MemberAddOrRemoveRequest{
	
	private List<Long> add;
	private List<Long> remove;
	
	public List<Long> getAdd() {
		return add;
	}
	public void setAdd(List<Long> add) {
		this.add = add;
	}
	public List<Long> getRemove() {
		return remove;
	}
	public void setRemove(List<Long> remove) {
		this.remove = remove;
	}
	
}
