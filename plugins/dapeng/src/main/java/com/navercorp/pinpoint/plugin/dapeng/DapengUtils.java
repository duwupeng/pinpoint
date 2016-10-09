/*
 * Copyright 2015 NAVER Corp.
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

package com.navercorp.pinpoint.plugin.dapeng;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.regex.Pattern;

import com.isuwang.dapeng.remoting.BaseServiceClient;

/**
 * @author duwupeng
 */
public class DapengUtils {
    
    private static final Pattern DOT_PATTERN = Pattern.compile("\\.");

    private DapengUtils() {}
    
    private static String convertDotPathToUriPath(String dotPath) {
        if (dotPath == null) {
            return "";
        }
        return DOT_PATTERN.matcher(dotPath).replaceAll("/");
    }
    


    /**
     * Returns the name of the specified {@link com.isuwang.dapeng.remoting.BaseServiceClient baseServiceClient}
     * to be used in Pinpoint.
     */
    public static String getClientServiceName(BaseServiceClient client) {
        String clientClassName = client.getClass().getName();
        return convertDotPathToUriPath(com.navercorp.pinpoint.plugin.dapeng.DapengConstants.CLIENT_PATTERN.split(clientClassName)[0]);
    }



    /**
     * Returns the ip address retrieved from the given {@link SocketAddress}.
     *
     * @param socketAddress the <tt>SocketAddress</tt> instance to retrieve the ip address from
     * @return the ip address retrieved from the given <tt>socketAddress</tt>,
     *      or {@literal ThriftConstants.UNKNOWN_ADDRESS} if it cannot be retrieved
     */
    // TODO should probably be pulled up as a common API
    public static String getIp(SocketAddress socketAddress) {
        if (socketAddress == null) {
            return com.navercorp.pinpoint.plugin.thrift.ThriftConstants.UNKNOWN_ADDRESS;
        }
        if (socketAddress instanceof InetSocketAddress) {
            InetSocketAddress addr = (InetSocketAddress)socketAddress;
            return addr.getAddress().getHostAddress();
        }
        return getSocketAddress(socketAddress);
    }

    /**
     * Returns the ip and port information retrieved from the given {@link SocketAddress}.
     *
     * @param socketAddress the <tt>SocketAddress</tt> instance to retrieve the ip/port information from
     * @return the ip/port retrieved from the given <tt>socketAddress</tt>,
     *      or {@literal ThriftConstants.UNKNOWN_ADDRESS} if it cannot be retrieved
     */
    // TODO should probably be pulled up as a common API
    public static String getIpPort(SocketAddress socketAddress) {
        if (socketAddress == null) {
            return com.navercorp.pinpoint.plugin.thrift.ThriftConstants.UNKNOWN_ADDRESS;
        }
        if (socketAddress instanceof InetSocketAddress) {
            InetSocketAddress addr = (InetSocketAddress)socketAddress;
            return addr.getAddress().getHostAddress() + ":" + addr.getPort();
        }
        return getSocketAddress(socketAddress);
    }

    
    private static String getSocketAddress(SocketAddress socketAddress) {
        String address = socketAddress.toString();
        int addressLength = address.length();

        if (addressLength > 0) {
            if (address.startsWith("/")) {
                return address.substring(1);
            } else {
                final int delimeterIndex = address.indexOf('/');
                if (delimeterIndex != -1 && delimeterIndex < addressLength) {
                    return address.substring(address.indexOf('/') + 1);
                }
            }
        }
        return address;
    }
}
