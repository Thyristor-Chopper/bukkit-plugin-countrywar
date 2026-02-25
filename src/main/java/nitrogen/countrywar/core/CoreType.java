package nitrogen.countrywar.core;

public enum CoreType {
	seoul("seoul"),
	busan("busan"),
	jeju("jeju"),
	pyongyang("pyongyang"),
	baekdu("baekdu");

	private final String value;
	
	CoreType(String value) {
		this.value = value;
	}
	
    public String getValue() {
    	return value;
    }
	
	public static String localize(CoreType type) {
		if(type.equals(CoreType.seoul))
            return "서울";
        if(type.equals(CoreType.busan))
        	return "부산";
        if(type.equals(CoreType.jeju))
        	return "제주";
        if(type.equals(CoreType.pyongyang))
        	return "평양";
        if(type.equals(CoreType.baekdu))
        	return "백두";
        return "수호 신상";
	}
}
