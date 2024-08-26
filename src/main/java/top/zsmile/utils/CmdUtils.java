package top.zsmile.common.core.utils;

import java.util.List;

/**
 * @author: B.Smile
 * @Date: 2021/11/1 13:00
 * @Description: 指令工具
 */
public class CmdUtils {

    /**
     * 16进制补位
     *
     * @param hex  补位字符串
     * @param len  长度
     * @param type true 后补0，false 前补0
     * @return
     */
    public static String hexNumberFormat(String hex, int len, boolean type) {
        StringBuilder sb = new StringBuilder();
        int j = 0;
        while (j < len - hex.length()) {
            sb.append("0");
            j++;
        }
        if (type) {
            sb.append(hex);
        } else {
            sb.insert(0, hex);
        }
        return sb.toString();
    }

    /**
     * 2进制转16进制，补位
     *
     * @param bin
     * @return
     */
    public static String binaryToHexStringFormat(String bin, int len) {
        Integer i = Integer.parseInt(bin, 2);
        String hexString = i.toHexString(i);
        StringBuilder sb = new StringBuilder();
        int j = 0;
        while (j < len - hexString.length()) {
            sb.append("0");
            j++;
        }
        sb.append(hexString);
        return sb.toString();
    }

    /**
     * 字节数组转16进制字符串
     *
     * @param src
     * @return
     */
    public static String bytesToHexString(byte[] src) {
        StringBuilder stringBuilder = new StringBuilder("");
        if (src == null || src.length <= 0) {
            return "";
        }
        for (int i = 0; i < src.length; i++) {
            int v = src[i] & 0xFF;
            String hv = Integer.toHexString(v);
            if (hv.length() < 2) {
                stringBuilder.append(0);
            }
            stringBuilder.append(hv);
        }
        return stringBuilder.toString().toUpperCase();
    }


    /**
     * 2进制转16进制
     *
     * @param bin
     * @return
     */
    public static String binaryToHexString(String bin) {
        Integer i = Integer.parseInt(bin, 2);
        String hexString = i.toHexString(i);
        return hexString;
    }


    /**
     * 将16进制字符串转换为byte[]
     *
     * @param str
     * @return
     */
    public static byte[] toBytes(String str) {
        if (str == null || str.trim().equals("")) {
            return new byte[0];
        }

        byte[] bytes = new byte[str.length() / 2];
        for (int i = 0; i < str.length() / 2; i++) {
            String subStr = str.substring(i * 2, i * 2 + 2);
            bytes[i] = (byte) Integer.parseInt(subStr, 16);
        }
        return bytes;
    }


    /**
     * 16进制字符串反转
     *
     * @param str
     * @return
     */
    public static String reverseByHex(String str) {
        StringBuffer strBuffer = new StringBuffer("");
        for (int i = str.length(); i > 0; i = i - 2) {
            strBuffer.append(str.substring(i - 2, i));
        }
        return strBuffer.toString();
    }

    /**
     * 获取异或迭代校验位
     */
    public static String XOR_Encryption(List<Integer> dat) {
        int res;
        StringBuilder sb = new StringBuilder();
        //数据校验
        res = dat.get(0);
        for (int i = 1; i < dat.size(); i++) {
            res = res ^ dat.get(i);
        }
        int v = res & 0xFF;
        String hv = Integer.toHexString(v);
        if (hv.length() < 2) {
            sb.append(0);
        }
        sb.append(hv);

        return sb.toString();
    }

    /**
     * 进行异或校验
     */
    public static Boolean XOR_decryption(String cmd) {
        int res;
        StringBuilder sb = new StringBuilder();
        StringBuilder sb2 = new StringBuilder();
        sb.append(cmd.substring(2, cmd.length() - 4));
        //数据校验
        res = Integer.parseInt(sb.substring(0, 2));
        for (int i = 2; i < sb.length(); i += 2) {
            res = res ^ Integer.parseInt(sb.substring(i, i + 2), 16);
        }
        int v = res & 0xFF;
        String hv = Integer.toHexString(v);
        if (hv.length() < 2) {
            sb2.append(0);
        }
        sb2.append(hv);
        if (sb2.toString().equals(cmd.substring(cmd.length() - 4, cmd.length() - 2))) {
            return true;
        } else {
            return false;
        }
    }


}
