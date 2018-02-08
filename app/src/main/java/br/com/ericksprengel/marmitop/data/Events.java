package br.com.ericksprengel.marmitop.data;


import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

import java.util.List;

@Root(strict = false)
public class Events {
    @ElementList(name = "event", required = true, inline = true)
    public List<Event> eventList;

}
