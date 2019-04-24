package com.hubtel.hubtelprinters;

import com.starmicronics.stario.PortInfo;
import com.starmicronics.stario.StarIOPort;

import java.util.List;

public interface PrinterManagerDelegate {

    void printerSearchBegan();
    void printerSearchSuccess(List<HubtelDeviceInfo> hubtelDeviceInfoList);
    void printerSearchFailed(String error);
    void printerSearchReturnZeroResults();



    void printerConnectionBegan();
    void printerConnectionSuccess(StarIOPort port);
    void printerConnectionFailed(String withError);
    void printingStatusReport(Communication.Result communicateResult);
    void cashDrawertatusReport(Communication.Result communicateResult);

}
