package com.lanxum.dstor.server.file;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import junit.framework.TestCase;

public class ConcurrentWriteTest extends TestCase {

    private static final long FILE_SIZE = 300000;
    private static final String FILE_NAME = "/tmp/bigfile";
    
	@Override
	protected void setUp() throws Exception
	{
        RandomAccessFile raf = new RandomAccessFile(FILE_NAME, "rw");
        raf.setLength(FILE_SIZE);
        raf.close();
	}

    @Override
    public void tearDown() throws Exception
    {
        File file = new File("/tmp/bigfile");
        file.delete();
    }

    public void testWrite() throws Exception
    {
        RandomAccessFile raf = new RandomAccessFile(FILE_NAME, "rw");
        FileChannel fc = raf.getChannel();
        MappedByteBuffer mb1 = fc.map(FileChannel.MapMode.READ_WRITE, 0, FILE_SIZE / 2);
        MappedByteBuffer mb2 = fc.map(FileChannel.MapMode.READ_WRITE, FILE_SIZE / 2, FILE_SIZE / 2);
        Thread t1 = new Thread(new ConcurrentWriteTestThread(mb1, FILE_SIZE / 2, 1));
        t1.start();
        Thread t2 = new Thread(new ConcurrentWriteTestThread(mb2, FILE_SIZE / 2, 2));
        t2.start();

        t1.join();
        t2.join();
        
        fc.close();
        raf.close();

        File file = new File(FILE_NAME);
        FileInputStream fis = new FileInputStream(file);
        byte[] data = new byte[(int) file.length()];
        fis.read(data);
        fis.close();

        ByteBuffer bb = ByteBuffer.wrap(data);
        for (long i = 0; i < file.length() * 8 / Long.SIZE; ++i) {
            assertEquals(i, bb.getLong());
        }
    }

    public class ConcurrentWriteTestThread implements Runnable
    {
        private MappedByteBuffer mappedBuffer;
        private long bufferSize;
        private int index;
        
        public ConcurrentWriteTestThread(MappedByteBuffer mb, long size, int i)
        {
            mappedBuffer = mb;
            bufferSize = size * 8 / Long.SIZE;
            index = i;
        }

        public void run() {
            for (long i = 0; i < bufferSize; ++i) {
                mappedBuffer.putLong((index - 1) * bufferSize + i);
            }
        }
    }

}
