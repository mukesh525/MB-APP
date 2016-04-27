package vmc.mcube.in.fragment.followUp;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mukesh on 10/7/15.
 */
public class DetailData {

    String label,name,type,value;
    ArrayList<Optionss> options;

    public List<Optionss> getOptions() {
        return options;
    }

    public void setOptions(ArrayList<Optionss> options) {
        this.options = options;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

}

