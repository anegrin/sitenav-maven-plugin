# SiteNav Maven Plugin [![Build Status](https://circleci.com/gh/sixdegreeshq/sitenav-maven-plugin.svg?style=shield&circle-token=676562787e5a683b6b21df5dd6a2f0928a6eebfa)](https://circleci.com/gh/sixdegreeshq/sitenav-maven-plugin.svg?style=shield&circle-token=676562787e5a683b6b21df5dd6a2f0928a6eebfa)

A [Maven](http://maven.apache.org/) plugin that generates classes from a given XML sitenav. You can describe your site structure with an xml a this plugin will generate a package and a set of classes reflecting the structure. This should help you in your java/jsp/thymeleaf code (but basically in all your code) with autocompletion (if you're using an IDE) and automatic checking while compiling/running.

It saves you from typo errors and helps you when you need to refactor your site's navigation structure.

# Installation

Add the plugin repository:

```xml
 <pluginRepositories>
    <pluginRepository>
        <id>sixdegreeshq</id>
        <url>https://sixdegreeshq.github.io</url>
        <releases>
            <enabled>true</enabled>
            <updatePolicy>interval:480</updatePolicy>
        </releases>
    </pluginRepository>
</pluginRepositories>
```

add an execution:

```xml
<plugin>
    <groupId>com.sixdegreeshq</groupId>
    <artifactId>sitenav-maven-plugin</artifactId>
    <version>0.2.0</version>
    <executions>
        <execution>
            <goals>
                <goal>generator</goal>
            </goals>
        </execution>
    </executions>
</plugin>
```

The plugin will look for a `sitenav.xml` file in your resources and generate classes under the `R` package.

# XML structure

Here is a sample sitenav.xml:

```xml
<?xml version="1.0" encoding="UTF-8"?>
<root>
    <page path="index">
    </page>
    <page path="faq" />
    <page alias="about">
        <paths>
            <it>chi-siamo</it>
            <en>about-us</en>
        </paths>
    </page>
    <page alias="category" path="{category}">
        <paths>
            <it>it/{category}</it>
            <en>en/{category}</en>
        </paths>
        <page alias="product" path="{sku}">
            <page alias="detail">
                <paths>
                    <it>dettaglio</it>
                    <en>detail</en>
                </paths>
            </page>
        </page>
    </page>
</root>
```

it always stars with a `root` node and a set of nested `page`; a `page` node can have an `alias` attribute and a mandatory `path` attribute and you must provide an `alias` if the path is not a valid java class name (i.e. reserved words like `new` or spring mvc path variables like `{sku}`). The attribute `path` is not mandatory if you specify a set of nested `paths` and in this case you MUST provide an `alias`; `paths` children are tags that represent 2-letters country codes. Please take a look at [test files](https://github.com/sixdegreeshq/sitenav-maven-plugin/blob/master/test/src/main/resources/sitenav.xml)

# Configuration

Each plugin execution can be configured to fulfill your requirements:

```xml
<executions>
	<execution>
		<id>main</id>
		<goals>
			<goal>generator</goal>
		</goals>
		<!-- defaults value -->
		<!-- configuration>
			<inputResourceLocation>sitenav.xml</inputResourceLocation>
			<outputPackage>R</outputPackage>
			<outputFolder>target/generated-sources/java</outputFolder>
			<testing>false</testing>
		</configuration -->
	</execution>
	<execution>
		<id>secondary</id>
		<goals>
			<goal>generator</goal>
		</goals>
		<configuration><!-- some alternative values -->
			<inputResourceLocation>api.xml</inputResourceLocation><!-- different xml -->
			<outputPackage>API</outputPackage><!-- different output package -->
			<localeResolutionCode>org.springframework.context.i18n.LocaleContextHolder.getLocale()</localeResolutionCode><!-- use spring locale resolution -->
			<testing>true</testing><!-- look for resource in testing folders too -->
		</configuration>
	</execution>
</executions>
```

Please take a look at [test files](https://github.com/sixdegreeshq/sitenav-maven-plugin/blob/master/test/pom.xml#L46)

# Use in your code

Each generated class is an implementation of [Page](https://github.com/sixdegreeshq/sitenav-maven-plugin/blob/master/plugin/src/main/resources/com/sixdegreeshq/sitenav/tpl/Page.tpl) so you can use its methods in your code, here are some examples:

```java
String build = R.category.product.detail.builder().param("test", "value").param("a", "b").param("tnull", null).expand("fruit", "banana").build();
System.out.println(build);
```

will print (if your default locale is `en`)

```text
/en/fruit/banana/detail?test=value&a=b
```

while

```java
String build = R.category.product.detail.builder(Locale.ITALIAN).param("test", "valore").param("tnull", null).expand("frutta", "banana").build();
System.out.println(build);
```

will print

```text
/it/frutta/banana/detail?test=valore&a=b
```

- `R.category.product.detail` is one the classes created by the generator
- `builder` creates an uri builder (with an optional locale; if no argument is passed than `localeResolutionCode` is used); shortcut method is `b`
- `param` appends a parameter to the uri; shortcut method is `p`
- `expand` expands uri path variables (i.e. `{variable}`); shortcut method is `e`
- `build` builds the uri; shortcut method is `b`

With Spring MVC you use the generated classes constants to write error proof `@RequestMapping`'s:

```java
@RequestMapping(value = R.index.path)
...
@RequestMapping(value = {R.category.product.detail.path_it, R.category.product.detail.path_en})
...
```

or a shorter version:

```java
@RequestMapping(value = R.index.p)
...
@RequestMapping(value = {R.category.product.detail.p_it, R.category.product.detail.p_en})
...
```

And you have the same commodity in Spring EL:

```jsp
${T(R.index).builder().build()}
...
${T(R.category$product$detail).b().e("fruit", "banana").b()}
...
```

that in thymeleaf becomes:

```html
<a th:href='@{${T(R.index).builder().build()}}'>index</a>
...
<a th:href='@{${T(R.category$product$detail).b().e("fruit", "banana").b()}}'>banana</a>
...
```

Do not forget to look at [test files](https://github.com/sixdegreeshq/sitenav-maven-plugin/tree/master/test) to read more examples! have fun!


