package com.example.metalconstructionsestimates.printings;

import android.content.Context;
import android.os.Bundle;
import android.os.CancellationSignal;
import android.os.ParcelFileDescriptor;
import android.print.PageRange;
import android.print.PrintAttributes;
import android.print.PrintDocumentAdapter;
import android.print.PrintDocumentInfo;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

public class PdfPrintAdapter extends PrintDocumentAdapter {

    private String path;

    public PdfPrintAdapter(Context context, String path) {
        this.path = path;
    }

    @Override
    public void onLayout(PrintAttributes oldAttributes,
                         PrintAttributes newAttributes,
                         CancellationSignal cancellationSignal,
                         LayoutResultCallback callback,
                         Bundle extras) {

        if (cancellationSignal.isCanceled()) {
            callback.onLayoutCancelled();
            return;
        }

        PrintDocumentInfo info = new PrintDocumentInfo
                .Builder("estimate.pdf")
                .setContentType(PrintDocumentInfo.CONTENT_TYPE_DOCUMENT)
                .setPageCount(PrintDocumentInfo.PAGE_COUNT_UNKNOWN)
                .build();

        callback.onLayoutFinished(info, true);
    }

    @Override
    public void onWrite(PageRange[] pages,
                        ParcelFileDescriptor destination,
                        CancellationSignal cancellationSignal,
                        WriteResultCallback callback) {

        try {
            FileInputStream input = new FileInputStream(new File(path));
            FileOutputStream output =
                    new FileOutputStream(destination.getFileDescriptor());

            byte[] buf = new byte[1024];
            int size;

            while ((size = input.read(buf)) > 0) {
                output.write(buf, 0, size);
            }

            callback.onWriteFinished(new PageRange[]{PageRange.ALL_PAGES});

        } catch (Exception e) {
            callback.onWriteFailed(e.toString());
        }
    }
}