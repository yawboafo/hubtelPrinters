package com.hubtel.hubtelprinters.Delegates;

import com.hubtel.hubtelprinters.printerCore.Communication;
import com.hubtel.hubtelprinters.receiptbuilder.HubtelDeviceInfo;

public interface PrintingTaskDelegate {

    void printingTaskBegan(HubtelDeviceInfo deviceInfo);
    void printingTaskFailed(HubtelDeviceInfo deviceInfo,String error);
    void printingTaskCompleted(HubtelDeviceInfo deviceInfo,Boolean results);
    void printingTaskCompleted(HubtelDeviceInfo deviceInfo,String results);
    void printingTaskFailed(String error);
    void cashDrawertatusReport(Communication.Result result);

}
