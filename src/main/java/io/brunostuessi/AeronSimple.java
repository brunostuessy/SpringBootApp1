package io.brunostuessi;

import io.aeron.Aeron;
import io.aeron.Publication;
import io.aeron.Subscription;
import io.aeron.driver.MediaDriver;
import io.aeron.logbuffer.FragmentHandler;
import org.agrona.concurrent.IdleStrategy;
import org.agrona.concurrent.SleepingIdleStrategy;
import org.agrona.concurrent.UnsafeBuffer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteBuffer;

public class AeronSimple {

    private static Logger LOG = LoggerFactory.getLogger(AeronSimple.class);

    public static void main(String[] args) {
        final String channel = "aeron:ipc";
        final String message = "my message";
        final IdleStrategy idle = new SleepingIdleStrategy();
        final UnsafeBuffer unsafeBuffer = new UnsafeBuffer(ByteBuffer.allocate(256));

        var context = new MediaDriver.Context()
                //.threadingMode(ThreadingMode.SHARED)
                //.dirDeleteOnStart(true)
                //.aeronDirectoryName(aeronDir.toString)

                // openshift issue
                // insufficient usable storage for new log of length=
                // 201330688 usable=
                //  58699776 in /dev/shm (shm)
                .publicationTermBufferLength(
                    33554432)
                .ipcTermBufferLength(
                    33554432);
        try (MediaDriver driver = MediaDriver.launch(context);
             Aeron aeron = Aeron.connect();
             Subscription sub = aeron.addSubscription(channel, 10);
             Publication pub = aeron.addPublication(channel, 10))
        {
            while (!pub.isConnected())
            {
                idle.idle();
            }
            unsafeBuffer.putStringAscii(0, message);
            LOG.info("sending:" + message);
            while (pub.offer(unsafeBuffer) < 0)
            {
                idle.idle();
            }
            FragmentHandler handler = (buffer, offset, length, header) ->
                    LOG.info("received:" + buffer.getStringAscii(offset));
            while (sub.poll(handler, 1) <= 0)
            {
                idle.idle();
            }
        }
    }

}
