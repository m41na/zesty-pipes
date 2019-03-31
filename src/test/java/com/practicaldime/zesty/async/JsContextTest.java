package com.practicaldime.zesty.async;

import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.Value;
import org.junit.Test;

public class JsContextTest {

	@Test
	public void testHelloWorld() {
		Context jsContext = Context.create("js");
		runScript("console.log('Hello from the project')", jsContext);
	}

	@Test
	public void testExchangeData() {
		Context jsContext = Context.create("js");
		String script = "console.log('I will welcome you ' + welcomeCount + ' times.');"
				+ "for(var i=0; i<welcomeCount; i++){ console.log('Welcome') }";
		Value bindings = jsContext.getBindings("js");
		bindings.putMember("welcomeCount", 3);
		runScript(script, jsContext);
	}
	
	@Test
	public void testJsBinding() {
		String printNumberScript = "console.log(phone.number)";
		String callingScript = "phone.call('Someone')";
		Phone phone = new Phone(123456);
		Context context = Context.create("js");
		context.getBindings("js").putMember("phone", phone);
		runScript(printNumberScript, context);
		runScript(callingScript, context);
	}

	private static Value runScript(String script, Context context) {
		return context.eval("js", script);
	}

	public static class Phone {
		
		public final int number;

		public Phone(int number) {
			this.number = number;
		}

		public void call(String name) {
			System.out.println("Calling...: " + name);
		}
	}
}
