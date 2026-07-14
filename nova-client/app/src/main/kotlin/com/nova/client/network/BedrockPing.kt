package com.nova.client.network

import com.nova.client.data.models.ServerStatus
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetAddress
import java.net.SocketTimeoutException
import java.nio.ByteBuffer
import java.nio.ByteOrder

/**
 * Pings a Minecraft Bedrock Edition server using the RakNet Unconnected Ping packet.
 *
 * Packet format (Bedrock 1.x):
 *   [0x01]          ID_UNCONNECTED_PING
 *   [8 bytes]       timestamp (long, big-endian)
 *   [16 bytes]      OFFLINE_MESSAGE_DATA_ID (magic)
 *   [8 bytes]       client GUID
 */
object BedrockPing {

    private val MAGIC = byteArrayOf(
        0x00, 0xFF.toByte(), 0xFF.toByte(), 0x00,
        0xFE.toByte(), 0xFE.toByte(), 0xFE.toByte(), 0xFE.toByte(),
        0xFD.toByte(), 0xFD.toByte(), 0xFD.toByte(), 0xFD.toByte(),
        0x12, 0x34, 0x56, 0x78
    )

    private const val UNCONNECTED_PING: Byte = 0x01
    private const val UNCONNECTED_PONG: Byte = 0x1C
    private const val TIMEOUT_MS = 3000

    suspend fun ping(address: String, port: Int = 19132): ServerStatus = withContext(Dispatchers.IO) {
        try {
            val socket = DatagramSocket()
            socket.soTimeout = TIMEOUT_MS

            val sendTime = System.currentTimeMillis()

            // Build ping packet
            val pingBuffer = ByteBuffer.allocate(33).order(ByteOrder.BIG_ENDIAN)
            pingBuffer.put(UNCONNECTED_PING)
            pingBuffer.putLong(sendTime)
            pingBuffer.put(MAGIC)
            pingBuffer.putLong(0x0000000000000000L) // client GUID (random is fine)

            val pingData = pingBuffer.array()
            val inetAddr = InetAddress.getByName(address)
            val sendPacket = DatagramPacket(pingData, pingData.size, inetAddr, port)
            socket.send(sendPacket)

            // Receive pong
            val receiveBuffer = ByteArray(1024)
            val receivePacket = DatagramPacket(receiveBuffer, receiveBuffer.size)
            socket.receive(receivePacket)

            val pingMs = System.currentTimeMillis() - sendTime
            socket.close()

            parsePong(address, port, receiveBuffer, receivePacket.length, pingMs)
        } catch (e: SocketTimeoutException) {
            ServerStatus(address, port, online = false, pingMs = -1)
        } catch (e: Exception) {
            ServerStatus(address, port, online = false, pingMs = -1)
        }
    }

    /**
     * Pong packet layout:
     *   [0x1C]         packet ID
     *   [8 bytes]      timestamp
     *   [8 bytes]      server GUID
     *   [16 bytes]     magic
     *   [2 bytes]      string length (big-endian short)
     *   [n bytes]      MOTD string (UTF-8)
     *
     * MOTD format: "MCPE;<motd>;<protocol>;<version>;<players>;<maxPlayers>;<serverUID>;<subMotd>;<gameMode>;<gameModeid>;<portIPv4>;<portIPv6>"
     */
    private fun parsePong(address: String, port: Int, data: ByteArray, length: Int, pingMs: Long): ServerStatus {
        if (length < 35) return ServerStatus(address, port, online = false, pingMs = pingMs)
        if (data[0] != UNCONNECTED_PONG) return ServerStatus(address, port, online = false, pingMs = pingMs)

        return try {
            val buf = ByteBuffer.wrap(data, 0, length).order(ByteOrder.BIG_ENDIAN)
            buf.get()                    // packet ID
            buf.getLong()                // timestamp
            buf.getLong()                // server GUID
            val magic = ByteArray(16); buf.get(magic)  // skip magic

            val strLen = buf.short.toInt() and 0xFFFF
            val strBytes = ByteArray(strLen); buf.get(strBytes)
            val motdRaw = String(strBytes, Charsets.UTF_8)

            val parts = motdRaw.split(";")
            // "MCPE" | motd | protocol | version | players | maxPlayers | ...
            if (parts.size < 6) {
                return ServerStatus(address, port, online = true, motd = motdRaw, pingMs = pingMs)
            }

            ServerStatus(
                address         = address,
                port            = port,
                online          = true,
                motd            = parts.getOrElse(1) { "" }.trimColors(),
                protocolVersion = parts.getOrElse(2) { "0" }.toIntOrNull() ?: 0,
                playerCount     = parts.getOrElse(4) { "0" }.toIntOrNull() ?: 0,
                maxPlayers      = parts.getOrElse(5) { "0" }.toIntOrNull() ?: 0,
                gameMode        = parts.getOrElse(8) { "" },
                pingMs          = pingMs
            )
        } catch (e: Exception) {
            ServerStatus(address, port, online = true, motd = "Server online", pingMs = pingMs)
        }
    }

    /** Strip Minecraft §color codes from a string. */
    private fun String.trimColors(): String = replace(Regex("§[0-9a-fk-or]"), "")
}
