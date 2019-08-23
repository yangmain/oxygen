/*
 * Copyright (C) 2019 justlive1
 *
 *  Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License. You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software distributed under the License
 *  is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 *  or implied. See the License for the specific language governing permissions and limitations under
 *  the License.
 */

package vip.justlive.oxygen.core.net.aio.core;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;

/**
 * 写操作worker
 *
 * @author wubo
 */
public class WriteWorker extends AbstractWorker<Object> {

  private final AioHandler aioHandler;
  private final Semaphore semaphore = new Semaphore(1);

  WriteWorker(ChannelContext channelContext) {
    super(channelContext);
    this.aioHandler = channelContext.getGroupContext().getAioHandler();
  }

  @Override
  public void handle(List<Object> data) {
    if (stopped) {
      return;
    }

    List<ByteBuffer> buffers = new ArrayList<>(data.size());
    for (Object obj : data) {
      ByteBuffer buffer = aioHandler.encode(obj, channelContext);
      if (!buffer.hasRemaining()) {
        buffer.flip();
      }
      buffers.add(buffer);
    }

    try {
      semaphore.acquire();
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    if (channelContext.isClosed()) {
      semaphore.release();
      return;
    }

    try {
      channelContext.getChannel()
          .write(Utils.composite(buffers), semaphore, channelContext.getWriteHandler());
    } catch (Exception e) {
      semaphore.release();
      throw e;
    }
  }
}