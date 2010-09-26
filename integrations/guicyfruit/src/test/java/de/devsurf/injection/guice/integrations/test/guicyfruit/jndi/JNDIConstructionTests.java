package de.devsurf.injection.guice.integrations.test.guicyfruit.jndi;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import javax.annotation.PostConstruct;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import junit.framework.Assert;

import org.junit.Test;

import de.devsurf.injection.guice.scanner.annotations.AutoBind;

public class JNDIConstructionTests {
    private static ThreadLocal<Boolean> called = new ThreadLocal<Boolean>();
    
    @Test
    public void createDynamicModule() {
	try {
	    InitialContext context = new InitialContext();
	    assertNotNull(context);
	    context.getEnvironment();
	} catch (NamingException e) {
	    Assert.fail(e.getMessage());
	}
    }

    @Test
    public void createInheritedInterceptor() {
	called.set(false);
	
	try {
	    InitialContext context = new InitialContext();
	    assertNotNull(context);
	    context.getEnvironment();
	    
	    TestInterface instance = (TestInterface) context.lookup(TestInterface.class.getName());
	    instance.sayHello();
	} catch (NamingException e) {
	    e.printStackTrace();
	    Assert.fail(e.getMessage());
	}
	
	assertTrue("@PostConstruction was not evaluated and Method was not invoked", called.get());
    }
    
    public static interface TestInterface {
	String sayHello();
    }

    @AutoBind
    public static class TestImplementation implements TestInterface {
	@PostConstruct
	public void inform() {
	    called.set(true);
	}
	
	public void cancel(){
	    Assert.fail("Should not be invoked.");
	}

	@Override
	public String sayHello() {
	    return "Good Morning!";
	}

    }
}
