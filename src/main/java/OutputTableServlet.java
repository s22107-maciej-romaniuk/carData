import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.locks.Lock;

public class OutputTableServlet extends HttpServlet {


    public void serviceRequest(HttpServletRequest req,
                               HttpServletResponse resp)
            throws ServletException, IOException
    {
        ServletContext context = getServletContext();

        // Włączenie strony generowanej przez serwlet pobierania parametrów
        // (formularz)
        String getParamsServ = context.getInitParameter("getParamsServ");
        RequestDispatcher disp = context.getRequestDispatcher(getParamsServ);
        disp.include(req,resp);

        // Uzyskanie wyników i wyprowadzenie ich
        // Controller po wykonaniu Command zapisał w atrybutach sesji
        // - referencje do listy wyników jako atrybut "Results"
        // - wartośc kodu wyniku wykonania jako atrybut "StatusCode"

        HttpSession ses = req.getSession();
        Lock mainLock = (Lock) ses.getAttribute("Lock");
        mainLock.unlock();
        List results = (List) ses.getAttribute("Results");
        Integer code = (Integer) ses.getAttribute("StatusCode");
        System.out.println("Status code: " + code);

        PrintWriter out = resp.getWriter();
        out.println("<hr>");

        // Uzyskanie napisu właściwego dla danego "statusCode"
        String msg = BundleInfo.getStatusMsg()[code.intValue()];
        out.println("<h2>" + msg + "</h2>");

        // Elementy danych wyjściowych (wyników) mogą być
        // poprzedzane jakimiś opisami (zdefiniowanymi w ResourceBundle)
        String[] dopiski = BundleInfo.getResultDescr();

        // Generujemy raport z wyników
        out.println("<table style=\"border-collapse: collapse\">");
        String[] columnHeaders = BundleInfo.getResultDescr();
        out.println("<tr>");
        for(String columnHeader : columnHeaders){
            out.print("<th style=\"border: 1px solid black\">" + columnHeader + "</td>");
        }
        out.println("</tr>");
        for (Iterator iter = results.iterator(); iter.hasNext(); ) {
            out.println("<tr>");

            int dlen = dopiski.length;  // długość tablicy dopisków
            Object res = iter.next();

            if (res.getClass().isArray()) {  // jezeli element wyniku jest tablicą
                Object[] res1 = (Object[]) res;
                int i;
                for (i=0; i < res1.length; i++) {
                    out.print("<td style=\"border: 1px solid black\">" + res1[i] + "</td>");
                }
            }
            else {                                      // może nie być tablicą
                out.print("<td style=\"border: 1px solid black\">" + res + "</td>");
            }
            out.println("</tr>");
        }
        out.println("</table>");
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