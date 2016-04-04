package matheusfroes.oracode.models;

import java.io.Serializable;

/**
 * Created by mathe on 03/04/2016.
 */
public class Oracode {
    private int id;
    private String oracode;
    private String explanation;
    private String cause;
    private String action;

    public Oracode(String oracode, String explanation, String cause, String action) {
        this.oracode = oracode;
        this.explanation = explanation;
        this.cause = cause;
        this.action = action;
    }

    public String getOracode() {
        return oracode;
    }

    public void setOracode(String oracode) {
        this.oracode = oracode;
    }

    public String getExplanation() {
        return explanation;
    }

    public void setExplanation(String explanation) {
        this.explanation = explanation;
    }

    public String getCause() {
        return cause;
    }

    public void setCause(String cause) {
        this.cause = cause;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
