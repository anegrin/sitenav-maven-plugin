package com.sixdegreeshq.sitenav;

import java.util.Locale;
import org.junit.After;
import org.junit.AfterClass;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;

/**
 *
 * @author alessandro
 */
public class GenerationTest {

    private Locale defaultLocale;

    public GenerationTest() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
        defaultLocale = Locale.getDefault();
        Locale.setDefault(Locale.ENGLISH);
    }

    @After
    public void tearDown() {
        Locale.setDefault(defaultLocale);
    }

    @Test
    public void java() {
        String build = R.category.product.detail.builder().param("test", "valore").param("a", "b").param("tnull", null).expand("fruit", "banana").build();
        assertTrue("/en/fruit/banana/detail?test=valore&a=b".equals(build));
        build = R.category.product.detail.builder(Locale.ITALIAN).param("test", "valore").param("tnull", null).expand("fruit", "banana").build();
        String rebuild = R.category.product.detail.builder("it").param("test", "valore").param("tnull", null).expand("fruit", "banana").build();
        String inverseBuild = R.category.product.detail.builder("it").expand("fruit", "banana").param("test", "valore").param("tnull", null).build();
        assertTrue("/it/fruit/banana/dettaglio?test=valore".equals(build));
        assertTrue(rebuild.equals(build));
        assertTrue(rebuild.equals(inverseBuild));
    }

    @Test
    public void spel() {
        ExpressionParser parser = new SpelExpressionParser();
        Expression exp = parser.parseExpression("T(R.category$product$detail).builder().param('test', 'valore').param('tnull', null).expand('fruit', 'banana').build()");
        Object result = exp.getValue();
        assertTrue("/en/fruit/banana/detail?test=valore".equals(result));
    }

    @Test
    public void noMultilang() {
        String build = R.search.what.builder().expand("stuff").build();
        assertTrue("/search/stuff".equals(build));
    }

    @Test
    public void regexp() {
        String re = R.regexp.builder().build();
        assertTrue("/regexp".equals(re));
        re = R.regexp.re1.builder().expand("value").build();
        assertTrue("/regexp/value".equals(re));
        re = R.regexp.re2.builder().build();
        assertTrue("/regexp/re2".equals(re));
        re = R.regexp.re2.re1.builder().expand("value").build();
        assertTrue("/regexp/re2/value".equals(re));
        re = R.regexp.re2.re1.close.builder().expand("value").build();
        assertTrue("/regexp/re2/value/close".equals(re));
    }

    @Test
    public void children() {
        assertTrue(R.regexp.children[0] == R.regexp.re1.class);
        assertTrue(R.regexp.children[1] == R.regexp.re2.class);
        assertTrue(R.regexp.re2.children[0] == R.regexp.re2.re1.class);
    }

    @Test
    public void shortcuts() {
        String build = R.category.product.detail.b("it").p("test", "valore").p("tnull", null).e("fruit", "banana").b();
        String rebuild = R.category.product.detail.b("it").e("fruit", "banana").p("test", "valore").p("tnull", null).b();
        assertTrue(build.equals(rebuild));

    }

}
