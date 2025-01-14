/*
 * Copyright 2022 DeNA Co., Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package packetproxy.quic.service.pnspace.level;

import lombok.Getter;
import packetproxy.quic.service.connection.Connection;
import packetproxy.quic.service.frame.FramesBuilder;
import packetproxy.quic.service.packet.QuicPacketBuilder;
import packetproxy.quic.service.pnspace.PnSpace;
import packetproxy.quic.utils.Constants;
import packetproxy.quic.value.frame.AckFrame;
import packetproxy.quic.value.frame.Frame;

import java.util.ArrayList;
import java.util.List;

import static packetproxy.quic.service.handshake.HandshakeState.State.AckReceived;

@Getter
public class HandshakePnSpace extends PnSpace {

    public HandshakePnSpace(Connection conn) {
        super(conn);
    }

    @Override
    public void OnAckReceived(AckFrame ackFrame) {
        super.conn.getHandshakeState().transit(AckReceived);
        super.OnAckReceived(ackFrame);
    }

    @Override
    public List<QuicPacketBuilder> getAndRemoveSendFramesAndConvertPacketBuilders() {
        List<QuicPacketBuilder> builders = new ArrayList<>();
        for (Frame frame: sendFrameQueue.pollAll()) {
            byte[] payload = new FramesBuilder().add(frame).getBytes();
            builders.add(QuicPacketBuilder.getBuilder()
                    .setPnSpaceType(Constants.PnSpaceType.PnSpaceHandshake)
                    .setPacketType(Constants.QuicPacketType.PacketHandshake)
                    .setPacketNumber(this.nextPacketNumber)
                    .setPayload(payload));
            this.nextPacketNumber = this.nextPacketNumber.plus(1);
        }
        return builders;
    }

}
