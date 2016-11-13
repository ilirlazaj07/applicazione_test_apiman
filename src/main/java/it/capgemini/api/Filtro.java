/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.capgemini.api;

import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author ILAZAJ
 */
@WebFilter(urlPatterns = "/servizi/*")
public class Filtro implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest richiesta = (HttpServletRequest) request;
        HttpServletResponse risposta = (HttpServletResponse) response;
        System.out.println("IL VALORE DEL TOKEN E: " + richiesta.getHeader("parola_d_ordine"));
        if (richiesta.getHeader("parola_d_ordine").equals("chiamata_rest_ok")) {
            chain.doFilter(richiesta, risposta);
        }

    }

    @Override
    public void destroy() {
    }

}
