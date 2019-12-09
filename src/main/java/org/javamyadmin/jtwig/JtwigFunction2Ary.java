package org.javamyadmin.jtwig;

import java.util.function.BiFunction;

import org.jtwig.functions.FunctionRequest;

/**
 * Skeleton for 1-ary functions.
 * 
 * @author lucav
 *
 */
public class JtwigFunction2Ary extends AbstractJtwigFunction {

	private BiFunction<Object, Object, Object> function;
	private Object default1;
	private Object default2;

	public JtwigFunction2Ary(String name, BiFunction<Object, Object, Object> function, Object default1,
			Object default2) {
		super(name);
		this.function = function;
		this.default1 = default1;
		this.default2 = default2;

	}

	@Override
	public Object execute(FunctionRequest arg) {
		if (arg.getNumberOfArguments() == 0) {
			return function.apply(default1, default2);
		} else if (arg.getNumberOfArguments() == 1) {
			return function.apply(arg.get(0), default2);
		} else {
			return function.apply(arg.get(0), arg.get(1));
		}
	}

}