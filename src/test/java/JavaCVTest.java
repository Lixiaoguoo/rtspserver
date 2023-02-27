import java.time.LocalDateTime;

public class JavaCVTest {

    public static void main(String[] args) {
        int a = 47546;
        System.out.println("原数据:" + String.format("%32s", Integer.toBinaryString(a)).replaceAll("\\s", "0"));
        int i = a >> 28;
        int hight4 = i & 0x0f;
        System.out.println("取高4位:"+ Integer.toBinaryString(hight4));
        i = i >> 24;
        int hight8 = i & 0xff;
        System.out.println("取高8位:"+ Integer.toBinaryString(hight8));

        int low4 = a & 0x0f;
        System.out.println("取低4位:"+ Integer.toBinaryString(low4));
        int low6 = a & 0x3f;
        System.out.println("取低6位:"+ Integer.toBinaryString(low6));
        int low8 = a & 0xff;
        System.out.println("取低8位:"+ Integer.toBinaryString(low8));
    }

}
