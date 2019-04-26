package com.hubtel.hubtelprinters;

import com.hubtel.hubtelprinters.receiptbuilder.HubtelDeviceInfo;

import java.util.List;

public interface PrinterSeachDelegate {

    void printerSearchBegan(HubtelDeviceInfo deviceInfo);
    void printerSearchFailed(HubtelDeviceInfo deviceInfo,String error);
    void printerSearchCompleted(List<HubtelDeviceInfo> devices);
    void printerSearchFailed(String error);
}
