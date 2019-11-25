package org.javamyadmin.jtwig;

import static org.javamyadmin.php.Gettext.__;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.function.Function;

import org.jtwig.environment.EnvironmentConfiguration;
import org.jtwig.environment.EnvironmentConfigurationBuilder;
import org.jtwig.functions.FunctionRequest;
import org.jtwig.functions.JtwigFunction;
import org.jtwig.model.expression.Expression;
import org.jtwig.model.position.Position;
import org.jtwig.model.tree.CompositeNode;
import org.jtwig.model.tree.Node;
import org.jtwig.parser.addon.AddonParserProvider;
import org.jtwig.parser.parboiled.ParserContext;
import org.jtwig.parser.parboiled.base.LimitsParser;
import org.jtwig.parser.parboiled.base.PositionTrackerParser;
import org.jtwig.parser.parboiled.base.SpacingParser;
import org.jtwig.parser.parboiled.expression.AnyExpressionParser;
import org.jtwig.parser.parboiled.node.AddonParser;
import org.jtwig.parser.parboiled.node.CompositeNodeParser;
import org.jtwig.render.RenderRequest;
import org.jtwig.render.expression.CalculateExpressionService;
import org.jtwig.render.expression.calculator.operation.binary.BinaryOperator;
import org.jtwig.render.expression.calculator.operation.binary.calculators.BinaryOperationCalculator;
import org.jtwig.render.node.RenderNodeService;
import org.jtwig.render.node.renderer.NodeRender;
import org.jtwig.renderable.Renderable;
import org.jtwig.renderable.impl.StringRenderable;
import org.jtwig.web.servlet.JtwigRenderer;
import org.parboiled.Rule;

/**
 * We add some PMA functions and some "original" Twig behaviours to JTwig
 * 
 * @author lucav
 *
 */
public class JtwigFactory {

	private static JtwigRenderer rendererInstance;

	/**
	 * 'trans' node rendering stuff. This class represents the syntax tree node.
	 */
	public static class TransNode extends Node {
		Expression expression;
		CompositeNode content;

		protected TransNode(Position position, Expression expression) {
			super(position);
			System.out.println("Now calling constructor." + position);
			this.expression = expression;
		}

		public TransNode(Position position, CompositeNode content) {
			super(position);
			System.out.println("Now calling constructor (composite). " + position);
			this.content = content;
		}

		public Expression getExpression() {
			return expression;
		}

		public CompositeNode getContent() {
			return content;
		}
	}

	/**
	 * 'trans' node rendering stuff. This is the class that renders output.
	 */
	public static class TransNodeRender implements NodeRender<TransNode> {

		@Override
		public Renderable render(RenderRequest renderRequest, TransNode node) {
			Object jtwigValue = null;
			if (node.getExpression() != null) {
				CalculateExpressionService calculateExpressionService = renderRequest.getEnvironment()
						.getRenderEnvironment().getCalculateExpressionService();
				jtwigValue = calculateExpressionService.calculate(renderRequest, node.getExpression());
			} else if (node.getContent() != null) {
				RenderNodeService renderNodeService = renderRequest.getEnvironment().getRenderEnvironment()
						.getRenderNodeService();
				jtwigValue = renderNodeService.render(renderRequest, node.getContent());
			}
			System.out.println("Here: " + jtwigValue);
			return new StringRenderable(__(jtwigValue.toString()));
		}
	}

	/**
	 * 'trans' node rendering stuff. This is the class that identifies input string.
	 */
	public static class TransParser extends AddonParser {

		public TransParser(ParserContext context) {
			super(TransParser.class, context);
		}

		@Override
		public Rule NodeRule() {
			PositionTrackerParser positionTrackerParser = parserContext().parser(PositionTrackerParser.class);
			LimitsParser limitsParser = parserContext().parser(LimitsParser.class);
			SpacingParser spacingParser = parserContext().parser(SpacingParser.class);
			AnyExpressionParser anyExpressionParser = parserContext().parser(AnyExpressionParser.class);
			CompositeNodeParser compositeNodeParser = parserContext().parser(CompositeNodeParser.class);
			return Sequence(positionTrackerParser.PushPosition(),

					limitsParser.startCode(), spacingParser.Spacing(), String("trans"), spacingParser.Spacing(),

					Mandatory(FirstOf(

							// single tag
							Sequence(anyExpressionParser.ExpressionRule(), spacingParser.Spacing(),
									limitsParser.endCode(), push(new TransNode(positionTrackerParser.pop(1),
											anyExpressionParser.pop()))),
							Sequence(
									// start tag
									limitsParser.endCode(),

									// body
									compositeNodeParser.NodeRule(),

									// end tag
									Sequence(limitsParser.startCode(), spacingParser.Spacing(), String("endtrans"),
											spacingParser.Spacing(), limitsParser.endCode(),
											push(new TransNode(positionTrackerParser.pop(1),
													compositeNodeParser.pop()))))),
							"Missing end tag for 'trans'"));
		}
	}

