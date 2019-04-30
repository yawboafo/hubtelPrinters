package com.hubtel.hubtelprinters.Delegates;

import com.hubtel.hubtelprinters.receiptbuilder.HubtelDeviceInfo;

public interface PrintingTaskDelegate {

    void printingTaskBegan(HubtelDeviceInfo deviceInfo);
    void printingTaskFailed(HubtelDeviceInfo deviceInfo,String error);
    void printingTaskCompleted(HubtelDeviceInfo deviceInfo,Boolean results);
    void printingTaskFailed(String error);
}
