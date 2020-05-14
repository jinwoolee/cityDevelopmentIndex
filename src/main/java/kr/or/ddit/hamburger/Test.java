package kr.or.ddit.hamburger;

public class Test {
	public static void main(String[] args) {
		System.out.println(String.format("%03d", 1));
		System.out.println(String.format("%03d", 17));
		
		String str = "http://map.daum.net/?q=롯데리아+양양점&map_type=TYPE_MAP";
		
		System.out.println(str.substring(str.indexOf("=")+1, str.indexOf("&")).replace("+", " "));
	}
}
