package pt.tecnico;

import java.io.*;
import java.net.*;

public class DatagramServer {

	/**
	 * Maximum size for a UDP packet. The field size sets a theoretical limit of
	 * 65,535 bytes (8 byte header + 65,527 bytes of data) for a UDP datagram.
	 * However the actual limit for the data length, which is imposed by the IPv4
	 * protocol, is 65,507 bytes (65,535 − 8 byte UDP header − 20 byte IP header.
	 */
	private static final int MAX_UDP_DATA_SIZE = (64 * 1024 - 1) - 8 - 20;

	/** Buffer size for receiving a UDP packet. */
	private static final int BUFFER_SIZE = MAX_UDP_DATA_SIZE;

	/** Custom command to stop the server. */
	private static final String END_MESSAGE = "end";

	public static void main(String[] args) throws IOException {
		// Check arguments
		if (args.length < 1) {
			System.err.println("Argument(s) missing!");
			System.err.printf("Usage: java %s port%n", DatagramServer.class.getName());
			return;
		}

		// Convert port from String to int
		final int port = Integer.parseInt(args[0]);

		// Create server socket
		DatagramSocket socket = new DatagramSocket(port);
		System.out.printf("Server will receive packets on port %d %n", port);

		// wait for client packets until end message is received
		boolean running = true;
		byte[] buf = new byte[BUFFER_SIZE];
		while (running) {
			DatagramPacket clientPacket = new DatagramPacket(buf, buf.length);
			socket.receive(clientPacket);
			System.out.println("Received packet: " + clientPacket);

			InetAddress clientAddress = clientPacket.getAddress();
			int clientPort = clientPacket.getPort();
			int clientLength = clientPacket.getLength();
			byte[] clientData = clientPacket.getData();

			System.out.printf("Received from: %s:%d %n", clientAddress, clientPort);
			System.out.printf("Received bytes: %d %n", clientLength);
			// this will usually be smaller than the buffer size
			System.out.printf("Receive buffer size: %d %n", clientData.length);

			String clientText = new String(clientData, 0, clientLength);
			System.out.println("Received text: " + clientText);

			if (END_MESSAGE.equals(clientText)) {
				// this will be the last reply from the server
				System.out.println("Received END message");
				running = false;
			}

			String serverText = "Hello " + clientText;
			System.out.println("Text to send: " + serverText);
			byte[] serverData = serverText.getBytes();
			System.out.printf("%d bytes to send%n", serverData.length);

			DatagramPacket serverPacket = new DatagramPacket(serverData, serverData.length, clientAddress, clientPort);
			socket.send(serverPacket);
			System.out.println("Sent packet: " + serverPacket);
		}
		// Close socket
		socket.close();
		System.out.println("Closed socket");
	}
}
