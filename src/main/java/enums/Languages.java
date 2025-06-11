package enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Languages {
    english("en"),
    polish("pl"),
    ukrainian("ua");
   final String localization;
    public  static  Languages get(String localization){
        for (Languages l: values()){
            if(l.getLocalization().equalsIgnoreCase(localization)) return  l;
        }
        return null;
    }

}
