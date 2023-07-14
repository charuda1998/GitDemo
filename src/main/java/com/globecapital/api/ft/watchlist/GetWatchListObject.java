package com.globecapital.api.ft.watchlist;

import com.google.gson.annotations.SerializedName;

public class GetWatchListObject {

	@SerializedName("ProfileId")
	protected String profileId;

	@SerializedName("ProfileName")
	protected String profileName;

	@SerializedName("SequenceNum")
	protected String sequenceNum;

	@SerializedName("ScripCount")
	protected String scripCount;

	@SerializedName("DefaultProfile")
	protected boolean defaultProfile;

	public String getProfileId() {
		return profileId;
	}

	public void setProfileId(String profileId) {
		this.profileId = profileId;
	}

	public String getProfileName() {
		return profileName;
	}

	public void setProfileName(String profileName) {
		this.profileName = profileName;
	}

	public String getSequenceNum() {
		return sequenceNum;
	}

	public void setSequenceNum(String sequenceNum) {
		this.sequenceNum = sequenceNum;
	}

	public boolean getDefaultProfile() {
		return defaultProfile;
	}

	public void setDefaultProfile(Boolean defaultProfile) {
		this.defaultProfile = defaultProfile;
	}

}
