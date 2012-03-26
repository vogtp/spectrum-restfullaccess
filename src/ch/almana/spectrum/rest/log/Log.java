package ch.almana.spectrum.rest.log;

public class Log {

	public static void e(String tag, String msg, Throwable t) {
		System.out.println(msg);
		t.printStackTrace();
	}

	public static void w(String tag, String msg) {
		System.out.println(msg);
		// TODO Auto-generated method stub
		
	}

	public static void d(String tag, String msg) {
		System.out.println(msg);
		// TODO Auto-generated method stub
		
	}

	public static void v(String tag, String msg) {
		System.out.println(msg);
		// TODO Auto-generated method stub
		
	}

	public static void i(String tag, String msg) {
		System.out.println(msg);
		// TODO Auto-generated method stub
		
	}

	public static void e(String tag, String msg) {
		System.out.println(msg);
		// TODO Auto-generated method stub
		
	}

	public static void d(String stacktraceTag, String msg, Throwable e) {
		System.out.println(msg);
		e.printStackTrace();
		// TODO Auto-generated method stub
		
	}

}
