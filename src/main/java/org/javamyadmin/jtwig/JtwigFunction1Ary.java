package org.javamyadmin.jtwig;

import java.util.function.Function;

import org.jtwig.functions.FunctionRequest;

/**
 * Skeleton for 1-ary functions.
 * 
 * @author lucav
 *
 */
public class JtwigFunction1Ary extends AbstractJtwigFunction {

	private Function<Object, Object> function;

	public JtwigFunction1Ary(String name, Function<Object, Object> function) {
		super(name);
		this.function = function;
	}

	@Override
	public Object execute(FunctionRequest arg) {
		return function.apply(arg.get(0));
	}

}