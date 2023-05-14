import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class ControllerServlet extends HttpServlet {

    private ServletContext context;
    private ICommand command;            // obiekt klasy wykonawczej
    private String presentationServ;    // nazwa serwlet prezentacji
    private String getParamsServ;       // mazwa serwletu pobierania parametrów
    private Object lock = new Object(); // semafor dla synchronizacji
    // odwołań wielu wątków
    public void init() {

        context = getServletContext();
        presentationServ = context.getInitParameter("presentationServ");
        getParamsServ = context.getInitParameter("getParamsServ");
        String commandClassName = context.getInitParameter("commandClassName");

        // Załadowanie klasy Command i utworzenie jej egzemplarza
        // który będzie wykonywał pracę
        try {
            Class<?> commandClass = Class.forName(commandClassName);
            command = (ICommand) commandClass.newInstance();
        } catch (Exception exc) {
            for(StackTraceElement s : exc.getStackTrace()){
                System.out.println(s);
            }
            System.out.println(exc.getClass());
            throw new NoCommandException("Couldn't find or instantiate " +
                    commandClassName);
        }
    }

    // Obsługa zleceń
    public void serviceRequest(HttpServletRequest req,
                               HttpServletResponse resp)
            throws ServletException, IOException
    {

        resp.setContentType("text/html");

        // Wywolanie serwletu pobierania parametrów
        System.out.println("Wywołanie servletu pobierania danych");
        RequestDispatcher disp = context.getRequestDispatcher(getParamsServ);
        disp.include(req,resp);

        // Pobranie bieżącej sesji
        // i z jej atrybutów - wartości parametrów
        // ustalonych przez servlet pobierania parametrów
        // Różne informacje o aplikacji (np. nazwy parametrów)
        // są wygodnie dostępne poprzez własną klasę BundleInfo

        HttpSession ses = req.getSession();

        String[] pnames = BundleInfo.getCommandParamNames();
        for (int i=0; i<pnames.length; i++) {

            String pval = (String) ses.getAttribute("param_"+pnames[i]);

            if (pval == null) return;  // jeszcze nie ma parametrów

            // Ustalenie tych parametrów dla Command
            command.setParameter(pnames[i], pval);
        }

        // Wykonanie działań definiowanych przez Command
        // i pobranie wyników
        // Ponieważ do serwletu może naraz odwoływać sie wielu klientów
        // (wiele watków) - potrzebna jest synchronizacja
        // przy czym rrygiel zamkniemy tutaj, a otworzymy w innym fragmnencie kodu
        // - w serwlecie przentacji (cały cykl od wykonania cmd do poazania wyników jest sekcją krytyczną)

        Lock mainLock = new ReentrantLock();

        mainLock.lock();
        // wykonanie
        System.out.println("Wyszukiwanie...");
        command.execute();
        System.out.println("Zakończono wyszukiwanie");

        // pobranie wyników
        List results = (List) command.getResults();
        System.out.println("Pobrano listę wyników");

        // Pobranie i zapamiętanie kodu wyniku (dla servletu prezentacji)
        ses.setAttribute("StatusCode", new Integer(command.getStatusCode()));

        System.out.println("Ustawiono status code w obiekcie sesji");
        // Wyniki - będą dostępne jako atrybut sesji
        ses.setAttribute("Results", results);
        System.out.println("Ustawiono wynik w obiekcie sesji");
        ses.setAttribute("Lock", mainLock);    // zapiszmy lock, aby mozna go było otworzyć później
        System.out.println("Przekazano lock do obiektu sesji");


        // Wywołanie serwletu prezentacji
        System.out.println("Wywołanie servletu prezentacji wyników");
        disp = context.getRequestDispatcher(presentationServ);
        disp.forward(req, resp);
    }


    public void doGet(HttpServletRequest request,
                      HttpServletResponse response)
            throws ServletException, IOException
    {
        serviceRequest(request, response);
    }

    public void doPost(HttpServletRequest request,
                       HttpServletResponse response)
            throws ServletException, IOException
    {
        serviceRequest(request, response);
    }

}
