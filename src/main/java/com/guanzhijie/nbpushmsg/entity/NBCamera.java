package com.guanzhijie.nbpushmsg.entity;

import java.io.Serializable;

public class NBCamera implements Serializable {

    private String nb_device_id;//门磁设备IMEI
    private String nb_device_name;
    private String camera_ip;
    private String camera_port;
    private String camera_id;

    public String getNb_device_id() {
        return nb_device_id;
    }

    public void setNb_device_id(String nb_device_id) {
        this.nb_device_id = nb_device_id;
    }

    public String getNb_device_name() {
        return nb_device_name;
    }

    public void setNb_device_name(String nb_device_name) {
        this.nb_device_name = nb_device_name;
    }

    public String getCamera_ip() {
        return camera_ip;
    }

    public void setCamera_ip(String camera_ip) {
        this.camera_ip = camera_ip;
    }

    public String getCamera_port() {
        return camera_port;
    }

    public void setCamera_port(String camera_port) {
        this.camera_port = camera_port;
    }

    public String getCamera_id() {
        return camera_id;
    }

    public void setCamera_id(String camera_id) {
        this.camera_id = camera_id;
    }

    @Override
    public String toString() {
        return "NBCamera{" +
                "nb_device_id='" + nb_device_id + '\'' +
                ", nb_device_name='" + nb_device_name + '\'' +
                ", camera_ip='" + camera_ip + '\'' +
                ", camera_port='" + camera_port + '\'' +
                ", camera_id='" + camera_id + '\'' +
                '}';
    }

    public NBCamera(String nb_device_id, String nb_device_name, String camera_ip, String camera_port, String camera_id) {
        this.nb_device_id = nb_device_id;
        this.nb_device_name = nb_device_name;
        this.camera_ip = camera_ip;
        this.camera_port = camera_port;
        this.camera_id = camera_id;
    }

    public NBCamera(){}


}
