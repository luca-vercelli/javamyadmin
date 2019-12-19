# javamyadmin
Java porting of PhpMyAdmin

Goal of the project: create a port of [PhpMyAdmin](https://github.com/phpmyadmin/phpmyadmin) that work under Tomcat, and that can connect to multiple databases using JDBC drivers (not MySQL only).

We want to use as much resources as possible from PhpMyAdmin (js, twig, images, ...).

Twig files are parsed with [JTwig](https://github.com/jtwig/jtwig-core), unluckily that project seems abandoned.

	
# Translating PHP to Java

Trivial conversions:

|  PHP     |   Java   |
|----------|----------|
|   ->     |    .     |
|   ::     |    .     |
|   '      |    "     |
|   .=     |    +=    |
| bool     | boolean  |
| string   | String   |
|  array   |  Map     |
| elseif   | else if  |
|  const   |  final   |
| self.    |          |
| $this    |   this   |
| sprintf  | String.format |

More PHP functions have been defined in Php class.

Less trivial conversions:

|  PHP     |   Java   |
|----------|----------|
| preg_replace(x,y,z)   |  y.replaceAll(x,z)         |
| preg_match(x,y)       |  y.matches(x)              |
| explode(x,y)          |  y.split(x)                |
| implode(x,y)          |  String.join(x,y)          |
| str_replace(x,y,z)    |  z.replace(x,y)            |
| $_REQUEST[x]          |  request.getParameter(x)   |
| $_SESSION[x]          |  request.getSession().getAttribute(x)      |
| session_id()          |  request.getSession().getId()              |
| echo x                |  response.getWriter().write(x)             |
| header("x:y")         |  response.addHeader(x, y)                  |

PHP "array" can be either a List or a Map
In the first case:

|  PHP     |   Java   |
|----------|----------|
| is_array(x)           | x instanceof List   |
| in_array(x,y)         | y.contains(x)       |
| x[] = y               | x.add(y)            |
| x[y]                  | x.get(y)            |

In the second case:

|  PHP     |   Java   |
|----------|----------|
| is_array(x)           | x instanceof Map          |
| in_array(x,y)         | y.valuesSet().contains(x) |
| x[y] = z              | x.put(y, z)               |
| x[y]                  | x.get(y)                  |


# Conversion of PMA classes

Please notice that a "Singleton" in PHP is not a singleton in Java, and a "global variable" in PHP is not global (nor static) in Java.


`Template` class has been merged into `JtwigFactory`.

All paths have been defined inside GLOBALS class.

# Conversion of twig templates

JTwig has some limitations. We added some configuration, however that's not enough.
 
(1) The construct for ... if is not allowed.

(2) The "?" construct requires parenthesis, e.g. (condition) ? x : y

(3) The precedence of "|" operator is different