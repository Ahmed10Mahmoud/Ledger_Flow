package com.ahmed.demo.api.filter;

import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.WriteListener;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpServletResponseWrapper;

public class CachedBodyHttpServletResponse extends HttpServletResponseWrapper {
    private final java.io.ByteArrayOutputStream buffer = new java.io.ByteArrayOutputStream();

    public CachedBodyHttpServletResponse(HttpServletResponse response) {
        super(response);
    }

    @Override
    public ServletOutputStream getOutputStream() {
        return new ServletOutputStream() {
            @Override
            public void write(int b) {
                buffer.write(b);
            }

            @Override public boolean isReady() { return true; }
            @Override public void setWriteListener(WriteListener writeListener) {}
        };
    }

    public String getBody() {
        return buffer.toString();
    }
}
