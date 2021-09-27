package com.blockset.ui.parser.domain;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.List;

@XmlRootElement(name = "bml")
public class BML {

    @XmlElement(name = "model")
    private Model model = new Model();
    @XmlElement(name = "location")
    private List<Location> locations = new ArrayList<>();

    public BML() {  }

    public Model getttModel() {
        return model;
    }

    public List<Location> getttLocations() {
        return locations;
    }

    public BML addLocation(String base, String template) {
        Location location = new Location(base, template);
        locations.add(location);
        return this;
    }
    public void addLocation(String base){
        locations.add(new Location(base));
    }

    public void addLocation(Location location){
        locations.add(location);
    }
    public Location getLocation(int i) {
        return locations.get(i);
    }

    public void setModel(Model model) {
        this.model = model;
    }
}
