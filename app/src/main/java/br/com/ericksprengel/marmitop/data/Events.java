package br.com.ericksprengel.marmitop.data;


import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;
import org.simpleframework.xml.Text;

import java.util.List;

@Root(strict = false) // only works in non-strict mode
public class Events {
    @ElementList(name = "event", required = true, inline = true)
    public List<Event> eventList;

}
