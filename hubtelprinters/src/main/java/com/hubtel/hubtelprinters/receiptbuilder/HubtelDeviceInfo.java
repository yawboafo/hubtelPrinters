package com.hubtel.hubtelprinters.receiptbuilder;

public class HubtelDeviceInfo {

    private int deviceType;
    private String target;
    private String deviceName;
    private String ipAddress;
    private String macAddress;
    private String bdAddress;
    private String portName;
    private String usbserialnumber;
    private String deviceManufacturer;


    public HubtelDeviceInfo(int deviceType, String target, String deviceName, String ipAddress, String macAddress, String bdAddress, String portName, String usbserialnumber, String deviceManufacturer) {
        this.deviceType = deviceType;
        this.target = target;
        this.deviceName = deviceName;
        this.ipAddress = ipAddress;
        this.macAddress = macAddress;
        this.bdAddress = bdAddress;
        this.portName = portName;
        this.usbserialnumber = usbserialnumber;
        this.deviceManufacturer = deviceManufacturer;
    }

    public HubtelDeviceInfo() {
    }

    public int getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(int deviceType) {
        this.deviceType = deviceType;
    }

    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public String getMacAddress() {
        return macAddress;
    }

    public void setMacAddress(String macAddress) {
        this.macAddress = macAddress;
    }

    public String getBdAddress() {
        return bdAddress;
    }

    public void setBdAddress(String bdAddress) {
        this.bdAddress = bdAddress;
    }

    public String getPortName() {
        return portName;
    }

    public void setPortName(String portName) {
        this.portName = portName;
    }

    public String getUsbserialnumber() {
        return usbserialnumber;
    }

    public void setUsbserialnumber(String usbserialnumber) {
        this.usbserialnumber = usbserialnumber;
    }


    public String getDeviceManufacturer() {
        return deviceManufacturer;
    }

    public void setDeviceManufacturer(String deviceManufacturer) {
        this.deviceManufacturer = deviceManufacturer;
    }



    public String getHumanReadableName(){

        return this.deviceManufacturer + " " + this.deviceName == null || this.deviceName.isEmpty() ? this.portName : this.deviceName;
    }
}


