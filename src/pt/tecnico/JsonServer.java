package pt.tecnico;

import java.io.*;
import java.net.*;
import java.util.*;

import com.google.gson.*;


public class JsonServer {

	/**
	 * Maximum size for a UDP packet. The field size sets a theoretical limit of
	 * 65,535 bytes (8 byte header + 65,527 bytes of data) for a UDP datagram.
	 * However the actual limit for the data length, which is imposed by the IPv4
	 * protocol, is 65,507 bytes (65,535 − 8 byte UDP header − 20 byte IP header.
	 */
	private static final int MAX_UDP_DATA_SIZE = (64 * 1024 - 1) - 8 - 20;

	/** Buffer size for receiving a UDP packet. */
	private static final int BUFFER_SIZE = MAX_UDP_DATA_SIZE;

	public static void main(String[] args) throws IOException {
		// Check arguments
		if (args.length < 1) {
			System.err.println("Argument(s) missing!");
			System.err.printf("Usage: java %s port%n", JsonServer.class.getName());
			return;
		}
		final int port = Integer.parseInt(args[0]);

		// Create server socket
		DatagramSocket socket = new DatagramSocket(port);
		System.out.printf("Server will receive packets on port %d %n", port);

		// Wait for client packets 
		byte[] buf = new byte[BUFFER_SIZE];
		while (true) {
			// Receive packet
			DatagramPacket clientPacket = new DatagramPacket(buf, buf.length);
			socket.receive(clientPacket);
			InetAddress clientAddress = clientPacket.getAddress();
			int clientPort = clientPacket.getPort();
			int clientLength = clientPacket.getLength();
			byte[] clientData = clientPacket.getData();
			System.out.printf("Received request packet from %s:%d!%n", clientAddress, clientPort);
			System.out.printf("%d bytes %n", clientLength);

			// Convert request to string
			String clientText = new String(clientData, 0, clientLength);
			System.out.println("Received request: " + clientText);

			// Parse JSON and extract arguments
			JsonObject requestJson = JsonParser.parseString​(clientText).getAsJsonObject();
			String from = null, body = null;
			{
				JsonObject infoJson = requestJson.getAsJsonObject("info");
				from = infoJson.get("from").getAsString();;
				body = requestJson.get("body").getAsString();
			}
			System.out.printf("Message from '%s':%n%s%n", from, body);

			// Create response message
			JsonObject responseJson = JsonParser.parseString​("{}").getAsJsonObject();
			{
				JsonObject infoJson = JsonParser.parseString​("{}").getAsJsonObject();
				infoJson.addProperty("from", "Bob");
				responseJson.add("info", infoJson);

				String bodyText = "Yes. See you tomorrow!";
				responseJson.addProperty("body", bodyText);
			}
			System.out.println("Response message: " + responseJson);

			// Send response
			byte[] serverData = responseJson.toString().getBytes();
			System.out.printf("%d bytes %n", serverData.length);
			DatagramPacket serverPacket = new DatagramPacket(serverData, serverData.length, clientPacket.getAddress(), clientPacket.getPort());
			socket.send(serverPacket);
			System.out.printf("Response packet sent to %s:%d!%n", clientPacket.getAddress(), clientPacket.getPort());
		}
	}
}
