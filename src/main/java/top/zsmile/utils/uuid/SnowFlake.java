package top.zsmile.utils.uuid;

/**
 * @author: B.Smile
 * @Date: 2021/11/1 13:00
 * @Description: 雪花算法
 */
public class SnowFlake {

    /**
     * 0 | 0001100 10100010 10111110 01011100 00 | 10001 | 11001 | 0000 00000000
     * 第一个部分， 1个bit  : 0 ，无意义。二进制中第一位表示正负
     * 第二个部分， 41个bit : 时间戳，单位是毫秒。 可以标识2^41-1个毫秒值，换算成年为69年的时间
     * 第三个部分， 5个bit  : 表示数据中心id 10001
     * 第四个部分， 5个bit  : 表示机器id 11001
     * 第五个部分， 12个bit : 表示的序号，就是某个机房某台机器上这1毫秒同时生成的id的序号。12 bit 可以代表的最大正整数是 2 ^ 12 - 1 = 4096
     */


    // 第二部分。设置一个时间初始值    2^41 - 1   差不多可以用69年
    private long twepoch = 1585644268888L;

    // 第三部分。数据中心
    private final long dataCenterIdBits = 5L;
    private final long maxDataCenterId = -1L ^ (-1L << dataCenterIdBits);

    // 第四部分。机器id
    private final long workerIdBits = 5L;
    private final long maxWorkerId = -1L ^ (-1L << workerIdBits);

    // 第五部分。序号
    private final long sequenceBits = 12L;
    private final long sqeuenceMask = -1L ^ (-1L << sequenceBits);

    // 偏移量
    //工作id需要左移的位数，12位
    private long workerIdShift = sequenceBits;
    //数据id需要左移位数 12+5=17位
    private long dataCenterIdShift = sequenceBits + workerIdBits;
    //时间戳需要左移位数 12+5+5=22位
    private long timestampLeftShift = sequenceBits + workerIdBits + dataCenterIdBits;

    // 本地信息
    private long dataCenterId;
    private long workerId;
    private long sequence = 0L;
    private long lastTimestamp = -1L;


    public SnowFlake(long workerId, long dataCenterId) {
        if (workerId > maxWorkerId || workerId < 0) {
            throw new IllegalArgumentException(String.format("worker Id can't be greater than %d or less than 0", maxWorkerId));
        }
        if (dataCenterId > maxDataCenterId || dataCenterId < 0) {
            throw new IllegalArgumentException(String.format("dataCenter Id can't be greater than %d or less than 0", maxDataCenterId));
        }
        this.workerId = workerId;
        this.dataCenterId = dataCenterId;
    }

    public synchronized long nextId() {
        long timestamp = System.currentTimeMillis();
        if (timestamp < lastTimestamp) {
            throw new RuntimeException(String.format("Clock moved backwards.  Refusing to generate id for %d milliseconds", lastTimestamp - timestamp));
        }
        if (lastTimestamp == timestamp) {
            sequence = (sequence + 1) & sqeuenceMask;
            /**
             * 当sequence为4096时进入循环，因为4096的二进制为：1000000000000；4095的二进制为：0111111111111
             * 进行按位与时，不同为1，则为0。得出结果为0;
             */
            if (sequence == 0) {
                timestamp = tilNextMillis(lastTimestamp);
            }
        } else {
            sequence = 0L;
        }
        lastTimestamp = timestamp;
        /**
         * (timestamp - twepoch) << timestampLeftShift) 表示将时间戳减去初始时间戳，再左移相应位数。这时的时间相当于1970年算起。也可以不减去twepoch。直接使用现在时间
         * (datacenterId << datacenterIdShift) 表示将数据id左移相应位数
         * (workerId << workerIdShift) 表示将工作id左移相应位数
         * 因为每个部分都进行了偏移，也就是对应位置都填了0。这时通过|操作，能够将各部分的纸拼凑起来。
         */
        return ((timestamp - twepoch) << timestampLeftShift) |
                (dataCenterId << dataCenterIdShift) |
                (workerId << workerIdShift) |
                sequence;
    }

    protected long tilNextMillis(long lastTimestamp) {
        long timestamp = System.currentTimeMillis();
        while (timestamp <= lastTimestamp) {
            timestamp = System.currentTimeMillis();
        }
        return timestamp;
    }

    public static void main(String[] args) {
        SnowFlake snowFlake = new SnowFlake(1,2);
        System.out.println("start:" + System.currentTimeMillis());
        for (int i = 0; i < 10000000; i++) {
            long l = snowFlake.nextId();
        }
        System.out.println("end:" + System.currentTimeMillis());
    }

}
