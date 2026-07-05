package com.github.duskbyte;

import javax.swing.*;
import java.lang.reflect.Method;

/**
 * 完整性检查：防止用户删除 {@code Warning} 类以移除免责声明后售卖客户端。
 * <p>
 * 若检测到 Warning类被改or删，直接崩溃JVM，别tm像启动
 */
public class IntegrityChecker {

    private static final Class<?>[] EXPECTED_PARAM_TYPES = {};
    private static final Class<?> EXPECTED_RETURN_TYPE = void.class;

    public static void check() {
        try {
            // 1. 检查Warning类存在
            Class<?> warningClass = Class.forName("com.github.duskbyte.Warning");

            // 检查show方法存在签名正确
            Method showMethod = warningClass.getDeclaredMethod("show", EXPECTED_PARAM_TYPES);
            if (showMethod.getReturnType() != EXPECTED_RETURN_TYPE) {
                fail("你tm改show了吧");
            }

            // 方法public static
            int modifiers = showMethod.getModifiers();
            if (!java.lang.reflect.Modifier.isPublic(modifiers) || !java.lang.reflect.Modifier.isStatic(modifiers)) {
                fail("改回去public static");
            }

            // 防止被替换为空的实现 查字节码
            byte[] bytecode = warningClass.getResourceAsStream("/com/github/duskbyte/Warning.class")
                    .readAllBytes();
            String bytecodeStr = new String(bytecode, java.nio.charset.StandardCharsets.ISO_8859_1);
            if (!bytecodeStr.contains("JOptionPane") || !bytecodeStr.contains("setAlwaysOnTop")) {
                fail("Warning.class bytecode tampered - JOptionPane reference missing");
            }

        } catch (ClassNotFoundException e) {
            fail("Warning class deleted! This client is free and open-source. Do NOT sell it.");
        } catch (NoSuchMethodException e) {
            fail("Warning.show() method deleted! Do NOT remove the disclaimer.");
        } catch (Exception e) {
            fail("Integrity check failed: " + e.getMessage());
        }
    }

    private static void fail(String message) {
        JOptionPane.showMessageDialog(
                null,
                "完整性检查失败了:\n" + message + "\n\n" +
                "这个客户端是免费的 别拿来卖\n" +
                "This client is FREE. Do NOT sell it.\n\n" +
                "程序要退出了",
                "INTEGRITY CHECK FAILED",
                JOptionPane.ERROR_MESSAGE
        );
        throw new RuntimeException("Integrity check failed: " + message);
    }
}
