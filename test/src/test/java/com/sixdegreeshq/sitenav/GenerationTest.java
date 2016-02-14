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

}
