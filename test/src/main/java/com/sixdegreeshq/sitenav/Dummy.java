/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sixdegreeshq.sitenav;

import R.category.product.detail;
import R.index;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 *
 * @author alessandro
 */
@Controller
public class Dummy {

    public String productDetails() {
        return "test";
    }

    @RequestMapping(value = index._p)
    public void index() {
    }
    @RequestMapping(value = {detail._p_it, detail._p_en})
    public void detail() {
    }
}
