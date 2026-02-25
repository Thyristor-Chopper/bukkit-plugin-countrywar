package nitrogen.countrywar.core;

public class Cores {
	public static Core SEOUL = null;
	public static Core BUSAN = null;
	public static Core JEJU = null;
	public static Core PYONGYANG = null;
	public static Core BAEKDU = null;
	
	public static Core get(String name) {
		if(name.equalsIgnoreCase("seoul"))
			return Cores.SEOUL;
		if(name.equalsIgnoreCase("busan"))
			return Cores.BUSAN;
		if(name.equalsIgnoreCase("jeju"))
			return Cores.JEJU;
		if(name.equalsIgnoreCase("pyongyang"))
			return Cores.PYONGYANG;
		if(name.equalsIgnoreCase("baekdu"))
			return Cores.BAEKDU;
		return null;
	}
	
	public static void set(String name, Core core) {
		if(name.equalsIgnoreCase("seoul"))
			Cores.SEOUL = core;
		else if(name.equalsIgnoreCase("busan"))
			Cores.BUSAN = core;
		else if(name.equalsIgnoreCase("jeju"))
			Cores.JEJU = core;
		else if(name.equalsIgnoreCase("pyongyang"))
			Cores.PYONGYANG = core;
		else if(name.equalsIgnoreCase("baekdu"))
			Cores.BAEKDU = core;
	}
}
