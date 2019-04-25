package com.hubtel.hubtelprinters;

import com.hubtel.hubtelprinters.printerCore.Communication;
import com.hubtel.hubtelprinters.receiptbuilder.HubtelDeviceInfo;

import java.util.List;

public interface PrinterManagerDelegate {

    void printerSearchBegan();
    void printerSearchSuccess(List<HubtelDeviceInfo> hubtelDeviceInfoList);
    void printerSearchSuccess(HubtelDeviceInfo hubtelDeviceInfo);
    void printerSearchFailed(String error);
    void printerSearchReturnZeroResults();
    void printerConnectionBegan();
    void printerConnectionSuccess(HubtelDeviceInfo deviceInfo);
    void printerConnectionFailed(String withError);


    void cashDrawertatusReport(Communication.Result communicateResult);

    void printingCompletedResult(Communication.Result communicateResult);

    void printingCompletedResult(String communicateResult);
    void printingBegan(HubtelDeviceInfo deviceInfo);
    void printingFailed(String error );
}
