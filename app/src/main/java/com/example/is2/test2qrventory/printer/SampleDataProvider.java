package com.example.is2.test2qrventory.printer;

import android.content.res.AssetManager;
import android.graphics.Bitmap;

import com.epson.lwprint.sdk.LWPrintDataProvider;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.content.Context;
import android.graphics.BitmapFactory;

/**
 * Created by Kevin on 11.06.2016.
 */
public class SampleDataProvider implements LWPrintDataProvider {

    private final String TAG = getClass().getSimpleName();

    private Context context;

    private static final String KEY_PREFFIX = "_CONTENTS";

    private String stringData = "String";
    private String qrCodeData = "QRCode";
    private String barcodeData = "Barcode";
    private String formName;

    InputStream formDataStringInputStream;
    InputStream formDataQRCodeInputStream;
    InputStream formDataBarcodeInputStream;

    List<Object> _formData = null;
    List<ContentsData> _contentsData = null;

    public String getFormName() {
        return formName;
    }

    public void setFormName(String formName) {
        this.formName = formName;
    }

    public String getStringData() {
        return stringData;
    }

    public void setStringData(String stringData) {
        this.stringData = stringData;
    }

    public String getQrCodeData() {
        return qrCodeData;
    }

    public void setQrCodeData(String qrCodeData) {
        this.qrCodeData = qrCodeData;
    }

    public String getBarcodeData() {
        return barcodeData;
    }

    public void setBarcodeData(String barcodeData) {
        this.barcodeData = barcodeData;
    }

    public void closeStreams() {
        if (formDataStringInputStream != null) {
            try {
                formDataStringInputStream.close();
            } catch (IOException e) {
                Logger.e(e.toString(), e);
            }
            formDataStringInputStream = null;
        }
        if (formDataQRCodeInputStream != null) {
            try {
                formDataQRCodeInputStream.close();
            } catch (IOException e) {
                Logger.e(e.toString(), e);
            }
            formDataQRCodeInputStream = null;
        }
        if (formDataBarcodeInputStream != null) {
            try {
                formDataBarcodeInputStream.close();
            } catch (IOException e) {
                Logger.e(e.toString(), e);
            }
        }
    }

    public SampleDataProvider(Context current) {
        this.context = current;
    }


    @Override
    public void startOfPrint() {
        // It is called only once when printing started
        Logger.d("startOfPrint");

        if ("Barcode.plist".equals(formName)) {
            AssetManager as = context.getResources().getAssets();

            // Contents data
            String contentsFileName = LWPrintSampleUtil.getPreffix(formName)
                    + KEY_PREFFIX + "." + LWPrintSampleUtil.getSuffix(formName);
            LWPrintContentsXmlParser xmlParser = new LWPrintContentsXmlParser();
            InputStream in = null;
            try {
                in = as.open(contentsFileName);
                //_contentsData = xmlParser.parse(in, "UTF-8");

                HashMap<String, String> elementMap = new HashMap<String, String>();
                String keyName = "Code-EAN8";
                String keyValue = "1234567";
                elementMap.put(keyName, keyValue);
                ContentsData objData = new ContentsData();
                objData.setElementMap(elementMap);
                _contentsData = new ArrayList<ContentsData>();
                _contentsData.add(objData);


            } catch (Exception e) {
                Logger.e(TAG, e.toString(), e);
            } finally {
                if (in != null) {
                    try {
                        in.close();
                    } catch (IOException e) {
                    }
                    in = null;
                }
            }
        }
    }

    @Override
    public void endOfPrint() {
        // It is called only once when printing finished
        Logger.d("endOfPrint");
    }

    @Override
    public void startPage() {
        // It is called when starting a page
        Logger.d("startPage");
    }

    @Override
    public void endPage() {
        // It is called when finishing a page
        Logger.d("endPage");
    }

