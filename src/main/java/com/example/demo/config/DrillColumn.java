package com.example.demo.config;

import java.io.Serializable;


public class DrillColumn implements Serializable {
    private static final long serialVersionUID = -8789471040491450517L;
    private String elementName;

    private String elementQueryName;

    private String elementValue;

    public String getElementName() {
        return elementName;
    }

    public void setElementName(String elementName) {
        this.elementName = elementName;
    }

    public String getElementQueryName() {
        return elementQueryName;
    }

    public void setElementQueryName(String elementQueryName) {
        this.elementQueryName = elementQueryName;
    }

    public String getElementValue() {
        return elementValue;
    }

    public void setElementValue(String elementValue) {
        this.elementValue = elementValue;
    }
}
