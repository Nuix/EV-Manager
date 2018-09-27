package com.nuix.evmanager.controls;

import java.util.List;

import com.nuix.evmanager.data.SQLUserRecord;

public interface UserRecordChangedListener {
	public void userRecordsChanged(List<SQLUserRecord> updatedValue);
}