    @Override
    public int getNumberOfPages() {
        // Return all pages printed

        if ("Barcode.plist".equals(formName)) {
            if (_contentsData == null) {
                Logger.d(TAG, "getNumberOfPages: 0");
                return 0;
            } else {
                Logger.d(TAG, "getNumberOfPages: "
                        + _contentsData.size());
                return _contentsData.size();
            }
        }

        return 1;

    }

    @Override
    public InputStream getFormDataForPage(int pageIndex) {
        // Return the form data for pageIndex page
        Logger.d("getFormDataForPage: pageIndex=" + pageIndex);

        InputStream formData = null;

        if ("Barcode.plist".equals(formName)) {
            if (formDataBarcodeInputStream != null) {
                try {
                    formDataBarcodeInputStream.close();
                } catch (IOException e) {
                    Logger.e(TAG, e.toString(), e);
                }
                formDataBarcodeInputStream = null;
            }
            try {
                AssetManager as = context.getResources().getAssets();
                formDataBarcodeInputStream = as.open(formName);
                formData = formDataBarcodeInputStream;
                Logger.d(TAG, "getFormDataForPage: " + formName + "=" + formDataBarcodeInputStream.available());
            } catch (IOException e) {
                Logger.e(TAG, e.toString(), e);
            }

            return formDataBarcodeInputStream;
        } else if ("QRCode.plist".equals(formName)) {
            Logger.d("QRCode: pageIndex=" + pageIndex);
            if (formDataQRCodeInputStream != null) {
                try {
                    formDataQRCodeInputStream.close();
                } catch (IOException e) {
                    Logger.e(e.toString(), e);
                }
                formDataQRCodeInputStream = null;
            }
            try {
                AssetManager as = context.getResources().getAssets();
                formDataQRCodeInputStream = as.open(formName);
                formData = formDataQRCodeInputStream;
                Logger.d("getFormDataForPage: " + formName + "=" + formDataQRCodeInputStream.available());
            } catch (IOException e) {
                Logger.e(e.toString(), e);
            }
        } else {
            Logger.d("String: pageIndex=" + pageIndex);
            if (formDataStringInputStream != null) {
                try {
                    formDataStringInputStream.close();
                } catch (IOException e) {
                    Logger.e(e.toString(), e);
                }
                formDataStringInputStream = null;
            }
            try {
                AssetManager as = context.getResources().getAssets();
                formDataStringInputStream = as.open(formName);
                formData = formDataStringInputStream;
                Logger.d("getFormDataForPage: " + formName + "=" + formDataStringInputStream.available()); //FORM_DATA_STRING
            } catch (IOException e) {
                Logger.e(e.toString(), e);
            }
        }

        return formData;
    }

    @Override
    public String getStringContentData(String contentName, int pageIndex) {
        // Return the data for the contentName of the pageIndex page
        Logger.d("getStringContentData: contentName=" + contentName
                + ", pageIndex=" + pageIndex);

        if ("String".equals(contentName)) {
            return stringData;
        } else if ("QRCode".equals(contentName)) {
            return qrCodeData;
        } else {
            String content = null;
            if (_contentsData != null) {
                int index = pageIndex - 1;
                ContentsData pageDictionary = _contentsData.get(index);
                content = pageDictionary.get(contentName);
            }
            return content;
        }
    }

    @Override
    public Bitmap getBitmapContentData(String contentName, int pageIndex) {
        // Return the data for the contentName of the pageIndex page
        Logger.d("getBitmapContentData: contentName=" + contentName
                + ", pageIndex=" + pageIndex);

        Bitmap bitmap = null;
        if (_contentsData != null) {
            int index = pageIndex - 1;
            ContentsData pageDictionary = _contentsData.get(index);
            String content = pageDictionary.get(contentName);
            InputStream is = null;
            try {
                is = context.getResources().getAssets().open(content);
                bitmap = BitmapFactory.decodeStream(is);
            } catch (IOException e) {
                Logger.e(TAG, e.toString(), e);
            } finally {
                if (is != null) {
                    try {
                        is.close();
                    } catch (IOException e) {
                    }
                    is = null;
                }
            }
        }
        return bitmap;
    }

}
