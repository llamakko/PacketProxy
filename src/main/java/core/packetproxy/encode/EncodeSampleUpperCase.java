/*
 * Copyright 2019 DeNA Co., Ltd.
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
package packetproxy.encode;

public class EncodeSampleUpperCase extends Encoder
{
	public EncodeSampleUpperCase(String ALPN) throws Exception {
		super(ALPN);
	}

	@Override
	public String getName() {
		return "Sample UpperCase";
	}

	/* 1つのリクエスト/レスポンスのサイズで区切ってください */
	@Override
	public int checkDelimiter(byte[] input_data) throws Exception {
		return input_data.length;
	}

	@Override
	public byte[] decodeServerResponse(byte[] input_data) throws Exception {
		return new String(input_data).toUpperCase().getBytes();
	}

	@Override
	public byte[] encodeServerResponse(byte[] input_data) throws Exception {
		return new String(input_data).toLowerCase().getBytes();
	}

	@Override
	public byte[] decodeClientRequest(byte[] input_data) throws Exception {
		return new String(input_data).toUpperCase().getBytes();
	}

	@Override
	public byte[] encodeClientRequest(byte[] input_data) throws Exception {
		return new String(input_data).toLowerCase().getBytes();
	}
}
