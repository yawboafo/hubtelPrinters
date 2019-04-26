package com.hubtel.hubtelprinters;

import com.hubtel.hubtelprinters.receiptbuilder.HubtelDeviceInfo;

public interface PrinterConnectionDelegate {

    void printerConnectionBegan(HubtelDeviceInfo deviceInfo);
    void printerConnectionFailed(HubtelDeviceInfo deviceInfo,String error);
    void printerConnectionSuccess(HubtelDeviceInfo deviceInfo);
    void printerConnectionFailed(String error);

}
