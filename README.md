# javamyadmin
Java porting of PhpMyAdmin

Goal of the project: create a port of [PhpMyAdmin](https://github.com/phpmyadmin/phpmyadmin) that work under Tomcat, and that can connect to multiple databases using JDBC drivers (not MySQL only).

We want to use as much resources as possible from PhpMyAdmin (js, twig, images, ...).

We want also some "good" Java code, so automatic translation of code is not what we want.

Twig files are parsed with [JTwig](https://github.com/jtwig/jtwig-core), unluckily that project seems abandoned.
