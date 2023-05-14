package international;

import java.util.ListResourceBundle;

public class CarDataParamsDef_pl extends ListResourceBundle {
    public Object[][] getContents() {
        return contents;
    }

    static final Object[][] contents = {
            { "charset", "ISO-8859-2" },
            { "header", new String[] { "Wczytywanie danych samochodów z pliku" } },
            { "param_input", "Typ samochodu:" },
            { "submit", "Pokaż wyniki wyszukiwania" },
            { "footer", new String[] { } },
            { "resCode", new String[]
                    { "Znaleziono", "Brak danych", "Bład podczas odczytu pliku", "Nie znaleziono pasującego auta" }
            },
            { "resDescr",
                    new String[] { "typ", "marka", "rok produkcji", "kolor" } },
            { "resKeys",
                    new String[] { "rodzaj", "marka", "rokProdukcji", "kolor" } },
    };
}
