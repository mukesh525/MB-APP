package vmc.mcube.in.fragment.followUp;

import java.util.ArrayList;

import vmc.mcube.in.fragment.track.OptionsData;

/**
 * Created by mukesh on 13/7/15.
 */

public class FollowUpDetailsData {
    private String Label;
    private String Name;
    private ArrayList<String> Options;
    private ArrayList<OptionsData> OptionsList;
    private String Type;
    private String Value;


    public FollowUpDetailsData(String label, String name, ArrayList<String> options, String type, String value) {
        Label = label;
        Name = name;
        Options = options;
        Type = type;
        Value = value;
    }

    public FollowUpDetailsData(String label, String name, ArrayList<String> options, String type, ArrayList<OptionsData> optionsList, String value) {
        Label = label;
        Name = name;
        Options = options;
        Type = type;
        OptionsList = optionsList;
        Value = value;
    }

    public String getLabel() {
        return Label;
    }

    public void setLabel(String label) {
        Label = label;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public ArrayList<String> getOptions() {
        return Options;
    }

    public void setOptions(ArrayList<String> options) {
        Options = options;
    }

    public ArrayList<OptionsData> getOptionsList() {
        return OptionsList;
    }

    public void setOptionsList(ArrayList<OptionsData> optionsList) {
        OptionsList = optionsList;
    }

    public String getType() {
        return Type;
    }

    public void setType(String type) {
        Type = type;
    }

    public String getValue() {
        return Value;
    }

    public void setValue(String value) {
        Value = value;
    }
}
