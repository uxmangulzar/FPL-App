package com.locksmith.fpl.Dentist.DTO;

import java.io.Serializable;

public class TicketDTO1 implements Serializable {
    String id = "";
    String user_id = "";
    String reason = "";
    String status = "";
    String craeted_at = "";

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCraeted_at() {
        return craeted_at;
    }

    public void setCraeted_at(String craeted_at) {
        this.craeted_at = craeted_at;
    }
}
