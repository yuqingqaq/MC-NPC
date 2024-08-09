package util;

import java.io.File;

import java.io.InputStream;

public class ResourcePathUtil {
    public static InputStream getResourceAsStream(String relativePath) {
        // 使用当前线程的类加载器来加载资源
        return Thread.currentThread().getContextClassLoader().getResourceAsStream(relativePath);
    }
}