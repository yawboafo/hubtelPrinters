package com.hubtel.hubtelprinters.Delegates;

import com.hubtel.hubtelprinters.receiptbuilder.HubtelDeviceInfo;

public interface PrinterTaskDelegate {

    void printingTaskBegan(HubtelDeviceInfo deviceInfo);
    void printingTaskFailed(HubtelDeviceInfo deviceInfo,String error);
    void printingTaskCompleted(HubtelDeviceInfo deviceInfo);
    void printingTaskFailed(String error);
}
