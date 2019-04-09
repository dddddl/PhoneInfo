package com.lx.qz.transform;

public interface Transform<T, M> {

    T map(byte[] bytes, int bytesRead) throws MessageException;

    byte[] parse(M m);

}
