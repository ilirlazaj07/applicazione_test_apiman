/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.capgemini.api;

import java.util.ArrayList;
import java.util.List;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author ILAZAJ
 */
@RestController
public class RestServices {

    @RequestMapping(value = "/servizi/dipendenti", method = RequestMethod.GET)
    public List<Dipendente> getDipendenti() {
        Dipendente d1 = new Dipendente();
        Dipendente d2 = new Dipendente();
        d1.setCognome("Lazaj");
        d1.setNome("Ilir");
        d1.setTipo("Consulente");
        d2.setCognome("Ferrari");
        d2.setNome("Giovanni");
        d2.setTipo("Dipendente");
        List<Dipendente> dipendenti = new ArrayList<Dipendente>();
        dipendenti.add(d2);
        dipendenti.add(d1);
        return dipendenti;
    }

}
