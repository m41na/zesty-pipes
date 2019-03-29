package com.practicaldime.zesty.basics;

public interface BodyReader<T> {
    
    T transform(String type, byte[] bytes);
}
