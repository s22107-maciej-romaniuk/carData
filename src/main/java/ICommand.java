import java.util.List;

public interface ICommand {
    void init();
    void setParameter(String name, Object value);
    Object getParameter(String name);
    void execute();
    List getResults();
    void setStatusCode(int code);
    int getStatusCode();
}
