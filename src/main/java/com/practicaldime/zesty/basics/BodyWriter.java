package com.practicaldime.zesty.basics;

public interface BodyWriter<T> {
    
    byte[] transform(T object);
}
