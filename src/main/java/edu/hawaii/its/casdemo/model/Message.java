package edu.hawaii.its.casdemo.model;

import java.io.Serial;
import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "message")
public class Message implements Serializable {

    @Serial
    private static final long serialVersionUID = 2L;

    public static final int JUMBOTRON_MESSAGE = 1;
    public static final int GATE_MESSAGE = 2;
    public static final int ACCESS_DENIED_MESSAGE = 3;
    public static final int STAFF_GATE_MESSAGE = 5;
    public static final int FACULTY_GATE_MESSAGE = 6;
    public static final int DUAL_USER_MESSAGE = 7;

    private Integer id;
    private Integer typeId;
    private String text = "";
    private String enabled;

    // Constructor.
    public Message() {
        // Empty.
    }

    @Id
    @Column(name = "MSG_ID")
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @Column(name = "MSG_TEXT")
    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    @Column(name = "MSG_ENABLED", columnDefinition = "char")
    public String getEnabled() {
        return enabled;
    }

    public void setEnabled(String enabled) {
        this.enabled = enabled;
    }

    @Column(name = "MSG_TYPE_ID")
    public Integer getTypeId() {
        return typeId;
    }

    public void setTypeId(Integer typeId) {
        this.typeId = typeId;
    }

    @Override
    public String toString() {
        return "Message [id=" + id
                + ", typeId=" + typeId
                + ", enabled=" + enabled
                + ", text=" + text
                + "]";
    }

}
