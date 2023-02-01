package com.ub.conscious.entity;

import java.io.Serializable;

public class Announcement implements Serializable {
    private Long id;
    private String event;
    private String title;
    private String latitude;
    private String longitude;
    private String city;
    private Long publisherId;
    private boolean isBanned;

    public Announcement() {
    }

    public Announcement(Long id, String event, String title, String latitude, String longitude, String city, Long publisherId, boolean isBanned) {
        this.id = id;
        this.event = event;
        this.title = title;
        this.latitude = latitude;
        this.longitude = longitude;
        this.city = city;
        this.publisherId = publisherId;
        this.isBanned = isBanned;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEvent() {
        return event;
    }

    public void setEvent(String event) {
        this.event = event;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public Long getPublisherId() {
        return publisherId;
    }

    public void setPublisherId(Long publisherId) {
        this.publisherId = publisherId;
    }

    public boolean isBanned() {
        return isBanned;
    }

    public void setBanned(boolean banned) {
        isBanned = banned;
    }
}
