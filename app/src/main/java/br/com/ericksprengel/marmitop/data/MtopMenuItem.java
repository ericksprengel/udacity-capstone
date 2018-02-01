package br.com.ericksprengel.marmitop.data;

import java.util.List;
import java.util.Map;

public class MtopMenuItem {

    private String key;

    private String name;
    private String description;
    private Map<String, Option> options;

    public static class Option {
        private String name;
        private float price;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public float getPrice() {
            return price;
        }

        public void setPrice(float price) {
            this.price = price;
        }
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Map<String, Option> getOptions() {
        return options;
    }

    public void setOptions(Map<String, Option> options) {
        this.options = options;
    }
}
