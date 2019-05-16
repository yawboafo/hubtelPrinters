package com.hubtel.hubtelprinters.Delegates;

import com.hubtel.hubtelprinters.receiptbuilder.HubtelDeviceInfo;

import java.util.List;

public interface PrinterSeachDelegate {

    void printerSearchBegan();
    void printerSearchFailed(String error);
    void printerSearchCompleted(List<HubtelDeviceInfo> devices);

}
