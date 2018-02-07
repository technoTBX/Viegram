package com.relinns.viegram.network;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okio.Buffer;
import okio.BufferedSink;
import okio.ForwardingSink;
import okio.Okio;
import okio.Sink;

/**
 * Created by admin on 01-08-2017.
 */

public class ProgressRequestBody extends RequestBody {


    private RequestBody mDelegate;
    private Listener listenerL;
    private CountingSink mCountingSink;

    public ProgressRequestBody(RequestBody body, final Listener listener) {
        mDelegate = body;
        listenerL = listener;

    }

    @Override
    public MediaType contentType() {

        return mDelegate.contentType();
    }

    @Override
    public long contentLength()  {
        try {
            return mDelegate.contentLength();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return -1;    }

    @Override
    public void writeTo(BufferedSink sink) throws IOException {
        mCountingSink = new CountingSink(sink);
        BufferedSink bufferedSink = Okio.buffer(mCountingSink);
        mDelegate.writeTo(bufferedSink);
        bufferedSink.flush();
    }
    protected final class CountingSink extends ForwardingSink {
        private long bytesWritten = 0;
        public CountingSink(Sink delegate) {
            super(delegate);
        }
        @Override
        public void write(Buffer source, long byteCount) throws IOException {
            super.write(source, byteCount);
            bytesWritten += byteCount;
            listenerL.onProgress((int) (100F * bytesWritten / contentLength()));
        }
    }
    public interface Listener {
        void onProgress(int progress);
    }
}
