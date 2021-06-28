package com.scaffold.chat.domains;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.scaffold.chat.datatransfer.UserDataTransfer;

public class InCallMembers {
	
	private static InCallMembers INSTANCE;
    
    private InCallMembers() {        
    }
    
    public static InCallMembers getInstance() {
        if(INSTANCE == null) {
            INSTANCE = new InCallMembers();
        }
        return INSTANCE;
    }

	private Map<String, List<UserDataTransfer>> inCallMembers = new ConcurrentHashMap<>();

	public void setInCallMembers(String chatRoomId, UserDataTransfer member) {
		if(inCallMembers.containsKey(chatRoomId)) {
			List<UserDataTransfer> allActiveMembers = inCallMembers.get(chatRoomId);
			if(!allActiveMembers.contains(member)) {
				allActiveMembers.add(member);
			}
			inCallMembers.put(chatRoomId, allActiveMembers);
		}else {
			List<UserDataTransfer> allActiveMembers = new ArrayList<>();
			allActiveMembers.add(member);
			inCallMembers.put(chatRoomId, allActiveMembers);
		}
	}
		
	public List<UserDataTransfer> getInCallMembers(String chatRoomId) {
		if(inCallMembers.containsKey(chatRoomId)) {
			return inCallMembers.get(chatRoomId);
		}
		return null;
	}
	
	public List<UserDataTransfer> removeInCallMembers(String chatRoomId, UserDataTransfer member) {
		if(inCallMembers.containsKey(chatRoomId)) {
			List<UserDataTransfer> allActiveMembers = inCallMembers.get(chatRoomId);
			allActiveMembers.remove(member);
			inCallMembers.put(chatRoomId, allActiveMembers);
			return allActiveMembers;
		}
		return null;
	}
	
	public void checkInCallMembers(String chatRoomId) {
		if(inCallMembers.containsKey(chatRoomId)) {
			inCallMembers.remove(chatRoomId);
		}
	}
}
