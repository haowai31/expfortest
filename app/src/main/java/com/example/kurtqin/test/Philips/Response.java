package com.example.kurtqin.test.Philips;

public class Response {
    private final int mError;
    private final String mResponse;
    private final ResponseHandler mResponseHandler;

    public Response(String arg1, int arg2, ResponseHandler arg3) {
        super();
        this.mResponse = arg1;
        this.mError = arg2;
        this.mResponseHandler = arg3;
    }

    public String getResponseMessage() {
        return this.mResponse;
    }

    public void notifyResponseHandler() {
        if(this.mError != 0) {
            this.mResponseHandler.onError(this.mError, this.mResponse);
        }
        else {
            this.mResponseHandler.onSuccess(this.mResponse);
        }
    }
}
