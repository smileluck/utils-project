package top.zsmile.utils.uuid;

import java.util.concurrent.ThreadLocalRandom;

public class FlexID {

    private static final long INITIAL_TIMESTAMP = 1680411660000L;
    private static final long MAX_CLOCK_SEQ = 99;

    private long lastTimeMillis = 0;//最后一次生成 ID 的时间
    private long clockSeq = 0;      //时间序列
    private long workId = 1;        //机器 ID

    public FlexID() {
    }

    public FlexID(long workId) {
        this.workId = workId;
    }

    public synchronized long nextId() {

        //当前时间
        long currentTimeMillis = System.currentTimeMillis();

        if (currentTimeMillis == lastTimeMillis) {
            clockSeq++;
            if (clockSeq > MAX_CLOCK_SEQ) {
                clockSeq = 0;
                currentTimeMillis++;
            }
        }

        //出现时间回拨
        else if (currentTimeMillis < lastTimeMillis) {
            currentTimeMillis = lastTimeMillis;
            clockSeq++;

            if (clockSeq > MAX_CLOCK_SEQ) {
                clockSeq = 0;
                currentTimeMillis++;
            }
        } else {
            clockSeq = 0;
        }

        lastTimeMillis = currentTimeMillis;

        long diffTimeMillis = currentTimeMillis - INITIAL_TIMESTAMP;

        //ID组成：时间（7+）| 毫秒内的时间自增 （00~99：2）| 机器ID（00 ~ 99：2）| 随机数（00~99：2）
        return diffTimeMillis * 1000000 + clockSeq * 10000 + workId * 100 + getRandomInt();
    }


    private int getRandomInt() {
        return ThreadLocalRandom.current().nextInt(100);
    }

    public static void main(String[] args) {
        FlexID flexID = new FlexID(1);
        System.out.println("start:" + System.currentTimeMillis());
        for (int i = 0; i < 100000; i++) {
            long l = flexID.nextId();
            System.out.println(l);
        }
        System.out.println("end:" + System.currentTimeMillis());
    }
}
