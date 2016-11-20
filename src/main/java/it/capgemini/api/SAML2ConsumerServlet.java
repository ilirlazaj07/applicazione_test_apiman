package it.capgemini.api;

import java.io.IOException;
import java.util.Map;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.opensaml.xml.ConfigurationException;


public class SAML2ConsumerServlet extends HttpServlet {

    private static final long serialVersionUID = 12345L;
    private SamlConsumerManager consumer;
    private static Log log = LogFactory.getLog(SAML2ConsumerServlet.class);

    
    public void init(ServletConfig config) throws ServletException {
        try {
            consumer = new SamlConsumerManager(config);
        } catch (ConfigurationException e) {
            throw new ServletException("Errore durante la configurazione dello SAMLConsumerManager", e);
        }
    }

   
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException,
            IOException {
        doPost(request, response);
    }

  
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException,
            IOException {

        String responseMessage = request.getParameter("SAMLResponse");

        if (responseMessage != null) { /* risposta dall identity provider */

            System.out.println("RISPOSTA IMMINENTE...");

            log.info("SAMLResponse ricevuta dall IDP ");

            Map<String, String> result = consumer.processResponseMessage(responseMessage);

            if (result == null) {
                System.out.println("lets logout the user...");
                // lets logout the user
                response.sendRedirect("index.jsp");
            } else if (result.size() == 1) {
                System.out.println("PAGINA DI DEFAULTTTTT...");
                
                 Object[] keys = result.keySet().toArray();
                for (int i = 0; i < result.size(); i++) {
                    String key = (String) keys[i];
                    String value = (String) result.get(key);
                    System.out.println("Leggi: " + value);
                }

                /* 
                 Nessun attributo restituito, vai sulla pagina autenticata
                 *
                 */
                response.sendRedirect("http://localhost:8080/api/#/utente_autenticato");
            } else if (result.size() > 1) {

                System.out.println("PAGINA CON ATRIBUTI...");
                /*
                 * Pagina con attributi esistenti restiuiti
                 * 
                 */
                String params = "paginadegli attributi";
                Object[] keys = result.keySet().toArray();
                for (int i = 0; i < result.size(); i++) {
                    String key = (String) keys[i];
                    String value = (String) result.get(key);
                    if (i != result.size()) {
                        params = params + key + "=" + value + "&";
                    } else {
                        params = params + key + "=" + value;
                    }
                }
                response.sendRedirect(params);
            } else {
                // errore nella gestione della richiesta, rispedire sulla pagina di login
                response.sendRedirect("http://localhost:8080/api");
            }

        } else { /* Creazione di una richiesta di autenticazione e di logout */

            try {
                String requestMessage = consumer.buildRequestMessage(request);

                response.sendRedirect(requestMessage);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