	/**
	 * Skeleton for 1-ary functions.
	 * 
	 * @author lucav
	 *
	 */
	public static class SimpleJtwigFunction implements JtwigFunction {

		private String name;
		private Function<Object, Object> function;

		public SimpleJtwigFunction(String name, Function<Object, Object> function) {
			this.name = name;
			this.function = function;
		}

		@Override
		public Collection<String> aliases() {
			return Collections.emptyList();
		}

		@Override
		public Object execute(FunctionRequest arg) {
			if (arg.getNumberOfArguments() != 1) {
				throw new IllegalArgumentException("'" + name + "' expects exacly 1 argument");
			}
			return function.apply(arg.get(0));
		}

		@Override
		public String name() {
			return name;
		}

	}

	/**
	 * PMA get_image() function
	 */
	public static class EmptyFunction extends SimpleJtwigFunction {

		public EmptyFunction() {
			super("empty",
					value -> value == null || "".equals(value)
							|| (value instanceof Number && ((Number) value).doubleValue() == 0.0)
							|| (value instanceof Collection && ((Collection<?>) value).isEmpty())
							|| (value instanceof Map && ((Map<?, ?>) value).isEmpty()));
		}
	};

	/**
	 * PMA get_image() function
	 */
	public static class GetImageFunction extends SimpleJtwigFunction {

		public GetImageFunction() {
			super("get_image", value -> "<IMG src=" + value + "/>");
		}
	};

	/**
	 * PMA link() function
	 */
	public static class LinkFunction extends SimpleJtwigFunction {

		public LinkFunction() {
			super("link", value -> "<A href=" + value + ">" + value + "</A>");
		}
	};

	/**
	 * PMA url() function
	 */
	public static class UrlFunction extends SimpleJtwigFunction {

		public UrlFunction() {
			super("url", value -> value); // TODO
		}
	};

	/**
	 * PMA show_php_docu() function
	 */
	public static class ShowPhpDocuFunction extends SimpleJtwigFunction {

		public ShowPhpDocuFunction() {
			super("show_php_docu", value -> value); // TODO
		}
	};

	/**
	 * PMA get_docu_link() function
	 */
	public static class GetDocuLinkFunction extends SimpleJtwigFunction {

		public GetDocuLinkFunction() {
			super("get_docu_link", value -> value); // TODO
		}
	};

	/**
	 * Twig '?' operator is different from JTwig's one
	 * 
	 * (x ? y) means (x ? y : null)
	 */
	public static class IfOperator implements BinaryOperator {

		@Override
		public String symbol() {
			return "?";
		}

		@Override
		public int precedence() {
			// | 30
			// and or 25
			// != == 20
			// > <= 15
			// ~ 12
			// + - 10
			// / * 5
			// . 1
			// is ???
			return 29;
		}
	};

	private static EnvironmentConfiguration createConfiguration() {
		return EnvironmentConfigurationBuilder.configuration()
			.functions()
				.add(new GetImageFunction())
				.add(new EmptyFunction())
				.add(new ShowPhpDocuFunction())
				.add(new GetDocuLinkFunction())
				.add(new LinkFunction())
				.add(new UrlFunction())
				.and()
			.parser()
				.addonParserProviders()
					.add(new AddonParserProvider() {
	
						@Override
						public Class<? extends AddonParser> parser() {
							return TransParser.class;
						}
	
						@Override
						public Collection<String> keywords() {
							return Collections.emptyList();
						}
					})
					.and()
				.binaryOperators()
					.add(new IfOperator())
					.and()
				.withoutTemplateCache()
				.and()
			.render()
				.nodeRenders()
					.add(TransNode.class, new TransNodeRender())
					.and()
				.binaryExpressionCalculators()
					.add(IfOperator.class, new BinaryOperationCalculator() {
	
						@Override
						public Object calculate(Request request) {
							CalculateExpressionService calculateExpressionService = request.getEnvironment()
									.getRenderEnvironment().getCalculateExpressionService();
							Object leftValue = calculateExpressionService.calculate(request, request.getLeftOperand());
							Object rightValue = calculateExpressionService.calculate(request, request.getRightOperand());
	
							if (leftValue != null && !leftValue.equals("") && !leftValue.equals(false)
									&& !leftValue.equals(0) && !leftValue.equals(Collections.emptyList())) {
								return rightValue; // to be rendered yet ?!?
							} else {
								return "";
							}
						}
					})
					.and()
				.and()
			.build();
	}

	private static JtwigRenderer createRenderer() {
		return new JtwigRenderer(createConfiguration());
	}

	/**
	 * Provider
	 */
	public static JtwigRenderer getRenderer() {
		if (rendererInstance == null) {
			rendererInstance = createRenderer();
		}
		return rendererInstance;
	}
}