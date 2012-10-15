package net.sourceforge.metware.binche.execs;

public class HelloWorld {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		HelloWorld hello = new HelloWorld();
		hello.printSomething();
	}

	public HelloWorld() {
	}
	
	public String printSomething() {
		return "Hello World!";
	}
}
