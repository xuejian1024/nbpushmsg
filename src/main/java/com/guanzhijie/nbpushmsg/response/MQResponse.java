package com.guanzhijie.nbpushmsg.response;

import com.guanzhijie.nbpushmsg.entity.NBCamera;

import java.io.Serializable;

public class MQResponse implements Serializable {

    public String imei;
    public String datetime;
    public String type;//事件类型：02:报警 03:报警恢复
    public String status;//门磁状态：00:门窗已关 01:门窗已打开

    public NBCamera nbCamera;


    public MQResponse(){}
    public MQResponse(String imei,String datetime, String type, String status, NBCamera nbCamera) {
        this.imei = imei;
        this.datetime = datetime;
        this.type = type;
        this.status = status;
        this.nbCamera = nbCamera;
    }

    public String getDatetime() {
        return datetime;
    }

    public void setDatetime(String datetime) {
        this.datetime = datetime;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public NBCamera getNbCamera() {
        return nbCamera;
    }

    public void setNbCamera(NBCamera nbCamera) {
        this.nbCamera = nbCamera;
    }

    public String getImei() {
        return imei;
    }

    public void setImei(String imei) {
        this.imei = imei;
    }

    @Override
    public String toString() {
        return "MQResponse{" +
                "imei='" + imei + '\'' +
                ", datetime='" + datetime + '\'' +
                ", type='" + type + '\'' +
                ", status='" + status + '\'' +
                ", nbCamera=" + nbCamera +
                '}';
    }
}