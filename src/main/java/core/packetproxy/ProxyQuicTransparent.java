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

package packetproxy;

import packetproxy.model.ListenPort;
import packetproxy.model.Servers;
import packetproxy.quic.service.connection.ClientConnection;
import packetproxy.quic.service.connection.ClientConnections;
import packetproxy.quic.service.connection.ServerConnection;
import packetproxy.quic.value.ConnectionIdPair;
import packetproxy.util.PacketProxyUtility;

public class ProxyQuicTransparent extends Proxy
{
	private ListenPort listen_info;
	private ClientConnections clientConnections;

	public ProxyQuicTransparent(ListenPort listenInfo) throws Exception {
		this.listen_info = listenInfo;
		this.clientConnections = new ClientConnections(listenInfo.getPort(), listenInfo.getCA().get());
	}
	
	@Override
	public void run() {
		try {
			while (true) {
				ClientConnection clientConnection = this.clientConnections.accept();
				PacketProxyUtility.getInstance().packetProxyLog("accept");

				String sniServerName = clientConnection.getSNI();
				PacketProxyUtility.getInstance().packetProxyLog(String.format("[QUIC-forward! using SNI] %s", sniServerName));

				ServerConnection serverConnection = new ServerConnection(
						ConnectionIdPair.generateRandom(),
						sniServerName,
						this.listen_info.getPort());

				String encoder = Servers.getInstance().queryByHostName(sniServerName).getEncoder();
				if (encoder == null) {
					encoder = "Sample";
				}

				DuplexAsync duplex = DuplexFactory.createDuplexAsync(
						clientConnection,
						serverConnection,
						encoder);

				duplex.start();
				DuplexManager.getInstance().registerDuplex(duplex);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void close() throws Exception {
		this.clientConnections.close();
	}
}
