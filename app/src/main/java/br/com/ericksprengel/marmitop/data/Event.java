package br.com.ericksprengel.marmitop.data;


import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;
import org.simpleframework.xml.Text;

@Root(strict = false) // only works in non-strict mode
public class Event {
    @Element(name="date", required = false)
    public String date;
    @Element(name="name", required = false)
    public String name;
    @Element(name="description", required = false)
    public String description;
}
