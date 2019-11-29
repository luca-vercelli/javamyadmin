# javamyadmin
Java porting of PhpMyAdmin

Goal of the project: create a port of [PhpMyAdmin](https://github.com/phpmyadmin/phpmyadmin) that work under Tomcat, and that can connect to multiple databases using JDBC drivers (not MySQL only).

We want to use as much resources as possible from PhpMyAdmin (js, twig, images, ...).

Twig files are parsed with [JTwig](https://github.com/jtwig/jtwig-core), unluckily that project seems abandoned.


# Translating PHP to Java

Trivial conversions:

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

Please notice that a "Singleton" or a "global variable" in PHP are not global at all in Java.

Less trivial conversions:

| preg_replace(x,y,z)   |  y.replaceAll(x,z)         |
| preg_match(x,y)       |  y.matches(x)              |
| explode(x,y)          |  y.split(x)                |
| str_replace(x,y,z)    |  z.replace(x,y)            |
| $_REQUEST[x]          |  request.getParameter(x)   |
| $_SESSION[x]          |  request.getSession().getAttribute(x)      |
| session_id()          |  request.getSession().getId()              |

PHP "array" can be either a Collection or a Map
In the first case:
| is_array(x)           |(x instanceof List   |
| in_array(x,y)         | y.contains(x)       |
| x[] = y               | x.add(y)            |
| x[y]                  | x.get(y)            |

In the second case:
| is_array(x)           | x instanceof Map          |
| in_array(x,y)         | y.valuesSet().contains(x) |
| x[y] = z              | x.put(y, z)               |
| x[y]                  | x.get(y)                  |

