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
