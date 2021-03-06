package org.javamyadmin.jtwig;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.jtwig.functions.FunctionRequest;

/**
 * Skeleton for n-ary functions.
 * 
 * @author lucav
 *
 */
public class JtwigFunctionVarargs extends AbstractJtwigFunction {

	private Method staticMethod;
	private Object service;
	private Object[] defaults;

	public JtwigFunctionVarargs(String name, Method staticMethod, Object service, Object... defaults) {
		super(name);
		this.staticMethod = staticMethod;
		this.service = service;
		this.defaults = defaults;
	}

	@Override
	public Object execute(FunctionRequest args) {

		Object[] realArgs = new Object[defaults.length];
		int i;
		for (i = 0; i < args.getNumberOfArguments() && i < defaults.length; ++i) {
			realArgs[i] = args.get(i);
		}
		for (; i < defaults.length; ++i) {
			realArgs[i] = defaults[i];
		}

		try {
			return staticMethod.invoke(service, realArgs);
		} catch (IllegalAccessException e) {
			throw new IllegalStateException("Exception on function " + name, e);
		} catch (IllegalArgumentException e) {
			throw new IllegalStateException("Exception on function " + name, e);
		} catch (InvocationTargetException e) {
			throw new IllegalStateException("Exception on function " + name, e);
		} catch (RuntimeException e) {
			throw new RuntimeException("Exception on function " + name, e);
		}
	}

}